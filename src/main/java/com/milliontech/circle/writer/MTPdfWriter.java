package com.milliontech.circle.writer;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.constants.PdfConstants;
import com.milliontech.circle.data.converter.ParameterDataConverter;
import com.milliontech.circle.data.model.CriteriaData;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.exception.MTReportException;
import com.milliontech.circle.helper.MultiTableHelper;
import com.milliontech.circle.helper.PdfFontRepository;
import com.milliontech.circle.helper.PdfHelper;
import com.milliontech.circle.helper.ValueHelper;
import com.milliontech.circle.model.AggregateCell;
import com.milliontech.circle.model.ObjectCell;
import com.milliontech.circle.model.RangeCell;
import com.milliontech.circle.model.Report;
import com.milliontech.circle.model.ReportSetting;
import com.milliontech.circle.model.SummaryRow;
import com.milliontech.circle.model.TableHeader;
import com.milliontech.circle.model.TableHeaderGroup;
import com.milliontech.circle.model.pdf.HeaderFooter;
import com.milliontech.circle.model.pdf.PdfStyle;
import com.milliontech.circle.utils.AggregateUtils;

public class MTPdfWriter implements MTWriter{

	private static final Logger log = LoggerFactory.getLogger(MTPdfWriter.class);

	private PdfStyle CRITERIA_STYLE;
	private PdfStyle HEADER_TITLE_STYLE;
	private PdfStyle TABLE_TITLE_STYLE;
	private PdfStyle CONTENT_STYLE;
	private PdfStyle SUMMARY_CELL_STYLE;
	private PdfStyle HEADER_FOOTER_STYLE;
	private ParameterData data;

	public MTPdfWriter(ParameterData data, ReportSetting setting){
	    List<BaseFont> bfList = data.getFontFilePathList().isEmpty() ? 
	            PdfFontRepository.SANS_SERIF_FONT_LIST : 
	            PdfFontRepository.createBaseFontList(data.getFontFilePathList());
	    
	    CRITERIA_STYLE = new PdfStyle("criteria", PdfFontRepository.createFontList(bfList, Optional.ofNullable(data.getFontSizeCriteria()).orElse(PdfConstants.DEFAULT_CRITERIA_FONT_SIZE), Font.NORMAL));
	    HEADER_TITLE_STYLE = new PdfStyle("title", PdfFontRepository.createFontList(bfList, Optional.ofNullable(data.getFontSizeTitle()).orElse(PdfConstants.DEFAULT_TITLE_FONT_SIZE), Font.BOLD));
	    TABLE_TITLE_STYLE = new PdfStyle("columnHeader", PdfFontRepository.createFontList(bfList, Optional.ofNullable(data.getFontSizeColumnHeader()).orElse(PdfConstants.DEFAULT_TABLE_HEADER_SIZE), Font.BOLD));
		CONTENT_STYLE = new PdfStyle("content", PdfFontRepository.createFontList(bfList, Optional.ofNullable(data.getFontSizeContent()).orElse(PdfConstants.DEFAULT_TABLE_CONTENT_FONT_SIZE), Font.NORMAL));
		SUMMARY_CELL_STYLE = new PdfStyle("summary", PdfFontRepository.createFontList(bfList, Optional.ofNullable(data.getFontSizeContent()).orElse(PdfConstants.DEFAULT_TABLE_CONTENT_FONT_SIZE), Font.NORMAL));
		HEADER_FOOTER_STYLE = new PdfStyle("headerFooter", PdfFontRepository.createFontList(bfList, 8f, Font.NORMAL));
		
		this.data = data;
	}

	public void writer(List dataList, ParameterData data, Report report, OutputStream out) throws Exception {
		this.data = data;
		Document document = PdfHelper.createDocument(data, report.getReportSetting());
	    PdfWriter writer = PdfWriter.getInstance(document, out);
	    writer.setPdfVersion(PdfWriter.VERSION_1_7);
	    writer.setPageEvent(new HeaderFooter(data, report.getReportSetting(), HEADER_FOOTER_STYLE, HEADER_FOOTER_STYLE));
	    document.open();

	    if (StringUtils.isNotBlank(data.getAuthorName())) {
        document.addAuthor(data.getAuthorName());
	    }
        document.addTitle(data.getHeaderTitle());
        
        Calendar printCal = Calendar.getInstance();
        printCal.setTime(data.getPrintDate());
        PdfDate pdfPrintDate = new PdfDate(printCal);
        
        writer.getInfo().put(PdfName.CREATIONDATE, pdfPrintDate);
        writer.getInfo().put(PdfName.MODDATE, pdfPrintDate);

	    if(data.getCustomPdfHeader()!=null){
	    	data.getCustomPdfHeader().writeCustomHeader(data, document, writer);
	    }else{
	    	setHeaderTitle(data, document);
	    }
	    if(data.getCustomPdfCriteria()!=null){
	    	data.getCustomPdfCriteria().writeCustomCriteria(data, document);
	    }else{
	    	setCriteria(data, report, document);
	    }

		//log.debug("noOfColumns = "+noOfColumns);

		if(report.getReportSetting().isMultiTable()){
			if(dataList.size()>0){
				writeMultipleTables(dataList, data, report, document);
			}
		}else{
			List list = report.getFullTableHeaderList();
			int noOfColumns = list.size();
			writeSingleTable(dataList, data, report, document, list, noOfColumns, null);
		}

		if(data.getCustomPdfFooter()!=null){
			data.getCustomPdfFooter().writeCustomFooter(data, document, writer);
		}

	    document.close();
	}

	private void writeMultipleTables(List dataList, ParameterData data, Report report, Document document) throws Exception, DocumentException {
		MultiTableHelper helper = new MultiTableHelper();
		List newDataList = new ArrayList();
		String lastValue = null;
		boolean writeTable = false;
		String splitBy = "";
		for(Iterator iter = dataList.iterator(); iter.hasNext();){
			Object obj = iter.next();
			String value = ValueHelper.getDataValue(obj.getClass(), obj, report.getReportSetting().getMultiTableSplitBy(),null, data.getRemapValueMap()).toString();
			if(lastValue == null){
				lastValue = value;
			}else if(!lastValue.equals(value)){
				writeTable = true;
				splitBy = lastValue;
				lastValue = value;
			}else{
				writeTable = false;
			}
			if(writeTable){
				helper.resetTableHeaderForMultiTableDynFld(report.getTableHeaderGroupList(), splitBy);
				helper.createTableHeaderForMultiTableDynFld(report.getTableHeaderGroupList(), splitBy);
				List list = report.getFullTableHeaderList();
				int noOfColumns = list.size();
				writeSingleTable(newDataList, data, report, document, list, noOfColumns, splitBy);
				newDataList.clear();
			}
			newDataList.add(obj);
		}
		splitBy = lastValue;
		helper.resetTableHeaderForMultiTableDynFld(report.getTableHeaderGroupList(), splitBy);
		helper.createTableHeaderForMultiTableDynFld(report.getTableHeaderGroupList(), splitBy);
		List list = report.getFullTableHeaderList();
		int noOfColumns = list.size();
		writeSingleTable(newDataList, data, report, document, list, noOfColumns, splitBy);
	}

	private void writeSingleTable(List dataList, ParameterData data, Report report, Document document, List list, int noOfColumns, String splitBy) throws Exception, DocumentException {
		int totalColumns = noOfColumns + (report.getReportSetting().isShowRowNo() ? 1 : 0);
		PdfPTable dataTable = new PdfPTable(totalColumns);
		dataTable.setComplete(false); // For release memory
		dataTable.getDefaultCell().setBorder(Rectangle.BOX);
		dataTable.setWidthPercentage(100f);
		dataTable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
		if(dataList.size()>0){
			dataTable.setHeaderRows(report.getReportSetting().getHeaderRows()+(splitBy==null?0:1));
		}
		dataTable.setSpacingBefore(3);
		dataTable.setSpacingAfter(3);

		if(splitBy!=null){
			PdfHelper.createPdfCell(dataTable, splitBy, TABLE_TITLE_STYLE, null, Rectangle.BOX, Constants.ALIGN_LEFT, null , totalColumns, 1);
		}
		setTableHeader(report, list, dataTable);
		setTableContent(dataList, list, dataTable, report);
		setSummaryRow(data, report, noOfColumns, dataTable, dataList);
		setTableColumnWidths(list, noOfColumns, dataTable, report.getReportSetting(), dataList.size());

		dataTable.completeRow();
		dataTable.setComplete(true);// For release memory
		document.add(dataTable);
	}

	private void setSummaryRow(ParameterData data, Report report, int noOfColumns, PdfPTable dataTable, List dataList) throws Exception {
		for(Iterator iter = report.getSummaryRowList().iterator(); iter.hasNext();){
			SummaryRow row = (SummaryRow)iter.next();
			Object dataRow = data.getSummaryRowDataMap().get(row.getKey());
			BaseColor color = row.isHightlight() ? PdfConstants.SUMMARY_ROW_COLOR : null;

			if(report.getReportSetting().isShowRowNo()){
				PdfHelper.createPdfCell(dataTable, "", SUMMARY_CELL_STYLE, null, Rectangle.BOX, Constants.ALIGN_LEFT, color);
			}

			int colspan = 1;
			for(int i=0; i<noOfColumns; i++){
				if(colspan > 1){
					colspan --;
					continue;
				}

				String value = "";
				String align = Constants.ALIGN_LEFT;
				for(Iterator rIter = row.getRangeCellList().iterator(); rIter.hasNext();){
					RangeCell cell = (RangeCell)rIter.next();
					if(cell.getStart()==i){
						value = cell.getTitle();
						colspan = cell.getEnd()-cell.getStart()+1;
						align = cell.getAlign();
					}
				}
				for(Iterator aIter = row.getAggregateCellList().iterator(); aIter.hasNext();){
					AggregateCell cell = (AggregateCell)aIter.next();
					if(cell.getColumn()==i){
						if(!dataList.isEmpty()){
							AggregateUtils utils = new AggregateUtils(cell.getCalValList());
							BigDecimal calcVal = utils.getValue(cell.getMode());
							if(calcVal != null){
								DecimalFormat fmt = new DecimalFormat(cell.getFormat()==null?"#.##":cell.getFormat());
								value = fmt.format(calcVal);
							} else {
								value = "";
							}
						}else{
							value = "";
						}
						align = Constants.ALIGN_RIGHT;
					}
				}
				for(Iterator oIter = row.getObjectCellList().iterator(); oIter.hasNext();){
					ObjectCell cell = (ObjectCell)oIter.next();
					if(cell.getColumn()==i){
						Object object = ValueHelper.getDataValue(dataRow.getClass(), dataRow, cell.getMethod(), null, data.getRemapValueMap());
						value = PdfHelper.getCellStringValue(object, cell.getFormat());
						align = cell.getAlign();
					}
				}

				PdfHelper.createPdfCell(dataTable, value, SUMMARY_CELL_STYLE, null, Rectangle.BOX, align, color, colspan, 1);
			}
		}
	}

	private void setTableHeader(Report report, List list, PdfPTable dataTable) {
		int[] colorCodes = report.getReportSetting().getTableHeaderColorArray();
		BaseColor tableHeaderColor = colorCodes != null ? new BaseColor(colorCodes[0], colorCodes[1], colorCodes[2]) : PdfConstants.TABLE_HEADER_COLOR;

		BaseColor color = (report.getReportSetting().isHightlightHeader() ? tableHeaderColor : null);
		if(!report.getTableHeaderList().isEmpty()){
			if(report.getReportSetting().isShowRowNo()){
				PdfHelper.createPdfCell(dataTable, "", TABLE_TITLE_STYLE, null, Rectangle.BOX, Constants.ALIGN_CENTER, color);
			}
			for(Iterator iter =  report.getTableHeaderList().iterator(); iter.hasNext();){
				TableHeader header = (TableHeader)iter.next();
				PdfHelper.createPdfCell(dataTable, header.getTitle(), TABLE_TITLE_STYLE, header, Rectangle.BOX, Constants.ALIGN_CENTER, color);
			}
		}else{

			if(report.getReportSetting().isShowRowNo()){
				PdfHelper.createPdfCell(dataTable, "", TABLE_TITLE_STYLE, null, Rectangle.BOX, Constants.ALIGN_CENTER, color, 1, report.getReportSetting().getHeaderRows());
			}
			for(Iterator gIter = report.getTableHeaderGroupList().iterator(); gIter.hasNext();){
				TableHeaderGroup group = (TableHeaderGroup)gIter.next();
				for(int i=0; i<report.getReportSetting().getHeaderRows(); i++){
					setTableHeaderForEachTableHeaderGroup(dataTable, group, report.getReportSetting(), 0, i, color);
				}

			}

		}
	}

	private void setTableHeaderForEachTableHeaderGroup(PdfPTable dataTable, TableHeaderGroup group, ReportSetting setting, int currentRow, int executeRow, BaseColor color){
		List fullList = group.getSortedFullList();
		int totalRows = setting.getHeaderRows();

		if(currentRow == executeRow){
			for(Iterator iter = fullList.iterator(); iter.hasNext(); ){
				Object obj = iter.next();

				int colspan = 1;
				int rowspan = 1;
				String title = "";
				Paragraph pv = null;

				if(obj instanceof TableHeader){

					TableHeader header = (TableHeader)obj;
					title = header.getTitle();
					pv = PdfHelper.createDisplayParagraph(title, TABLE_TITLE_STYLE.getFontList(), false, header);
					if(totalRows-currentRow>1){
						rowspan = totalRows-currentRow;
					}

				}else if(obj instanceof TableHeaderGroup){

					TableHeaderGroup groupChild = (TableHeaderGroup)obj;
					title = groupChild.getTitle();
					int noOfColumns = groupChild.numOfColumns();
					if(noOfColumns>1){
						colspan = noOfColumns;
					}

				}else{
					throw new MTReportException("Unsupport Exception");
				}

				PdfHelper.createPdfCell(dataTable, title, TABLE_TITLE_STYLE, null, Rectangle.BOX, Constants.ALIGN_CENTER, color, colspan, rowspan);

			}
		}

		for(Iterator iter = fullList.iterator(); iter.hasNext(); ){
			Object obj = iter.next();
			if(obj instanceof TableHeaderGroup){
				TableHeaderGroup groupChild = (TableHeaderGroup)obj;
				this.setTableHeaderForEachTableHeaderGroup(dataTable, groupChild, setting, currentRow+1, executeRow, color);
			}
		}
	}

	private Map createRowspanMapForMergeIsSameOption(List dataList, List list) throws Exception{
		Map rowspanMap = new HashMap();

		Map lastColumnValueMap = new HashMap();
		Map lastRowIndexMap = new HashMap();

		int rowIndex = 0;
		for(Iterator iter = dataList.iterator(); iter.hasNext();){
			Object obj = iter.next();

			for(Iterator hIter = list.iterator(); hIter.hasNext();){
				TableHeader header = (TableHeader)hIter.next();
				if(header.isMergeIfSame()){
					Object value = ValueHelper.getDataValue(obj.getClass(), obj, header.getMethod(), header.getProperty(), data.getRemapValueMap());
					String strValue = PdfHelper.getCellStringValue(value, header.getFormat());
					String mergeKeyValue = "";
					if(header.getMergeKey()!=null){
						Object v = ValueHelper.getDataValue(obj.getClass(), obj, header.getMergeKey(), null, data.getRemapValueMap());
						if(v != null){
							mergeKeyValue = v.toString();
						}
					}

					String key = (header.getMethod()==null ? header.getProperty() : header.getMethod());
					String compareDataKey = mergeKeyValue + key;

					if(lastColumnValueMap.get(compareDataKey)==null || !lastColumnValueMap.get(compareDataKey).equals(strValue) ){
						lastRowIndexMap.put(compareDataKey, new Integer(rowIndex));
						lastColumnValueMap.put(compareDataKey, strValue);
						rowspanMap.put(getRowspanKey(key, new Integer(rowIndex) ), new Integer(1));
					}else{
						Integer lastIndex = (Integer)lastRowIndexMap.get(compareDataKey);
						Integer rowspan = (Integer)rowspanMap.get(getRowspanKey(key, lastIndex));
						rowspanMap.put(getRowspanKey(key, lastIndex), new Integer(rowspan.intValue()+1));
					}
				}
			}

			rowIndex ++;
		}
		return rowspanMap;
	}

	private String getRowspanKey(String methodProperty, Integer rowIndex){
		return methodProperty+"_"+rowIndex.intValue();
	}

	private void setTableContent(List dataList, List list, PdfPTable dataTable, Report report) throws Exception {

		Map rowspanMap = this.createRowspanMapForMergeIsSameOption(dataList, list);

		int rowIndex = 0;
		for(Iterator iter = dataList.iterator(); iter.hasNext();){
			Object obj = iter.next();

			Float grey = (rowIndex % 2 == 1) ? new Float(0.92f) : null;
			if(report.getReportSetting().isPdfAlterRowColorOff()){
				grey = null;
			}

			if(report.getReportSetting().isShowRowNo()){
				boolean highlight = ValueHelper.isHighlight(obj.getClass(), obj, report.getReportSetting().getHighlightRowNoFld());
				BaseColor color = highlight ? PdfHelper.getHighlightColor(report.getReportSetting().getHighlightRowNoColor()) : null;
				PdfHelper.createPdfCell(dataTable, ""+(rowIndex+1), CONTENT_STYLE, null, Rectangle.BOX, Constants.ALIGN_LEFT, color, grey);
			}

			int colIndex = 0;
			for(Iterator hIter = list.iterator(); hIter.hasNext();){
				TableHeader header = (TableHeader)hIter.next();

				Object value = ValueHelper.getDataValue(obj.getClass(), obj, header.getMethod(), header.getProperty(), data.getRemapValueMap());
				String strValue = PdfHelper.getCellStringValue(value, header.getFormat());
				String align = Constants.ALIGN_LEFT;
				boolean highlight = ValueHelper.isHighlight(obj.getClass(), obj, header.getHighlightFld());
				BaseColor color = highlight ? PdfHelper.getHighlightColor(header.getHighlightColor()) : null;
				if(header.getAlign()!=null){
					align = header.getAlign();
				}else if(value instanceof BigDecimal || value instanceof Long || value instanceof Integer || value instanceof Double){
					align = Constants.ALIGN_RIGHT;
				}

				//log.debug(header.getMethod()+" - "+align+value.getClass()+header.getAlign());

				if(header.isMergeIfSame()){
					String key = (header.getMethod()==null ? header.getProperty() : header.getMethod());
					Integer rowspan = (Integer)rowspanMap.get(getRowspanKey(key,new Integer(rowIndex)));
					if(rowspan!=null){
						PdfHelper.createPdfCell(dataTable, strValue, CONTENT_STYLE, header, Rectangle.BOX, align, color, 1, rowspan.intValue());
					}
				}else{
					PdfHelper.createPdfCell(dataTable, strValue, CONTENT_STYLE, header, Rectangle.BOX, align, color, highlight? null : grey);
				}

				for(Iterator sIter = report.getSummaryRowList().iterator();sIter.hasNext();){
					SummaryRow row = (SummaryRow)sIter.next();
					if(!row.getAggregateCellMap().isEmpty()){
						Map map = row.getAggregateCellMap();
						AggregateCell aCell = (AggregateCell)map.get(new Integer(colIndex));
						if(aCell!=null){
							aCell.getCalValList().add(value);
						}
					}
				}

				colIndex++;
			}

			rowIndex++;
		}
	}

	private void setTableColumnWidths(List list, int noOfColumns, PdfPTable dataTable, ReportSetting setting, int noOfRows) throws DocumentException {
		float[] widths = new float[noOfColumns + (setting.isShowRowNo() ? 1 : 0) ];
		int i=0;
		if(setting.isShowRowNo()){
			widths[0] = (new Chunk(""+noOfRows)).getWidthPoint()+5;
			i++;
		}
		for(Iterator hIter = list.iterator(); hIter.hasNext();){
			TableHeader header = (TableHeader)hIter.next();
			widths[i] = header.getCalcWidth();
			i++;
		}
		dataTable.setWidths(widths);
	}

	private void setCriteria(ParameterData data, Report report, Document document) throws Exception {
		if(report.getReportSetting().isShowCriteria()){
			List criteriaList = ParameterDataConverter.convertToCriteriaDataList(data);
			if(!criteriaList.isEmpty()){
				int columnWidth = 3;
				PdfPTable criteriaTable = new PdfPTable(columnWidth);
				criteriaTable.setComplete(false); // For release memory
				criteriaTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
				criteriaTable.setWidthPercentage(100f);
				criteriaTable.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
				criteriaTable.setSpacingBefore(5);

				createSeperator(criteriaTable, columnWidth);

				int i=0;
				for (Iterator iter = criteriaList.iterator(); iter.hasNext();) {
					CriteriaData criteriaData = (CriteriaData)iter.next();

					String value = criteriaData.getName() + " : " + criteriaData.getValue();
					PdfHelper.createPdfCell(criteriaTable, value, CRITERIA_STYLE, null, Rectangle.NO_BORDER, Constants.ALIGN_LEFT, null);

					i++;
				}

				if(i % columnWidth > 0){
					for(int j=0; j< columnWidth - (i % columnWidth) ; j++){
						PdfHelper.createPdfCell(criteriaTable, "", CRITERIA_STYLE, null, Rectangle.NO_BORDER, Constants.ALIGN_LEFT, null);
					}
				}

				createSeperator(criteriaTable, columnWidth);

				criteriaTable.completeRow();
				criteriaTable.setComplete(true);// For release memory
				document.add(criteriaTable);
			}
		}
	}

	private void createSeperator(PdfPTable criteriaTable, int columnWidth) {
		PdfPCell spaceCell = new PdfPCell();
		spaceCell.setBorder(Rectangle.NO_BORDER);
		spaceCell.setGrayFill(0.6f);
		spaceCell.setColspan(columnWidth);
		spaceCell.setMinimumHeight(3f);
		criteriaTable.addCell(spaceCell);
	}

	private void setHeaderTitle(ParameterData data, Document document) throws DocumentException {
		Paragraph p = PdfHelper.createDisplayParagraph(data.getHeaderTitle(), HEADER_TITLE_STYLE.getFontList(), false);
		p.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(p);
		document.add(new Paragraph("\n"));
	}

}

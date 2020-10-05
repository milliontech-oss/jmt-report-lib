package com.milliontech.circle.writer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.TempFileCreationStrategy;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.constants.ExcelConstants;
import com.milliontech.circle.constants.PaperSizeConstants;
import com.milliontech.circle.constants.ReportType;
import com.milliontech.circle.data.converter.ParameterDataConverter;
import com.milliontech.circle.data.model.CriteriaData;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.exception.MTReportException;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.helper.ExcelHelper;
import com.milliontech.circle.helper.MultiTableHelper;
import com.milliontech.circle.helper.ValueHelper;
import com.milliontech.circle.model.AggregateCell;
import com.milliontech.circle.model.ObjectCell;
import com.milliontech.circle.model.RangeCell;
import com.milliontech.circle.model.Report;
import com.milliontech.circle.model.ReportSetting;
import com.milliontech.circle.model.SummaryRow;
import com.milliontech.circle.model.TableHeader;
import com.milliontech.circle.model.TableHeaderGroup;

public class MTExcelWriter implements MTWriter{

	private static final Logger log = LoggerFactory.getLogger(MTExcelWriter.class);

	private static final int MAX_COLUMN_WIDTH;

	private CellStyle SEPERATOR_STYLE;
	private CellStyle CRITERIA_STYLE;
	private CellStyle TABLE_TITLE_STYLE;
	private CellStyle SUMMARY_CELL_LEFT_STYLE;
	private CellStyle SUMMARY_CELL_RIGHT_STYLE;
	private CellStyle SUMMARY_CELL_HIGHLIGHT_LEFT_STYLE;
	private CellStyle SUMMARY_CELL_HIGHLIGHT_RIGHT_STYLE;
	private DataFormat format;
	private Map<String, CellStyle> styleMap;

	private Map<TableHeader, Integer> sxssfColumnWidthMap;

	private Workbook wb;

	private static class SXSSFTempFileStrategy implements TempFileCreationStrategy {

		private String tmpFilePath;

		public SXSSFTempFileStrategy(String tmpFilePath){
			this.tmpFilePath = tmpFilePath;
		}

		public String getTmpFilePath() {
			return tmpFilePath;
		}

		@Override
		public File createTempFile(String prefix, String suffix) throws IOException {
			final File tmpFolder = new File(tmpFilePath);
			if (!tmpFolder.exists()){
				tmpFolder.mkdir();
			}
			File newFile = File.createTempFile(prefix, suffix, tmpFolder);
			log.info("temp file " + newFile.getAbsolutePath() + " is created");
			return newFile;
		}

		@Override
		public File createTempDirectory(String prefix) throws IOException {
			final File tmpFolder = new File(tmpFilePath);
			if (!tmpFolder.exists()){
				log.info("temp folder " + tmpFolder.getAbsolutePath() + " is created");
				tmpFolder.mkdir();
			}
			return tmpFolder;
		}
	}

	private static SXSSFTempFileStrategy SXSSF_SYS_TEMP_FILE_STRATEGY;
	private static SXSSFTempFileStrategy SXSSF_USER_TEMP_FILE_STRATEGY;

	static {
		String sysTmpPath = System.getProperty("java.io.tmpdir");
		SXSSF_SYS_TEMP_FILE_STRATEGY = new SXSSFTempFileStrategy(sysTmpPath);
		MAX_COLUMN_WIDTH = 255 * 256;
	}

	public MTExcelWriter(ParameterData data, ReportSetting setting){
		this(data, setting, ReportType.SXSSF);
	}

	public MTExcelWriter(ParameterData data, ReportSetting setting, ReportType reportType){
		Workbook wb = null;

		if(ReportType.XLS.getType().equalsIgnoreCase(reportType.getType())){
			wb = new HSSFWorkbook();
		} else if(ReportType.XLSX.getType().equalsIgnoreCase(reportType.getType())){
			wb = new XSSFWorkbook();
		} else {
			wb = new SXSSFWorkbook(null, -1, data.isSxssfCompressTmpFile(), data.isSxssfUseSharedStringTables());

			String tmpFilePath = data.getSxssfTmpFilePath();
			if(StringUtils.isBlank(tmpFilePath)){
				synchronized(MTExcelWriter.class){
					if(SXSSF_USER_TEMP_FILE_STRATEGY == null){
						log.warn("No temp file path path provided. Using system property [java.io.tmpdir] = " + SXSSF_SYS_TEMP_FILE_STRATEGY.getTmpFilePath());
						SXSSF_USER_TEMP_FILE_STRATEGY = SXSSF_SYS_TEMP_FILE_STRATEGY;
						TempFile.setTempFileCreationStrategy(SXSSF_USER_TEMP_FILE_STRATEGY);
					}
				}
			} else {
				synchronized(MTExcelWriter.class){
					if (SXSSF_USER_TEMP_FILE_STRATEGY == null || !(SXSSF_USER_TEMP_FILE_STRATEGY.getTmpFilePath().equalsIgnoreCase(tmpFilePath))){
						SXSSFTempFileStrategy strategy = new SXSSFTempFileStrategy(tmpFilePath);
						SXSSF_USER_TEMP_FILE_STRATEGY = strategy;
						TempFile.setTempFileCreationStrategy(strategy);
					}
				}
			}
			sxssfColumnWidthMap = new HashMap<TableHeader, Integer>();

		}

		this.wb = wb;
		init(data, setting);
	}

	public MTExcelWriter(ParameterData data, ReportSetting setting, Workbook templateWorkbook){
		this.wb = templateWorkbook;
		init(data, setting);
	}

	private void init(ParameterData data, ReportSetting setting){
		this.styleMap = new HashMap<String, CellStyle>();
		this.SEPERATOR_STYLE = ExcelHelper.createSeperatorStyle(wb);
		this.CRITERIA_STYLE = ExcelHelper.createCriteriaStyle(wb, data, setting);
		this.TABLE_TITLE_STYLE = ExcelHelper.createTableTitleStyle(wb, data, setting);
		this.SUMMARY_CELL_LEFT_STYLE = ExcelHelper.createSummaryCellStyle(wb, data, setting, Constants.ALIGN_LEFT, false);
		this.SUMMARY_CELL_RIGHT_STYLE = ExcelHelper.createSummaryCellStyle(wb, data, setting, Constants.ALIGN_RIGHT, false);
		this.SUMMARY_CELL_HIGHLIGHT_LEFT_STYLE = ExcelHelper.createSummaryCellStyle(wb, data, setting, Constants.ALIGN_LEFT, true);
		this.SUMMARY_CELL_HIGHLIGHT_RIGHT_STYLE = ExcelHelper.createSummaryCellStyle(wb, data, setting, Constants.ALIGN_RIGHT, true);
		this.format = wb.createDataFormat();
		
		if(this.wb instanceof HSSFWorkbook){
		    HSSFWorkbook hwb = (HSSFWorkbook)wb;
			ExcelHelper.resetColorPalette(hwb);
			hwb.createInformationProperties();
		}
	}

	public void writer(List dataList, ParameterData data, Report report, OutputStream out) throws Exception{
		String sheetName = "Data";
		if(StringUtils.isNotBlank(data.getHeaderTitle())){
			sheetName = WorkbookUtil.createSafeSheetName(data.getHeaderTitle());
		}

		Sheet sheet = wb.createSheet(sheetName);

		int rowNo = 0;
		int numOfColumns = report.numOfColumns();

		if(data.getCustomExcelCriteria()!=null){
			rowNo = data.getCustomExcelCriteria().writeCriteriaAndReturnRowNum(data, wb, sheet, rowNo, CRITERIA_STYLE);
		} else if (report.getReportSetting().isShowCriteria()) {
			rowNo = setCriteria(sheet, data, report.getReportSetting(), rowNo, numOfColumns);
		}

		if(report.getReportSetting().isMultiTable()){
			if(dataList.size()>0){
				rowNo = writeMultipleTables(dataList, data, report, sheet, rowNo);
			}

		}else{
			rowNo = writeSingleTable(dataList, data, report, sheet, rowNo);
		}

		if(data.getCustomExcelPageSetup()!=null){
			data.getCustomExcelPageSetup().setPrintAndPageSetup(data, wb, sheet);
		}else{
			setPageAndPrintSetup(sheet, data, report.getReportSetting());
		}
		
		if (wb instanceof HSSFWorkbook) {
		    doWriteDocumentProperties((HSSFWorkbook) wb, data, sheetName);
		} else if (wb instanceof XSSFWorkbook) {
		    doWriteDocumentProperties((XSSFWorkbook) wb, data, sheetName);
		} else if (wb instanceof SXSSFWorkbook) {
		    SXSSFWorkbook swb = (SXSSFWorkbook) wb;
		    doWriteDocumentProperties(swb.getXSSFWorkbook(), data, sheetName);
		}
		
	    wb.write(out);

	    if(wb instanceof SXSSFWorkbook){
	    	SXSSFWorkbook swb = (SXSSFWorkbook) wb;
	    	if(!data.isSxssfKeepTmpFile()){
		    	boolean delResult = swb.dispose();
		    	if(!delResult){
		    		log.warn("Unable to delete the temp file");
		    	}
	    	}
	    }
	}

	private void doWriteDocumentProperties(HSSFWorkbook wb, ParameterData data, String title) {
	    SummaryInformation summaryInfo = wb.getSummaryInformation();
	    summaryInfo.getFirstSection().setCodepage(CodePageUtil.CP_UTF8);
	    summaryInfo.setAuthor(data.getAuthorName());
	    summaryInfo.setCreateDateTime(data.getPrintDate());
	    summaryInfo.setTitle(title);
	    
	    DocumentSummaryInformation docInfo = wb.getDocumentSummaryInformation();
	    docInfo.getFirstSection().setCodepage(CodePageUtil.CP_UTF8);
	}
	
	private void doWriteDocumentProperties(XSSFWorkbook wb, ParameterData data, String title) {
	    POIXMLProperties xmlProps = wb.getProperties();
        POIXMLProperties.CoreProperties coreProps =  xmlProps.getCoreProperties();
        coreProps.setCreator(data.getAuthorName());
        coreProps.setCreated(Optional.of(data.getPrintDate()));
        coreProps.setTitle(title);
	}

    private int writeMultipleTables(List dataList, ParameterData data, Report report, Sheet sheet, int rowNo) throws Exception {
		MultiTableHelper helper = new MultiTableHelper();
		List newDataList = new ArrayList();
		String lastValue = null;
		boolean writeTable = false;
		String splitBy = "";
		for(Iterator iter = dataList.iterator(); iter.hasNext();){
			Object obj = iter.next();
			String value = ValueHelper.getDataValue(obj.getClass(), obj, report.getReportSetting().getMultiTableSplitBy(), null, data.getRemapValueMap()).toString();
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

				rowNo++;
				Row splitByRow = sheet.createRow(rowNo);
				splitByRow.createCell(0).setCellValue(splitBy);
				rowNo++;
				rowNo = writeSingleTable(newDataList, data, report, sheet, rowNo);

				newDataList.clear();
			}
			newDataList.add(obj);
		}
		splitBy = lastValue;
		helper.resetTableHeaderForMultiTableDynFld(report.getTableHeaderGroupList(), splitBy);
		helper.createTableHeaderForMultiTableDynFld(report.getTableHeaderGroupList(), splitBy);

		rowNo++;
		Row splitByRow = sheet.createRow(rowNo);
		splitByRow.createCell(0).setCellValue(splitBy);
		rowNo++;
		rowNo = writeSingleTable(newDataList, data, report, sheet, rowNo);
		return rowNo;
	}

	private int writeSingleTable(List dataList, ParameterData data,
			Report report, Sheet sheet, int rowNo) throws Exception {
		int startContentRow;
		int endContentRow;
		rowNo = setTableHeader(sheet, data, report, rowNo);
		startContentRow = rowNo;
		rowNo = setTableContent(dataList, sheet, data, report, rowNo);
		endContentRow = rowNo-1;
		rowNo = setSummaryRow(dataList, sheet, data, report, rowNo, startContentRow, endContentRow);
		if (!(sheet instanceof SXSSFSheet)){
			this.setTableColumnWidthsNormalExcel(dataList, sheet, data, report);
		}

		return rowNo;
	}

	private int setCriteria(Sheet sheet, ParameterData data, ReportSetting setting, int rowNo, int numOfColumns){
		List criteriaList = ParameterDataConverter.convertToCriteriaDataList(data);
		if(!criteriaList.isEmpty()){
			int startRowNo = rowNo;
			
			int i=0;
			int j=0;
			Row row = null;

			int criPerRow = 3;
			int colPerCri = 3;
            
			int seperatorNumOfColumns = Math.max(criPerRow * colPerCri, numOfColumns);
			
            rowNo = ExcelHelper.createSeperatorRow(rowNo, sheet, seperatorNumOfColumns, SEPERATOR_STYLE);
            
			for(Iterator iter=criteriaList.iterator(); iter.hasNext();){
				CriteriaData criteria = (CriteriaData)iter.next();
				if(i % criPerRow == 0){
					j = 0;
					row = sheet.createRow(rowNo);
					rowNo++;
				}

				Cell cellName = row.createCell(j*colPerCri);
				for(int a=1; a<colPerCri; a++){
					row.createCell(j*colPerCri+a);
				}

				cellName.setCellStyle(CRITERIA_STYLE);
				cellName.setCellValue(criteria.getName()+" : "+criteria.getValue());

				sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), j*colPerCri, j*colPerCri+(colPerCri-1)));

				i++;
				j++;
			}

			rowNo = ExcelHelper.createSeperatorRow(rowNo, sheet, seperatorNumOfColumns, SEPERATOR_STYLE);

			Name headerName = wb.createName();
			headerName.setNameName("Criteria");
			CellRangeAddress criteriaRangeAddress = new CellRangeAddress(startRowNo, rowNo - 1, 0, seperatorNumOfColumns - 1);
			headerName.setRefersToFormula(criteriaRangeAddress.formatAsString(sheet.getSheetName(), false));
		}
		return rowNo;
	}

	private int setTableHeader(Sheet sheet, ParameterData data, Report report, int rowNo){
		int startRowNo = rowNo;

		if(!report.getTableHeaderList().isEmpty()){
			Row row = sheet.createRow(rowNo);
			rowNo++;

			int i = 0;
			for(Iterator iter =  report.getTableHeaderList().iterator(); iter.hasNext();){
				TableHeader header = (TableHeader)iter.next();
				Cell cell = row.createCell(i);
				cell.setCellValue(header.getTitle());
				cell.setCellStyle(TABLE_TITLE_STYLE);
				i++;
			}
		}else{
			int startRow = rowNo;
			int headerRows = report.getReportSetting().getHeaderRows();
			for(int i=0; i<headerRows; i++){
				Row row = sheet.createRow(rowNo);
				//log.debug("create row = "+row.getRowNum());
				rowNo++;
			}

			int hIndex = 0;
			for(Iterator gIter = report.getTableHeaderGroupList().iterator(); gIter.hasNext();){
				TableHeaderGroup group = (TableHeaderGroup)gIter.next();
				hIndex = writerHeaderRow(hIndex, sheet, rowNo, startRow, group);
			}
		}

		if(!report.getReportSetting().isMultiTable()){
			int numOfColumns = report.numOfColumns();

			Name headerName = wb.createName();
			headerName.setNameName("Header");
			CellRangeAddress headerRangeAddress = new CellRangeAddress(startRowNo, rowNo - 1, 0, numOfColumns - 1);
			headerName.setRefersToFormula(headerRangeAddress.formatAsString(sheet.getSheetName(), false));

			if(report.getReportSetting().isExcelAutoFilter()){
				sheet.setAutoFilter(new CellRangeAddress(rowNo - 1, rowNo - 1, 0, numOfColumns - 1));
			}
			if(report.getReportSetting().isExcelFreezeHeader()){
				sheet.createFreezePane(0, rowNo, 0, rowNo);
			}
		}
		this.resizeColumnAndFlushRowForSXSSF(sheet, data, report, true);

		return rowNo;
	}

	private int writerHeaderRow(int currentCol, Sheet sheet, int endRows, int currentRow, TableHeaderGroup group){

		if(group.getTitle()!=null){

			Row row = sheet.getRow(currentRow-1);
			int numOfColumns = group.numOfColumns();
			Cell cell = row.createCell(currentCol);

			cell.setCellValue(group.getTitle());
			cell.setCellStyle(TABLE_TITLE_STYLE);

			//log.debug("["+row.getRowNum()+","+currentCol+"] : "+group.getTitle());

			if(numOfColumns>1){
				sheet.addMergedRegion(new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex(), cell.getColumnIndex(), (cell.getColumnIndex()+numOfColumns-1)));
				for(int a=currentCol+1; a<currentCol+numOfColumns;a++){
					Cell spaceCell = row.createCell(a);
					spaceCell.setCellStyle(TABLE_TITLE_STYLE);
				}
			}
		}

		//log.debug("Current Row : "+currentRow);

		List fullList = group.getSortedFullList();
		for(Iterator aIter = fullList.iterator(); aIter.hasNext();){
			Object obj = aIter.next();

			if(obj instanceof TableHeader){

				TableHeader header = (TableHeader)obj;
				Row row = sheet.getRow(currentRow);
				Cell cell = row.createCell(currentCol);
				currentCol++;
				cell.setCellValue(header.getTitle());
				cell.setCellStyle(TABLE_TITLE_STYLE);

				//log.debug("["+cell.getRowIndex()+","+cell.getColumnIndex()+"] : "+header.getTitle());

				if(endRows - 1 > currentRow){
					log.debug(String.format("currentRow: %s, endRows: %s, columnIndex: %s", currentRow, endRows, cell.getColumnIndex()));
					sheet.addMergedRegion(new CellRangeAddress(currentRow, endRows-1, cell.getColumnIndex(), cell.getColumnIndex()));

					for(int a=currentRow+1; a<endRows;a++){
						Row spaceRow = sheet.getRow(a);
						Cell spaceCell = spaceRow.createCell(cell.getColumnIndex());
						spaceCell.setCellStyle(TABLE_TITLE_STYLE);
					}
				}

			}else if(obj instanceof TableHeaderGroup){

				TableHeaderGroup groupChild = (TableHeaderGroup)obj;
				currentCol = writerHeaderRow(currentCol, sheet, endRows, currentRow+1, groupChild);

			}else{
				throw new MTReportException("Unsupport Exception");
			}

		}

		return currentCol;
	}

	private int setTableContent(List dataList, Sheet sheet, ParameterData data, Report report, int rowNo) throws Exception{
		List headers = report.getFullTableHeaderList();
		//log.debug("Start Row = "+rowNo);
		for(Iterator iter = dataList.iterator(); iter.hasNext();){
			Object obj = iter.next();

			Row row = sheet.createRow(rowNo);
			rowNo++;

			for (int index = 0; index < headers.size(); index++) {
				TableHeader header = (TableHeader) headers.get(index);
				Cell cell = row.createCell(index);

				boolean isHighlight = ValueHelper.isHighlight(obj.getClass(), obj, header.getHighlightFld());
				boolean isItalics = ValueHelper.isItalics(obj.getClass(), obj, header.getItalicsFld());

				String styleKey = MessageFormat.format("ColumnContent_{0}_Font_{1}_Highlight_{2}",
						new Object[] { header.getColumn(), (isItalics ? "I" : ""), (isHighlight ? header.getHighlightColor() : "NA") });

				CellStyle columnStyle = styleMap.get(styleKey);
				if(columnStyle==null){
					columnStyle = ExcelHelper.createContentStyle(wb, data, report.getReportSetting(), header.getFormat(), 
							isHighlight, header.getHighlightColor(), isItalics, header.isWrapText(), header.getAlign());
					styleMap.put(styleKey, columnStyle);
				}
				cell.setCellStyle(columnStyle);

				Object actualCellValue = ExcelHelper.setCellValue(obj.getClass(), obj, header.getMethod(), header.getProperty(), cell, format, header.getAlign(), data.getRemapValueMap());

				for(Iterator sIter = report.getSummaryRowList().iterator();sIter.hasNext();){
					SummaryRow sRow = (SummaryRow)sIter.next();
					if(!sRow.getAggregateCellMap().isEmpty()){
						Map map = sRow.getAggregateCellMap();
						AggregateCell aCell = (AggregateCell)map.get(new Integer(index));
						if(aCell!=null && actualCellValue != null){
							List list = aCell.getCalValList();

							if(actualCellValue instanceof BigDecimal){
								list.add((BigDecimal) actualCellValue);
							} else if (actualCellValue instanceof Integer || actualCellValue instanceof Long || actualCellValue instanceof Double){
								list.add(new BigDecimal(actualCellValue.toString()));
							}

						}
					}
				}

			}

			resizeColumnAndFlushRowForSXSSF(sheet, data, report, false);
		}

		resizeColumnAndFlushRowForSXSSF(sheet, data, report, true);
		return rowNo;
	}

	private int setSummaryRow(List dataList, Sheet sheet, ParameterData data, Report report, int rowNo, int startContentRow, int endContentRow) throws Exception{
		for(Iterator iter = report.getSummaryRowList().iterator(); iter.hasNext();){
			SummaryRow sRow = (SummaryRow)iter.next();

			Row previousRow = sheet.getRow(rowNo-1);
			Row row = sheet.createRow(rowNo);
			rowNo++;

			Object dataRow = null;
			if(sRow.getKey()!=null){
				dataRow = data.getSummaryRowDataMap().get(sRow.getKey());
			}

			for(Iterator rIter = sRow.getRangeCellList().iterator(); rIter.hasNext();){
				RangeCell c = (RangeCell)rIter.next();

				Cell cell = row.createCell(c.getStart());
				cell.setCellValue(c.getTitle());
				if(c.getAlign()!=null && Constants.ALIGN_LEFT.equalsIgnoreCase(c.getAlign())){
					cell.setCellStyle( (sRow.isHightlight()) ? SUMMARY_CELL_HIGHLIGHT_LEFT_STYLE : SUMMARY_CELL_LEFT_STYLE);
				}else{
					cell.setCellStyle( (sRow.isHightlight()) ? SUMMARY_CELL_HIGHLIGHT_RIGHT_STYLE : SUMMARY_CELL_RIGHT_STYLE);
				}

				for(int i=c.getStart()+1; i<=c.getEnd(); i++){

					Cell spaceCell = row.createCell(i);
					if(c.getAlign()!=null && Constants.ALIGN_LEFT.equalsIgnoreCase(c.getAlign())){
						spaceCell.setCellStyle( (sRow.isHightlight()) ? SUMMARY_CELL_HIGHLIGHT_LEFT_STYLE : SUMMARY_CELL_LEFT_STYLE);
					}else{
						spaceCell.setCellStyle( (sRow.isHightlight()) ? SUMMARY_CELL_HIGHLIGHT_RIGHT_STYLE : SUMMARY_CELL_RIGHT_STYLE);
					}

				}

				// single cell title cell
				if (c.getStart() != c.getEnd()){
					sheet.addMergedRegion(new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex(), c.getStart(), c.getEnd()));
				}
				
			}

			for(Iterator aIter = sRow.getAggregateCellList().iterator(); aIter.hasNext();){
				AggregateCell c = (AggregateCell)aIter.next();
				Cell cell = row.createCell(c.getColumn());

				if(dataList.size()>0){

					CellRangeAddress rangeAddress = new CellRangeAddress(startContentRow, endContentRow, cell.getColumnIndex(), cell.getColumnIndex());
					int nonEmptyValCount = c.getNonEmptyValList().size();

					if("upperQuartile".equals(c.getMode())){

						int index = (new BigDecimal(nonEmptyValCount)).divide(new BigDecimal(4), BigDecimal.ROUND_CEILING).intValue();
						if(index == 0){
							index = 1;
						} else if (index > nonEmptyValCount){
							index = nonEmptyValCount;
						}

						String formula = "LARGE"+"("+rangeAddress.formatAsString()+","+index+")";
						cell.setCellFormula(formula);

					} else if ("lowerQuartile".equals(c.getMode())){

						int index = (new BigDecimal(nonEmptyValCount))
								.subtract(((new BigDecimal(nonEmptyValCount)).divide(new BigDecimal(4), BigDecimal.ROUND_FLOOR)))
								.add(new BigDecimal(1))
								.intValue();

						if(index == 0){
							index = 1;
						} else if (index > nonEmptyValCount){
							index = nonEmptyValCount;
						}

						String formula = "LARGE"+"("+rangeAddress.formatAsString()+","+index+")";
						cell.setCellFormula(formula);

					} else {

						String formula = c.getMode()+"("+rangeAddress.formatAsString()+")";
						cell.setCellFormula(formula);

					}

				}
				
				if (StringUtils.isBlank(c.getAlign()) || Constants.ALIGN_LEFT.equalsIgnoreCase(c.getAlign())) {
					cell.setCellStyle( (sRow.isHightlight()) ? SUMMARY_CELL_HIGHLIGHT_LEFT_STYLE : SUMMARY_CELL_LEFT_STYLE);
				} else {
					cell.setCellStyle( (sRow.isHightlight()) ? SUMMARY_CELL_HIGHLIGHT_RIGHT_STYLE : SUMMARY_CELL_RIGHT_STYLE);
				}

				if (StringUtils.isNotBlank(c.getFormat())) {
					CellUtil.setCellStyleProperty(cell, "dataFormat", format.getFormat(c.getFormat()));
				}

			}

			if(dataRow!=null){
				for(Iterator oIter = sRow.getObjectCellList().iterator(); oIter.hasNext();){
					ObjectCell o = (ObjectCell)oIter.next();
					Cell cell = row.createCell(o.getColumn());

					if(o.getFormat()!=null){
						CellStyle columnStyle = styleMap.get("Summary"+o.getColumn());
						if(columnStyle==null){
							columnStyle = ExcelHelper.createSummaryCellStyle(wb, data, report.getReportSetting(), o.getAlign(), sRow.isHightlight());
							columnStyle.setDataFormat(format.getFormat(o.getFormat()));
							styleMap.put("Summary"+o.getColumn(), columnStyle);
						}
						cell.setCellStyle(columnStyle);
					}else{
						if (StringUtils.isBlank(o.getAlign()) || Constants.ALIGN_LEFT.equalsIgnoreCase(o.getAlign())) {
							cell.setCellStyle( (sRow.isHightlight()) ? SUMMARY_CELL_HIGHLIGHT_LEFT_STYLE : SUMMARY_CELL_LEFT_STYLE);
						} else {
							cell.setCellStyle( (sRow.isHightlight()) ? SUMMARY_CELL_HIGHLIGHT_RIGHT_STYLE : SUMMARY_CELL_RIGHT_STYLE);
						}
					}

					ExcelHelper.setCellValue(dataRow.getClass(),dataRow, o.getMethod(), o.getProperty(), cell, format, null, data.getRemapValueMap());

				}
			}

			if(previousRow!=null){
				for(int i=0; i<previousRow.getLastCellNum(); i++){
					Cell cell = row.getCell(i);
					if(cell==null){
						Cell spaceCell = row.createCell(i);
						spaceCell.setCellStyle(SUMMARY_CELL_RIGHT_STYLE);
					}
				}
			}

		}

		if (!CollectionUtils.isEmpty(report.getSummaryRowList())) {
			this.resizeColumnAndFlushRowForSXSSF(sheet, data, report, true);
		}

		return rowNo;
	}

	private void setTableColumnWidthsNormalExcel(List dataList, Sheet sheet, ParameterData data, Report report){
		if (!(sheet instanceof SXSSFSheet)){
			List headers = report.getFullTableHeaderList();
			int index = 0;
			for(Iterator hIter = headers.iterator(); hIter.hasNext();){
				TableHeader header = (TableHeader)hIter.next();
				if(header.getXlsWidth() > 0){
					int newWidth = header.getXlsWidth() * 256;
					if(newWidth <= MAX_COLUMN_WIDTH){
						sheet.setColumnWidth(index, newWidth);
					} else{
						log.warn(MessageFormat.format("Column Id: {0} request custom width {1} is greater than excel max limit, ignoring the custom width",
								new Object[] { header.getColumn(), header.getXlsWidth() } ));
					}

				}else {
					sheet.autoSizeColumn(index);
					int newWidth = sheet.getColumnWidth(index) + report.getReportSetting().getExcelAutosizeMargin() * 256;
					if(newWidth <= MAX_COLUMN_WIDTH){
						sheet.setColumnWidth(index, sheet.getColumnWidth(index) + report.getReportSetting().getExcelAutosizeMargin() * 256);
					}
				}
				index++;
			}
		}

	}

	private Short convertToPoiPaperSize(ReportSetting setting){
		if(PaperSizeConstants.A4.equalsIgnoreCase(setting.getPageSize())){
			return PrintSetup.A4_PAPERSIZE;
		} else if(PaperSizeConstants.A3.equalsIgnoreCase(setting.getPageSize())){
			return PrintSetup.A3_PAPERSIZE;
		} else if (StringUtils.isBlank(setting.getPageSize())){
			log.warn(String.format("Unsupported paper size: %s, using A4 as default", setting.getPageSize()));
			return PrintSetup.A4_PAPERSIZE;
		}
		return PrintSetup.A4_PAPERSIZE;
	}

	private void setPageAndPrintSetup(Sheet sheet, ParameterData data, ReportSetting setting){
		String normalFont = HSSFHeader.font(ExcelConstants.DEFAULT_FONT_NAME, "Normal") +  HSSFHeader.fontSize(ExcelConstants.DEFAULT_FONT_SIZE);
		short titleFontSize = data.getFontSizeTitle()!=null ? data.getFontSizeTitle().shortValue() : ExcelConstants.DEFAULT_FONT_SIZE_LARGE;
		//log.debug("titleFontSize = "+titleFontSize);
		String titleFont = HSSFHeader.font(ExcelConstants.DEFAULT_FONT_NAME, "Bold") +  HSSFHeader.fontSize(titleFontSize);

		//Print Setup
		if(setting.isFitAllColumnsOnePage()){
			sheet.setAutobreaks(true);
			sheet.setFitToPage(true);
			sheet.getPrintSetup().setFitWidth((short)1);
			sheet.getPrintSetup().setFitHeight((short)0);
		}

		sheet.getPrintSetup().setLandscape(setting.isHorizontal());
		sheet.getPrintSetup().setPaperSize(this.convertToPoiPaperSize(setting));

		//Header
		if(setting.isShowReportId()){
			sheet.getHeader().setLeft(normalFont+setting.getReportId());
		}

		sheet.getHeader().setCenter(titleFont+data.getHeaderTitle());

        List<String> rightHeaderList = new ArrayList<String>();

        String restrictedLabel = null;
        if (setting.isShowRestrictedLabel() == null && data.getDefaultShowRestrictedLabel()) {
            restrictedLabel = data.getDefaultRestrictedLabel();
        } else if (setting.isShowRestrictedLabel() != null && setting.isShowRestrictedLabel().booleanValue()){
            restrictedLabel = setting.getRestrictedLabel();
			}
        if (StringUtils.isNotBlank(restrictedLabel)) {
            String restrictedFont = HSSFHeader.font(ExcelConstants.DEFAULT_FONT_NAME, "Bold") +  HSSFHeader.fontSize(ExcelConstants.DEFAULT_FONT_SIZE);
            rightHeaderList.add(restrictedFont+restrictedLabel);
			}

		if (setting.isShowPrintDate()) {
			rightHeaderList.add(String.format("%s%s: %s", 
			        normalFont,
			        data.getPrintTimeLabel(),
			        DataHelper.getPrintDateStr(data.getPrintDate(), StringUtils.defaultIfEmpty(setting.getPrintDateFormat(), data.getDefaultPrintDateFormat()))
			));
		}
		
		if (setting.isShowPrintBy()) {
		    rightHeaderList.add(String.format("%s%s: %s", 
		            normalFont,
		            data.getPrintByLabel(), 
		            data.getPrintBy())
		    );
		}
		if (!rightHeaderList.isEmpty()) {
		    sheet.getHeader().setRight(StringUtils.join(rightHeaderList, "\r\n"));
		}

		//Footer
		if(setting.isShowPageNumber()){
			sheet.getFooter().setCenter(normalFont+data.getPageLabel() + HeaderFooter.page() + " / " + HeaderFooter.numPages() );
		}

		if(setting.isExcelPrintRepeatHeader()){
			Name headerName = wb.getName("Header");

			int firstRepeatRow = -1;
			int lastRepeatRow = -1;

			if(headerName != null){
				AreaReference aref = new AreaReference(headerName.getRefersToFormula(), (wb instanceof HSSFWorkbook) ? SpreadsheetVersion.EXCEL97 : SpreadsheetVersion.EXCEL2007);
				firstRepeatRow = aref.getFirstCell().getRow();
				lastRepeatRow = aref.getLastCell().getRow();
			}

			if(firstRepeatRow >= 0 && lastRepeatRow >= 0){
				sheet.setRepeatingRows(CellRangeAddress.valueOf(MessageFormat.format("{0}:{1}", new Object[] { firstRepeatRow + 1, lastRepeatRow + 1})));
			}
		}

	}

	private void resizeColumnAndFlushRowForSXSSF(Sheet sheet, ParameterData data, Report report, boolean forceFlush) {
		if(sheet instanceof SXSSFSheet){
			SXSSFSheet s = (SXSSFSheet)sheet;
			if(forceFlush || (s.getLastRowNum() - s.getLastFlushedRowNum() >= data.getSxssfRowAccessWindowSize())){
				List headers = report.getFullTableHeaderList();
				
				if(log.isTraceEnabled()){
					log.trace(String.format("LastRow: %d, LastFlushedRow: %d, Window Size: %d", s.getLastRowNum(), s.getLastFlushedRowNum(), data.getSxssfRowAccessWindowSize()));
				}
				
				for (int index = 0; index < headers.size(); index++) {
					TableHeader header = (TableHeader) headers.get(index);
					Integer origWidth = sxssfColumnWidthMap.get(header);
					int newWidth = -1;
					
					if (header.isHidden()) {
						log.debug(String.format("Column %d, set hidden", index));
						sheet.setColumnHidden(index, true);
						continue;
					}

					if(header.getXlsWidth() > 0){
						newWidth = header.getXlsWidth() * 256;
					} else {
						double width = SheetUtil.getColumnWidth(sheet, index, false, s.getLastFlushedRowNum(), s.getLastRowNum());
						if (width != -1.0D) {
							newWidth = (int) (width * 256.0D) + report.getReportSetting().getExcelAutosizeMargin() * 256;
						}
					}

					if (newWidth >= MAX_COLUMN_WIDTH){
						newWidth = MAX_COLUMN_WIDTH;
					}

					if (origWidth == null || newWidth > origWidth.intValue()){
						if(log.isTraceEnabled()){
							log.trace(String.format("Column %d, Width %d --> %d", index, origWidth, newWidth));
						}
						sheet.setColumnWidth(index, newWidth);
						sxssfColumnWidthMap.put(header, new Integer(newWidth));
					}
				}
				this.flushRow(s);
			}
		}
	}


	private void flushRow(SXSSFSheet s) {
		if(s.getLastRowNum() > 0){
			if(log.isTraceEnabled()){
				log.trace(String.format("Flushing %d row to disk", s.getLastRowNum() - s.getLastFlushedRowNum()));
			}
			
			try {
				s.flushRows();
			} catch (IOException e) {
				log.error("Cannot flush row", e);
				throw new MTReportException("Cannot flush row");
			}
		}
	}


}

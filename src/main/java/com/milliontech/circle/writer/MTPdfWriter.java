package com.milliontech.circle.writer;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.constants.PdfConstants;
import com.milliontech.circle.data.converter.ParameterDataConverter;
import com.milliontech.circle.data.model.CriteriaData;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.exception.MTReportException;
import com.milliontech.circle.helper.BorderType;
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
import com.milliontech.circle.model.pdf.FooterEvent;
import com.milliontech.circle.model.pdf.HeaderEvent;
import com.milliontech.circle.model.pdf.PdfFontSetting;
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

	private PdfFontSetting fontSetting;

	public MTPdfWriter(ParameterData data, ReportSetting setting){

		this.fontSetting = PdfFontRepository.getPdfFontSetting(data);

        List<PdfFont> bfList = this.fontSetting.getFontList();
        CRITERIA_STYLE = new PdfStyle("criteria", PdfFontRepository.createPdfFontInfoList(bfList, Optional.ofNullable(data.getFontSizeCriteria()).orElse(PdfConstants.DEFAULT_CRITERIA_FONT_SIZE), false));
        HEADER_TITLE_STYLE = new PdfStyle("title", PdfFontRepository.createPdfFontInfoList(bfList, Optional.ofNullable(data.getFontSizeTitle()).orElse(PdfConstants.DEFAULT_TITLE_FONT_SIZE), true));
        TABLE_TITLE_STYLE = new PdfStyle("columnHeader", PdfFontRepository.createPdfFontInfoList(bfList, Optional.ofNullable(data.getFontSizeColumnHeader()).orElse(PdfConstants.DEFAULT_TABLE_HEADER_SIZE), true));
        CONTENT_STYLE = new PdfStyle("content", PdfFontRepository.createPdfFontInfoList(bfList, Optional.ofNullable(data.getFontSizeContent()).orElse(PdfConstants.DEFAULT_TABLE_CONTENT_FONT_SIZE), false));
        SUMMARY_CELL_STYLE = new PdfStyle("summary", PdfFontRepository.createPdfFontInfoList(bfList, Optional.ofNullable(data.getFontSizeContent()).orElse(PdfConstants.DEFAULT_TABLE_CONTENT_FONT_SIZE), false));
        HEADER_FOOTER_STYLE = new PdfStyle("headerFooter", PdfFontRepository.createPdfFontInfoList(bfList, Optional.ofNullable(data.getFontSizePrintTimeHeader()).orElse(PdfConstants.DEFAULT_PRINT_TIME_FONT_SIZE), false));

        this.data = data;
    }

	public void writer(List dataList, ParameterData data, Report report, OutputStream out) throws Exception {
		this.data = data;
        WriterProperties wp = new WriterProperties();
        wp.setPdfVersion(PdfVersion.PDF_1_7);

        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out, wp);
        PdfDocument pdf = new PdfDocument(writer);

        HeaderEvent headerEvent = new HeaderEvent(data, report.getReportSetting(), HEADER_FOOTER_STYLE, fontSetting);
        FooterEvent footerEvent = new FooterEvent(data, report.getReportSetting(), HEADER_FOOTER_STYLE, fontSetting);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, headerEvent);
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, footerEvent);
        Document document = PdfHelper.createDocument(pdf, data, report.getReportSetting());
        document.setProperty(Property.LEADING, new Leading(Leading.MULTIPLIED, 0.8f));
        document.setFontProvider(fontSetting.getFontProvider());
        document.setFontFamily(fontSetting.getDefaultFontFamily());

        PdfDocumentInfo info = pdf.getDocumentInfo();

        if (StringUtils.isNotBlank(data.getAuthorName())) {
            info.setAuthor(data.getAuthorName());
        }
        info.setTitle(data.getHeaderTitle());

        info.addCreationDate();
        info.addModDate();

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

        //int pageCount = writer.getPageNum();

        footerEvent.writeTotal(pdf);
        document.close();

        // byte[] pdfAsBytes = baos.toByteArray();
        //writePageNumber(pdfAsBytes, pageCount, out);
	}

	/*
	private void writePageNumber(byte[] pdfAsBytes, int pageCount, OutputStream out) throws IOException, DocumentException {
	    PdfReader reader = new PdfReader(pdfAsBytes);
	    Rectangle pageSize = reader.getPageSizeWithRotation(1);
	    float middle = pageSize.getWidth() / 2;

	    PdfStamper stamper = new PdfStamper(reader, out);
	    for (int i = 1; i <= pageCount; i++) {
	        Paragraph p = PdfHelper.createDisplayParagraph(
	                String.format(data.getPageLabel() + " %d  / %d", i, pageCount),
	                HEADER_FOOTER_STYLE.getFontList(),
	                false
	        );
	        ColumnText.showTextAligned(stamper.getOverContent(i), Element.ALIGN_CENTER, p, middle, 12, 0);
	    }
	    stamper.close();
	}
	*/

	private void writeMultipleTables(List dataList, ParameterData data, Report report, Document document) throws Exception {
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

    private void writeSingleTable(List dataList, ParameterData data, Report report, Document document, List list, int noOfColumns, String splitBy) throws Exception {
        int totalColumns = noOfColumns + (report.getReportSetting().isShowRowNo() ? 1 : 0);
        Map rowspanMap = this.createRowspanMapForMergeIsSameOption(dataList, list);
        calcWidthsForTableHeader(report);
        calcWidthForTableContent(dataList, list, report, rowspanMap);
        float tableWidth = document.getPdfDocument().getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();
        float[] widths = getTableColumnWidths(list, noOfColumns, report.getReportSetting(), dataList.size(), tableWidth);

        boolean isUseLargeTable = rowspanMap.isEmpty();

        Table dataTable = new Table(UnitValue.createPercentArray(widths), isUseLargeTable);
        dataTable.setFixedLayout();
        dataTable.setWidth(UnitValue.createPercentValue(100f));
        dataTable.setTextAlignment(TextAlignment.LEFT);
        dataTable.setMarginTop((isUseLargeTable ? 1.5f : 3f) * PdfConstants.DEFAULT_ITEXT7_TO_5_SPACING_RATIO);
        dataTable.setMarginBottom((isUseLargeTable ? 1.5f : 3f) * PdfConstants.DEFAULT_ITEXT7_TO_5_SPACING_RATIO);

        if(splitBy!=null){
            PdfHelper.createPdfHeaderCell(dataTable, splitBy, TABLE_TITLE_STYLE, null, BorderType.ALL, Constants.ALIGN_LEFT, null , totalColumns, 1);
        }
        setTableHeader(report, list, dataTable);

        if (isUseLargeTable) {
            document.add(dataTable);
        }

        setTableContent(dataList, list, dataTable, report, rowspanMap, isUseLargeTable);
        setSummaryRow(data, report, noOfColumns, dataTable, dataList);

        if (isUseLargeTable) {
            dataTable.complete();
        } else {
            document.add(dataTable);
        }
    }

    private void setSummaryRow(ParameterData data, Report report, int noOfColumns, Table dataTable, List dataList) throws Exception {
        for(Iterator iter = report.getSummaryRowList().iterator(); iter.hasNext();){
            SummaryRow row = (SummaryRow)iter.next();
            Object dataRow = data.getSummaryRowDataMap().get(row.getKey());
            DeviceRgb color = row.isHightlight() ? PdfConstants.SUMMARY_ROW_COLOR : null;

            if(report.getReportSetting().isShowRowNo()){
                PdfHelper.createPdfCell(dataTable, "", SUMMARY_CELL_STYLE, null, BorderType.ALL, Constants.ALIGN_LEFT, color);
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

                PdfHelper.createPdfCell(dataTable, value, SUMMARY_CELL_STYLE, null, BorderType.ALL, align, color, colspan, 1);
            }
        }
    }

    private void setTableHeader(Report report, List list, Table dataTable) {
        int[] colorCodes = report.getReportSetting().getTableHeaderColorArray();
        DeviceRgb tableHeaderColor = colorCodes != null ? new DeviceRgb(colorCodes[0], colorCodes[1], colorCodes[2]) : PdfConstants.TABLE_HEADER_COLOR;

        DeviceRgb color = (report.getReportSetting().isHightlightHeader() ? tableHeaderColor : null);
        if(!report.getTableHeaderList().isEmpty()){
            if(report.getReportSetting().isShowRowNo()){
                PdfHelper.createPdfHeaderCell(dataTable, "", TABLE_TITLE_STYLE, null, BorderType.ALL, Constants.ALIGN_CENTER, color);
            }
            for(Iterator iter =  report.getTableHeaderList().iterator(); iter.hasNext();){
                TableHeader header = (TableHeader)iter.next();
                PdfHelper.createPdfHeaderCell(dataTable, header.getTitle(), TABLE_TITLE_STYLE, header, BorderType.ALL, Constants.ALIGN_CENTER, color);
            }
        }else{
            if(report.getReportSetting().isShowRowNo()){
                PdfHelper.createPdfHeaderCell(dataTable, "", TABLE_TITLE_STYLE, null, BorderType.ALL, Constants.ALIGN_CENTER, color, 1, report.getReportSetting().getHeaderRows());
            }
            for(Iterator gIter = report.getTableHeaderGroupList().iterator(); gIter.hasNext();){
                TableHeaderGroup group = (TableHeaderGroup)gIter.next();
                for(int i=0; i<report.getReportSetting().getHeaderRows(); i++){
                    setTableHeaderForEachTableHeaderGroup(dataTable, group, report.getReportSetting(), 0, i, color);
                }
            }
        }
    }

    private void setTableHeaderForEachTableHeaderGroup(Table dataTable, TableHeaderGroup group, ReportSetting setting, int currentRow, int executeRow, Color color){
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
                    pv = PdfHelper.createDisplayParagraph(title, TABLE_TITLE_STYLE.getFontInfoList(), true, true, header);
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

                PdfHelper.createPdfHeaderCell(dataTable, title, TABLE_TITLE_STYLE, null, BorderType.ALL, Constants.ALIGN_CENTER, color, colspan, rowspan);

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

    private void setTableContent(List dataList, List list, Table dataTable, Report report, Map rowspanMap, boolean isUseLargeTable) throws Exception {
        int rowIndex = 0;
        for(Iterator iter = dataList.iterator(); iter.hasNext();){
            if (isUseLargeTable && rowIndex % 50 == 0) {
                dataTable.flush();
            }

            Object obj = iter.next();

            Float grey = (rowIndex % 2 == 1) ? new Float(0.92f) : null;
            if(report.getReportSetting().isPdfAlterRowColorOff()){
                grey = null;
            }

            if(report.getReportSetting().isShowRowNo()){
                boolean highlight = ValueHelper.isHighlight(obj.getClass(), obj, report.getReportSetting().getHighlightRowNoFld());
                DeviceRgb color = highlight ? PdfHelper.getHighlightColor(report.getReportSetting().getHighlightRowNoColor()) : null;
                PdfHelper.createPdfCell(dataTable, ""+(rowIndex+1), CONTENT_STYLE, null, BorderType.ALL, Constants.ALIGN_LEFT, color, grey);
            }

            int colIndex = 0;
            for(Iterator hIter = list.iterator(); hIter.hasNext();){
                TableHeader header = (TableHeader)hIter.next();

                Object value = ValueHelper.getDataValue(obj.getClass(), obj, header.getMethod(), header.getProperty(), data.getRemapValueMap());
                String strValue = PdfHelper.getCellStringValue(value, header.getFormat());
                String align = Constants.ALIGN_LEFT;
                boolean highlight = ValueHelper.isHighlight(obj.getClass(), obj, header.getHighlightFld());
                DeviceRgb color = highlight ? PdfHelper.getHighlightColor(header.getHighlightColor()) : null;
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
                        PdfHelper.createPdfCell(dataTable, strValue, CONTENT_STYLE, header, BorderType.ALL, align, color, 1, rowspan.intValue());
                    }
                }else{
                    PdfHelper.createPdfCell(dataTable, strValue, CONTENT_STYLE, header, BorderType.ALL, align, color, highlight? null : grey);
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

        if (isUseLargeTable) {
            dataTable.flush();
        }
    }

    private void setCriteria(ParameterData data, Report report, Document document) {
        if(report.getReportSetting().isShowCriteria()){
            List criteriaList = ParameterDataConverter.convertToCriteriaDataList(data);
            if(!criteriaList.isEmpty()){
                int columnWidth = 3;
                Table criteriaTable = new Table(columnWidth);
                criteriaTable.setFixedLayout();
                criteriaTable.setBorder(null);
                criteriaTable.setWidth(UnitValue.createPercentValue(100f));
                criteriaTable.setTextAlignment(TextAlignment.LEFT);
                criteriaTable.setMarginTop(5f * PdfConstants.DEFAULT_ITEXT7_TO_5_SPACING_RATIO);

                createSeperator(criteriaTable, columnWidth);

                int i=0;
                for (Iterator iter = criteriaList.iterator(); iter.hasNext();) {
                    CriteriaData criteriaData = (CriteriaData)iter.next();

                    String value = criteriaData.getName() + " : " + criteriaData.getValue();
                    PdfHelper.createPdfCell(criteriaTable, value, CRITERIA_STYLE, null, BorderType.NONE, Constants.ALIGN_LEFT, null);

                    i++;
                }

                if(i % columnWidth > 0){
                    for(int j=0; j< columnWidth - (i % columnWidth) ; j++){
                        PdfHelper.createPdfCell(criteriaTable, "", CRITERIA_STYLE, null, BorderType.NONE, Constants.ALIGN_LEFT, null);
                    }
                }

                createSeperator(criteriaTable, columnWidth);

                document.add(criteriaTable);
            }
        }
    }

    private void createSeperator(Table criteriaTable, int columnWidth) {
        Cell spaceCell = new Cell(1, columnWidth);
        spaceCell.setBorder(Border.NO_BORDER);
        spaceCell.setBackgroundColor(new DeviceGray(0.6f));
        spaceCell.setHeight(0.5f);
        criteriaTable.addCell(spaceCell);
    }

    private void setHeaderTitle(ParameterData data, Document document) {
        Paragraph p = PdfHelper.createDisplayParagraph(data.getHeaderTitle(), HEADER_TITLE_STYLE.getFontInfoList(), false, false);
        p.setTextAlignment(TextAlignment.CENTER);
        p.setMargin(0);
        document.add(p);

        Paragraph sp = new Paragraph("\n");
        document.add(sp);
    }

    private void calcWidthsForTableHeader(Report report) {
        if(!report.getTableHeaderList().isEmpty()){
            for(Iterator iter =  report.getTableHeaderList().iterator(); iter.hasNext();){
                TableHeader header = (TableHeader)iter.next();
                PdfHelper.calcWidth(header.getTitle(), TABLE_TITLE_STYLE.getFontInfoList(), header);
            }
        }else{
            for(Iterator gIter = report.getTableHeaderGroupList().iterator(); gIter.hasNext();){
                TableHeaderGroup group = (TableHeaderGroup)gIter.next();
                for(int i=0; i<report.getReportSetting().getHeaderRows(); i++){
                    calcWidthForEachTableHeaderGroup(group, 0, i);
                }
            }
        }
    }

    private void calcWidthForEachTableHeaderGroup(TableHeaderGroup group, int currentRow, int executeRow){
        List fullList = group.getSortedFullList();

        if(currentRow == executeRow){
            for(Iterator iter = fullList.iterator(); iter.hasNext(); ){
                Object obj = iter.next();
                String title = "";

                if(obj instanceof TableHeader){
                    TableHeader header = (TableHeader)obj;
                    title = header.getTitle();
                    PdfHelper.calcWidth(title, TABLE_TITLE_STYLE.getFontInfoList(), header);
                }
            }
        }

        for(Iterator iter = fullList.iterator(); iter.hasNext(); ){
            Object obj = iter.next();
            if(obj instanceof TableHeaderGroup){
                TableHeaderGroup groupChild = (TableHeaderGroup)obj;
                this.calcWidthForEachTableHeaderGroup(groupChild, currentRow+1, executeRow);
            }
        }
    }

    private void calcWidthForTableContent(List dataList, List list, Report report, Map rowspanMap) throws Exception {
        int rowIndex = 0;
        for(Iterator iter = dataList.iterator(); iter.hasNext();){
            Object obj = iter.next();

            int colIndex = 0;
            for(Iterator hIter = list.iterator(); hIter.hasNext();){
                TableHeader header = (TableHeader)hIter.next();

                Object value = ValueHelper.getDataValue(obj.getClass(), obj, header.getMethod(), header.getProperty(), data.getRemapValueMap());
                String strValue = PdfHelper.getCellStringValue(value, header.getFormat());

                if(header.isMergeIfSame()){
                    String key = (header.getMethod()==null ? header.getProperty() : header.getMethod());
                    Integer rowspan = (Integer)rowspanMap.get(getRowspanKey(key,new Integer(rowIndex)));
                    if(rowspan!=null){
                        PdfHelper.calcWidth(strValue, CONTENT_STYLE.getFontInfoList(), header);
                    }
                }else{
                    PdfHelper.calcWidth(strValue, CONTENT_STYLE.getFontInfoList(), header);
                }
                colIndex++;
            }
            rowIndex++;
        }
    }



    private float[] getTableColumnWidths(List list, int noOfColumns, ReportSetting setting, int noOfRows, float tableWidth) {
        float total = 0;
        float[] widths = new float[noOfColumns + (setting.isShowRowNo() ? 1 : 0) ];
        float[] colMinWidths = new float[widths.length];
        int i=0;
        if(setting.isShowRowNo()){
            widths[0] = PdfFontRepository.SANS_SERIF_FONT_LIST.get(0).getWidth(""+noOfRows, 12)+5;
            colMinWidths[0] = 12 + 2.2f;
            total += widths[0];
            i++;
        }
        for(Iterator hIter = list.iterator(); hIter.hasNext();){
            TableHeader header = (TableHeader)hIter.next();
            widths[i] = header.getCalcWidth();
            colMinWidths[i] = (header.getMaxFontSize() > 0 ? header.getMaxFontSize() : 10) + 2 + 2.2f;
            total += widths[i];
            i++;
        }

        for(int x = 0; x < widths.length; x++) {
            if(widths[x] * tableWidth/ total < colMinWidths[x]) {
                widths[x] = colMinWidths[x] * total / tableWidth;
            }
        }
        return widths;
    }

}

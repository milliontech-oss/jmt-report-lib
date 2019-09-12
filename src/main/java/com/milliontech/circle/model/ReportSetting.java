package com.milliontech.circle.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.milliontech.circle.constants.ExcelConstants;

public class ReportSetting {

	private boolean showPrintDate;
	private String printDateKey;
	private String printDateFormat;

	private boolean showPrintBy;
	private String printByKey;

	private boolean showCriteria;
	private String criteriaTitleKey;
	private String criteriaValueKey;

	private boolean showPageNumber;
	private String pageKey;

	private boolean showReportId;
	private String reportId;

	private Boolean showRestrictedLabel;
	private String restrictedLabel;

	private boolean horizontal;
	private String pageSize;
	private boolean fitAllColumnsOnePage;

	private int headerRows;
	private int criteriaRows;
	private boolean hightlightHeader;
	private String excelFontName;

	private boolean showRowNo;

	private boolean multiTable;
	private String multiTableSplitBy;
	private String highlightRowNoFld;
	private String highlightRowNoColor;

	private String tableHeaderColor;
	private boolean pdfAlterRowColorOff;

	private boolean excelAutoFilter;
	private boolean excelFreezeHeader;
	private int excelAutosizeMargin;
	private boolean excelPrintRepeatHeader;

	public ReportSetting(){
		showPrintDate = true;
		printDateKey = "circle.header.print.date";
		printDateFormat = null;

		showPrintBy = false;
		printByKey = "circle.header.print.by";

		showCriteria = true;
		criteriaTitleKey = "circle.searching.criteria.title";
		criteriaValueKey = "circle.searching.criteria.value";

		showPageNumber = true;
		showReportId = false;

		showRestrictedLabel = null;
		restrictedLabel = "RESTRICTED";

		horizontal = true;
		pageSize = "A4";
		fitAllColumnsOnePage = true;

		headerRows = 1;
		criteriaRows = 2;
		hightlightHeader = true;
		excelFontName = ExcelConstants.DEFAULT_FONT_NAME;

		showRowNo = true;
		pdfAlterRowColorOff = false;

		excelAutoFilter = true;
		excelFreezeHeader = true;
		excelAutosizeMargin = 5;
		excelPrintRepeatHeader = true;
	}

	public boolean isShowPrintDate() {
		return showPrintDate;
	}

	public void setShowPrintDate(boolean showPrintDate) {
		this.showPrintDate = showPrintDate;
	}

	public boolean isShowPrintBy() {
		return showPrintBy;
	}

	public void setShowPrintBy(boolean showPrintBy) {
		this.showPrintBy = showPrintBy;
	}

	public String getPrintByKey() {
		return printByKey;
	}

	public void setPrintByKey(String printByKey) {
		this.printByKey = printByKey;
	}

	public boolean isShowCriteria() {
		return showCriteria;
	}

	public void setShowCriteria(boolean isShowCriteria) {
		this.showCriteria = isShowCriteria;
	}

	public String getCriteriaTitleKey() {
		return criteriaTitleKey;
	}

	public void setCriteriaTitleKey(String criteriaTitleKey) {
		this.criteriaTitleKey = criteriaTitleKey;
	}

	public String getCriteriaValueKey() {
		return criteriaValueKey;
	}

	public void setCriteriaValueKey(String criteriaValueKey) {
		this.criteriaValueKey = criteriaValueKey;
	}

	public boolean isShowPageNumber() {
		return showPageNumber;
	}

	public void setShowPageNumber(boolean isShowPageNumber) {
		this.showPageNumber = isShowPageNumber;
	}

	public boolean isShowReportId() {
		return showReportId;
	}

	public void setShowReportId(boolean isShowReportId) {
		this.showReportId = isShowReportId;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public void setHorizontal(boolean isHorizontal) {
		this.horizontal = isHorizontal;
	}

	public String getPageSize() {
		return pageSize;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isFitAllColumnsOnePage() {
		return fitAllColumnsOnePage;
	}

	public void setFitAllColumnsOnePage(boolean fitAllColumnsOnePage) {
		this.fitAllColumnsOnePage = fitAllColumnsOnePage;
	}

	public int getHeaderRows() {
		return headerRows;
	}

	public void setHeaderRows(int headerRows) {
		this.headerRows = headerRows;
	}

	public int getCriteriaRows() {
		return criteriaRows;
	}

	public void setCriteriaRows(int criteriaRows) {
		this.criteriaRows = criteriaRows;
	}

	public String getPrintDateKey() {
		return printDateKey;
	}

	public void setPrintDateKey(String printDateKey) {
		this.printDateKey = printDateKey;
	}

	public String getPageKey() {
		return pageKey;
	}

	public void setPageKey(String pageKey) {
		this.pageKey = pageKey;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public Boolean isShowRestrictedLabel() {
        return showRestrictedLabel;
    }

    public void setShowRestrictedLabel(Boolean showRestrictedLabel) {
        this.showRestrictedLabel = showRestrictedLabel;
    }

    public String getRestrictedLabel() {
        return restrictedLabel;
    }

    public void setRestrictedLabel(String restrictedLabel) {
        this.restrictedLabel = restrictedLabel;
    }

	public String getPrintDateFormat() {
		return printDateFormat;
	}

	public void setPrintDateFormat(String printDateFormat) {
		this.printDateFormat = printDateFormat;
	}

	public boolean isHightlightHeader() {
		return hightlightHeader;
	}

	public void setHightlightHeader(boolean hightlightHeader) {
		this.hightlightHeader = hightlightHeader;
	}

	public String getExcelFontName() {
		return excelFontName;
	}

	public void setExcelFontName(String excelFontName) {
		this.excelFontName = excelFontName;
	}

	public boolean isShowRowNo() {
		return showRowNo;
	}

	public void setShowRowNo(boolean showRowNo) {
		this.showRowNo = showRowNo;
	}

	public boolean isMultiTable() {
		return multiTable;
	}

	public void setMultiTable(boolean multiTable) {
		this.multiTable = multiTable;
	}

	public String getMultiTableSplitBy() {
		return multiTableSplitBy;
	}

	public void setMultiTableSplitBy(String multiTableSplitBy) {
		this.multiTableSplitBy = multiTableSplitBy;
	}

	public String getHighlightRowNoFld() {
		return highlightRowNoFld;
	}

	public void setHighlightRowNoFld(String highlightRowNoFld) {
		this.highlightRowNoFld = highlightRowNoFld;
	}

	public String getHighlightRowNoColor() {
		return highlightRowNoColor;
	}

	public void setHighlightRowNoColor(String highlightRowNoColor) {
		this.highlightRowNoColor = highlightRowNoColor;
	}

	public String getTableHeaderColor() {
		return tableHeaderColor;
	}

	public int[] getTableHeaderColorArray() {
		if(tableHeaderColor!=null){
			String[] a = tableHeaderColor.split(",");
			if(a!=null && a.length==3){
				int[] array = new int[3];
				array[0] = Integer.parseInt(a[0]);
				array[1] = Integer.parseInt(a[1]);
				array[2] = Integer.parseInt(a[2]);
				return array;
			}
		}
		return null;
	}

	public void setTableHeaderColor(String tableHeaderColor) {
		this.tableHeaderColor = tableHeaderColor;
	}

	public boolean isPdfAlterRowColorOff() {
		return pdfAlterRowColorOff;
	}

	public void setPdfAlterRowColorOff(boolean pdfAlterRowColorOff) {
		this.pdfAlterRowColorOff = pdfAlterRowColorOff;
	}

	public boolean isExcelAutoFilter() {
		return excelAutoFilter;
	}

	public void setExcelAutoFilter(boolean excelAutoFilter) {
		this.excelAutoFilter = excelAutoFilter;
	}

	public boolean isExcelFreezeHeader() {
		return excelFreezeHeader;
	}

	public void setExcelFreezeHeader(boolean excelFreezeHeader) {
		this.excelFreezeHeader = excelFreezeHeader;
	}

	public int getExcelAutosizeMargin() {
		return excelAutosizeMargin;
	}

	public void setExcelAutosizeMargin(int excelAutosizeMargin) {
		this.excelAutosizeMargin = excelAutosizeMargin;
	}

	public boolean isExcelPrintRepeatHeader() {
		return excelPrintRepeatHeader;
	}

	public void setExcelPrintRepeatHeader(boolean excelPrintRepeatHeader) {
		this.excelPrintRepeatHeader = excelPrintRepeatHeader;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}

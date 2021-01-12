package com.milliontech.circle.xml.converter;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.w3c.dom.Element;

import com.milliontech.circle.constants.ExcelConstants;
import com.milliontech.circle.constants.ParameterDataConstants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.model.ReportSetting;

public class ReportSettingXmlNodeConverter implements XmlNodeConverter<ReportSetting> {

	public ReportSetting convertAndAddToList(List dataList, Element elmt, Map parameter, ParameterData data, Class clazz) {
		ReportSetting setting = new ReportSetting();
		this.convertToObject(setting, elmt, parameter, data);
		dataList.add(setting);
		return setting;
	}

	public void convertToObject(ReportSetting setting, Element elmt, Map parameter, ParameterData data) {
		if(elmt.hasAttribute("showPrintDate")){
			setting.setShowPrintDate(DataHelper.isTrue(elmt.getAttribute("showPrintDate"), true));
		}
		if(elmt.hasAttribute("printDateKey")){
			setting.setPrintDateKey(DataHelper.getString(elmt.getAttribute("printDateKey"),null));
		}
		if(elmt.hasAttribute("printDateFormat")){
			setting.setPrintDateFormat(DataHelper.getString(elmt.getAttribute("printDateFormat"), null));
		}


		if(elmt.hasAttribute("showPrintBy")){
			setting.setShowPrintBy(DataHelper.isTrue(elmt.getAttribute("showPrintBy"), false));
		}
		if(elmt.hasAttribute("printByKey")){
			setting.setPrintByKey(DataHelper.getString(elmt.getAttribute("printByKey"),null));
		}

		if(elmt.hasAttribute("showRestrictedLabel")) {
		    setting.setShowRestrictedLabel(DataHelper.isTrue(elmt.getAttribute("showRestrictedLabel"), false));
		}
		if(elmt.hasAttribute("restrictedLabel")) {
		    setting.setRestrictedLabel(DataHelper.getString(elmt.getAttribute("restrictedLabel"),null));
		}



		if(elmt.hasAttribute("showCriteria")){
			setting.setShowCriteria(DataHelper.isTrue(elmt.getAttribute("showCriteria"), true));
		}
		if(elmt.hasAttribute("criteriaTitleKey")){
			setting.setCriteriaTitleKey(DataHelper.getString(elmt.getAttribute("criteriaTitleKey"),null));
		}
		if(elmt.hasAttribute("criteriaValueKey")){
			setting.setCriteriaValueKey(DataHelper.getString(elmt.getAttribute("criteriaValueKey"),null));
		}


		if(elmt.hasAttribute("showPageNumber")){
			setting.setShowPageNumber(DataHelper.isTrue(elmt.getAttribute("showPageNumber"), true));
		}
		if(elmt.hasAttribute("pageKey")){
			setting.setPageKey(DataHelper.getString(elmt.getAttribute("pageKey"),null));
		}


		if(elmt.hasAttribute("showReportId")){
			setting.setShowReportId(DataHelper.isTrue(elmt.getAttribute("showReportId"), false));
		}
		if(elmt.hasAttribute("reportId")){
			setting.setReportId(DataHelper.getString(elmt.getAttribute("reportId"),null));
		}


		if(elmt.hasAttribute("horizontal")){
			setting.setHorizontal(DataHelper.isTrue(elmt.getAttribute("horizontal"), true));
		}
		if(elmt.hasAttribute("pageSize")){
			setting.setPageSize(DataHelper.getString(elmt.getAttribute("pageSize"),"A4"));
		}
		if(elmt.hasAttribute("fitAllColumnsOnePage")){
			setting.setFitAllColumnsOnePage(DataHelper.isTrue(elmt.getAttribute("fitAllColumnsOnePage"), true));
		}


		if(elmt.hasAttribute("headerRows")){
			setting.setHeaderRows(DataHelper.getInteger(elmt.getAttribute("headerRows"),1));
		}
		if(elmt.hasAttribute("criteriaRows")){
			setting.setCriteriaRows(DataHelper.getInteger(elmt.getAttribute("criteriaRows"),2));
		}
		if(elmt.hasAttribute("hightlightHeader")){
			setting.setHightlightHeader(DataHelper.isTrue(elmt.getAttribute("hightlightHeader"), false));
		}
		if(elmt.hasAttribute("excelFontName")){
			setting.setExcelFontName(DataHelper.getString(elmt.getAttribute("excelFontName"), ExcelConstants.DEFAULT_FONT_NAME));
		}
		
		if (parameter.containsKey(ParameterDataConstants.PDF_DEFAULT_SHOW_ROW_NO)) {
		    setting.setShowRowNo(DataHelper.isTrue((String)parameter.get(ParameterDataConstants.PDF_DEFAULT_SHOW_ROW_NO), true));
		}
		if(elmt.hasAttribute("showRowNo")){
			setting.setShowRowNo(DataHelper.isTrue(elmt.getAttribute("showRowNo"), true));
		}

		if(elmt.hasAttribute("multiTable")){
			setting.setMultiTable(DataHelper.isTrue(elmt.getAttribute("multiTable"), false));
		}

		if(elmt.hasAttribute("multiTableSplitBy")){
			setting.setMultiTableSplitBy(DataHelper.getString(elmt.getAttribute("multiTableSplitBy"),null));
		}

		if(elmt.hasAttribute("highlightRowNoFld")){
			setting.setHighlightRowNoFld(DataHelper.getString(elmt.getAttribute("highlightRowNoFld"),null));
		}

		if(elmt.hasAttribute("highlightRowNoColor")){
			setting.setHighlightRowNoColor(DataHelper.getString(elmt.getAttribute("highlightRowNoColor"),null));
		}

		if(elmt.hasAttribute("tableHeaderColor")){
			setting.setTableHeaderColor(DataHelper.getString(elmt.getAttribute("tableHeaderColor"),null));
		}

		if(elmt.hasAttribute("pdfAlterRowColorOff")){
			setting.setPdfAlterRowColorOff(DataHelper.isTrue(elmt.getAttribute("pdfAlterRowColorOff"), false));
		}
		
		if (elmt.hasAttribute("excelDefaultCellWrapText")) {
		    setting.setExcelDefaultCellWrapText(DataHelper.isTrue(elmt.getAttribute("excelDefaultCellWrapText"), false));
		}

		if(elmt.hasAttribute("excelAutoFilter")){
			setting.setExcelAutoFilter(DataHelper.isTrue(elmt.getAttribute("excelAutoFilter"), true));
		}

		if(elmt.hasAttribute("excelFreezeHeader")){
			setting.setExcelFreezeHeader(DataHelper.isTrue(elmt.getAttribute("excelFreezeHeader"), true));
		}

		if(elmt.hasAttribute("excelAutosizeMargin")){
			setting.setExcelAutosizeMargin(DataHelper.getInteger(elmt.getAttribute("excelAutosizeMargin"), 5));
		}

		if(elmt.hasAttribute("excelPrintRepeatHeader")){
			setting.setExcelPrintRepeatHeader(DataHelper.isTrue(elmt.getAttribute("excelPrintRepeatHeader"), true));
		}

	}

}

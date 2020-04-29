package com.milliontech.circle.data.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.constants.ParameterDataConstants;
import com.milliontech.circle.constants.ReportWriterConstant;
import com.milliontech.circle.custom.CustomExcelCriteria;
import com.milliontech.circle.custom.CustomExcelPageSetup;
import com.milliontech.circle.custom.CustomPdfCriteria;
import com.milliontech.circle.custom.CustomPdfFooter;
import com.milliontech.circle.custom.CustomPdfHeader;
import com.milliontech.circle.data.model.CriteriaData;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.model.ReportSetting;

public class ParameterDataConverter {

	private static final Logger log = LoggerFactory.getLogger(ParameterDataConverter.class);

	public static ParameterData convertToParameterData(Map parameter){
		ParameterData data = (ParameterData)parameter.get(Constants.PARAMETER_DATA);
		if(data != null){
		    return data;
		}
		
		data = new ParameterData();
		data.setAuthorName(StringUtils.defaultIfEmpty((String)parameter.get(ParameterDataConstants.AUTHOR_NAME), "MT"));
		
		if (parameter.get(ParameterDataConstants.PRINT_DATE) != null) {
		    data.setPrintDate((java.util.Date)parameter.get(ParameterDataConstants.PRINT_DATE));
		}
		if (parameter.get(ParameterDataConstants.DEFUALT_PRINT_DATE_FORMAT) != null) {
		    data.setDefaultPrintDateFormat((String)parameter.get(ParameterDataConstants.DEFUALT_PRINT_DATE_FORMAT));
		}
		
		data.setPdfHeaderImagePath((String)parameter.get(ReportWriterConstant.HEADER_IMAGE));
		data.setDataTableTitleListStr((String)parameter.get(ReportWriterConstant.TITLE_FIELD));
		data.setHeaderTitle((String)parameter.get(ReportWriterConstant.HEADER));

		data.setFontSizeTitle((Float)parameter.get(ReportWriterConstant.FONT_SIZE_TITLE));
		data.setFontSizeCriteria((Float)parameter.get(ReportWriterConstant.FONT_SIZE_CRITERIA));
		data.setFontSizeColumnHeader((Float)parameter.get(ReportWriterConstant.FONT_SIZE_COLUMN_HEADER));
		data.setFontSizeContent((Float)parameter.get(ReportWriterConstant.FONT_SIZE_CONTENT));

		data.setPageLayout((String)parameter.get(ReportWriterConstant.PAGE_LAYOUT));
		data.setPageSize((String)parameter.get(ReportWriterConstant.PAGE_SIZE));
		data.setPageRotate((String)parameter.get(ReportWriterConstant.PAGE_ROTATE));

		if(parameter.get(ReportWriterConstant.PRINT_TIME_LABEL)!=null){
			data.setPrintTimeLabel((String)parameter.get(ReportWriterConstant.PRINT_TIME_LABEL));
		}
		if(parameter.get(ParameterDataConstants.PRINT_USER)!=null){
			data.setPrintBy((String)parameter.get(ParameterDataConstants.PRINT_USER));
		}
		if(parameter.get(ParameterDataConstants.PRINT_BY_LABEL)!=null){
			data.setPrintByLabel((String)parameter.get(ParameterDataConstants.PRINT_BY_LABEL));
		}
		if(parameter.get(ParameterDataConstants.DEFAULT_SHOW_RESTRICTED_LABEL) != null) {
		    data.setDefaultShowRestrictedLabel((boolean)parameter.get(ParameterDataConstants.DEFAULT_SHOW_RESTRICTED_LABEL));
		}
		if(parameter.get(ParameterDataConstants.DEFAULT_RESTRICTED_LABEL) != null) {
		    data.setDefaultRestrictedLabel((String)parameter.get(ParameterDataConstants.DEFAULT_RESTRICTED_LABEL));
		}
		if(parameter.get(ParameterDataConstants.PAGE_LABEL)!=null){
			data.setPageLabel((String)parameter.get(ParameterDataConstants.PAGE_LABEL));
		}

		data.setCriteriaNameList((List)parameter.get(ReportWriterConstant.CRITERIA_NAME));
		data.setCriteriaValueList((List)parameter.get(ReportWriterConstant.CRITERIA_VALUE));

		data.setRemapXmlPath((String)parameter.get(ReportWriterConstant.XML_REMAP));
		data.setRemapValueMap((Map)parameter.get(ReportWriterConstant.VALUE_REMAP));

		if(parameter.get(ParameterDataConstants.LANG)!=null){
			data.setLang((String)parameter.get(ParameterDataConstants.LANG));
		}
		data.setCustomExcelCriteria((CustomExcelCriteria)parameter.get(ParameterDataConstants.CUSTOM_EXCEL_CRITERIA));
		data.setCustomExcelPageSetup((CustomExcelPageSetup)parameter.get(ParameterDataConstants.CUSTOM_EXCEL_PRINT_PAGE_SETUP));
		data.setCustomPdfFooter((CustomPdfFooter)parameter.get(ParameterDataConstants.CUSTOM_PDF_FOOTER));
		data.setCustomPdfCriteria((CustomPdfCriteria)parameter.get(ParameterDataConstants.CUSTOM_PDF_CRITERIA));
		data.setCustomPdfHeader((CustomPdfHeader)parameter.get(ParameterDataConstants.CUSTOM_PDF_HEADER));

		if(parameter.get(ParameterDataConstants.SXSSF_TMP_FILE_PATH)!=null){
			data.setSxssfTmpFilePath((String)parameter.get(ParameterDataConstants.SXSSF_TMP_FILE_PATH));
		}
		if(parameter.get(ParameterDataConstants.SXSSF_ROW_ACCESS_WINDOW_SIZE)!=null){
			data.setSxssfRowAccessWindowSize((Integer)parameter.get(ParameterDataConstants.SXSSF_ROW_ACCESS_WINDOW_SIZE));
		}
		if(parameter.get(ParameterDataConstants.SXSSF_COMPRESS_TMP_FILE)!=null){
			data.setSxssfCompressTmpFile((Boolean)parameter.get(ParameterDataConstants.SXSSF_COMPRESS_TMP_FILE));
		}
		if(parameter.get(ParameterDataConstants.SXSSF_USE_SHARED_STRINGS_TABLE)!=null){
			data.setSxssfCompressTmpFile((Boolean)parameter.get(ParameterDataConstants.SXSSF_USE_SHARED_STRINGS_TABLE));
		}
		if(parameter.get(ParameterDataConstants.SXSSF_KEEP_TMP_FILE)!=null){
			data.setSxssfKeepTmpFile((Boolean)parameter.get(ParameterDataConstants.SXSSF_KEEP_TMP_FILE));
		}
		if(parameter.get(ParameterDataConstants.USE_WIDTH_IF_EMPTY)!=null){
			data.setUseWidthIfEmpty((Boolean)parameter.get(ParameterDataConstants.USE_WIDTH_IF_EMPTY));
		}
		if(parameter.get(ParameterDataConstants.FONT_FILE_PATH_LIST) != null) {
		    data.setFontFilePathList((List<String>) parameter.get(ParameterDataConstants.FONT_FILE_PATH_LIST));
		}

		return data;
	}

	public static void convertToParameterDataByReportSetting(ParameterData data, Map parameter,ReportSetting setting){
		if(parameter.get(setting.getPrintDateKey())!=null){
			data.setPrintTimeLabel((String)parameter.get(setting.getPrintDateKey()));
		}

		if(parameter.get(setting.getPrintByKey())!=null){
			data.setPrintByLabel((String)parameter.get(setting.getPrintByKey()));
		}

		if(parameter.get(setting.getCriteriaTitleKey())!=null){
			data.setCriteriaNameList((List)parameter.get(setting.getCriteriaTitleKey()));
		}

		if(parameter.get(setting.getCriteriaValueKey())!=null){
			data.setCriteriaValueList((List)parameter.get(setting.getCriteriaValueKey()));
		}

		if(parameter.get(setting.getPageKey())!=null){
			data.setPageLabel((String)parameter.get(setting.getPageKey()));
		}

	}

	public static List<CriteriaData> convertToCriteriaDataList(ParameterData data){
		List<CriteriaData> list = new ArrayList<CriteriaData>();
		if (CollectionUtils.isEmpty(data.getCriteriaNameList()) || CollectionUtils.isEmpty(data.getCriteriaValueList())) {
		    return list;
		}
		
        String[] nameArr = (String[])data.getCriteriaNameList().toArray(new String[]{});
        String[] valueArr = (String[])data.getCriteriaValueList().toArray(new String[]{});
        if(nameArr!=null && valueArr!=null && nameArr.length==valueArr.length){
            for(int i=0; i<nameArr.length; i++){
                CriteriaData d = new CriteriaData();
                d.setName(nameArr[i]);
                d.setValue(valueArr[i]);
                list.add(d);
            }
        } else {
            log.warn(String.format("Criteria name and value list size are not consistent. Name size: [%d], Value size: [%d]",
                    data.getCriteriaNameList().size(), data.getCriteriaValueList().size()));
        }
		return list;
	}


}

package com.milliontech.circle.data.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.milliontech.circle.custom.CustomExcelCriteria;
import com.milliontech.circle.custom.CustomExcelPageSetup;
import com.milliontech.circle.custom.CustomPdfCriteria;
import com.milliontech.circle.custom.CustomPdfFooter;
import com.milliontech.circle.custom.CustomPdfHeader;

public class ParameterData {
    private String authorName;
    
    private java.util.Date printDate;
    private String defaultPrintDateFormat;
    
    private String pdfHeaderImagePath;
    private String dataTableTitleListStr;
    private String headerTitle;

    private Float fontSizeTitle;
    private Float fontSizeCriteria;
    private Float fontSizeColumnHeader;
    private Float fontSizeContent;

    private String pageLayout;
    private String pageSize;
    private String pageRotate;

    private String printTimeLabel;
    private String printByLabel;
    private boolean defaultShowRestrictedLabel;
    private String defaultRestrictedLabel;
    private String pageLabel;
    private String printBy;

    private List criteriaNameList;
    private List criteriaValueList;

    private String remapXmlPath;
    private Map remapValueMap;
    private Map summaryRowDataMap;
    private String lang;

    private CustomPdfCriteria customPdfCriteria;
    private CustomPdfHeader customPdfHeader;
    private CustomPdfFooter customPdfFooter;

    private CustomExcelCriteria customExcelCriteria;
    private CustomExcelPageSetup customExcelPageSetup;

    private boolean useWidthIfEmpty;

    private String sxssfTmpFilePath;
    private int sxssfRowAccessWindowSize;
    private boolean sxssfCompressTmpFile;
    private boolean sxssfUseSharedStringTables;
    private boolean sxssfKeepTmpFile;
    
    private List<String> fontFilePathList;

    public ParameterData(){
        authorName = "";
        printDate = new java.util.Date(System.currentTimeMillis());
        defaultPrintDateFormat = "dd/MM/yyyy HH:mm";
        printTimeLabel = "Print Date";
        printByLabel = "Print By";
        defaultShowRestrictedLabel = false;
        defaultRestrictedLabel = "RESTIRCITED";
        pageLabel = "P. ";
        summaryRowDataMap = new HashMap();
        lang = "Eng";
        sxssfRowAccessWindowSize = 100;
        sxssfCompressTmpFile = false;
        sxssfUseSharedStringTables = false;
        sxssfKeepTmpFile = false;
        useWidthIfEmpty = false;
        fontFilePathList = new ArrayList<String>(0);
    }

    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public java.util.Date getPrintDate() {
        return printDate;
    }
    public void setPrintDate(java.util.Date printDate) {
        this.printDate = printDate;
    }
    public String getDefaultPrintDateFormat() {
        return defaultPrintDateFormat;
    }
    public void setDefaultPrintDateFormat(String defaultPrintDateFormat) {
        this.defaultPrintDateFormat = defaultPrintDateFormat;
    }
    public String getPdfHeaderImagePath() {
        return pdfHeaderImagePath;
    }
    public void setPdfHeaderImagePath(String pdfHeaderImagePath) {
        this.pdfHeaderImagePath = pdfHeaderImagePath;
    }
    public String getDataTableTitleListStr() {
        return dataTableTitleListStr;
    }
    public void setDataTableTitleListStr(String dataTableTitleListStr) {
        this.dataTableTitleListStr = dataTableTitleListStr;
    }
    public String getHeaderTitle() {
        return headerTitle;
    }
    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }
    public Float getFontSizeTitle() {
        return fontSizeTitle;
    }
    public void setFontSizeTitle(Float fontSizeTitle) {
        this.fontSizeTitle = fontSizeTitle;
    }
    public Float getFontSizeCriteria() {
        return fontSizeCriteria;
    }
    public void setFontSizeCriteria(Float fontSizeCriteria) {
        this.fontSizeCriteria = fontSizeCriteria;
    }
    public Float getFontSizeColumnHeader() {
        return fontSizeColumnHeader;
    }
    public void setFontSizeColumnHeader(Float fontSizeColumnHeader) {
        this.fontSizeColumnHeader = fontSizeColumnHeader;
    }
    public Float getFontSizeContent() {
        return fontSizeContent;
    }
    public void setFontSizeContent(Float fontSizeContent) {
        this.fontSizeContent = fontSizeContent;
    }
    public String getPageLayout() {
        return pageLayout;
    }
    public void setPageLayout(String pageLayout) {
        this.pageLayout = pageLayout;
    }
    public String getPageSize() {
        return pageSize;
    }
    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
    public String getPageRotate() {
        return pageRotate;
    }
    public void setPageRotate(String pageRotate) {
        this.pageRotate = pageRotate;
    }
    public String getPrintTimeLabel() {
        return printTimeLabel;
    }
    public void setPrintTimeLabel(String printTimeLabel) {
        this.printTimeLabel = printTimeLabel;
    }
    public List getCriteriaNameList() {
        return criteriaNameList;
    }
    public void setCriteriaNameList(List criteriaNameList) {
        this.criteriaNameList = criteriaNameList;
    }
    public List getCriteriaValueList() {
        return criteriaValueList;
    }
    public void setCriteriaValueList(List criteriaValueList) {
        this.criteriaValueList = criteriaValueList;
    }
    public String getRemapXmlPath() {
        return remapXmlPath;
    }
    public void setRemapXmlPath(String remapXmlPath) {
        this.remapXmlPath = remapXmlPath;
    }
    public Map getRemapValueMap() {
        return remapValueMap;
    }
    public void setRemapValueMap(Map remapValueMap) {
        this.remapValueMap = remapValueMap;
    }
    public boolean getDefaultShowRestrictedLabel() {
        return defaultShowRestrictedLabel;
    }
    public void setDefaultShowRestrictedLabel(boolean defaultShowRestrictedLabel) {
        this.defaultShowRestrictedLabel = defaultShowRestrictedLabel;
    }
    public String getDefaultRestrictedLabel() {
        return defaultRestrictedLabel;
    }
    public void setDefaultRestrictedLabel(String defaultRestrictedLabel) {
        this.defaultRestrictedLabel = defaultRestrictedLabel;
    }
    public String getPrintByLabel() {
        return printByLabel;
    }
    public void setPrintByLabel(String printByLabel) {
        this.printByLabel = printByLabel;
    }
    public String getPageLabel() {
        return pageLabel;
    }
    public void setPageLabel(String pageLabel) {
        this.pageLabel = pageLabel;
    }
    public String getPrintBy() {
        return printBy;
    }
    public void setPrintBy(String printBy) {
        this.printBy = printBy;
    }
    public Map getSummaryRowDataMap() {
        return summaryRowDataMap;
    }
    public void setSummaryRowDataMap(Map summaryRowDataMap) {
        this.summaryRowDataMap = summaryRowDataMap;
    }
    public String getLang() {
        return lang;
    }
    public void setLang(String lang) {
        this.lang = lang;
    }
    public CustomPdfCriteria getCustomPdfCriteria() {
        return customPdfCriteria;
    }
    public void setCustomPdfCriteria(CustomPdfCriteria customPdfCriteria) {
        this.customPdfCriteria = customPdfCriteria;
    }
    public CustomPdfHeader getCustomPdfHeader() {
        return customPdfHeader;
    }
    public void setCustomPdfHeader(CustomPdfHeader customPdfHeader) {
        this.customPdfHeader = customPdfHeader;
    }
    public CustomPdfFooter getCustomPdfFooter() {
        return customPdfFooter;
    }
    public void setCustomPdfFooter(CustomPdfFooter customPdaFooter) {
        this.customPdfFooter = customPdaFooter;
    }
    public CustomExcelCriteria getCustomExcelCriteria() {
        return customExcelCriteria;
    }
    public void setCustomExcelCriteria(CustomExcelCriteria customExcelCriteria) {
        this.customExcelCriteria = customExcelCriteria;
    }
    public CustomExcelPageSetup getCustomExcelPageSetup() {
        return customExcelPageSetup;
    }
    public void setCustomExcelPageSetup(CustomExcelPageSetup customExcelPageSetup) {
        this.customExcelPageSetup = customExcelPageSetup;
    }
    public String getSxssfTmpFilePath() {
        return sxssfTmpFilePath;
    }
    public void setSxssfTmpFilePath(String sxssfTmpFilePath) {
        this.sxssfTmpFilePath = sxssfTmpFilePath;
    }
    public int getSxssfRowAccessWindowSize() {
        return sxssfRowAccessWindowSize;
    }
    public void setSxssfRowAccessWindowSize(int sxssfRowAccessWindowSize) {
        this.sxssfRowAccessWindowSize = sxssfRowAccessWindowSize;
    }
    public boolean isSxssfCompressTmpFile() {
        return sxssfCompressTmpFile;
    }
    public void setSxssfCompressTmpFile(boolean sxssfCompressTmpFile) {
        this.sxssfCompressTmpFile = sxssfCompressTmpFile;
    }
    public boolean isSxssfUseSharedStringTables() {
        return sxssfUseSharedStringTables;
    }
    public void setSxssfUseSharedStringTables(boolean sxssfUseSharedStringTables) {
        this.sxssfUseSharedStringTables = sxssfUseSharedStringTables;
    }
    public boolean isSxssfKeepTmpFile() {
        return sxssfKeepTmpFile;
    }
    public void setSxssfKeepTmpFile(boolean sxssfKeepTmpFile) {
        this.sxssfKeepTmpFile = sxssfKeepTmpFile;
    }
    public boolean isUseWidthIfEmpty() {
        return useWidthIfEmpty;
    }
    public void setUseWidthIfEmpty(boolean useWidthIfEmpty) {
        this.useWidthIfEmpty = useWidthIfEmpty;
    }
    public List<String> getFontFilePathList() {
        return fontFilePathList;
    }
    public void setFontFilePathList(List<String> fontFilePathList) {
        this.fontFilePathList = fontFilePathList;
    }
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}

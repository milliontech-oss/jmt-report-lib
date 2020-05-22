package com.milliontech.circle.model;

import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TableHeader implements Comparable, MessageKeyTitleElement, TableColumn{

	private int column;
	private int subColumn;
	private String title;
	private String msgKey;
	private String method;
	private String property;
	private String format;
	private String align;
	private int width;
	private float pdfWidth;
	private int xlsWidth;
	private float calcWidth;
	private boolean extractClass;
	private boolean extractHonz;
	private boolean mergeIfSame;
	private String mergeKey;
	private boolean multiTableDynFld;
	private Map titleMap;
	private boolean mutiTableDummyFld;
	private String highlightFld;
	private String highlightColor;
	private String italicsFld;
	private boolean wrapText;
	private boolean hidden;
	private String xmlRemap;

	public TableHeader(){
		subColumn = 0;
		mutiTableDummyFld = false;
		hidden = false;
	}

	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMsgKey() {
		return msgKey;
	}
	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public boolean isExtractClass() {
		return extractClass;
	}
	public void setExtractClass(boolean extractClass) {
		this.extractClass = extractClass;
	}
	public boolean isExtractHonz() {
		return extractHonz;
	}
	public void setExtractHonz(boolean extractHonz) {
		this.extractHonz = extractHonz;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public int getSubColumn() {
		return subColumn;
	}
	public void setSubColumn(int subColumn) {
		this.subColumn = subColumn;
	}
	public float getPdfWidth() {
		return pdfWidth;
	}
	public void setPdfWidth(float pdfWidth) {
		this.pdfWidth = pdfWidth;
	}
	public int getXlsWidth() {
		return xlsWidth;
	}
	public void setXlsWidth(int xlsWidth) {
		this.xlsWidth = xlsWidth;
	}
	public float getCalcWidth() {
		return calcWidth;
	}
	public void setCalcWidth(float calcWidth) {
		this.calcWidth = calcWidth;
	}
	public boolean isMergeIfSame() {
		return mergeIfSame;
	}
	public void setMergeIfSame(boolean mergeIfSame) {
		this.mergeIfSame = mergeIfSame;
	}
	public String getMergeKey() {
		return mergeKey;
	}
	public void setMergeKey(String mergeKey) {
		this.mergeKey = mergeKey;
	}
	public boolean isMultiTableDynFld() {
		return multiTableDynFld;
	}
	public void setMultiTableDynFld(boolean multiTableDynFld) {
		this.multiTableDynFld = multiTableDynFld;
	}
	public Map getTitleMap() {
		return titleMap;
	}
	public void setTitleMap(Map titleMap) {
		this.titleMap = titleMap;
	}
	public boolean isMutiTableDummyFld() {
		return mutiTableDummyFld;
	}
	public void setMutiTableDummyFld(boolean mutiTableDummyFld) {
		this.mutiTableDummyFld = mutiTableDummyFld;
	}
	public String getHighlightFld() {
		return highlightFld;
	}
	public void setHighlightFld(String highlightFld) {
		this.highlightFld = highlightFld;
	}
	public String getHighlightColor() {
		return highlightColor;
	}
	public void setHighlightColor(String highlightColor) {
		this.highlightColor = highlightColor;
	}
	public String getItalicsFld() {
		return italicsFld;
	}
	public void setItalicsFld(String italicsFld) {
		this.italicsFld = italicsFld;
	}
	public boolean isWrapText() {
		return wrapText;
	}

	public void setWrapText(boolean wrapText) {
		this.wrapText = wrapText;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public String getXmlRemap() {
        return xmlRemap;
    }

    public void setXmlRemap(String xmlRemap) {
        this.xmlRemap = xmlRemap;
    }

    public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	public int compareTo(Object o) {
		TableColumn h = (TableColumn)o;
		int value = this.getColumn()-h.getColumn();
		return value != 0 ? value : (this.getSubColumn()-h.getSubColumn());
	}
}

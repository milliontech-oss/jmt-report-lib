package com.milliontech.circle.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ObjectCell {

	private int column;
	private String method;
	private String property;
	private String format;
	private String align;
	
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
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
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}	
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}

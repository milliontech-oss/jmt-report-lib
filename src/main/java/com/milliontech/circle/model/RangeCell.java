package com.milliontech.circle.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RangeCell implements MessageKeyTitleElement{

	private int start;
	private int end;
	private String title;
	private String msgKey;
	private String align;
	private boolean dynamicProperty;
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
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
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}	
	public boolean isDynamicProperty() {
		return dynamicProperty;
	}
	public void setDynamicProperty(boolean dynamicProperty) {
		this.dynamicProperty = dynamicProperty;
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}

package com.milliontech.circle.xml.converter;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.helper.MessageHelper;
import com.milliontech.circle.model.RangeCell;

public class RangeCellXmlNodeConverter implements XmlNodeConverter{

	public void convertAndAddToList(List list, Element elmt, Map parameter, ParameterData data) {
		RangeCell cell = new RangeCell();
		this.convertToObject(cell, elmt, parameter, data);		
		list.add(cell);		
	}

	public void convertToObject(Object object, Element elmt, Map parameter, ParameterData data) {
		RangeCell cell = (RangeCell)object;
		cell.setAlign(DataHelper.getString(elmt.getAttribute("align"), Constants.ALIGN_RIGHT));
		cell.setEnd(DataHelper.getInteger(elmt.getAttribute("end"), 0));
		cell.setMsgKey(DataHelper.getString(elmt.getAttribute("msgKey"), null));
		cell.setStart(DataHelper.getInteger(elmt.getAttribute("start"), 0));
		cell.setTitle(DataHelper.getString(elmt.getAttribute("title"), null));
		cell.setDynamicProperty(DataHelper.isTrue(elmt.getAttribute("dynamicProperty"), false));
		MessageHelper.setTitleByMessageKey(parameter, cell);
	}
	
}

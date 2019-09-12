package com.milliontech.circle.xml.converter;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.model.ObjectCell;

public class ObjectCellXmlNodeConverter implements XmlNodeConverter{

	public void convertAndAddToList(List list, Element elmt, Map parameter, ParameterData data) {
		ObjectCell cell = new ObjectCell();
		this.convertToObject(cell, elmt, parameter, data);
		list.add(cell);
	}

	public void convertToObject(Object object, Element elmt, Map parameter, ParameterData data) {
		ObjectCell cell = (ObjectCell)object;
		cell.setColumn(DataHelper.getInteger(elmt.getAttribute("column"), 0));
		cell.setMethod(DataHelper.getString(elmt.getAttribute("method"), null));
		cell.setProperty(DataHelper.getString(elmt.getAttribute("property"), null));
		cell.setFormat(DataHelper.getString(elmt.getAttribute("format"), null));
		cell.setAlign(DataHelper.getString(elmt.getAttribute("align"), Constants.ALIGN_RIGHT));
	}

}

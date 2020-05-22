package com.milliontech.circle.xml.converter;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.milliontech.circle.constants.Constants;
import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.model.ObjectCell;

public class ObjectCellXmlNodeConverter implements XmlNodeConverter<ObjectCell>{

	public ObjectCell convertAndAddToList(List list, Element elmt, Map parameter, ParameterData data, Class clazz) {
		ObjectCell cell = new ObjectCell();
		this.convertToObject(cell, elmt, parameter, data);
		list.add(cell);
		return cell;
	}

	public void convertToObject(ObjectCell cell, Element elmt, Map parameter, ParameterData data) {
		cell.setColumn(DataHelper.getInteger(elmt.getAttribute("column"), 0));
		cell.setMethod(DataHelper.getString(elmt.getAttribute("method"), null));
		cell.setProperty(DataHelper.getString(elmt.getAttribute("property"), null));
		cell.setFormat(DataHelper.getString(elmt.getAttribute("format"), null));
		cell.setAlign(DataHelper.getString(elmt.getAttribute("align"), Constants.ALIGN_RIGHT));
	}

}

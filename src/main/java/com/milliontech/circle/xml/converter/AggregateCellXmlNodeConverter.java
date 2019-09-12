package com.milliontech.circle.xml.converter;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.model.AggregateCell;

public class AggregateCellXmlNodeConverter implements XmlNodeConverter{
	
	public void convertAndAddToList(List list, Element elmt, Map parameter, ParameterData data) {
		AggregateCell cell = new AggregateCell();
		this.convertToObject(cell, elmt, parameter, data);
		list.add(cell);
	}

	public void convertToObject(Object object, Element elmt, Map parameter, ParameterData data) {
		AggregateCell cell = (AggregateCell)object;
		cell.setColumn(DataHelper.getInteger(elmt.getAttribute("column"), 0));
		cell.setMode(DataHelper.getString(elmt.getAttribute("mode"), null));
		cell.setFormat(DataHelper.getString(elmt.getAttribute("format"), null));
	}

}

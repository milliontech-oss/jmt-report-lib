package com.milliontech.circle.xml.converter;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.DataHelper;
import com.milliontech.circle.model.AggregateCell;

public class AggregateCellXmlNodeConverter implements XmlNodeConverter<AggregateCell>{
	
	public AggregateCell convertAndAddToList(List list, Element elmt, Map parameter, ParameterData data, Class clazz) {
		AggregateCell cell = new AggregateCell();
		this.convertToObject(cell, elmt, parameter, data);
		list.add(cell);
		return cell;
	}

	public void convertToObject(AggregateCell cell, Element elmt, Map parameter, ParameterData data) {
		cell.setColumn(DataHelper.getInteger(elmt.getAttribute("column"), 0));
		cell.setMode(DataHelper.getString(elmt.getAttribute("mode"), null));
		cell.setFormat(DataHelper.getString(elmt.getAttribute("format"), null));
	}

}

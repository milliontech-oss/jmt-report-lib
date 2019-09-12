package com.milliontech.circle.xml.converter;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.milliontech.circle.data.model.ParameterData;

public interface XmlNodeConverter {

	public void convertAndAddToList(List list, Element elmt, Map parameter, ParameterData data);
	public void convertToObject(Object object, Element elmt, Map parameter, ParameterData data);
	
}

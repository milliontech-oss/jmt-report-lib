package com.milliontech.circle.xml.converter;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.milliontech.circle.data.model.ParameterData;

public interface XmlNodeConverter<T> {

	public T convertAndAddToList(List list, Element elmt, Map parameter, ParameterData data, Class clazz);
	public void convertToObject(T object, Element elmt, Map parameter, ParameterData data);
	
}

package com.milliontech.circle.custom;

import com.itextpdf.layout.Document;
import com.milliontech.circle.data.model.ParameterData;

public interface CustomPdfCriteria {

	public void writeCustomCriteria(ParameterData data, Document document) throws Exception;
	
}

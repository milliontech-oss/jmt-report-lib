package com.milliontech.circle.custom;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.milliontech.circle.data.model.ParameterData;

public interface CustomPdfHeader {

	public void writeCustomHeader(ParameterData data, Document document, PdfWriter writer) throws Exception;	
	
}

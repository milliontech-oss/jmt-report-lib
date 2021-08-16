package com.milliontech.circle.custom;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.milliontech.circle.data.model.ParameterData;

public interface CustomPdfFooter {

	public void writeCustomFooter(ParameterData data, Document document, PdfWriter writer) throws Exception;
	
}

package com.milliontech.circle.model.pdf;

import com.itextpdf.text.Font;

public class PdfStyle {

	private Font[] fontList;
	private short fontWeight;
	
	public Font[] getFontList() {
		return fontList;
	}

	public void setFontList(Font[] fontList) {
		this.fontList = fontList;
	}

	public short getFontWeight() {
		return fontWeight;
	}

	public void setFontWeight(short fontWeight) {
		this.fontWeight = fontWeight;
	}
	
}

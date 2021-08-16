package com.milliontech.circle.helper;

import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;

public class StandardFontInfo {
	private String fontName;
	private String encoding;
	
	public StandardFontInfo(String fontName, String encoding) {
		super();
		this.fontName = fontName;
		this.encoding = encoding;
	}
	
	public String getFontName() {
		return fontName;
	}
	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
}

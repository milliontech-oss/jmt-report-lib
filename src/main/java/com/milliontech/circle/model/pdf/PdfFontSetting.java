package com.milliontech.circle.model.pdf;

import java.util.List;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.font.FontProvider;

public class PdfFontSetting {
	private List<PdfFont> fontList;
	private FontProvider fontProvider;
	private String defaultFontFamily;
	
	public PdfFontSetting(List<PdfFont> fontList, FontProvider fontProvider, String defaultFontFamily) {
		super();
		this.fontList = fontList;
		this.fontProvider = fontProvider;
		this.defaultFontFamily = defaultFontFamily;
	}
	
	public List<PdfFont> getFontList() {
		return fontList;
	}
	public void setFontList(List<PdfFont> fontList) {
		this.fontList = fontList;
	}
	public FontProvider getFontProvider() {
		return fontProvider;
	}
	public void setFontProvider(FontProvider fontProvider) {
		this.fontProvider = fontProvider;
	}
	public String getDefaultFontFamily() {
		return defaultFontFamily;
	}
	public void setDefaultFontFamily(String defaultFontFamily) {
		this.defaultFontFamily = defaultFontFamily;
	}
	
}

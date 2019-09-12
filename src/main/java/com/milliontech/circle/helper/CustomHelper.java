package com.milliontech.circle.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.milliontech.circle.model.pdf.PdfStyle;

public class CustomHelper {
	
	private static final Logger log = LoggerFactory.getLogger(CustomHelper.class);
	
	private static PdfStyle createHeaderTitleStyle(boolean bold, float fontSize) throws Exception{
		PdfStyle style = new PdfStyle();
		Font[] fontArray = PdfHelper.createFontArray();
		for (int i = 0; i < fontArray.length; i++) {
			Font f = fontArray[i];			
			f.setSize(fontSize);
			if(bold){
				f.setStyle(Font.BOLD);
			}
		}
		style.setFontList(fontArray);
		
		return style;
	}
	
	public static Paragraph createWordsWithStyle(String value, boolean underLine, boolean bold, float fontSize) {	
		try{
			PdfStyle style = CustomHelper.createHeaderTitleStyle(bold, fontSize);
			return PdfHelper.createDisplayParagraph(value, style.getFontList(), underLine);
		}catch(Exception e){
			log.error("cannot createWordsWithStyle", e);
			return null;
		}
	}
	
	public static void addPdfCellToTable(PdfPTable table, Image image, int rowspan, int colspan, int border, int align){
		PdfPCell cell = new PdfPCell();
		cell.setImage(image);
		cell.setHorizontalAlignment(align);
		cell.setBorder(border);
		cell.setRowspan(rowspan);
		cell.setColspan(colspan);
		table.addCell(cell);
	}
	
	public static void addPdfCellToTable(PdfPTable table, Phrase p, int rowspan, int colspan, int border, int align){
		PdfPCell cell = new PdfPCell();
		cell.setPhrase(p);
		cell.setHorizontalAlignment(align);
		cell.setBorder(border);
		cell.setRowspan(rowspan);
		cell.setColspan(colspan);
		table.addCell(cell);
	}
	
	public static void addPdfCellToTable(PdfPTable table, String p, int rowspan, int colspan, int border, int align){
		PdfPCell cell = new PdfPCell();
		cell.setPhrase(new Phrase(p));
		cell.setHorizontalAlignment(align);
		cell.setBorder(border);
		cell.setRowspan(rowspan);
		cell.setColspan(colspan);
		table.addCell(cell);
	}
	
}

package com.milliontech.circle.helper;

import java.util.List;

import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class CustomHelper {
    
    public static Paragraph createWordsWithStyle(String value, boolean underLine, boolean bold, float fontSize) {
        return createWordsWithStyle(PdfFontRepository.SANS_SERIF_FONT_LIST, value, underLine, bold, fontSize);
    }
	
    /**
     * use PdfFontRepository.createBaseFontList to create the baseFont list
     */
	public static Paragraph createWordsWithStyle(List<BaseFont> bfList, String value, boolean underLine, boolean bold, float fontSize) {
	    List<Font> fonts = PdfFontRepository.createFontList(bfList, fontSize, bold ? Font.BOLD : Font.NORMAL);
	    return PdfHelper.createDisplayParagraph(value, fonts, underLine);
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

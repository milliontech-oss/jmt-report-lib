package com.milliontech.circle.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.milliontech.circle.constants.PdfConstants;

public class CustomHelper {
    public static Paragraph createWordsWithStyle(String value, boolean underLine, boolean bold, float fontSize) {
        return createWordsWithStyle(PdfFontRepository.getSansSerifFontList(), value, underLine, bold, fontSize);
    }

    /**
     * use PdfFontRepository.createBaseFontList to create the baseFont list
     */
    public static Paragraph createWordsWithStyle(List<PdfFont> bfList, String value, boolean underLine, boolean bold, float fontSize) {
        List<PdfFontInfo> fonts = PdfFontRepository.createPdfFontInfoList(bfList, fontSize, bold);
        return PdfHelper.createDisplayParagraph(value, fonts, underLine, false);
    }

    public static void addPdfCellToTable(Table table, Image image, int rowspan, int colspan, BorderType border, HorizontalAlignment align){
        Cell cell = new Cell(rowspan, colspan);
        cell.add(image);
        cell.setHorizontalAlignment(align);
        setBorder(cell, new ArrayList<>(Collections.singletonList(border)));
        table.addCell(cell);
    }

    public static void addPdfCellToTable(Table table, Paragraph p, int rowspan, int colspan, BorderType border, TextAlignment align){
        Cell cell = new Cell(rowspan, colspan);
        cell.add(p);
        cell.setTextAlignment(align);
        setBorder(cell, new ArrayList<>(Collections.singletonList(border)));
        table.addCell(cell);
    }

    public static void addPdfCellToTable(Table table, String p, int rowspan, int colspan, BorderType border, TextAlignment align){
        Cell cell = new Cell(rowspan, colspan);
        cell.add(new Paragraph(p));
        cell.setTextAlignment(align);
        setBorder(cell, new ArrayList<>(Collections.singletonList(border)));
        table.addCell(cell);
    }

    private static void setBorder(Cell cell, List<BorderType> border) {
        Border defaultBorder = new SolidBorder(PdfConstants.DEFAULT_TABLE_BORDER_WIDTH);
        if(border.contains(BorderType.ALL)) {
            cell.setBorder(defaultBorder);
        } else if(border.contains(BorderType.NONE)) {
            cell.setBorder(Border.NO_BORDER);
        } else {
            if (border.contains(BorderType.TOP)) {
                cell.setBorderTop(defaultBorder);
            }
            if (border.contains(BorderType.BOTTOM)) {
                cell.setBorderBottom(defaultBorder);
            }
            if (border.contains(BorderType.LEFT)) {
                cell.setBorderLeft(defaultBorder);
            }
            if (border.contains(BorderType.RIGHT)) {
                cell.setBorderRight(defaultBorder);
            }
        }
    }
}

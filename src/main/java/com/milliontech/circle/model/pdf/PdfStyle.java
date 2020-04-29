package com.milliontech.circle.model.pdf;

import java.util.List;

import com.itextpdf.text.Font;

public class PdfStyle {

    private String name;
    private List<Font> fontList;
    private float size;
    private int style;
    
    public PdfStyle(String name, List<Font> fontList) {
        this(name, fontList, fontList.get(0).getSize(), fontList.get(0).getStyle());
    }
    
    public PdfStyle(String name, List<Font> fontList, float size, int style) {
        super();
        this.name = name;
        this.fontList = fontList;
        this.size = size;
        this.style = style;
    }
    
    public String getName() {
        return name;
    }
    public List<Font> getFontList() {
        return fontList;
    }
    public float getSize() {
        return size;
    }
    public int getStyle() {
        return style;
    }
    
}

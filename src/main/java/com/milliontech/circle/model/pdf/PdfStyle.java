package com.milliontech.circle.model.pdf;

import java.util.List;

import com.milliontech.circle.helper.PdfFontInfo;

public class PdfStyle {

    private String name;
    private List<PdfFontInfo> fontInfoList;
    private float size;
    private boolean bold;

    public PdfStyle(String name, List<PdfFontInfo> fontInfoList) {
        this(name, fontInfoList, fontInfoList.get(0).getFontSize(), fontInfoList.get(0).isBold());
    }

    public PdfStyle(String name, List<PdfFontInfo> fontInfoList, float size, boolean bold) {
        super();
        this.name = name;
        this.fontInfoList = fontInfoList;
        this.size = size;
        this.bold = bold;
    }

    public String getName() {
        return name;
    }
    public List<PdfFontInfo> getFontInfoList() {
        return fontInfoList;
    }
    public float getSize() {
        return size;
    }
    public boolean isBold() {
        return bold;
    }

}

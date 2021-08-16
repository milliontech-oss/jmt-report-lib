package com.milliontech.circle.helper;

import com.itextpdf.kernel.font.PdfFont;

public class PdfFontInfo {
    PdfFont font;
    float fontSize;
    boolean bold;

    PdfFontInfo(PdfFont font, float fontSize) {
        this.font = font;
        this.fontSize = fontSize;
        this.bold = false;
    }

    public PdfFont getFont() {
        return font;
    }

    public void setFont(PdfFont font) {
        this.font = font;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }
}

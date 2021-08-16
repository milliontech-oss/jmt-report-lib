package com.milliontech.circle.custom;

import java.util.List;

import com.itextpdf.layout.font.FontCharacteristics;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelectorStrategy;
import com.itextpdf.layout.font.FontSet;

public class CustomFontProvider extends FontProvider {
    private final boolean defaultEmbeddingFlag;

    public CustomFontProvider(boolean defaultEmbeddingFlag) {
        super();
        this.defaultEmbeddingFlag = defaultEmbeddingFlag;
    }

    @Override
    public boolean getDefaultEmbeddingFlag() {
        return defaultEmbeddingFlag;
    }

    @Override
    public FontSelectorStrategy getStrategy(String text, List<String> fontFamilies, FontCharacteristics fc, FontSet additionalFonts) {
        return new CustomFontSelectorStrategy(text, getFontSelector(fontFamilies, fc, additionalFonts), this, additionalFonts);
    }

}

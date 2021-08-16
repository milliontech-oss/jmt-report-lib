package com.milliontech.circle.custom;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.layout.font.FontSelectorStrategy;
import com.itextpdf.layout.font.FontSet;

/*
Copy from com.itextpdf.layout.font.ComplexFontSelectorStrategy
*/
public class CustomFontSelectorStrategy extends FontSelectorStrategy {

    private PdfFont font;
    private FontSelector selector;


    public CustomFontSelectorStrategy(String text, FontSelector selector, FontProvider provider, FontSet additionalFonts) {
        super(text, provider, additionalFonts);
        this.font = null;
        this.selector = selector;
    }

    public CustomFontSelectorStrategy(String text, FontSelector selector, FontProvider provider) {
        super(text, provider, null);
        this.font = null;
        this.selector = selector;
    }

    @Override
    public PdfFont getCurrentFont() {
        return font;
    }

    @Override
    public List<Glyph> nextGlyphs() {
        font = null;
        int nextUnignorable = nextSignificantIndex();
        if (nextUnignorable < text.length()) {
            for (FontInfo f : selector.getFonts()) {
                int codePoint = isSurrogatePair(text, nextUnignorable)
                        ? TextUtil.convertToUtf32(text, nextUnignorable)
                        : (int) text.charAt(nextUnignorable);

                if (f.getFontUnicodeRange().contains(codePoint)) {
                    PdfFont currentFont = getPdfFont(f);
                    Glyph glyph = currentFont.getGlyph(codePoint);
                    if (null != glyph && 0 != glyph.getCode() && -1 != glyph.getCode()) {
                        font = currentFont;
                        break;
                    }
                }
            }
        }
        List<Glyph> glyphs = new ArrayList<>();
        boolean anyGlyphsAppended = false;
        if (font != null) {
            Character.UnicodeScript unicodeScript = nextSignificantUnicodeScript(nextUnignorable);
            int to = nextUnignorable;
            for (int i = nextUnignorable; i < text.length(); i++) {
                int codePoint = isSurrogatePair(text, i) ? TextUtil.convertToUtf32(text, i) : (int) text.charAt(i);
                Character.UnicodeScript currScript = Character.UnicodeScript.of(codePoint);
                if (isSignificantUnicodeScript(currScript) && currScript != unicodeScript) {
                    break;
                }
                if (codePoint > 0xFFFF) i++;
                to = i;
            }

            int numOfAppendedGlyphs = font.appendGlyphs(text, index, to, glyphs);
            anyGlyphsAppended = numOfAppendedGlyphs > 0;
            assert anyGlyphsAppended;
            index += numOfAppendedGlyphs;
        }
        if (!anyGlyphsAppended) {
            font = getPdfFont(selector.bestMatch());
            if (index != nextUnignorable) {
                index += font.appendGlyphs(text, index, nextUnignorable - 1, glyphs);
            }
            while (index <= nextUnignorable && index < text.length()) {
                index += font.appendAnyGlyph(text, index, glyphs);
            }
            if (glyphs.isEmpty()) {
                if (!(nextUnignorable > 0 && isSurrogatePair(text, nextUnignorable - 1))) {
                    font.appendAnyGlyph("?", 0, glyphs);
                }
            }
        }
        return glyphs;
    }

    private int nextSignificantIndex() {
        int nextValidChar = index;
        for (; nextValidChar < text.length(); nextValidChar++) {
            if (!TextUtil.isWhitespaceOrNonPrintable(text.charAt(nextValidChar))) {
                break;
            }
        }
        return nextValidChar;
    }

    private Character.UnicodeScript nextSignificantUnicodeScript(int from) {
        for (int i = from; i < text.length(); i++) {
            int codePoint;
            if (isSurrogatePair(text, i)) {
                codePoint = TextUtil.convertToUtf32(text, i);
                i++;
            } else {
                codePoint = (int) text.charAt(i);
            }
            Character.UnicodeScript unicodeScript = Character.UnicodeScript.of(codePoint);
            if (isSignificantUnicodeScript(unicodeScript)) {
                return unicodeScript;
            }
        }
        return Character.UnicodeScript.COMMON;
    }

    private static boolean isSignificantUnicodeScript(Character.UnicodeScript unicodeScript) {
        // Character.UnicodeScript.UNKNOWN will be handled as significant unicode script
        return unicodeScript != Character.UnicodeScript.COMMON && unicodeScript != Character.UnicodeScript.INHERITED;
    }

    //This method doesn't perform additional checks if compare with TextUtil#isSurrogatePair()
    private static boolean isSurrogatePair(String text, int idx) {
        return TextUtil.isSurrogateHigh(text.charAt(idx)) && idx < text.length() - 1
                && TextUtil.isSurrogateLow(text.charAt(idx + 1));
    }
}

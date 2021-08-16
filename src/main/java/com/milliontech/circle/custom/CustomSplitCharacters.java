package com.milliontech.circle.custom;

import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.layout.splitting.ISplitCharacters;

/**
 * Modified from iText 7.1.15 BreakAllSplitCharacters.java
 * nextGlyphIsLetterOrDigit is removed
 */
public class CustomSplitCharacters implements ISplitCharacters {
	
	@Override
    public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
        if (text.size() - 1 == glyphPos) {
            return true;
        }

        Glyph glyphToCheck = text.get(glyphPos);
        if (!glyphToCheck.hasValidUnicode()) {
            return true;
        }
        int charCode = glyphToCheck.getUnicode();

        Glyph nextGlyph = text.get(glyphPos + 1);
        if (!nextGlyph.hasValidUnicode()) {
            return true;
        }

        //boolean nextGlyphIsLetterOrDigit = TextUtil.isLetterOrDigit(nextGlyph);
        boolean nextGlyphIsMark = TextUtil.isMark(nextGlyph);

        boolean currentGlyphIsDefaultSplitCharacter = charCode <= ' ' || charCode == '-' || charCode == '\u2010'
                // block of whitespaces
                || (charCode >= 0x2002 && charCode <= 0x200b);

        return (
        			currentGlyphIsDefaultSplitCharacter 
    				//|| nextGlyphIsLetterOrDigit
    				|| nextGlyphIsMark
        		)
                && !TextUtil.isNonBreakingHyphen(glyphToCheck);
    }
	
}

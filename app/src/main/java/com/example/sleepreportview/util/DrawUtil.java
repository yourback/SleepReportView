package com.example.sleepreportview.util;

import android.graphics.Paint;

public class DrawUtil {
    public static float draw_text_middle_y(Paint text_paint, float baseY) {
        Paint.FontMetrics fontMetrics = text_paint.getFontMetrics();
        return baseY + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }
}

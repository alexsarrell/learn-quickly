package com.jet.learnq;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScrollViewBuilder {
    private final Context context;

    public ScrollViewBuilder(Context context) {
        this.context = context;
    }

    public TextView setFirstLetter(char firstLetter, Typeface font, int color) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, -20, 0, -80);
        TextView textView = setTextOptions(
                (new TextView(context)),
                String.valueOf(firstLetter),
                font,
                30,
                color,
                null);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public void strokeDiv(LinearLayout layout, int image) {
        ImageView lineDiv = new ImageView(context);
        lineDiv.setImageDrawable(context.getDrawable(image));
        layout.addView(lineDiv);
    } //Исправь обрезку текста

    public void strokeDiv(LinearLayout layout, int image, int marginLeft, int marginTop, int marginRight, int marginBottom) {
        ImageView lineDiv = new ImageView(context);
        lineDiv.setImageDrawable(context.getDrawable(image));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        lineDiv.setLayoutParams(layoutParams);
        layout.addView(lineDiv);
    }

    public TextView setTextOptions(
            TextView text,
            String textValue,
            Typeface typeface,
            int textSize,
            int color,
            Drawable background) {
        if (background != null) {
            text.setBackground(background);
        }
        text.setTypeface(typeface);
        StringBuilder sb = new StringBuilder(" ");
        sb.append(textValue);
        text.setText(sb);
        text.setMaxWidth(context.getResources().getDisplayMetrics().widthPixels / 2);
        text.setTextSize(textSize);
        text.setTextColor(color);
        text.setEms(10);
        text.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        text.setPadding(5, 0, 0, 0);
        return text;
    }

}

package com.smalsus.redhorizonvbr.view;

import android.text.style.ImageSpan;

class HashTagSpan {

    private String text;
    private ImageSpan span;

    public HashTagSpan(String text, ImageSpan span) {
        this.text = text;
        this.span = span;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ImageSpan getSpan() {
        return span;
    }

    public void setSpan(ImageSpan span) {
        this.span = span;
    }
}

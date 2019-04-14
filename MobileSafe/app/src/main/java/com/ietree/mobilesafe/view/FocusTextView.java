package com.ietree.mobilesafe.view;


import android.content.Context;
import android.util.AttributeSet;

/**
 * 默认获取焦点的TextView
 */
public class FocusTextView extends android.support.v7.widget.AppCompatTextView {

    public FocusTextView(Context context) {
        super(context);
    }

    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 默认获取焦点
    @Override
    public boolean isFocused() {
        return true;
    }
}

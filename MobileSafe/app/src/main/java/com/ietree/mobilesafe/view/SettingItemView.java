package com.ietree.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ietree.mobilesafe.R;

/**
 * 重写Setting页面的相对布局，实现页面布局可复用
 */
public class SettingItemView extends RelativeLayout {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.ietree.mobilesafe";
    private TextView tv_des;
    private CheckBox cb_box;
    private String mDestitle;
    private String mDeson;
    private String mDesoff;
    private TextView tv_title;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // 将自定义的相对布局放置到Setting页面中去
        View.inflate(context, R.layout.setting_item_view, this);
        tv_title = findViewById(R.id.tv_title);
        tv_des = findViewById(R.id.tv_des);
        cb_box = findViewById(R.id.cb_box);

        // 初始化属性值
        initAttrs(attrs);
        tv_title.setText(mDestitle);
    }

    /**
     * 初始化属性值
     *
     * @param attrs 空间属性
     */
    private void initAttrs(AttributeSet attrs) {
        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
    }

    /**
     * 判断是否开启
     *
     * @return 返回设置状态，开启返回true，关闭返回false
     */
    public boolean isCheck() {
        return cb_box.isChecked();
    }

    /**
     * 更改设置状态
     *
     * @param isCheck 设置状态
     */
    public void setCheck(boolean isCheck) {
        cb_box.setChecked(isCheck);
        if (isCheck) {
            // 开启
            tv_des.setText(mDeson);
        } else {
            // 关闭
            tv_des.setText(mDesoff);
        }
    }
}

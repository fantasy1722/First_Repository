package com.skyworth.sernorvalue.widget;

import com.skyworth.sernorvalue.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class SimpleButton extends Button implements android.view.View.OnTouchListener {

    private int mButtonColor;
    private int mShadowColor;
    private int mShadowHeight;
    private int mCornerRadius;
    //Native values
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;
    //Background drawable
    private Drawable pressedDrawable;
    private Drawable unpressedDrawable;
	
	
	public SimpleButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
    private void init() {
        //Init default values
        Resources resources = getResources();
        if (resources == null) return;
        mButtonColor = resources.getColor(R.color.default_color);
        mShadowColor = resources.getColor(R.color.default_shadow_color);
        mShadowHeight = resources.getDimensionPixelSize(R.dimen.default_shadow_height);
        mCornerRadius = resources.getDimensionPixelSize(R.dimen.default_conner_radius);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
    	
    }
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
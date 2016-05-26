package com.yilvtzj.t9.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RectangleLayout extends RelativeLayout {
	public RectangleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RectangleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RectangleLayout(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// For simple implementation, or internal size is always 0.
		// We depend on the container to specify the layout size of
		// our view. We can't really know what it is since we will be
		// adding and removing different arbitrary views and do not
		// want the layout to change as this happens.
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

		// Children are just made to fill our space.
		int childWidthSize = getMeasuredWidth();
		// 高度和宽度一样
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
		heightMeasureSpec = 200;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
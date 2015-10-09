package com.grandtorino.grid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.grandtorino.grid.utils.Settings;
import com.grandtorino.grid.utils.Units;

public class DragView extends View {

	private int color;
	private float size;

	public DragView(Context context) {
		this(context, null);
	}

	public DragView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public void init() {
		Settings settings = new Settings(getContext());
		setColor(settings.getDragColor());
		setOpacity(settings.getOpacity());
		setSize(settings.getDragSizeValue(), settings.getDragSizeUnits());
	}

	public void setColor(int newColor) {
		color = (color & 0xff000000) | (newColor & 0x00ffffff);
		invalidate();
	}

	public void setOpacity(int opacity) {
		color = (opacity << 24) | (color & 0x00ffffff);
		invalidate();
	}
	
	public void setSize(float value, Units units) {
		size = units.toPixels(getContext(), value);
		requestLayout();
		invalidate();
	}
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension((int) size, (int) size);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(color);
	}
	
}
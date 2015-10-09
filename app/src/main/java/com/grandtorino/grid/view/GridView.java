package com.grandtorino.grid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.grandtorino.grid.utils.Settings;
import com.grandtorino.grid.utils.Units;

public class GridView extends View {

	private float step;
	private float i;
	private float startX = 0;
	private float startY = 0;
	
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public GridView(Context context) {
		this(context, null, 0);
	}

	public GridView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint.setStyle(Paint.Style.STROKE);
		init();
	}
	
	public void init() {
		Settings settings = new Settings(getContext());
		setOpacity(settings.getOpacity());
		setGridLineColor(settings.getGridLineColor());
		setGridStep(settings.getGridStepValue(), settings.getGridStepUnits());
		setGridLineWidth(settings.getGridLineWidthValue(), settings.getGridLineWidthUnits());
	}
	
	public void setGridLineWidth(float value, Units units) {
		paint.setStrokeWidth(units.toPixels(getContext(), value));
		invalidate();
	}

	public void setGridStep(float value, Units units) {
		step = units.toPixels(getContext(), value);
		invalidate();
	}
	
	public void setGridLineColor(int color) {
		paint.setColor((paint.getColor() & 0xff000000) | (color & 0x00ffffff));
		invalidate();
	}

	public void setOpacity(int opacity) {
		paint.setAlpha(opacity);
		invalidate();
	}

	public void moveStartPosition(float deltaX, float deltaY) {
		startX = (startX + deltaX) % step;
		startY = (startY + deltaY) % step;
		invalidate();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		i = startX;
		while (i <= width) {
			if (i >= 0)
				canvas.drawLine(i, 0, i, height, paint);
			i += step;
		}
		i = startY;
		while (i <= height) {
			if (i >= 0)
				canvas.drawLine(0, i, width, i, paint);
			i += step;
		}
	}

}
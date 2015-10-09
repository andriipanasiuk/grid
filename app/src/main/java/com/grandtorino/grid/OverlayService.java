package com.grandtorino.grid;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.grandtorino.grid.utils.Settings;
import com.grandtorino.grid.utils.Units;
import com.grandtorino.grid.view.DragView;
import com.grandtorino.grid.view.GridView;

public class OverlayService extends Service {

	private static final int NOTIFICATION_ID = 123;

	private WindowManager windowManager;
	private WindowManager.LayoutParams gridViewlayoutParams;
	private WindowManager.LayoutParams dragViewlayoutParams;

	private GridView gridView;
	private DragView dragView;

	private boolean isForeground = false;
	private boolean isDraggable = false;
	
	private float lastMotionX;
	private float lastMotionY;
	private boolean isDragging = false;

	private final ServiceBinder binder = new ServiceBinder();
	
	public class ServiceBinder extends Binder {

		public OverlayService getService() {
			return OverlayService.this;
		}

	}
	
	@Override
	public void onCreate() {
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		
		gridViewlayoutParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
					| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
					| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				PixelFormat.TRANSLUCENT);
		gridViewlayoutParams.gravity = Gravity.FILL;
		
		dragViewlayoutParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
					| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
					| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		dragViewlayoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		
		gridView = new GridView(this);
		dragView = new DragView(this);
		dragView.setOnTouchListener(dragViewTouchListener);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public boolean isForeground() {
		return isForeground;
	}
	
	public void show(boolean draggable) {
		if (isForeground) return;
		isForeground = true;
		startForeground(NOTIFICATION_ID, prepareNotification());
		gridView.init();
		windowManager.addView(gridView, gridViewlayoutParams);
		if (draggable) showDrag();
	}

	public void hide() {
		if (!isForeground) return;
		hideDrag();
		windowManager.removeView(gridView);
		isForeground = false;
		stopForeground(true);
	}
	
	public void setGridStep(float value, Units units) {
		if (!isForeground) return;
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, prepareNotification());
		gridView.setGridStep(value, units);
	}
	
	public void setGridLineWidth(float value, Units units) {
		if (!isForeground) return;
		gridView.setGridLineWidth(value, units);
	}
	
	public void setGridLineColor(int color) {
		if (!isForeground) return;
		gridView.setGridLineColor(color);
	}
	
	public void setOpacity(int opacity) {
		if (!isForeground) return;
		gridView.setOpacity(opacity);
		if (isDraggable) {
			dragView.setOpacity(opacity);
		}
	}
	
	public void setDragSize(float value, Units units) {
		if (isDraggable) {
			dragView.setSize(value, units);
		}
	}
	
	public void setDragColor(int color) {
		if (isDraggable) {
			dragView.setColor(color);
		}
	}
	
	public void showDrag() {
		if (!isForeground) return;
		isDraggable = true;
		dragView.init();
		windowManager.addView(dragView, dragViewlayoutParams);
	}
	
	public void hideDrag() {
		if (isDraggable) {
			windowManager.removeView(dragView);
			isDraggable = false;
		}
	}

	@SuppressLint("InlinedApi")
	private Notification prepareNotification() {
		Settings settings = new Settings(this);
		float gridStepValue = settings.getGridStepValue();
		String gridStepUnitsName = settings.getGridStepUnits().getName();
		
		String gridStepValueString;
		if (gridStepValue == (int) gridStepValue) {
			gridStepValueString = String.format(Locale.getDefault(), "%d", (int) gridStepValue);
		} else {
			gridStepValueString = String.format(Locale.getDefault(), "%f", gridStepValue);
		}
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_stat_grid);
		builder.setContentIntent(pendingIntent);
		builder.setAutoCancel(false);
		builder.setOngoing(true);
		builder.setTicker(getString(R.string.notification_ticker_grid_on));
		builder.setContentTitle(getString(R.string.notification_title_grid_step_size, gridStepValueString, gridStepUnitsName));
		builder.setContentText(getString(R.string.notification_message_settings));
		return builder.build();
	}
	
	private OnTouchListener dragViewTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (!isDraggable) return false;
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					lastMotionX = event.getRawX();
					lastMotionY = event.getRawY();
					int[] location = new int[2];
					dragView.getLocationOnScreen(location);
					RectF dragRect = new RectF(location[0], location[1], location[0] + dragView.getWidth(), location[1] + dragView.getHeight());
					if (dragRect.contains(lastMotionX, lastMotionY)) {
						isDragging = true;
						return true;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (isDragging) {
						float deltaX = event.getRawX() - lastMotionX;
						float deltaY = event.getRawY() - lastMotionY;
						if (deltaX != 0 || deltaY != 0) {
							gridView.moveStartPosition(deltaX, deltaY);
						}
						lastMotionX = event.getRawX();
						lastMotionY = event.getRawY();
						return true;
					}
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					if (isDragging) {
						isDragging = false;
						return true;
					}
					break;
			}
			return false;
		}
		
	};

}
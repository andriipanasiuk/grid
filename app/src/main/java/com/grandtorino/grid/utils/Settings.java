package com.grandtorino.grid.utils;

import com.grandtorino.grid.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class Settings {

	public static final String PREF_GRID_STEP_VALUE = "grid_step_value";
	public static final String PREF_GRID_STEP_UNITS = "grid_step_units";
	public static final String PREF_GRID_LINE_WIDTH_VALUE = "grid_line_width_value";
	public static final String PREF_GRID_LINE_WIDTH_UNITS = "grid_line_width_units";
	public static final String PREF_GRID_LINE_COLOR = "grid_line_color";

	public static final String PREF_DRAG_ENABLED = "drag_enabled";
	public static final String PREF_DRAG_SIZE_VALUE = "drag_size_value";
	public static final String PREF_DRAG_SIZE_UNITS = "drag_size_units";
	public static final String PREF_DRAG_COLOR = "drag_color";
	
	public static final String PREF_OPACITY = "opacity";
	
	private final SharedPreferences preferences;
	private final Resources resources;
	
	public Settings(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		resources = context.getResources();
	}
	
	public float getGridStepValue() {
		return preferences.getFloat(PREF_GRID_STEP_VALUE, resources.getInteger(R.integer.default_grid_step_value));
	}
	
	public Units getGridStepUnits() {
		return Units.byName(preferences.getString(PREF_GRID_STEP_UNITS, resources.getString(R.string.default_grid_step_units)));
	}
	
	public float getGridLineWidthValue() {
		return preferences.getFloat(PREF_GRID_LINE_WIDTH_VALUE, resources.getInteger(R.integer.default_grid_line_width_value));
	}
	
	public Units getGridLineWidthUnits() {
		return Units.byName(preferences.getString(PREF_GRID_LINE_WIDTH_UNITS, resources.getString(R.string.default_grid_line_width_units)));
	}
	
	public int getGridLineColor() {
		return preferences.getInt(PREF_GRID_LINE_COLOR, resources.getColor(R.color.default_grid_line_color));
	}
	
	public boolean getDragEnabled() {
		return preferences.getBoolean(PREF_DRAG_ENABLED, resources.getBoolean(R.bool.default_drag_enabled));
	}
	
	public int getDragColor() {
		return preferences.getInt(PREF_DRAG_COLOR, resources.getColor(R.color.default_drag_color));
	}
	
	public float getDragSizeValue() {
		return preferences.getFloat(PREF_DRAG_SIZE_VALUE, resources.getInteger(R.integer.default_drag_size_value));
	}
	
	public Units getDragSizeUnits() {
		return Units.byName(preferences.getString(PREF_DRAG_SIZE_UNITS, resources.getString(R.string.default_drag_size_units)));
	}
	
	public int getOpacity() {
		return preferences.getInt(PREF_OPACITY, resources.getInteger(R.integer.default_opacity));
	}
	
	public void setGridStep(float value, Units units) {
		preferences.edit()
			.putFloat(PREF_GRID_STEP_VALUE, value)
			.putString(PREF_GRID_STEP_UNITS, units.getName())
			.commit();
	}
	
	public void setLineWidth(float value, Units units) {
		preferences.edit()
			.putFloat(PREF_GRID_LINE_WIDTH_VALUE, value)
			.putString(PREF_GRID_LINE_WIDTH_UNITS, units.getName())
			.commit();
	}
	
	public void setLineColor(int color) {
		preferences.edit().putInt(PREF_GRID_LINE_COLOR, color).commit();
	}
	
	public void setDragEnabled(boolean enabled) {
		preferences.edit().putBoolean(PREF_DRAG_ENABLED, enabled).commit();
	}
	
	public void setDragSize(float value, Units units) {
		preferences.edit()
			.putFloat(PREF_DRAG_SIZE_VALUE, value)
			.putString(PREF_DRAG_SIZE_UNITS, units.getName())
			.commit();
	}
	
	public void setOpacity(int opacity) {
		preferences.edit().putInt(PREF_OPACITY, opacity).commit();
	}
	
}
package com.grandtorino.grid.utils;

import android.content.Context;
import android.util.TypedValue;

public enum Units {
	
	DP (TypedValue.COMPLEX_UNIT_DIP, "dp"),
	PX (TypedValue.COMPLEX_UNIT_PX, "px"),
	MM (TypedValue.COMPLEX_UNIT_MM, "mm"),
	IN (TypedValue.COMPLEX_UNIT_IN, "in"),
	PT (TypedValue.COMPLEX_UNIT_PT, "pt");
	
	private final int unitType;
	private final String unitName;
	
	private Units(int unitType, String unitName) {
		this.unitType = unitType;
		this.unitName = unitName;
	}
	
	public String getName() {
		return unitName;
	}
	
	public int getType() {
		return unitType;
	}
	
	public float toPixels(Context context, float value) {
		return TypedValue.applyDimension(unitType, value, context.getResources().getDisplayMetrics());
	}

	public static Units byName(String name) {
		for (Units unit : Units.values())
			if (name != null && name.equals(unit.unitName)) return unit;
		return null;
	}
	
	public int getPosition() {
		for (int i = 0; i < Units.values().length; i++)
			if (unitName != null && unitName.equals(Units.values()[i].unitName)) return i;
		return -1;
	}
	
}
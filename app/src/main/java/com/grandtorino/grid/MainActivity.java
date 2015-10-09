package com.grandtorino.grid;

import java.util.Locale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.grandtorino.grid.utils.Settings;
import com.grandtorino.grid.utils.Units;

public class MainActivity extends Activity {

	private Button toggleGridButton;
	private EditText gridStepValueEditText;
	private Spinner gridStepUnitsSpinner;
	private SeekBar opacitySeekBar;
	private CheckBox dragCheckBox;

	private Intent overlayServiceIntent;
	private OverlayService overlayService;
	private boolean serviceBound = false;

	private Settings settings;

	private float stepValue;
	private Units stepUnits;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		overlayServiceIntent = new Intent(this, OverlayService.class);
		startService(overlayServiceIntent);

		toggleGridButton = (Button) findViewById(R.id.main_togglegrid_button);
		gridStepValueEditText = (EditText) findViewById(R.id.main_gridstepvalue_edittext);
		gridStepUnitsSpinner = (Spinner) findViewById(R.id.main_gridstepunits_spinner);
		opacitySeekBar = (SeekBar) findViewById(R.id.main_opacity_seekbar);
		dragCheckBox = (CheckBox) findViewById(R.id.main_drag_checkbox);

		gridStepValueEditText.addTextChangedListener(stepValueTextWatcher);
		toggleGridButton.setOnClickListener(toggleButtonClickListener);
		opacitySeekBar.setOnSeekBarChangeListener(opacityChangedListener);
		dragCheckBox.setOnCheckedChangeListener(dragChangedListener);

		String[] unitsNames = new String[Units.values().length];
		for (int i = 0; i < unitsNames.length; i++)
			unitsNames[i] = Units.values()[i].getName();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, unitsNames);
		gridStepUnitsSpinner.setAdapter(adapter);
		gridStepUnitsSpinner.setOnItemSelectedListener(stepUnitsSelectedListener);

		settings = new Settings(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		bindService(overlayServiceIntent, serviceConnection, 0);
		
		opacitySeekBar.setProgress(settings.getOpacity());
		dragCheckBox.setChecked(settings.getDragEnabled());
		
		stepValue = settings.getGridStepValue();
		stepUnits = settings.getGridStepUnits();
		gridStepValueEditText.setText(formatValue(stepValue));
		gridStepUnitsSpinner.setSelection(stepUnits.getPosition());
	}

	@Override
	public void onStop() {
		super.onStop();
		if (serviceBound) {
			unbindService(serviceConnection);
			serviceBound = false;
		}
	}

	private String formatValue(float value) {
		if (value == (int) value) {
			return String.format(Locale.getDefault(), "%d", (int) value);
		} else {
			return String.format(Locale.getDefault(), "%f", value);
		}
	}

	private boolean isGridVisible() {
		return serviceBound && overlayService != null && overlayService.isForeground();
	}

	private void updateToggleButtonText() {
		toggleGridButton.setText(isGridVisible() ? R.string.main_hidegrid_button : R.string.main_showgrid_button);
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			overlayService = ((OverlayService.ServiceBinder) service).getService();
			serviceBound = true;
			updateToggleButtonText();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBound = false;
		}

	};
	
	private View.OnClickListener toggleButtonClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!serviceBound || overlayService == null) return;
			if (isGridVisible()) {
				overlayService.hide();
			} else {
				overlayService.show(dragCheckBox.isChecked());
			}
			updateToggleButtonText();
		}
		
	};

	private TextWatcher stepValueTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			try {
				float newValue = Float.parseFloat(s.toString());
				if (newValue != stepValue && newValue > 0) {
					stepValue = newValue;
					settings.setGridStep(stepValue, stepUnits);
					if (isGridVisible()) {
						overlayService.setGridStep(stepValue, stepUnits);
					}
				}
			} catch (NumberFormatException e) {}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void afterTextChanged(Editable s) {}

	};
	
	private AdapterView.OnItemSelectedListener stepUnitsSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			int oldPosition = stepUnits.getPosition();
			if (oldPosition != position) {
				stepUnits = Units.values()[position];
				settings.setGridStep(stepValue, stepUnits);
				if (isGridVisible()) {
					overlayService.setGridStep(stepValue, stepUnits);
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}

	};

	private SeekBar.OnSeekBarChangeListener opacityChangedListener = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			settings.setOpacity(progress);
			if (isGridVisible()) {
				overlayService.setOpacity(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}

	};

	private CheckBox.OnCheckedChangeListener dragChangedListener = new CheckBox.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			settings.setDragEnabled(isChecked);
			if (isGridVisible()) {
				if (isChecked) {
					overlayService.showDrag();
				} else {
					overlayService.hideDrag();
				}
			}
		}

	};

}
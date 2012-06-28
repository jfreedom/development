package com.jfreedom.AndroidPlot;

import java.util.List;
import java.util.Vector;
import java.lang.Math;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AndroidPlotActivity extends Activity {
	/** Called when the activity is first created. */
	AndroidPlot ap;
	AndroidPlotView apv;
	private static final String TAG = "AndroidPlotActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		apv = (AndroidPlotView) findViewById(R.id.androidPlotView);

		Log.d(TAG, "Test");
		List<Double> x = new Vector<Double>();
		List<Double> y = new Vector<Double>();

		for (double ix = -2*Math.PI; ix < 2*Math.PI; ix += .001) {
			x.add(Double.valueOf(ix));
			y.add(Double.valueOf(Math.pow(Math.cos(ix),1)));
		}

		ap = new AndroidPlot(x, y);
		apv.addPlot(ap);
		apv.invalidate();

	}

}
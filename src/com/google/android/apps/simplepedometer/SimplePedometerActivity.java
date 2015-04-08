/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.simplepedometer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class SimplePedometerActivity extends Activity implements SensorEventListener, StepListener {
  private TextView textView;
  private SimpleStepDetector simpleStepDetector;
  private SensorManager sensorManager;
  private Sensor accel;
  private static final String TEXT_NUM_STEPS = "Steps: ";
  private int numSteps;
  
  private GraphView graphView;
  private GraphViewSeries dataSeries;
  private int dataCounter = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    
    textView = new TextView(this);
    textView.setTextSize(30);
    layout.addView(textView);
    
    dataSeries = new GraphViewSeries(new GraphViewData[] {});
    dataCounter = 0;
    graphView = new LineGraphView(
        this // context
        , "" // heading
    );
    graphView.addSeries(dataSeries); // data
    graphView.setScalable(true);
    graphView.setScrollable(true);
    // graphView.setShowHorizontalLabels(false);
    graphView.getGraphViewStyle().setNumHorizontalLabels(8);
    graphView.getGraphViewStyle().setNumVerticalLabels(8);
    graphView.setViewPort(0, 1024);
    layout.addView(graphView);
    
    setContentView(layout);

    // Get an instance of the SensorManager
    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    simpleStepDetector = new SimpleStepDetector();
    simpleStepDetector.registerListener(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    numSteps = 0;
    textView.setText(TEXT_NUM_STEPS + numSteps);
    sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
  }

  @Override
  public void onPause() {
    super.onPause();
    sensorManager.unregisterListener(this);
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      simpleStepDetector.updateAccel(
          event.timestamp, event.values[0], event.values[1], event.values[2]);
    }
  }
  
  @Override
  public void step(long timeNs) {
    numSteps++;
    textView.setText(TEXT_NUM_STEPS + numSteps);
  }
  
  @Override
  public void probe(float value) {
	  dataCounter++;
	  GraphViewData d = new GraphViewData(dataCounter, value);
	  dataSeries.appendData(d, true, 1024);
  }

}

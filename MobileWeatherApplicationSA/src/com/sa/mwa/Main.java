package com.sa.mwa;

import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity implements SensorListener{

	private final String PEER_SERVICE_NAME = "com.sa.mwa.PEER_SERVICE";
	private GuiNotifyTemperatureChanged guiListener;
	private LogNotifyTemperatureChanged logListener;
	private PeerServiceConnector peerServiceConnection;
	
	private SensorManager sensorManager;
	
	TextView lbl_temperature;
	Button btn_change;
	
	private void establishServiceConnection()
	{
		//listeners for peer service
		guiListener = new GuiNotifyTemperatureChanged(handler);
		logListener = new LogNotifyTemperatureChanged();
		List<INotifyTemperatureChanged> listeners = new ArrayList<INotifyTemperatureChanged>();
		listeners.add(guiListener);
		listeners.add(logListener);
		
		//establishing connection to peer service
		peerServiceConnection = new PeerServiceConnector(listeners);
		
		//binding connection to service
		bindService(new Intent(IPeerRemoteService.class.getName()), peerServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void uiElementInitializing()
	{
		lbl_temperature = (TextView) findViewById(R.id.lbl_temperature);
		
		btn_change = (Button) findViewById(R.id.btn_change);
		btn_change.setOnClickListener(btn_change_onClick);
	}
	
	private Button.OnClickListener btn_change_onClick = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			
			
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//preparing connection to peer service
		establishServiceConnection();
		
		//ui element initialization
		uiElementInitializing();
		
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

	}
	
	@Override
	protected void onDestroy() {
		
		if (peerServiceConnection != null)
			unbindService(peerServiceConnection);
	}
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what)
			{
				case PeerService.TEMPERATURE_MESSAGE:
				{
					Temperature temperature = (Temperature) msg.obj;
					lbl_temperature.setText(Double.valueOf(temperature.getValue()).toString());
				}
				break;
				default:
					super.handleMessage(msg);
			}
		};
	};

	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void onAccuracyChanged(Sensor sensor, int accuracy) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onSensorChanged(SensorEvent event) {
//		// TODO Auto-generated method stub
//		if (event.sensor.getType() == Sensor.TYPE_TEMPERATURE)
//		{
//			 
//		}
// 	}
//	
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
//		sensorManager.registerListener(this, sensorList.get(0), SensorManager.SENSOR_DELAY_NORMAL);
//	}
}

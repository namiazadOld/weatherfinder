package com.sa.mwa;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Activity {

	private GuiNotifyTemperatureChanged guiListener;
	private LogNotifyTemperatureChanged logListener;
	private PeerServiceConnector peerServiceConnection;
	
	TextView lbl_temperature, lbl_location, lbl_status;
	Button btn_change;
	int i = 0;
	Chat chat;
	
	private void establishServiceConnection()
	{
		//listeners for peer service
		guiListener = new GuiNotifyTemperatureChanged(handler);
		logListener = new LogNotifyTemperatureChanged();
		List<INotifyValueChanged> listeners = new ArrayList<INotifyValueChanged>();
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
		lbl_location = (TextView) findViewById(R.id.lbl_location);
		lbl_status = (TextView)findViewById(R.id.lbl_status);
		
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
	}
	
	@Override
	protected void onDestroy() {
		
//		if (peerServiceConnection != null)
//			unbindService(peerServiceConnection);
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
				case PeerService.QUERY_MESSAGE:
				{
					String content = (String) msg.obj;
					lbl_location.setText(content);
				}
				break;
				case PeerService.CONNECTION_TO_CHAT_SERVER_ESTABLISHED:
				{
					lbl_status.setText("Connected");
				}
				break;
				case PeerService.CONNECTION_TO_CHAT_SERVER_FAILED:
				{
					lbl_status.setText("Connection Failed");
				}
				break;
				default:
					super.handleMessage(msg);     
			}   
		};
	};
}

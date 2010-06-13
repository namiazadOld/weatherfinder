package com.sa.mwa;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private GuiNotifyTemperatureChanged guiListener;
	private LogNotifyTemperatureChanged logListener;
	private PeerServiceConnector peerServiceConnection;
	private ConnectionStatus connectionStatus;
	
	TextView lbl_temperature, lbl_location, lbl_status;
	Button btn_setting;
	int i = 0;
	Chat chat;
	Login dlg_login;
	
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
		lbl_status.setOnClickListener(lbl_status_onClick);
		
		btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(btn_setting_onClick);
		
		dlg_login = new Login(this);
	}
	
	private void initializeEnvironmentParameter()
	{
		connectionStatus = ConnectionStatus.Disconnected;
		
		
		
	}
	
	private Button.OnClickListener btn_setting_onClick = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			try {
				peerServiceConnection.getRemoteService().findWeather("DELFT");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			
		}
		
		
	};
	
	private TextView.OnClickListener lbl_status_onClick = new TextView.OnClickListener(){

		@Override
		public void onClick(View v) {
			switch (connectionStatus)
			{
				case Disconnected:
				{
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							dlg_login.Prepare(peerServiceConnection);
							dlg_login.show();							
						}
					});
				}break;
				case Connected:
				{
					Thread thread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								peerServiceConnection.getRemoteService().disconnect();
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					});
					
					thread.start();
					
				}break;
				default:
			}
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
		
		//initializing environment parameters
		initializeEnvironmentParameter();
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
				case PeerService.QUERY_RESULT:
				{
					String content = (String) msg.obj;
					lbl_location.setText(content);
				}break;
				case PeerService.QUERY_MESSAGE:
				{
					String content = (String) msg.obj;
					lbl_location.setText(content);
				}
				break;
				case PeerService.CONNECTION_TO_CHAT_SERVER_ESTABLISHED:
				{
					connectionStatus = ConnectionStatus.Connected;
					lbl_status.setText("Connected.");
				}
				break;
				case PeerService.CONNECTION_TO_CHAT_SERVER_FAILED:
				{
					connectionStatus = ConnectionStatus.Disconnected;
					lbl_status.setText("Connection Failed.");
				}
				break;
				case PeerService.CONNECTION_TO_CHAT_SERVER_PROCESSING:
				{
					connectionStatus = ConnectionStatus.Connecting;
					lbl_status.setText("Connecting...");
				}
				break;
				case PeerService.CONNECTION_TO_CHAT_SERVER_DISCONNECTED:
				{
					connectionStatus = ConnectionStatus.Disconnected;
					lbl_status.setText("Not Connected!");
				}
				break;
				case PeerService.EXCEPTION_OCCURED:
				{
					Toast.makeText(getBaseContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
				}
				break;
				default:
					super.handleMessage(msg);     
			}   
		};
	};
}

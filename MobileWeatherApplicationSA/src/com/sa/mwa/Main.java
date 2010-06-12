package com.sa.mwa;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.ChatState;
import org.jivesoftware.smackx.ChatStateListener;

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

public class Main extends Activity implements ChatManagerListener, ChatStateListener {

	private final String PEER_SERVICE_NAME = "com.sa.mwa.PEER_SERVICE";
	private GuiNotifyTemperatureChanged guiListener;
	private LogNotifyTemperatureChanged logListener;
	private PeerServiceConnector peerServiceConnection;
	
	TextView lbl_temperature, lbl_location;
	Button btn_change;
	
	Chat chat;
	
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
		lbl_location = (TextView) findViewById(R.id.lbl_location);
		
		btn_change = (Button) findViewById(R.id.btn_change);
		btn_change.setOnClickListener(btn_change_onClick);
	}
	
	private Button.OnClickListener btn_change_onClick = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			
			try {
				chat.sendMessage("From All");
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		
		
		/////////////////////
		try {
			ConnectionConfiguration config = new ConnectionConfiguration("jabber.org", 5222, "jabber.org");
			XMPPConnection connection = new XMPPConnection(config);
			connection.connect();
			System.out.println("Connected!");
			connection.login("all_mwa_users", "Intermilan1");
			
			chat = connection.getChatManager().createChat("all_mwa_users@Jabber.org", new MessageListener() {
				
				@Override 
				public void processMessage(Chat chat, Message message) {
					//lbl_location.setText(message.toString()); 
					handler.sendMessage(handler.obtainMessage(PeerService.QUERY_MESSAGE, message.getBody().toString()));
					//System.out.println("Received message: " + message);   
					
				}
			});
			
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				case PeerService.QUERY_MESSAGE:
				{
					String content = (String) msg.obj;
					lbl_location.setText(content);
				}
				break;
				default:
					super.handleMessage(msg);     
			}   
		};
	};

	@Override
	public void chatCreated(Chat arg0, boolean arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChanged(Chat arg0, ChatState arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void processMessage(Chat arg0, Message arg1) {
		// TODO Auto-generated method stub
		
	}
}

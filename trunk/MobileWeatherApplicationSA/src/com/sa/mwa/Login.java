package com.sa.mwa;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login extends Dialog {

	Main mwaClient;
	Button btn_login;
	
	public Login(Main mwaClient) {
		super(mwaClient);
		this.mwaClient = mwaClient;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(btn_login_onClick);
	}
	
	private Button.OnClickListener btn_login_onClick = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			dismiss();
		}
		
	};

}

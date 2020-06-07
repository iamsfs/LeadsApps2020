package com.ics.freecashregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ConfirmationActivity extends Activity {
	private TextView action_next;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation);

		if(action_next == null){
			action_next = (TextView)findViewById(R.id.action_next);}
	}
	
	
	@Override
	protected void onStart(){
		super.onStart();
		action_next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(ConfirmationActivity.this, HomeActivity.class);
				finish();startActivity(i);
			}
		});
	}
	

}

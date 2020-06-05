package com.threeaspen.merchant.pro;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.threeaspen.merchant.pro.helper.ShowNotification;

public class SignInActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String TAG = "CCR - SignIn";
	
	private TextView action_next, action_cancel, action_call;
	private EditText ln_username, ln_password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		
		if(ln_username == null){
			ln_username = (EditText)findViewById(R.id.ln_username);}
		if(ln_password == null){
			ln_password = (EditText)findViewById(R.id.ln_password);}

		if(action_next == null){
			action_next = (TextView)findViewById(R.id.action_next);}
		if (action_call == null) {
			action_call = (TextView) findViewById(R.id.action_call);
		}
		if(action_cancel == null){
			action_cancel = (TextView)findViewById(R.id.action_cancel);}
	}
	
	
	@Override
	protected void onStart(){
		super.onStart();
		action_next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(validateForm()){
					//Intent i = new Intent(SignInActivity.this, HomeActivity.class);
					//finish();startActivity(i);
					ShowNotification.showErrorDialog(SignInActivity.this, "This username or password is incorrect, please try again.");
                }
			}
		});
		action_call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Uri uri = Uri.parse("tel:18003353303");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		action_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private boolean validateForm(){
		boolean errorNotFound = true;

		if (TextUtils.isEmpty(ln_username.getText().toString()) ||
				TextUtils.isEmpty(ln_password.getText().toString())) {
			errorNotFound = false;
			ShowNotification.showErrorDialog(SignInActivity.this, "Please enter a User ID and Password to continue.");
			//Toast.makeText(SignInActivity.this, "Please enter a User ID and Password to continue.", Toast.LENGTH_SHORT).show();
        }
		
		return errorNotFound;
	}
}

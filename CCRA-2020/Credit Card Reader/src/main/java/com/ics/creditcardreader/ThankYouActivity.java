package com.ics.creditcardreader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ThankYouActivity extends Activity {
	private TextView action_next, call_now;

	private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			((TextView)findViewById(R.id.text_view_response)).setText(intent.getStringExtra("test"));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thankyou);

        registerReceiver(mBroadCastReceiver,new IntentFilter("test"));


        if(action_next == null){
			action_next = (TextView)findViewById(R.id.action_next);}

		 if (call_now == null) {
			 call_now = (TextView) findViewById(R.id.call_now);
		 }



	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadCastReceiver);
		super.onDestroy();
	}

	@Override
	protected void onStart(){
		super.onStart();
		action_next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(ThankYouActivity.this, HomeActivity.class);
				startActivity(i);
			}
		});

		call_now.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
				callIntent.setData(Uri.parse("tel:18003353303"));
				startActivity(callIntent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent(ThankYouActivity.this, HomeActivity.class);
		startActivity(i);
	}

}

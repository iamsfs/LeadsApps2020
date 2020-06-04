package com.pattrns.toycashregister;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pattrns.toycashregister.utility.Utility;

public class HomeActivity extends Activity {
    private ImageView action_sign_in, action_sign_up, action_pricing;
    private ImageView action_demo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //openFistTimeActivity();

        if (action_sign_in == null) {
            action_sign_in = (ImageView) findViewById(R.id.sign_in);
        }
        if (action_sign_up == null) {
            action_sign_up = (ImageView) findViewById(R.id.sign_up);
        }
        if (action_demo == null) {
            action_demo = (ImageView) findViewById(R.id.demo);
        }
        if (action_pricing == null) {
            action_pricing = (ImageView) findViewById(R.id.pricing);
        }
    }

    @SuppressWarnings("unused")
    private void openFistTimeActivity() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        if (prefs.getBoolean("APP_JUST_LOADED", true)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("APP_JUST_LOADED", false);
            editor.commit();

            Intent i = new Intent(HomeActivity.this, SignUpActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        action_sign_in.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, SignInActivity.class);
                startActivity(i);
                //sendEmail();
            }
        });
        action_sign_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, SignUpActivity.class);
                setFlowKey(1);
                startActivity(i);
            }
        });
        action_demo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, CreateSaleActivity.class);
                startActivity(i);
            }
        });
        action_pricing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, PricingActivity.class);
                setFlowKey(2);
                startActivity(i);
            }
        });
    }

    private void setFlowKey(int num) {
        Utility.setFlow(this, num);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        System.exit(0);
    }
}

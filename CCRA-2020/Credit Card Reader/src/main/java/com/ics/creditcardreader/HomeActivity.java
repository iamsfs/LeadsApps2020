package com.ics.creditcardreader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ics.creditcardreader.utility.Utility;
import com.parse.Parse;
import com.parse.ParseObject;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class HomeActivity extends Activity {
    private TextView action_sign_in, action_sign_up, action_demo, action_pricing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //openFistTimeActivity();

        if (action_sign_in == null) {
            action_sign_in = (TextView) findViewById(R.id.sign_in);
        }
        if (action_sign_up == null) {
            action_sign_up = (TextView) findViewById(R.id.sign_up);
        }
        if (action_demo == null) {
            action_demo = (TextView) findViewById(R.id.demo);
        }
        if (action_pricing == null) {
            action_pricing = (TextView) findViewById(R.id.pricing);
        }

        setVersionNumber();
    }

    private void setVersionNumber() {
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

            ((TextView) findViewById(R.id.text_view_version_number)).setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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

package com.ics.freecashregister;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ics.freecashregister.helper.MySSLSocketFactory;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProcessingActivity extends Activity {
    private TextView action_yes, action_no, action_cancel;
    private String verificationCode;
    public static ProcessingActivity processingActivity;
    public ProgressDialog progressDialog;
    String tocken_id = "";

    private String source = "", card_processing = "";


    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        processingActivity = this;
        progressDialog = new ProgressDialog(ProcessingActivity.this);
        if (action_yes == null) {
            action_yes = (TextView) findViewById(R.id.action_yes);
        }

        if (action_no == null) {
            action_no = (TextView) findViewById(R.id.action_no);
        }

        if (action_cancel == null) {
            action_cancel = (TextView) findViewById(R.id.action_cancel);
        }
        if (getIntent().hasExtra("VERIFICATION_CODE")) {
            verificationCode = getIntent().getStringExtra("VERIFICATION_CODE");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        action_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                source = getString(R.string.source_and_class_name) + "Y";
                card_processing = "Yes";
                progressDialog = new ProgressDialog(ProcessingActivity.this);
                progressDialog.setMessage("Please wait..");

                updateDataOnParse();

            }
        });
        action_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                source = getString(R.string.source_and_class_name);
                card_processing = "No";
                progressDialog = new ProgressDialog(ProcessingActivity.this);
                progressDialog.setMessage("Please wait..");

                updateDataOnParse();

            }
        });

//        action_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
    }

    private void updateDataOnParse() {
        progressDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getResources().getString(R.string.source_and_class_name));
        query.getInBackground(getIntent().getStringExtra("objectId"), new GetCallback<ParseObject>() {
            public void done(ParseObject entity, ParseException e) {
                if (e == null) {
                    entity.put("Processing", card_processing);
                    entity.saveInBackground();
                    progressDialog.dismiss();
                    startNewActivity();
                } else {
                    Toast.makeText(ProcessingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startNewActivity() {
        ParseObject query = new ParseObject(getString(R.string.source_and_class_name));
        query.put("Name", getIntent().getStringExtra("first_name"));
        query.put("LastName", getIntent().getStringExtra("last_name"));
        query.put("BusinessName", getIntent().getStringExtra("company"));
        query.put("Email", getIntent().getStringExtra("email_address"));
        query.put("Phone", getIntent().getStringExtra("mobile_phone"));
        query.put("Processing", card_processing);
        query.put("source", source);
        BuisnessInfoActivity.start(this, query, getIntent().getStringExtra("objectId"), (ParseObject) getIntent().getExtras().get("pricingObject"));
    }
}
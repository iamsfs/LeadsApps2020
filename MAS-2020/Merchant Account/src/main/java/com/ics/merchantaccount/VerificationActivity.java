package com.ics.merchantaccount;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ics.merchantaccount.helper.MySSLSocketFactory;
import com.ics.merchantaccount.helper.ShowNotification;
import com.ics.merchantaccount.utility.Utility;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
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

public class VerificationActivity extends Activity {
    private static final String TAG = "MAS Verification";
    private TextView action_next, action_back, action_call, action_cancel;
    private EditText text_verification;
    public ProgressDialog progressDialog;
    private String verificationCode;
    public String tocken_id;
    private String objectId = "";

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        if (action_next == null) {
            action_next = (TextView) findViewById(R.id.action_next);
        }
        if (action_back == null) {
            action_back = (TextView) findViewById(R.id.action_back);
        }
        if (action_call == null) {
            action_call = (TextView) findViewById(R.id.action_call);
        }
        if (action_cancel == null) {
            action_cancel = (TextView) findViewById(R.id.action_cancel);
        }
        if (text_verification == null) {
            text_verification = (EditText) findViewById(R.id.text_verification);
        }

        if (getIntent().hasExtra("VERIFICATION_CODE")) {
            verificationCode = getIntent().getStringExtra("VERIFICATION_CODE");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        action_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isNetworkAvailable(VerificationActivity.this)) {
                    if (validateForm()) {
                        progressDialog = new ProgressDialog(VerificationActivity.this);
                        saveDataOnParse();
                    }
                } else {
                    ShowNotification.showErrorDialog(VerificationActivity.this, "Internet is not available.");
                }

//                if(validateForm()){
//                    startNewActivity();
//
//
//                }

            }
        });
        action_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
                //ProcessingActivity.processingActivity.finish();
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

        action_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        text_verification.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 4) {
                    text_verification.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(text_verification.getWindowToken(), 0);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }

    private void startNewActivity() {

        Intent i = new Intent(VerificationActivity.this, ProcessingActivity.class);

        if (!objectId.isEmpty()) {
            i.putExtra("objectId", objectId);
        }

        if (getIntent().hasExtra("full_name")) {
            i.putExtra("full_name", getIntent().getStringExtra("full_name"));

        }

        if (getIntent().hasExtra("company")) {
            i.putExtra("company", getIntent().getStringExtra("company"));
        }
        if (getIntent().hasExtra("first_name")) {
            i.putExtra("first_name", getIntent().getStringExtra("first_name"));
        }
        if (getIntent().hasExtra("last_name")) {
            i.putExtra("last_name", getIntent().getStringExtra("last_name"));
        }
        if (getIntent().hasExtra("mobile_phone")) {
            i.putExtra("mobile_phone", getIntent().getStringExtra("mobile_phone"));
        }
        if (getIntent().hasExtra("home_phone")) {
            i.putExtra("home_phone", getIntent().getStringExtra("home_phone"));
            i.putExtra("business_phone", getIntent().getStringExtra("home_phone"));
        }
        if (getIntent().hasExtra("email_address")) {
            i.putExtra("email_address", getIntent().getStringExtra("email_address"));
        }
        i.putExtra("Tocken_ID", getIntent().getStringExtra("Tocken_ID"));

        if (Utility.getFlow(VerificationActivity.this) == 2) {
            if (getIntent().hasExtra("pricingObject")) {
                i.putExtra("pricingObject", (ParseObject) getIntent().getExtras().get("pricingObject"));
            }
        }

        startActivity(i);
    }

    private boolean validateForm() {
        boolean errorNotFound = true;
        String verificationCode = "";
        if (getIntent().hasExtra("VERIFICATION_CODE")) {
            verificationCode = getIntent().getStringExtra("VERIFICATION_CODE");
        }
        if (TextUtils.isEmpty(text_verification.getText().toString())) {
            errorNotFound = false;
            ShowNotification.showErrorDialog(VerificationActivity.this, "Please enter code sent to your phone.");
        } else {
            if (!TextUtils.equals(verificationCode, text_verification.getText().toString())) {
                errorNotFound = false;
                ShowNotification.showErrorDialog(VerificationActivity.this, "Please enter valid code.");
            }
        }

        return errorNotFound;
    }



    public void saveDataOnParse() {
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        final ParseObject query = new ParseObject(getResources().getString(R.string.source_and_class_name));
        query.put("Name", getIntent().getStringExtra("first_name"));
        query.put("LastName", getIntent().getStringExtra("last_name"));
        query.put("BusinessName", getIntent().getStringExtra("company"));
        query.put("Email", getIntent().getStringExtra("email_address"));
        query.put("Phone", getIntent().getStringExtra("mobile_phone"));
        query.put("source", getIntent().getStringExtra("source"));
        query.put("IPAddress", ApplicationClass.getMyApp().getLocalIpAddress());
        query.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    objectId = query.getObjectId();
                    Log.e("Data:", "Saved on Parse.");
                    sendEmail(true);
                    saveLeadThroughCloudCode();
                } else {
                    Log.e("Err1:", e.toString());
                    sendEmail(false);
                }

            }
        });

    }

    public void sendEmail(boolean isSuccess) {
        SendEmailThroughCloudCode(isSuccess,
                "leads@pospros.com",
                getIntent().getStringExtra("full_name"),
                getIntent().getStringExtra("company"),
                getIntent().getStringExtra("email_address"),
                getIntent().getStringExtra("mobile_phone"),
                getIntent().getStringExtra("card_processing"),
                getIntent().getStringExtra("source")
        );
    }

    public void SendEmailThroughCloudCode(boolean isSuccess, String recipients, String name, String bn,
                                          String email, String pass, String cardProcessing, String source) {
        try {
            String bodyMassege = "";
            final String subject = "New Lead From " + source;

            bodyMassege = "Name: <strong>" + name + "</strong> <br><br>" +
                    "Business Name: <strong>" + bn + "</strong><br><br>" +
                    "Email: <strong>" + email + "</strong><br><br>" +
                    "Phone: <strong>" + pass + "</strong><br><br>" +
                    "Processing Credit Cards: <strong>" + cardProcessing + "</strong><br><br>";

            final String body = bodyMassege;
            //text/plain

            HashMap<String, Object> finalParams = new HashMap<String, Object>();
            finalParams.put("toEmail", recipients);
            finalParams.put("fromEmail", "support@cjtechapps.com");
            finalParams.put("subject", subject);
            finalParams.put("message", bodyMassege);
            //below function will update data in User Table
            ParseCloud.callFunctionInBackground("sendMailgun",
                    finalParams,
                    new FunctionCallback<Map<String, Object>>() {
                        public void done(Map<String, Object> results, ParseException e) {
                            if (e == null) {
                                progressDialog.dismiss();
                                Log.e("Email:", "sending complete.");

                                startNewActivity();

//                                progressDialog.dismiss();
//                                Intent i = new Intent(VerificationActivity.this, ThankYouActivity.class);
//                                startActivity(i);
                                //Now below function will update data in Client User Table
                            } else {
                                Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("err:", e.toString());

//                                progressDialog.dismiss();
//                                Intent i = new Intent(VerificationActivity.this, ThankYouActivity.class);
//                                startActivity(i);

                            }
                        }
                    });
        } catch (Exception e) {
            Log.v("Err email1:", e.toString());
        }
    }

    public void saveLeadThroughCloudCode() {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("objectId", objectId);
            params.put("app", getResources().getString(R.string.source_and_class_name));

            //below function will trigger saveLeadToPospros function from cloud code
            ParseCloud.callFunctionInBackground("saveLeadToPospros",
                    params,
                    new FunctionCallback<String>() {
                        public void done(String results, ParseException e) {
                            if (e == null) {
                                Log.e("results", results);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
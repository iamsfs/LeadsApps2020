package com.threeaspen.merchant.lite;

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
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.threeaspen.merchant.lite.helper.MySSLSocketFactory;
import com.threeaspen.merchant.lite.helper.ShowNotification;
import com.threeaspen.merchant.lite.utility.Utility;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Random;

public class SignUpActivity extends Activity {

    private static final String TAG = "Merchant Account SignUp";
    private TextView action_next, action_call, action_cancel;
    private EditText sign_up_name, sign_up_last_name, sign_up_bus_name, sign_up_email, sign_up_phone, sign_up_password, sign_up_v_password;
    private String phoneNumber = "";
    String tocken_id = "";
    private ProgressDialog progressDialog;
    String verificationCode = "1234";

    private String source = "";

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        source = getString(R.string.source_and_class_name);
        progressDialog = new ProgressDialog(SignUpActivity.this);
        if (sign_up_name == null) {
            sign_up_name = (EditText) findViewById(R.id.sign_up_name);
        }

        if (sign_up_last_name == null) {
            sign_up_last_name = (EditText) findViewById(R.id.sign_up_last_name);
        }

        if (sign_up_bus_name == null) {
            sign_up_bus_name = (EditText) findViewById(R.id.sign_up_bus_name);
        }
        if (sign_up_email == null) {
            sign_up_email = (EditText) findViewById(R.id.sign_up_email);
        }
        if (sign_up_phone == null) {
            sign_up_phone = (EditText) findViewById(R.id.sign_up_phone);
        }
        if (sign_up_password == null) {
            sign_up_password = (EditText) findViewById(R.id.sign_up_password);
        }
        if (sign_up_v_password == null) {
            sign_up_v_password = (EditText) findViewById(R.id.sign_up_v_password);
        }

        if (action_next == null) {
            action_next = (TextView) findViewById(R.id.action_next);
        }
        if (action_call == null) {
            action_call = (TextView) findViewById(R.id.action_call);
        }
        if (action_cancel == null) {
            action_cancel = (TextView) findViewById(R.id.action_cancel);
        }
        //setSeeds();

    }




    @Override
    protected void onStart() {
        super.onStart();

        sign_up_phone.addTextChangedListener(phoneWatcher);
        action_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // WebServer Request URL
                if (isNetworkAvailable(SignUpActivity.this)) {
                    if (validateForm()) {
                        // Use AsyncTask execute Method To Prevent ANR Problem

                        SendSMSThroughTwilioCloudCode();
                    }
                } else {
                    ShowNotification.showErrorDialog(SignUpActivity.this, "Internet is not available.");
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

        action_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private TextWatcher phoneWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (phoneNumber.length() < s.length()) {
                //s.clear();s.insert(0, s.toString().replaceAll("[\\W]", ""));
                switch (s.length()) {
                    case 1:
                        if (s.charAt(0) == '0' || s.charAt(0) == '1') {
                            s.delete(0, 1);
                        } else {
                            //s.insert(0, "(");
                        }
                        break;
                    case 3:
                        s.insert(3, "-");
                        break;
                    case 4:
                        if (!(s.charAt(3) == '-')) {
                            s.insert(3, "-");
                        }
                        break;
                    case 7:
                        s.insert(7, "-");
                        break;

                    case 8:
                        if (!(s.charAt(7) == '-')) {
                            s.insert(7, "-");
                        }
                        break;
                    case 10:
                        //s.insert(9, "-");
                        break;
                }
            }
            phoneNumber = s.toString();
        }
    };

    private boolean validateForm() {
        boolean errorNotFound = true;

        if (TextUtils.isEmpty(sign_up_name.getText().toString())) {
            errorNotFound = false;
            ShowNotification.showErrorDialog(SignUpActivity.this, "Please enter first name.");
            //Toast.makeText(SignUpActivity.this, "Please enter email.", Toast.LENGTH_SHORT).show();
            sign_up_name.requestFocus();
            return errorNotFound;
        }

        if (TextUtils.isEmpty(sign_up_last_name.getText().toString())) {
            errorNotFound = false;
            ShowNotification.showErrorDialog(SignUpActivity.this, "Please enter last name.");
            sign_up_last_name.requestFocus();
            return errorNotFound;
        }

        if (TextUtils.isEmpty(sign_up_bus_name.getText().toString())) {
            errorNotFound = false;
            ShowNotification.showErrorDialog(SignUpActivity.this, "Please enter business name.");
            //Toast.makeText(SignUpActivity.this, "Please enter email.", Toast.LENGTH_SHORT).show();
            sign_up_bus_name.requestFocus();
            return errorNotFound;
        }

        if (TextUtils.isEmpty(sign_up_email.getText().toString())) {
            errorNotFound = false;
            ShowNotification.showErrorDialog(SignUpActivity.this, "Please enter email.");
            //Toast.makeText(SignUpActivity.this, "Please enter email.", Toast.LENGTH_SHORT).show();
            sign_up_email.requestFocus();
            return errorNotFound;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(sign_up_email.getText().toString()).matches()) {
                errorNotFound = false;
                ShowNotification.showErrorDialog(SignUpActivity.this, "Please enter valid email.");
                //Toast.makeText(SignUpActivity.this, "Please enter valid email.", Toast.LENGTH_SHORT).show();
                sign_up_email.requestFocus();
                return errorNotFound;
            }
        }

        if (TextUtils.isEmpty(sign_up_phone.getText().toString())) {
            errorNotFound = false;
            ShowNotification.showErrorDialog(SignUpActivity.this, "Please enter phone.");
            //Toast.makeText(SignUpActivity.this, "Please enter phone.", Toast.LENGTH_SHORT).show();
            sign_up_phone.requestFocus();
            return errorNotFound;
        } else {
            String regexStr = "^[+]?[01]?[- .]?(\\([2-9]\\d{2}\\)|[2-9]\\d{2})[- .]?\\d{3}[- .]?\\d{4}$";
            String number = sign_up_phone.getText().toString();
            if (number.length() < 10 || number.length() > 14 || number.matches(regexStr) == false) {
                errorNotFound = false;
                ShowNotification.showErrorDialog(SignUpActivity.this, "Please enter valid phone number.");
                //Toast.makeText(SignUpActivity.this,"Please enter valid phone number.",Toast.LENGTH_SHORT).show();
                sign_up_phone.requestFocus();
                return errorNotFound;
            }
        }
        if (TextUtils.isEmpty(sign_up_password.getText().toString())) {
            errorNotFound = false;
            ShowNotification.showErrorDialog(SignUpActivity.this, "Please enter password.");
            sign_up_password.requestFocus();
            return errorNotFound;
        }
        if (!sign_up_password.getText().toString().equals(sign_up_v_password.getText().toString())) {
            errorNotFound = false;
            ShowNotification.showErrorDialog(SignUpActivity.this, "Password Do Not Match.");
            sign_up_password.requestFocus();
            return errorNotFound;
        }

        return errorNotFound;
    }

    public void SendSMSThroughTwilioCloudCode() {
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        verificationCode = String.valueOf(new Random().nextInt(8999) + 1000);
        String body = "Here is your activation code. " + verificationCode + ". Thank you for choosing " + getString(R.string.app_name) + " App.";
        HashMap<String, Object> finalParams = new HashMap<String, Object>();
        finalParams.put("number", phoneNumber);
        finalParams.put("body", body);
        //below function will update data in User Table
        ParseCloud.callFunctionInBackground("sendSMSWithTwilio",
                finalParams,
                new FunctionCallback<String>() {
                    public void done(String results, com.parse.ParseException e) {
                        if (e == null) {
                            Log.e("verificationCode:", verificationCode);
                            Toast.makeText(getApplicationContext(), "SMS is Send.", Toast.LENGTH_LONG).show();
                            Log.i("SMS:", "Sended");
                            Log.i("verificationCode:", verificationCode);
                            Intent i = new Intent(SignUpActivity.this, VerificationActivity.class);
                            i.putExtra("source", getString(R.string.source_and_class_name));
                            i.putExtra("company", SignUpActivity.this.sign_up_bus_name.getText().toString());
//                            String[] arrayOfString = SignUpActivity.this.sign_up_name.getText().toString().split("\\s+");
//                            i.putExtra("first_name", arrayOfString[0]);
//                            if (arrayOfString.length >= 2) {
//                                i.putExtra("last_name", arrayOfString[1]);
//                            }
                            i.putExtra("first_name", SignUpActivity.this.sign_up_name.getText().toString());
                            i.putExtra("last_name", SignUpActivity.this.sign_up_last_name.getText().toString());
                            i.putExtra("full_name", SignUpActivity.this.sign_up_name.getText().toString() + " " + SignUpActivity.this.sign_up_last_name.getText().toString());
                            i.putExtra("mobile_phone", phoneNumber);
                            i.putExtra("home_phone", phoneNumber);
                            i.putExtra("email_address", SignUpActivity.this.sign_up_email.getText().toString());
                            i.putExtra("VERIFICATION_CODE", verificationCode);
                            i.putExtra("Tocken_ID", tocken_id);


                            if (Utility.getFlow(SignUpActivity.this) == 2) {
                                if (getIntent().hasExtra("pricingObject")) {
                                    i.putExtra("pricingObject", (ParseObject) getIntent().getExtras().get("pricingObject"));
                                }
                            }

                            startActivity(i);

                        } else {
                            Log.i("err:", e.toString());
                        }
                    }
                });
    }


}

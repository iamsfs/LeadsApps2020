package com.ics.creditcardreader;

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

import com.ics.creditcardreader.helper.MySSLSocketFactory;
import com.ics.creditcardreader.helper.ShowNotification;
import com.ics.creditcardreader.utility.Utility;
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

    private static final String TAG = "CCR - SignUp";
    private TextView action_next, action_call;
    private EditText sign_up_name, sign_up_last_name, sign_up_bus_name, sign_up_email, sign_up_phone, sign_up_password, sign_up_v_password;
    private String phoneNumber = "";
    String tocken_id = "";
    private HttpClient httpClient = MySSLSocketFactory.getNewHttpClient(MySSLSocketFactory.getKeystore());
    private ProgressDialog progressDialog;
    String verificationCode = "1234";

    private String source = "CCRA", card_processing = "No";
    private String serverURL = "https://api.marketingoptimizer.com/api/v1/";

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
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
        //setSeeds();

    }


    // TODO: 18/6/19 Remove before sharing
    private void setSeeds() {
        sign_up_name.setText("Snake");
        sign_up_bus_name.setText("Snake bizz");
        sign_up_email.setText("Snake@gmail.com");
        sign_up_phone.setText("703-539-7183");
        sign_up_password.setText("123456");
        sign_up_v_password.setText("123456");
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
                        //getTockenIdFromAggregateCRM();
                    }
                } else {
                    ShowNotification.showErrorDialog(SignUpActivity.this, "Internet is not available.");
                }
            }
        });

        action_call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Uri uri = Uri.parse("tel:1-888-994-9610");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
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

    // Class with extends AsyncTask class
    private class SendSMSThroughAPI extends AsyncTask<String, Void, Void> {
        private String Error = null;
        String phoneNumber = sign_up_phone.getText().toString().replaceAll("[\\W]", "");
        private String verificationCode = "1234";

        protected void onPreExecute() {
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            verificationCode = String.valueOf(new Random().nextInt(8999) + 1000);
            String msgData = "";
            try {
                // Set Request parameter
                msgData += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + phoneNumber;
                msgData += "&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(String.format("Here is your activation code. %s. Thank you for choosing %s App", verificationCode, getString(R.string.app_name)), "UTF-8");
                msgData += "&" + URLEncoder.encode("token", "UTF-8") + "=raj12345";

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HttpClient client = new DefaultHttpClient();
            String response = "";
            // Send data
            try {
                HttpGet httpget = new HttpGet(urls[0] + "?" + msgData);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Log.d(TAG, "Message Request: " + urls[0] + "?" + msgData);
                response = client.execute(httpget, responseHandler);
                Log.d(TAG, "Message Response: " + response);
            } catch (IOException ex) {
                Error = ex.getMessage();
            } catch (Exception ex) {
                Error = ex.getMessage();
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            // Close progress dialog
            progressDialog.dismiss();
            if (Error != null) {
					/*new AlertDialog.Builder(SignUpActivity.this)
						.setTitle(R.string.app_name)
						.setMessage(Error)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {}
						}).show();*/
                Log.d(TAG, "Error : " + Error);
            }
            Log.i("verificationCode:", this.verificationCode);
            Intent i = new Intent(SignUpActivity.this, ProcessingActivity.class);
            i.putExtra("source", "CCRA");
            i.putExtra("company", SignUpActivity.this.sign_up_bus_name.getText().toString());
            String[] arrayOfString = SignUpActivity.this.sign_up_name.getText().toString().split("\\s+");
            i.putExtra("first_name", arrayOfString[0]);
            if (arrayOfString.length >= 2) {
                i.putExtra("last_name", arrayOfString[1]);
            }
            i.putExtra("full_name", SignUpActivity.this.sign_up_name.getText().toString().trim());
            i.putExtra("mobile_phone", this.phoneNumber);
            i.putExtra("home_phone", this.phoneNumber);
            i.putExtra("email_address", SignUpActivity.this.sign_up_email.getText().toString());
            i.putExtra("VERIFICATION_CODE", this.verificationCode);
            i.putExtra("Tocken_ID", tocken_id);
            startActivity(i);
        }
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
                        progressDialog.dismiss();
                        if (e == null) {
                            Log.e("verificationCode:", verificationCode);
                            Toast.makeText(getApplicationContext(), "SMS is Send.", Toast.LENGTH_LONG).show();
                            Log.i("SMS:", "Sended");
                            Log.i("verificationCode:", verificationCode);
                            Intent i = new Intent(SignUpActivity.this, VerificationActivity.class);
                            i.putExtra("source", "CCRA");
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

    public void getTockenIdFromAggregateCRM() {
        class APIOperation extends AsyncTask<String, Void, Void> {
            private String Error = null;

            protected void onPreExecute() {
                progressDialog.setTitle("Saving");
                progressDialog.setMessage("Please wait..");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            // Call after onPreExecute method
            protected Void doInBackground(String... urls) {
                try {
                    //getting tocken id from Aggregate crm
                    Log.i("Get Url:", "https://app.marketingoptimizer.com/api/v1/login/jfkpresident/Johnk@420");
                    HttpGet httpGet = new HttpGet("https://app.marketingoptimizer.com/api/v1/login/jfkpresident/Johnk@420");
                    HttpResponse loginResponse = httpClient.execute(httpGet);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(
                                    (loginResponse.getEntity().getContent())
                            )
                    );
                    StringBuilder content = new StringBuilder();
                    String line2;
                    while (null != (line2 = br.readLine())) {
                        content.append(line2);
                    }
                    JSONObject jsonObject = new JSONObject(content.toString());
                    JSONObject jsonObject2 = jsonObject.getJSONObject("data");

                    Log.e("Tocken_ID_PA: ", jsonObject2.getString("id_token"));
                    Log.e("response", jsonObject.toString());
                    //Log.e("response", content.toString() );
                    //  Log.i("access_token: ", jsonObject2.getString("access_token") );
                    //  Log.i("token_type: ", jsonObject2.getString("token_type") );
                    tocken_id = jsonObject2.getString("id_token");
                    //end
                } catch (IOException ex) {
                    Error = ex.getMessage();
                } catch (Exception ex) {
                    Error = ex.getMessage();
                }
                return null;
            }

            protected void onPostExecute(Void unused) {
                if (Error != null) {
                    Log.i("Error:", Error);
                }
                //new APIOperationSend().execute(serverURL);
                progressDialog.dismiss();
                SendSMSThroughTwilioCloudCode();
            }
        }
        new APIOperation().execute("");
    }


    //send Data
    class APIOperationSend extends AsyncTask<String, Void, Void> {
        private String Error = null;
        JSONObject jsonParams = new JSONObject();
        StringBuilder stringReader = new StringBuilder();
        HttpPost httpPost;
        String str;

        protected void onPreExecute() {
            try {
                if (!source.equals("")) {
                    Object localObject = new JSONObject();
                    ((JSONObject) localObject).put("id", "67516");//
                    ((JSONObject) localObject).put("value", source);
                    ((JSONObject) localObject).put("name", "source");
                    localObject = new JSONArray().put(localObject);
                    this.jsonParams.put("fields", localObject);
                    this.jsonParams.put("position", source);
                }
                this.jsonParams.put("company", SignUpActivity.this.sign_up_bus_name.getText().toString());
                this.jsonParams.put("first_name", SignUpActivity.this.sign_up_name.getText().toString().split("\\s+")[0]);
                if (SignUpActivity.this.sign_up_name.getText().toString().split("\\s+").length >= 2) {
                    this.jsonParams.put("last_name", SignUpActivity.this.sign_up_name.getText().toString().split("\\s+")[1]);
                }
                this.jsonParams.put("mobile_phone", phoneNumber);
                this.jsonParams.put("home_phone", phoneNumber);
                this.jsonParams.put("business_phone", phoneNumber);
                this.jsonParams.put("email_address", SignUpActivity.this.sign_up_email.getText().toString());
            } catch (JSONException localJSONException) {
                this.Error = localJSONException.getMessage();
            }
            Log.d("CCR - Verification", "Request: " + this.jsonParams.toString());
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                httpPost = new HttpPost(urls[0] + "contacts");
                str = "Bearer " + tocken_id;
                Log.e("TockenIdVA: ", str);

                httpPost.addHeader("Authorization", str);
                httpPost.setHeader("content-type", "application/json");
                StringEntity se = new StringEntity(jsonParams.toString(), HTTP.UTF_8);
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httpPost.setEntity(se);
                HttpResponse response = httpClient.execute(httpPost);

                // Get hold of the response entity
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // A Simple JSON Response Read
                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        stringReader.append(line);
                    }
                    Log.d(TAG, "XML Response: " + stringReader.toString());
                }
            } catch (IOException ex) {
                Error = ex.getMessage();
            } catch (Exception ex) {
                Error = ex.getMessage();
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            saveDataOnParse();
            String leadID = "0";
            if (Error != null) {
            	/*new AlertDialog.Builder(VerificationActivity.this)
            		.setTitle(R.string.app_name)
	                .setMessage(Error)
	                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	                	public void onClick(DialogInterface dialog, int which) {}
	                }).show();*/
//				Log.d(TAG, "Output : "+Error);
            } else {
                JSONObject localObject = null;
                try {
                    localObject = new JSONObject(stringReader.toString());
                    JSONObject localJSONObject = new JSONObject(localObject.getString("data"));
                    Log.d("CCR - Verification", "Success: " + localObject.getString("success"));

                    if (!localJSONObject.getString("id").equals("0")) {
                    }
                } catch (Exception ex) {
                    Log.d("CCR - Verification", "Error : " + ex.getMessage());
                }
                Log.d("CCR - Verification", "Output : " + localObject);
            }
        }
    }

    public void saveDataOnParse() {
        ParseObject query = new ParseObject("CCRA");
        query.put("Name", SignUpActivity.this.sign_up_name.getText().toString());
        query.put("BusinessName", SignUpActivity.this.sign_up_bus_name.getText().toString());
        query.put("Email", SignUpActivity.this.sign_up_email.getText().toString());
        query.put("Phone", phoneNumber);
        query.put("Processing", card_processing);
        query.put("source", source);

        query.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
//				if(e== null){
//					Log.e("Data:","Saved on Parse.");
//					sendEmail();
//				}else{
//					Log.e("Err1:",e.toString());
//					sendEmail();
//				}
                progressDialog.dismiss();
                Intent i = new Intent(SignUpActivity.this, ThankYouActivity.class);
                startActivity(i);

            }
        });
    }
}

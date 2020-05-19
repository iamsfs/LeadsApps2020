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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ics.creditcardreader.helper.MySSLSocketFactory;
import com.ics.creditcardreader.helper.ShowNotification;
import com.ics.creditcardreader.utility.Utility;
import com.ics.creditcardreader.webservices.impl.IrsLeadsWebserviceImpl;
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
    private static final String TAG = "CCR - Verification";
    private TextView action_next, action_back, action_call;
    private EditText text_verification;
    public ProgressDialog progressDialog;
    private String verificationCode;
    public String tocken_id;
    private String serverURL = "https://api.marketingoptimizer.com/api/v1/";
    private HttpClient httpClient = MySSLSocketFactory.getNewHttpClient(MySSLSocketFactory.getKeystore());
    private String objectId="";

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
//                        IrsLeadsWebserviceImpl.saveLeads(
//                                getIntent().getStringExtra("full_name"),
//                                getIntent().getStringExtra("source"),
//                                getIntent().getStringExtra("company"),
//                                getIntent().getStringExtra("mobile_phone"));
//


                        // new APIOperation().execute(serverURL);
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
                Uri uri = Uri.parse("tel:1-888-994-9610");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
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
    public void saveLeadThroughCloudCode() {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("objectId", objectId);
            params.put("app", "CCRA");
            //below function will trigger saveLeadToPospros function from cloud code

            ParseCloud.callFunctionInBackground("saveLeadToPospros",
                    params,
                    new FunctionCallback<String>() {
                        public void done(String results, ParseException e) {
                            if (e == null) {
                                Log.e("results",results);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startNewActivity() {

        Intent i = new Intent(VerificationActivity.this, ProcessingActivity.class);

        if(!objectId.isEmpty())
        {
            i.putExtra("objectId",objectId);
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

    class APIOperation extends AsyncTask<String, Void, Void> {
        private String Error = null;
        JSONObject jsonParams = new JSONObject();
        StringBuilder stringReader = new StringBuilder();
        HttpPost httpPost;
        String str;

        protected void onPreExecute() {
            progressDialog.setTitle("Saving");
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            try {
                if (getIntent().hasExtra("source")) {
                    Object localObject = new JSONObject();
                    ((JSONObject) localObject).put("id", "67516");//
                    ((JSONObject) localObject).put("value", getIntent().getStringExtra("source"));
                    ((JSONObject) localObject).put("name", "source");
                    localObject = new JSONArray().put(localObject);
                    this.jsonParams.put("fields", localObject);
                    this.jsonParams.put("position", getIntent().getStringExtra("source"));
                }
                if (getIntent().hasExtra("company")) {
                    this.jsonParams.put("company", getIntent().getStringExtra("company"));
                }
                if (getIntent().hasExtra("first_name")) {
                    this.jsonParams.put("first_name", getIntent().getStringExtra("first_name"));
                }
                if (getIntent().hasExtra("last_name")) {
                    this.jsonParams.put("last_name", getIntent().getStringExtra("last_name"));
                }
                if (getIntent().hasExtra("mobile_phone")) {
                    this.jsonParams.put("mobile_phone", getIntent().getStringExtra("mobile_phone"));
                }
                if (getIntent().hasExtra("home_phone")) {
                    this.jsonParams.put("home_phone", getIntent().getStringExtra("home_phone"));
                }
                if (getIntent().hasExtra("business_phone")) {
                    this.jsonParams.put("business_phone", getIntent().getStringExtra("business_phone"));
                }
                if (getIntent().hasExtra("email_address")) {
                    this.jsonParams.put("email_address", getIntent().getStringExtra("email_address"));
                }
            } catch (JSONException localJSONException) {
                this.Error = localJSONException.getMessage();
            }
            Log.d("CCR - Verification", "Request: " + this.jsonParams.toString());
        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                httpPost = new HttpPost(urls[0] + "contacts");
                str = "Bearer " + getIntent().getStringExtra("Tocken_ID");
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
            //saveDataOnParse();
            String leadID = "0";
            if (Error != null) {
            	/*new AlertDialog.Builder(VerificationActivity.this)
            		.setTitle(R.string.app_name)
	                .setMessage(Error)
	                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	                	public void onClick(DialogInterface dialog, int which) {}
	                }).show();*/
                Log.d(TAG, "Output : " + Error);
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

            //sendingDataToICSLeads();

            //sendingDataToNewCRM();

        }
    }

    public void saveDataOnParse() {
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
        final ParseObject query = new ParseObject("CCRA");
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
                    objectId=query.getObjectId();
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

    public void SendEmailThroughCloudCode(boolean isSuccess,String recipients, String name, String bn,
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

    public void sendingDataToNewCRM() {
        class NewCRMAPIOperation extends AsyncTask<String, Void, Void> {
            private String Error = null, apiResponce = "";
            List<NameValuePair> paramlist = new ArrayList<NameValuePair>();
            StringBuilder stringReader = new StringBuilder();

            protected void onPreExecute() {
                try {
                    String phone = getIntent().getStringExtra("mobile_phone");
                    phone = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6, 10);
                    String fullName = getIntent().getStringExtra("full_name");
                    paramlist.add(new BasicNameValuePair("status", "New Lead"));
                    paramlist.add(new BasicNameValuePair("list", getIntent().getStringExtra("source")));

                    if (getIntent().hasExtra("full_name")) {
                        paramlist.add(new BasicNameValuePair("ContactName", fullName));
                    }
                    if (getIntent().hasExtra("company")) {
                        paramlist.add(new BasicNameValuePair("BusinessName", getIntent().getStringExtra("company")));
                    }
                    if (getIntent().hasExtra("email_address")) {
                        paramlist.add(new BasicNameValuePair("Email", getIntent().getStringExtra("email_address")));
                    }

                    if (getIntent().hasExtra("mobile_phone")) {
                        paramlist.add(new BasicNameValuePair("OfficePhone", phone));
                    }
                    if (getIntent().hasExtra("card_processing")) {
                        paramlist.add(new BasicNameValuePair("AcceptCreditCards2", getIntent().getStringExtra("card_processing")));
                    }
                } catch (Exception ex) {
                    this.Error = ex.getMessage();
                }
                Log.i("NewCrmPostRequest:", this.paramlist.toString());
            }

            // Call after onPreExecute method
            protected Void doInBackground(String... urls) {
                // Send data
                try {
                    // Defined URL  where to send data
                    Log.i("NewCrmPostUrl:", urls[0]);

                    HttpPost httpPost = new HttpPost(urls[0]);
                    httpPost.setHeader(HTTP.CONTENT_TYPE,
                            "application/x-www-form-urlencoded;charset=UTF-8");
                    httpPost.setEntity(new UrlEncodedFormEntity(paramlist, "UTF-8"));

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
                        apiResponce = stringReader.toString();

                    }
                } catch (IOException ex) {
                    Error = ex.getMessage();
                } catch (Exception ex) {
                    Error = ex.getMessage();
                }
                return null;
            }

            protected void onPostExecute(Void unused) {
                Log.i("NewCrmResponse: ", apiResponce);
                if (Error != null) {
                   /*new AlertDialog.Builder(VerificationActivity.this)
                            .setTitle(R.string.app_name)
                            .setMessage(Error)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();*/
                    Log.i("Error : ", Error);
                }
                progressDialog.dismiss();
                Intent i = new Intent(VerificationActivity.this, ThankYouActivity.class);
                startActivity(i);
            }
        }
        new NewCRMAPIOperation().execute("http://merchantaccountcrm.com/ext_lead_import.php");
    }

    public void sendingDataToICSLeads() {
        class IcsLeadsAPIOperation extends AsyncTask<String, Void, Void> {
            private String Error = null, apiResponce = "";
            private StringReader xmlData = null;
            JSONObject jsonParams = new JSONObject();
            List<NameValuePair> paramlist = new ArrayList<NameValuePair>();
            StringBuilder stringReader = new StringBuilder();
            private String data = "";
            private String crmdata = "";

            protected void onPreExecute() {
                try {
                    String phone = getIntent().getStringExtra("mobile_phone");
                    //phone = "("+phone.substring(0,3)+") "+phone.substring(3,6)+"-"+phone.substring(6,10);
                    phone = phone.replace("-", "");
                    String fullName = getIntent().getStringExtra("full_name");
//                    String isProcessing = getIntent().getStringExtra("card_processing");
//                    if (isProcessing.equals("Yes")) {
//                        isProcessing = "True";
//                    } else {
//                        isProcessing = "False";
//                    }

                    try {
                        // Set Request parameter
                        data += "&" + URLEncoder.encode("source", "UTF-8") + "=" + getIntent().getStringExtra("source");
                        data += "&" + URLEncoder.encode("originator", "UTF-8") + "=" + "1029";
                        data += "&" + URLEncoder.encode("returnType", "UTF-8") + "=" + "xml";
                        data += "&" + URLEncoder.encode("traceId", "UTF-8") + "=" + "1234567890";
                        data += "&" + URLEncoder.encode("businessName", "UTF-8") + "=" + getIntent().getStringExtra("company");
                        data += "&" + URLEncoder.encode("contactName", "UTF-8") + "=" + fullName;
                        data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + phone;
                        data += "&" + URLEncoder.encode("phoneAlt", "UTF-8") + "=" + phone;

                        //data += "&" + URLEncoder.encode("currentlyProcessing", "UTF-8") + "=" + isProcessing;
                        data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + getIntent().getStringExtra("email_address");

                        Log.e("check",data);

                    } catch (UnsupportedEncodingException e) {
                        Log.e("check",e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
                    Log.e("check",ex.getLocalizedMessage());
                    this.Error = ex.getMessage();
                }
            }

            // Call after onPreExecute method
            protected Void doInBackground(String... urls) {
                BufferedReader reader = null;
                // Send data
                try {
                    // Defined URL  where to send data
                    URL url = new URL(urls[0]);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    Log.d(TAG, "XML Response: " + sb.toString());
                    xmlData = new StringReader(sb.toString());
                    apiResponce = sb.toString();
                } catch (IOException ex) {
                    Error = ex.getMessage();
                } catch (Exception ex) {
                    Error = ex.getMessage();
                }

                return null;
            }

            protected void onPostExecute(Void unused) {
                Log.e("IcsLeadsResponse: ", apiResponce);
                progressDialog.dismiss();
                if (Error != null) {
                   /* new AlertDialog.Builder(VerificationActivity.this)
                            .setTitle(R.string.app_name)
                            .setMessage(Error)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();*/
                    Log.e("Error : ", Error);
                }

                startNewActivity();

                //Intent i = new Intent(VerificationActivity.this, ThankYouActivity.class);
                //startActivity(i);
            }
        }
        new IcsLeadsAPIOperation().execute("https://www.icsleads.com/Api/AddLead/");
    }

    public void sendEmailOld() {
        new AsyncTask<Void, Void, Void>() {
            boolean isSuccess;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i("sending Email:", "Start..");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (isSuccess) {
                    Log.i("sending Email:", "Completed..");
                } else {
                    Log.i("sending Email:", "Fail..");
                }
                progressDialog.dismiss();
                Intent i = new Intent(VerificationActivity.this, ThankYouActivity.class);
                startActivity(i);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String phone = getIntent().getStringExtra("mobile_phone");
                    phone = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6, 10);
                    String fullName = getIntent().getStringExtra("full_name");
                    new MailHelper().sendMail(
                            "leads@merchantaccountsolutions.com",
                            fullName,
                            getIntent().getStringExtra("company"),
                            getIntent().getStringExtra("email_address"),
                            phone,
                            getIntent().getStringExtra("card_processing"),
                            getIntent().getStringExtra("source")
                    );
                    isSuccess = true;
                } catch (Exception ex) {
                    isSuccess = false;
                }
                return null;
            }
        }.execute();

    }

}
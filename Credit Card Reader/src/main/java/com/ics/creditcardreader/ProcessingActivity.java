package com.ics.creditcardreader;

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

import com.ics.creditcardreader.helper.MySSLSocketFactory;
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
    private TextView action_yes, action_no;
    private String verificationCode;
    public static ProcessingActivity processingActivity;
    public ProgressDialog progressDialog;
    private HttpClient httpClient = MySSLSocketFactory.getNewHttpClient(MySSLSocketFactory.getKeystore());
    String tocken_id = "";

    private String source = "", card_processing = "";
    //private String serverURL = "https://api.marketingoptimizer.com/api/v1/";

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        processingActivity = this;
        progressDialog = new ProgressDialog(ProcessingActivity.this);
        progressDialog.setMessage("Please wait..");
        if (action_yes == null) {
            action_yes = (TextView) findViewById(R.id.action_yes);
        }

        if (action_no == null) {
            action_no = (TextView) findViewById(R.id.action_no);
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
                source = "CCRAY";
                card_processing = "Yes";
                progressDialog = new ProgressDialog(ProcessingActivity.this);
                progressDialog.setMessage("Please wait..");
                //new APIOperation().execute(serverURL);

//                Intent i = new Intent(ProcessingActivity.this, VerificationActivity.class);
//                i.putExtra("source", "CCRAY");
//                i.putExtra("VERIFICATION_CODE", verificationCode);
//                i.putExtra("card_processing", "Yes");
//                i.putExtra("full_name", getIntent().getStringExtra("full_name"));
//                if (getIntent().hasExtra("company")) {
//                    i.putExtra("company", getIntent().getStringExtra("company"));
//                }
//                if (getIntent().hasExtra("first_name")) {
//                    i.putExtra("first_name", getIntent().getStringExtra("first_name"));
//                }
//                if (getIntent().hasExtra("last_name")) {
//                    i.putExtra("last_name", getIntent().getStringExtra("last_name"));
//                }
//                if (getIntent().hasExtra("mobile_phone")) {
//                    i.putExtra("mobile_phone", getIntent().getStringExtra("mobile_phone"));
//                }
//                if (getIntent().hasExtra("home_phone")) {
//                    i.putExtra("home_phone", getIntent().getStringExtra("home_phone"));
//                    i.putExtra("business_phone", getIntent().getStringExtra("home_phone"));
//                }
//                if (getIntent().hasExtra("email_address")) {
//                    i.putExtra("email_address", getIntent().getStringExtra("email_address"));
//                }
//                i.putExtra("Tocken_ID", getIntent().getStringExtra("Tocken_ID"));
//
//                if (Utility.getFlow(ProcessingActivity.this) == 2) {
//                    if (getIntent().hasExtra("pricingObject")) {
//                        i.putExtra("pricingObject", (ParseObject) getIntent().getExtras().get("pricingObject"));
//                    }
//                }


                updateDataOnParse();
                //startNewActivity();


                //startActivity(i);
            }
        });
        action_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                source = "CCRA";
                card_processing = "No";
                progressDialog = new ProgressDialog(ProcessingActivity.this);
                //new APIOperation().execute(serverURL);

//                Intent i = new Intent(ProcessingActivity.this, VerificationActivity.class);
//                i.putExtra("source", "CCRA");
//                i.putExtra("VERIFICATION_CODE", verificationCode);
//                i.putExtra("card_processing", "No");
//                i.putExtra("full_name", getIntent().getStringExtra("full_name"));
//                if (getIntent().hasExtra("company")) {
//                    i.putExtra("company", getIntent().getStringExtra("company"));
//                }
//                if (getIntent().hasExtra("first_name")) {
//                    i.putExtra("first_name", getIntent().getStringExtra("first_name"));
//                }
//                if (getIntent().hasExtra("last_name")) {
//                    i.putExtra("last_name", getIntent().getStringExtra("last_name"));
//                }
//                if (getIntent().hasExtra("mobile_phone")) {
//                    i.putExtra("mobile_phone", getIntent().getStringExtra("mobile_phone"));
//                }
//                if (getIntent().hasExtra("home_phone")) {
//                    i.putExtra("home_phone", getIntent().getStringExtra("home_phone"));
//                    i.putExtra("business_phone", getIntent().getStringExtra("home_phone"));
//                }
//                if (getIntent().hasExtra("email_address")) {
//                    i.putExtra("email_address", getIntent().getStringExtra("email_address"));
//                }
//                i.putExtra("Tocken_ID", getIntent().getStringExtra("Tocken_ID"));
//
//                if (Utility.getFlow(ProcessingActivity.this) == 2) {
//                    if (getIntent().hasExtra("pricingObject")) {
//                        i.putExtra("pricingObject", (ParseObject) getIntent().getExtras().get("pricingObject"));
//                    }
//                }
//
//                startActivity(i);

                updateDataOnParse();
                //startNewActivity();

            }
        });
    }

    private void startNewActivity() {
        ParseObject query = new ParseObject("CCRA");
        query.put("Name", getIntent().getStringExtra("first_name"));
        query.put("LastName", getIntent().getStringExtra("last_name"));
        query.put("BusinessName", getIntent().getStringExtra("company"));
        query.put("Email", getIntent().getStringExtra("email_address"));
        query.put("Phone", getIntent().getStringExtra("mobile_phone"));
        query.put("Processing", card_processing);
        query.put("source", source);
        BuisnessInfoActivity.start(this, query, getIntent().getStringExtra("objectId"), (ParseObject) getIntent().getExtras().get("pricingObject"));
    }

    private void updateDataOnParse(){
        progressDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CCRA");
        query.getInBackground(getIntent().getStringExtra("objectId"), new GetCallback<ParseObject>() {
            public void done(ParseObject entity, ParseException e) {
                if (e == null) {
                    entity.put("Processing", card_processing);
                    entity.saveInBackground();
                    progressDialog.dismiss();
                    startNewActivity();
                }
                else{
                    Toast.makeText(ProcessingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                if (!source.equals("")) {
                    Object localObject = new JSONObject();
                    ((JSONObject) localObject).put("id", "67516");//
                    ((JSONObject) localObject).put("value", source);
                    ((JSONObject) localObject).put("name", "source");
                    localObject = new JSONArray().put(localObject);
                    this.jsonParams.put("fields", localObject);
                    this.jsonParams.put("position", source);
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
//					Log.d(TAG, "XML Response: " + stringReader.toString());
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
            sendingDataToICSLeads();
            //sendingDataToNewCRM();

        }
    }

    public void saveDataOnParse() {
        ParseObject query = new ParseObject("CCRA");
        query.put("Name", getIntent().getStringExtra("full_name"));
        query.put("BusinessName", getIntent().getStringExtra("company"));
        query.put("Email", getIntent().getStringExtra("email_address"));
        query.put("Phone", getIntent().getStringExtra("mobile_phone"));
        query.put("Processing", card_processing);
        query.put("source", source);
        query.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e("Data:", "Saved on Parse.");
                    sendEmail();
                } else {
                    Log.e("Err1:", e.toString());
                    sendEmail();
                }

            }
        });

    }

    public void sendEmail() {
        SendEmailThroughCloudCode(
                "leads@merchantaccountsolutions.com",
                getIntent().getStringExtra("full_name"),
                getIntent().getStringExtra("company"),
                getIntent().getStringExtra("email_address"),
                getIntent().getStringExtra("mobile_phone"),
                card_processing,
                source
        );
    }

    public void SendEmailThroughCloudCode(String recipients, String name, String bn,
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
                                Log.e("Email:", "sending complete.");
                                progressDialog.dismiss();
                                Intent i = new Intent(ProcessingActivity.this, ThankYouActivity.class);
                                startActivity(i);
                                //Now below function will update data in Client User Table
                            } else {
                                Log.e("err:", e.toString());

                                progressDialog.dismiss();
                                Intent i = new Intent(ProcessingActivity.this, ThankYouActivity.class);
                                startActivity(i);

                            }
                        }
                    });
        } catch (Exception e) {
            Log.v("Err email1:", e.toString());
        }
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
                    String isProcessing = card_processing;
                    if (isProcessing.equals("Yes")) {
                        isProcessing = "True";
                    } else {
                        isProcessing = "False";
                    }

                    try {
                        // Set Request parameter
                        data += "&" + URLEncoder.encode("source", "UTF-8") + "=" + source;
                        data += "&" + URLEncoder.encode("originator", "UTF-8") + "=" + "1029";
                        data += "&" + URLEncoder.encode("returnType", "UTF-8") + "=" + "xml";
                        data += "&" + URLEncoder.encode("traceId", "UTF-8") + "=" + "1234567890";
                        data += "&" + URLEncoder.encode("businessName", "UTF-8") + "=" + getIntent().getStringExtra("company");
                        data += "&" + URLEncoder.encode("contactName", "UTF-8") + "=" + fullName;
                        data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + phone;
                        data += "&" + URLEncoder.encode("phoneAlt", "UTF-8") + "=" + phone;

                        data += "&" + URLEncoder.encode("currentlyProcessing", "UTF-8") + "=" + isProcessing;
                        data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + getIntent().getStringExtra("email_address");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
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
//					Log.d(TAG, "XML Response: " + sb.toString());
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
                Log.i("IcsLeadsResponse: ", apiResponce);
                progressDialog.dismiss();
                if (Error != null) {
                   /* new AlertDialog.Builder(VerificationActivity.this)
                            .setTitle(R.string.app_name)
                            .setMessage(Error)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();*/
                    Log.i("Error : ", Error);
                }
                Intent i = new Intent(ProcessingActivity.this, ThankYouActivity.class);
                startActivity(i);
            }
        }
        new IcsLeadsAPIOperation().execute("https://www.icsleads.com/Api/AddLead/");
    }
}
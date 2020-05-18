package com.ics.creditcardreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.ics.creditcardreader.helper.GestureOverlayView;
import com.ics.creditcardreader.helper.MySSLSocketFactory;
import com.ics.creditcardreader.helper.ShowNotification;
import com.ics.creditcardreader.helper.pdf.CreatePDF;
import com.ics.creditcardreader.utility.Utility;
import com.ics.creditcardreader.webservices.IrsLeadsWebservice;
import com.ics.creditcardreader.webservices.impl.IrsLeadsWebserviceImpl;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
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
import java.io.File;
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

@EActivity(R.layout.activity_signature)
public class SignatureActivity extends AppCompatActivity {
     private static final String TAG = "CCR - SIGNATURE";
    private final String serverURL = "https://api.marketingoptimizer.com/api/v1/";
    private HttpClient httpClient = MySSLSocketFactory.getNewHttpClient(MySSLSocketFactory.getKeystore());
    private CreatePDF mCreatePDF;

//    @ViewById(R.id.check_box_terms)
//    CheckBox mCheckBoxTerms;

    @ViewById(R.id.tip_sign)
    GestureOverlayView tip_sign;

    private ProgressDialog mprogressDialog;


    private Gesture mGesture;
    private static final float LENGTH_THRESHOLD = 12000.0f;

    private ParseObject mSignature;
    private boolean mIsSignatureEmpty = true;


    @Extra
    String touchAbleId;
    @Extra
    ParseObject mCCRA;
    @Extra
    ParseObject mBusinessInfo;
    @Extra
    ParseObject mPrincipalInfo;
    @Extra
    ParseObject mAccountInfo;
    @Extra
    String terms;
    @Extra
    ParseObject pricingObject;
    @Extra
    String objectId;

    @Bean(IrsLeadsWebserviceImpl.class)
    IrsLeadsWebservice mIrsLeadsWebservice;


    public static void start(Context context, String objectId, ParseObject CCRA, ParseObject businessInfo,
                             ParseObject principalInfo, ParseObject accountInfo, String terms, ParseObject pricingObject) {
        SignatureActivity_.intent(context)
                .objectId(objectId)
                .mCCRA(CCRA)
                .mBusinessInfo(businessInfo)
                .mPrincipalInfo(principalInfo)
                .mAccountInfo(accountInfo)
                .terms(terms)
                .pricingObject(pricingObject)
                .start();
    }

    @Click(R.id.btnNext)
    void onClick() {

        if (mIsSignatureEmpty) {
            ShowNotification.showErrorDialog(this, "Make Signature");
        } else {
            uploadIRSLeadData();
            updateDataOnParse();
//            if (!uploadIRSLeadData()) {
//                ShowNotification.showErrorDialog(this, "Something went wrong.");
//            }
//            sendData();
        }


    }

    public void saveDataOnParse() {

        ParseFile parseFile = new ParseFile("Signature.png", Utility.loadByteArray(mGesture, 100, 100));
        mSignature = new ParseObject("Signature");
        mSignature.put("imageFile", parseFile);


        ArrayList<ParseObject> parseObjectArrayList = new ArrayList<>();
        parseObjectArrayList.add(mBusinessInfo);
        parseObjectArrayList.add(mPrincipalInfo);
        parseObjectArrayList.add(mAccountInfo);
        parseObjectArrayList.add(mSignature);


        ParseObject.saveAllInBackground(parseObjectArrayList, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mCCRA.put("businessInfo", mBusinessInfo);
                    mCCRA.put("principalInfo", mPrincipalInfo);
                    mCCRA.put("accountInfo", mAccountInfo);
                    mCCRA.put("signature", mSignature);


                    mCCRA.put("IPAddress", ApplicationClass.getMyApp().getLocalIpAddress());
                    mCCRA.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                sendEmail();
                                closeProgressDialog();
                                Intent i = new Intent(SignatureActivity.this, ThankYouActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(SignatureActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                                closeProgressDialog();
                                Log.e("error_ccra", e.getLocalizedMessage());
                            }
                        }
                    });

                } else {
                    Toast.makeText(SignatureActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                    closeProgressDialog();
                    Log.e("error_saveAll", e.getLocalizedMessage());
                }
            }
        });


    }

    private void updateDataOnParse(){
        mprogressDialog.setMessage("Please wait..");
        mprogressDialog.show();

        ParseFile parseFile = new ParseFile("Signature.png", Utility.loadByteArray(mGesture, 100, 100));
        mSignature = new ParseObject("Signature");
        mSignature.put("imageFile", parseFile);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("CCRA");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject entity, ParseException e) {
                if (e == null) {
                    entity.put("signature", mSignature);
                    entity.saveInBackground();
                    mprogressDialog.dismiss();

                    Intent i = new Intent(SignatureActivity.this, ThankYouActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(SignatureActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*By jugal*/
    public void PrintDocument(File dest) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(dest), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

    }

    /*by jugal -end*/

    private boolean uploadIRSLeadData() {
        if (mGesture == null) {
            return false;
        }
        File file = Utility.getFile(Utility.SIGNATURE_FILE, mGesture.toBitmap(100, 100, 8, Color.BLACK));
        if (file == null)
            return false;

        String path = Utility.getPdfDocumentPath(mGesture.toBitmap(100, 100, 8, Color.BLACK));
        if (path == null)
            return false;
        //PDF FILE
        File pdf = new File(path);

        //ACH FORM PDF

        File achPdfFile = null;
        if (pricingObject.has(PricingActivity.IS_SWIPED)) {
            byte[] signatureImageBytes = Utility.loadByteArray(mGesture, 400, 100);
            achPdfFile = mCreatePDF.createPdf(signatureImageBytes);
        }

        pricingObject.remove(PricingActivity.IS_SWIPED);

        mIrsLeadsWebservice.saveLeads(mCCRA, mPrincipalInfo, mBusinessInfo, mAccountInfo, file, pdf, achPdfFile, pricingObject);
        return true;
    }


    @AfterViews
    void init() {
        mprogressDialog = new ProgressDialog(SignatureActivity.this);
        initGesture();
        mCreatePDF = new CreatePDF(this, mCCRA, mBusinessInfo, mAccountInfo, pricingObject);

        //Toast.makeText(this,""+pricingObject.getDouble("credit_discount_rate"),Toast.LENGTH_LONG).show();
    }

  /*  private void initDexter() {
        Dexter.withActivity(SignatureActivity.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(
                        new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(SignatureActivity.this, "Accept permission to continue", Toast.LENGTH_LONG).show();

                                if (response.isPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }
                ).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignatureActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }*/


    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void initGesture() {
        tip_sign.setGestureColor(Color.BLACK);
        tip_sign.addOnGestureListener(new GestureOverlayView.OnGestureListener() {
            @Override
            public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {

            }

            @Override
            public void onGesture(GestureOverlayView overlay, MotionEvent event) {

            }

            @Override
            public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
                mGesture = overlay.getGesture();
                mIsSignatureEmpty = false;
                if (mGesture.getLength() < LENGTH_THRESHOLD) {
                    overlay.clear(true);
                    //mIsSignatureEmpty= true;
                }
            }

            @Override
            public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {

            }
        });

    }

//    @Click(R.id.txtViewAgreeToTerms)
//    void aggreToTerms() {
//        ShowNotification.showErrorDialog(SignatureActivity.this, terms);
//    }

    @Click(R.id.sign_clear)
    void signClear() {
        mIsSignatureEmpty = true;
        tip_sign.clear(true);
    }


    private void sendData() {

        //new APIOperation().execute(serverURL);

        mprogressDialog.setTitle("Saving");
        mprogressDialog.setMessage("Please wait..");
        mprogressDialog.show();
        saveDataOnParse();

    }

    class APIOperation extends AsyncTask<String, Void, Void> {
        private String Error = null;
        JSONObject jsonParams = new JSONObject();
        StringBuilder stringReader = new StringBuilder();
        HttpPost httpPost;
        String str;

        protected void onPreExecute() {
            mprogressDialog.setTitle("Saving");
            mprogressDialog.setMessage("Please wait..");
            mprogressDialog.show();
            try {
                if (mCCRA.get("source") != null) {
                    Object localObject = new JSONObject();
                    ((JSONObject) localObject).put("id", "67516");//
                    ((JSONObject) localObject).put("value", mCCRA.get("source").toString());
                    ((JSONObject) localObject).put("name", "source");
                    localObject = new JSONArray().put(localObject);
                    this.jsonParams.put("fields", localObject);
                    this.jsonParams.put("position", mCCRA.get("source"));
                }
                if (mCCRA.get("BusinessName") != null) {
                    this.jsonParams.put("company", mCCRA.get("BusinessName").toString());
                }

                if (mCCRA.get("Name") != null) {
                    this.jsonParams.put("full_name", mCCRA.get("Name").toString());
                }
                if (mCCRA.get("Phone") != null) {
                    this.jsonParams.put("mobile_phone", mCCRA.get("Phone").toString());
                }

                if (mCCRA.get("Email") != null) {
                    this.jsonParams.put("email_address", mCCRA.get("Email").toString());
                }
            } catch (JSONException localJSONException) {
                this.Error = localJSONException.getMessage();
            }
            Log.d(TAG, "Request: " + this.jsonParams.toString());
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
                    Log.d(TAG, "Success: " + localObject.getString("success"));

                    if (!localJSONObject.getString("id").equals("0")) {
                    }
                } catch (Exception ex) {
                    Log.d(TAG, "Error : " + ex.getMessage());
                }
                Log.d(TAG, "Output : " + localObject);
            }
            sendingDataToICSLeads();
            //sendingDataToNewCRM();

        }
    }


    private void closeProgressDialog() {
        if (mprogressDialog != null) {
            mprogressDialog.dismiss();
        }
    }

    public void sendEmail() {
        SendEmailThroughCloudCode(
                "leads@merchantaccountsolutions.com",
                mCCRA.get("Name").toString(),
                mCCRA.get("BusinessName").toString(),
                mCCRA.get("Email").toString(),
                mCCRA.get("Phone").toString(),
                mCCRA.get("Processing").toString(),
                mCCRA.get("source").toString()
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
//                                mprogressDialog.dismiss();
//                                Intent i = new Intent(VerificationActivity.this, ThankYouActivity.class);
//                                startActivity(i);
                                //Now below function will update data in Client User Table
                            } else {
                                Log.e("err:", e.toString());

//                                mprogressDialog.dismiss();
//                                Intent i = new Intent(VerificationActivity.this, ThankYouActivity.class);
//                                startActivity(i);

                            }
                        }
                    });
        } catch (Exception e) {
            Log.v("Err email1:", e.toString());
        }
    }

    private void sendingDataToICSLeads() {
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
                    String phone = mCCRA.get("Phone").toString();
                    //phone = "("+phone.substring(0,3)+") "+phone.substring(3,6)+"-"+phone.substring(6,10);
                    phone = phone.replace("-", "");
                    String fullName = mCCRA.get("Name").toString();
                    String isProcessing = mCCRA.get("Processing").toString();
                    if (isProcessing.equals("Yes")) {
                        isProcessing = "True";
                    } else {
                        isProcessing = "False";
                    }

                    try {
                        // Set Request parameter
                        data += "&" + URLEncoder.encode("source", "UTF-8") + "=" + mCCRA.get("source").toString();
                        data += "&" + URLEncoder.encode("originator", "UTF-8") + "=" + "1029";
                        data += "&" + URLEncoder.encode("returnType", "UTF-8") + "=" + "xml";
                        data += "&" + URLEncoder.encode("traceId", "UTF-8") + "=" + "1234567890";
                        data += "&" + URLEncoder.encode("businessName", "UTF-8") + "=" + mCCRA.get("BusinessName").toString();
                        data += "&" + URLEncoder.encode("contactName", "UTF-8") + "=" + fullName;
                        data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + phone;
                        data += "&" + URLEncoder.encode("phoneAlt", "UTF-8") + "=" + phone;

                        data += "&" + URLEncoder.encode("currentlyProcessing", "UTF-8") + "=" + isProcessing;
                        data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + mCCRA.get("Email");

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
                Log.i("IcsLeadsResponse: ", apiResponce);
                //mprogressDialog.dismiss();
                saveDataOnParse();
                if (Error != null) {
                   /* new AlertDialog.Builder(VerificationActivity.this)
                            .setTitle(R.string.app_name)
                            .setMessage(Error)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();*/
                    Log.i("Error : ", Error);
                }
//                Intent i = new Intent(SignatureActivity.this, ThankYouActivity.class);
//                startActivity(i);
            }
        }
       // new IcsLeadsAPIOperation().execute("https://www.icsleads.com/Api/AddLead/");
    }


}

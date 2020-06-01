package com.ics.creditcardprocessing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class ProcessingActivity extends Activity {
    private TextView action_yes, action_no;
    private String verificationCode;
    public ProgressDialog progressDialog;
    private String source = "", card_processing = "";

    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
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
                source = "CCPAY";
                card_processing = "Yes";
                progressDialog = new ProgressDialog(ProcessingActivity.this);
                progressDialog.setMessage("Please wait..");

                updateDataOnParse();

            }
        });
        action_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                source = "CCPA";
                card_processing = "No";
                progressDialog = new ProgressDialog(ProcessingActivity.this);

                updateDataOnParse();

            }
        });
    }

    private void startNewActivity() {
        ParseObject query = new ParseObject("CCPA");
        query.put("Name", getIntent().getStringExtra("first_name"));
        query.put("LastName", getIntent().getStringExtra("last_name"));
        query.put("BusinessName", getIntent().getStringExtra("company"));
        query.put("Email", getIntent().getStringExtra("email_address"));
        query.put("Phone", getIntent().getStringExtra("mobile_phone"));
        query.put("Processing", card_processing);
        query.put("source", source);
        BuisnessInfoActivity.start(this, query, getIntent().getStringExtra("objectId"), (ParseObject) getIntent().getExtras().get("pricingObject"));
    }

    private void updateDataOnParse() {
        progressDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CCPA");
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
}
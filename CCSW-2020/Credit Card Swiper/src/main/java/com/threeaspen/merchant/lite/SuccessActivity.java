package com.threeaspen.merchant.lite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class SuccessActivity extends Activity {
    private TextView action_next, action_cancel;
    private TextView total_sale_amt;
    private EditText phone;
    private String sale_amt = "0.00";
    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        if (getIntent().hasExtra("sale_amount")) {
            sale_amt = getIntent().getStringExtra("sale_amount");
        }

        if (action_next == null) {
            action_next = (TextView) findViewById(R.id.action_next);
        }
        if (action_cancel == null) {
            action_cancel = (TextView) findViewById(R.id.action_cancel);
        }
        if (total_sale_amt == null) {
            total_sale_amt = (TextView) findViewById(R.id.total_sale_amt);
        }
        if (phone == null) {
            phone = (EditText) findViewById(R.id.cust_phone);
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        total_sale_amt.setText(String.format("$%.2f", Double.parseDouble(sale_amt)));

        phone.addTextChangedListener(phoneWatcher);
        action_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SuccessActivity.this);
                builder.setTitle(R.string.lbl_activate_account)
                        .setView(getLayoutInflater().inflate(R.layout.success_popup, null))
                        .setNeutralButton(R.string.lbl_apply_phone, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Uri uri = Uri.parse("tel:18003353303");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.lbl_apply_now, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(SuccessActivity.this, SignUpActivity.class);
                                //finish();
                                startActivity(i);
                            }
                        })
                        .setPositiveButton(R.string.lbl_demo, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(SuccessActivity.this, HomeActivity.class);
                                //finish();
                                startActivity(i);
                            }
                        }).create();
                builder.show();
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


}

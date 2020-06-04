package com.pattrns.toycashregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.pattrns.toycashregister.helper.ShowNotification;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.kal.rackmonthpicker.listener.DateMonthDialogListener;
import com.kal.rackmonthpicker.listener.OnCancelMonthDialogListener;

import java.util.Calendar;
import java.util.Locale;

public class PaymentActivity extends Activity implements OnClickListener {
    private static final String TAG = "CCR - Payment";

    private String sale_amt = String.format("%.2f", 0.00);
    private TextView total_sale_amt, sales_tax_amt;
    private TextView action_next, action_cancel;
    private CheckBox cc_sales_tax;
    private double salex_tax_per = 10.0;
    private EditText cc_no, cc_exp, cc_cvv2, cc_zip, cc_st_add;
    private int mYearSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (getIntent().hasExtra("sale_amount")) {
            sale_amt = getIntent().getStringExtra("sale_amount");
        }
        if (sale_amt.length() <= 0) {
            sale_amt = String.format("%.2f", 0.00);
        } else {
            sale_amt = String.format("%.2f", Double.parseDouble(sale_amt));
        }
        if (total_sale_amt == null) {
            total_sale_amt = (TextView) findViewById(R.id.total_sale_amt);
        }
        if (sales_tax_amt == null) {
            sales_tax_amt = (TextView) findViewById(R.id.sales_tax_amt);
        }
        total_sale_amt.setText("$" + sale_amt);

        if (action_next == null) {
            action_next = (TextView) findViewById(R.id.action_next);
        }

        if (action_cancel == null) {
            action_cancel = (TextView) findViewById(R.id.action_cancel);
        }

        if (cc_sales_tax == null) {
            cc_sales_tax = (CheckBox) findViewById(R.id.cc_sales_tax);
        }

        if (cc_no == null) {
            cc_no = (EditText) findViewById(R.id.cc_no);
        }
        if (cc_exp == null) {
            cc_exp = (EditText) findViewById(R.id.cc_exp);
        }
        if (cc_cvv2 == null) {
            cc_cvv2 = (EditText) findViewById(R.id.cc_cvv2);
        }
        if (cc_zip == null) {
            cc_zip = (EditText) findViewById(R.id.cc_zip);
        }
        if (cc_st_add == null) {
            cc_st_add = (EditText) findViewById(R.id.cc_st_add);
        }


        //new EditTextDatePicker(this, cc_exp);
        cc_exp.findViewById(R.id.cc_exp).setOnClickListener(this);
        action_cancel.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();

        action_next.setOnClickListener(this);

        cc_sales_tax.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    double tax = ((Double.valueOf(sale_amt) * salex_tax_per) / 100);
                    sales_tax_amt.setText(String.format("$%.2f", tax));
                    total_sale_amt.setText(String.format("$%.2f", Double.valueOf(sale_amt) + tax));
                } else {
                    sales_tax_amt.setText(R.string.lbl_total_amt);
                    total_sale_amt.setText("$" + sale_amt);
                }
            }
        });

        cc_no.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (cc_no.getText().length() == 1) {
                    setCardType(cc_no.getText());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void setCardType(CharSequence no) {
        String visaRegex = "^4$";
        String masterRegex = "^5$";
        String amexRegex = "^3$";
        String discoverRegex = "^6$";

        cc_cvv2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        if (no.toString().matches(visaRegex)) {
            cc_no.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_visa, 0);
        } else if (no.toString().matches(masterRegex)) {
            cc_no.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_master, 0);
        } else if (no.toString().matches(amexRegex)) {
            cc_no.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_amex, 0);
            cc_cvv2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        } else if (no.toString().matches(discoverRegex)) {
            cc_no.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_discover, 0);
        } else {
            cc_no.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_next:

//                if (validateData()) {
                startNewActivity();
//                } else {
//
//                }

                break;

            case R.id.action_cancel:
                onBackPressed();
                break;


            case R.id.cc_exp:
                showExpiryDialog();
                break;
        }
        Log.d(TAG, cc_no.getText().toString());
    }

    private void showExpiryDialog() {
        new RackMonthPicker(PaymentActivity.this)
                .setLocale(Locale.ENGLISH)
                .setColorTheme(R.color.color_primary)
                .setPositiveButton(new DateMonthDialogListener() {
                    @Override
                    public void onDateMonth(int month, int startDate, int endDate, int year, String monthLabel) {
                        mYearSelected = year;
                        cc_exp.setText("" + String.valueOf(month) + "/" + String.valueOf(year % 100));
                    }
                })
                .setNegativeButton(new OnCancelMonthDialogListener() {
                    @Override
                    public void onCancel(AlertDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void callErrorNotification(View view, String content) {
        ShowNotification.showErrorDialog(PaymentActivity.this, content);
        view.requestFocus();
    }

    private boolean validateData() {


        if (TextUtils.isEmpty(cc_no.getText().toString().trim())) {
            callErrorNotification(cc_no, "Please enter credit card number");
            return false;
        }

        if (cc_no.length() < 16) {
            callErrorNotification(cc_no, "Invalid credit card number");
            return false;
        }


        if (TextUtils.isEmpty(cc_cvv2.getText().toString().trim())) {
            callErrorNotification(cc_cvv2, "Please enter CVV code");
            return false;
        }

        if (cc_cvv2.length() < 3) {
            callErrorNotification(cc_cvv2, "Invalid CVV code");
            return false;
        }

        if (TextUtils.isEmpty(cc_exp.getText().toString())) {
            callErrorNotification(cc_exp, "Invalid expiration date");
            return false;
        }

        if (Calendar.getInstance().get(Calendar.YEAR) > mYearSelected) {
            callErrorNotification(cc_exp, "year cannot be smaller than current year");
            return false;
        }


        if (TextUtils.isEmpty(cc_zip.getText().toString().trim())) {
            callErrorNotification(cc_zip, "Please enter zip code");
            return false;
        }

        if (cc_zip.length() < 5) {
            callErrorNotification(cc_zip, "Invalid zip code");
            return false;
        }


        return true;
    }

    private void startNewActivity() {
        Intent i = new Intent(PaymentActivity.this, TipActivity.class);
        if (cc_sales_tax.isChecked()) {
            i.putExtra("sale_amount", String.format("%.2f", Double.valueOf(sale_amt) + (Double.valueOf(sale_amt) * salex_tax_per) / 100));
        } else {
            i.putExtra("sale_amount", sale_amt);
        }
        i.putExtra("cc_no", cc_no.getText().toString());
        i.putExtra("cc_exp", cc_exp.getText().toString());
        i.putExtra("cc_cvv2", cc_cvv2.getText().toString());
        i.putExtra("cc_zip", cc_zip.getText().toString());
        i.putExtra("cc_st_add", cc_st_add.getText().toString());

        startActivity(i);
    }
}

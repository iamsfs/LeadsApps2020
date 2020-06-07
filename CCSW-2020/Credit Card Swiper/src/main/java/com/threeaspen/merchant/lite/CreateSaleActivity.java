package com.threeaspen.merchant.lite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateSaleActivity extends Activity implements OnClickListener {
    private static final String TAG = "CCR - Sale Amount";

    private TextView action_next, action_cancel;
    private EditText sale_amt;

    private boolean amountValidated = false;
    private String valAmount = "", tmpAmount = "";
    private char operator;
    private Button key_01, key_02, key_03, key_04, key_05, key_06, key_07, key_08, key_09, key_0, key_x,
            key_clear, key_devide, key_multiply, key_minus, key_plus, key_done, key_dot, key_equal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sale);

        if (action_next == null) {
            action_next = (TextView) findViewById(R.id.action_next);
        }
        if (action_cancel == null) {
            action_cancel = (TextView) findViewById(R.id.action_cancel);
        }
        if (sale_amt == null) {
            sale_amt = (EditText) findViewById(R.id.sale_amt);
        }
        findViewById(R.id.calculator).setVisibility(View.VISIBLE);
        key_01 = (Button) findViewById(R.id.key_01);
        key_02 = (Button) findViewById(R.id.key_02);
        key_03 = (Button) findViewById(R.id.key_03);
        key_04 = (Button) findViewById(R.id.key_04);
        key_05 = (Button) findViewById(R.id.key_05);
        key_06 = (Button) findViewById(R.id.key_06);
        key_07 = (Button) findViewById(R.id.key_07);
        key_08 = (Button) findViewById(R.id.key_08);
        key_09 = (Button) findViewById(R.id.key_09);
        key_0 = (Button) findViewById(R.id.key_0);
        key_x = (Button) findViewById(R.id.key_x);

        key_clear = (Button) findViewById(R.id.key_clear);
        key_devide = (Button) findViewById(R.id.key_devide);
        key_multiply = (Button) findViewById(R.id.key_multiply);
        key_minus = (Button) findViewById(R.id.key_minus);
        key_plus = (Button) findViewById(R.id.key_plus);
        key_done = (Button) findViewById(R.id.key_done);
        key_dot = (Button) findViewById(R.id.key_dot);
        key_equal = (Button) findViewById(R.id.key_equal);

    }


    @Override
    protected void onStart() {
        super.onStart();

        sale_amt.setOnClickListener(this);
        action_next.setOnClickListener(this);
        action_cancel.setOnClickListener(this);

        key_0.setOnClickListener(this);
        key_01.setOnClickListener(this);
        key_02.setOnClickListener(this);
        key_03.setOnClickListener(this);
        key_04.setOnClickListener(this);
        key_05.setOnClickListener(this);
        key_06.setOnClickListener(this);
        key_07.setOnClickListener(this);
        key_08.setOnClickListener(this);
        key_09.setOnClickListener(this);
        key_x.setOnClickListener(this);
        key_clear.setOnClickListener(this);
        key_devide.setOnClickListener(this);
        key_multiply.setOnClickListener(this);
        key_minus.setOnClickListener(this);
        key_plus.setOnClickListener(this);
        key_done.setOnClickListener(this);
        key_dot.setOnClickListener(this);
        key_equal.setOnClickListener(this);

        sale_amt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!amountValidated) {
                    //if(!amountValidated && s.toString().matches("^(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d+)?$")){
                    //String userInput = s.toString().replaceAll("[^\\d]", "");
                    String userInput = s.toString();
                    StringBuilder cashAmountBuilder = new StringBuilder(userInput);

                    //while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                    //    cashAmountBuilder.deleteCharAt(0);
                    //}
                    //while (cashAmountBuilder.length() < 3) {
                    //    cashAmountBuilder.insert(0, '0');
                    //}
                    //cashAmountBuilder.insert(cashAmountBuilder.length() - 2, '.');
                    //cashAmountBuilder.insert(0, '$');
                    amountValidated = true;
                    valAmount = cashAmountBuilder.toString();
                    setAmtValue(valAmount);
                    amountValidated = false;
                }

            }
        });
    }


    private void setAmtValue(String cashAmount) {
        sale_amt.setText(cashAmount);
        // keeps the cursor always to the right
        Selection.setSelection(sale_amt.getText(), sale_amt.getText().toString().length());
        Log.d(TAG, sale_amt.getText().toString());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sale_amt:
                findViewById(R.id.calculator).setVisibility(View.VISIBLE);
                break;
            case R.id.action_next:
                //if (validateData()) {
                startNewActivity();
                //} else {
                //  ShowNotification.showErrorDialog(CreateSaleActivity.this, "Enter Valid Amount");
                //}
                break;

            case R.id.action_cancel:
                onBackPressed();
                break;
            case R.id.key_0:
                valAmount = valAmount + "0";
                break;
            case R.id.key_01:
                valAmount = valAmount + "1";
                break;
            case R.id.key_02:
                valAmount = valAmount + "2";
                break;
            case R.id.key_03:
                valAmount = valAmount + "3";
                break;
            case R.id.key_04:
                valAmount = valAmount + "4";
                break;
            case R.id.key_05:
                valAmount = valAmount + "5";
                break;
            case R.id.key_06:
                valAmount = valAmount + "6";
                break;
            case R.id.key_07:
                valAmount = valAmount + "7";
                break;
            case R.id.key_08:
                valAmount = valAmount + "8";
                break;
            case R.id.key_09:
                valAmount = valAmount + "9";
                break;
            case R.id.key_dot:
                if (!valAmount.contains(".")) {
                    valAmount = valAmount + ".";
                }
                break;
            case R.id.key_x:
                if (valAmount.length() > 0) {
                    valAmount = valAmount.substring(0, (valAmount.length() - 1));
                }
                break;
            case R.id.key_clear:
                valAmount = "";
                break;
            case R.id.key_devide:
                tmpAmount = valAmount;
                valAmount = "";
                operator = '/';
                break;
            case R.id.key_multiply:
                tmpAmount = valAmount;
                valAmount = "";
                operator = '*';
                break;
            case R.id.key_minus:
                tmpAmount = valAmount;
                valAmount = "";
                operator = '-';
                break;
            case R.id.key_plus:
                tmpAmount = valAmount;
                valAmount = "";
                operator = '+';
                break;
            case R.id.key_done:
                findViewById(R.id.calculator).setVisibility(View.VISIBLE);
                break;
            case R.id.key_equal:
                if (valAmount.length() > 0 && tmpAmount.length() > 0) {
                    switch (operator) {
                        case '/':
                            if (valAmount.length() > 0 && Double.parseDouble(valAmount) != 0) {
                                valAmount = String.format("%.2f", (Double.parseDouble(tmpAmount) / Double.parseDouble(valAmount)));
                            }
                            break;
                        case '*':
                            valAmount = String.format("%.2f", (Double.parseDouble(tmpAmount) * Double.parseDouble(valAmount)));
                            break;
                        case '-':
                            valAmount = String.format("%.2f", (Double.parseDouble(tmpAmount) - Double.parseDouble(valAmount)));
                            break;
                        case '+':
                            valAmount = String.format("%.2f", (Double.parseDouble(tmpAmount) + Double.parseDouble(valAmount)));
                            break;
                    }
                }
                break;

        }
        if (v.getId() != R.id.key_devide && v.getId() != R.id.key_multiply && v.getId() != R.id.key_minus && v.getId() != R.id.key_plus)
            setAmtValue(valAmount);
    }

    private boolean validateData() {

        if (sale_amt.getText().toString().equals("0.00")) {
            return false;
        }

        try {

            double num = Double.parseDouble(sale_amt.getText().toString());
            if (num < 0.0) {
                return false;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void startNewActivity() {
        Intent i = new Intent(CreateSaleActivity.this, PaymentActivity.class);
        i.putExtra("sale_amount", sale_amt.getText().toString());
        startActivity(i);
    }
}

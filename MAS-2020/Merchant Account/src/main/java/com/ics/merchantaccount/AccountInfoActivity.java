package com.ics.merchantaccount;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ics.merchantaccount.helper.ShowNotification;
import com.ics.merchantaccount.utility.Utility;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

@EActivity(R.layout.activity_account_info)
public class AccountInfoActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;


    @ViewById(R.id.spinnerSell)
    Spinner mSpinnerSell;


    @ViewById(R.id.txtAverageSales)
    EditText mTxtAverageSales;
    @ViewById(R.id.txtMoSales)
    EditText mTxtMoSales;

    @ViewById(R.id.txtBankName)
    EditText mTxBankName;

    @ViewById(R.id.txtBankRoutingNumber)
    EditText mTxBankRoutingNumber;

    @ViewById(R.id.txtBankAccountNumber)
    EditText mTxBankAccountNumber;


    @StringArrayRes(R.array.sell_customers_type_array)
    String[] mSellType;

    private String mSellCustomersDropDown;
    private String mAverageSellCustomers;
    private String mCreditCardSales;
    private String mBankName;
    private String mBankRoutingNumber;
    private String mBankAccountNumber;



    @Extra
    ParseObject CCRA;
    @Extra
    ParseObject mBusinessInfo;
    @Extra
    ParseObject principalInfo;
    @Extra
    ParseObject pricingObject;

    @Extra
    String objectId;

    public static void start(Context context, String objectId, ParseObject CCRA, ParseObject businessInfo, ParseObject principalInfo, ParseObject pricingObject) {
        AccountInfoActivity_.intent(context)
                .objectId(objectId)
                .CCRA(CCRA)
                .mBusinessInfo(businessInfo)
                .principalInfo(principalInfo)
                .pricingObject(pricingObject)
                .start();
    }


    @AfterViews
    void init() {

        ArrayAdapter<CharSequence> ownerAdapter = ArrayAdapter.createFromResource(
                this, R.array.sell_customers_type_array, android.R.layout.simple_spinner_item);
        ownerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerSell.setAdapter(ownerAdapter);

        setListeners();
        //setSeed();

    }

    // TODO: 18/6/19 Remove before sharing
    private void setSeed() {
        mTxtAverageSales.setText("200");
        mTxtMoSales.setText("600");
        mTxBankName.setText("PNB");
        mTxBankRoutingNumber.setText("42040044");
        mTxBankAccountNumber.setText("OB11358181");


    }


    @Click(R.id.action_next)
    void onClick() {
        if (SignUpActivity.isNetworkAvailable(AccountInfoActivity.this)) {

            if (validateForm()) {

                updateDataOnParse();

            }
        } else {
            ShowNotification.showErrorDialog(AccountInfoActivity.this, "Internet Required");
        }
    }

    @Click(R.id.action_cancel)
    void onActionCancel() {
        onBackPressed();
    }

    private void updateDataOnParse(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        final ParseObject AccountInfo = new ParseObject("AccountInfo");
        AccountInfo.put("sellType", mSellCustomersDropDown);
        AccountInfo.put("averageSell", mAverageSellCustomers);
        AccountInfo.put("estimatedCCSale", mCreditCardSales);
        AccountInfo.put("bankName", mBankName);
        AccountInfo.put("routingNumber", mBankRoutingNumber);
        AccountInfo.put("accountNumber", mBankAccountNumber);



        ParseQuery<ParseObject> query = ParseQuery.getQuery(getResources().getString(R.string.source_and_class_name));
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject entity, ParseException e) {
                if (e == null) {
                    entity.put("accountInfo", AccountInfo);
                    entity.saveInBackground();
                    progressDialog.dismiss();
                    startNewActivity();
                }
                else{
                    Toast.makeText(AccountInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setListeners() {


        mSpinnerSell.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSellCustomersDropDown = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mTxtAverageSales.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && mTxtAverageSales.length() > 0 && mTxtAverageSales.getText().toString().charAt(0) != '$') {
                    mTxtAverageSales.setText("$" + mTxtAverageSales.getText().toString());
                }
            }
        });

        mTxtMoSales.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b && mTxtMoSales.length() > 0 && mTxtMoSales.getText().toString().charAt(0) != '$') {
                    mTxtMoSales.setText("$" + mTxtMoSales.getText().toString());
                }
            }
        });


    }

    private void startNewActivity() {
        ParseObject AccountInfo = new ParseObject("AccountInfo");
        AccountInfo.put("sellType", mSellCustomersDropDown);
        AccountInfo.put("averageSell", mAverageSellCustomers);
        AccountInfo.put("estimatedCCSale", mCreditCardSales);
        AccountInfo.put("bankName", mBankName);
        AccountInfo.put("routingNumber", mBankRoutingNumber);
        AccountInfo.put("accountNumber", mBankAccountNumber);

        if (Utility.getFlow(this) == 1) {

            Intent intent = new Intent(this, PricingActivity.class);
            intent.putExtra("objectId", objectId);
            intent.putExtra(getString(R.string.source_and_class_name), CCRA);
            intent.putExtra("BusinessInfo", mBusinessInfo);
            intent.putExtra("PrincipalInfo", principalInfo);
            intent.putExtra("AccountInfo", AccountInfo);
            startActivity(intent);


        } else if (Utility.getFlow(this) == 2) {
            TermsActivity.start(this, objectId, CCRA, mBusinessInfo, principalInfo, AccountInfo, pricingObject);
        }


    }

    private boolean validateForm() {

        mAverageSellCustomers = mTxtAverageSales.getText().toString().trim();
        mCreditCardSales = mTxtMoSales.getText().toString().trim();
        mBankName = mTxBankName.getText().toString().trim();
        mBankRoutingNumber = mTxBankRoutingNumber.getText().toString().trim();
        mBankAccountNumber = mTxBankAccountNumber.getText().toString().trim();

        if (mSpinnerSell.getSelectedItemPosition() == 0) {
            callErrorNotification(mSpinnerSell, "Please Select How Do You Sell To Your Customers");
            return false;
        }

        if (TextUtils.isEmpty(mAverageSellCustomers)) {
            callErrorNotification(mTxtAverageSales, "Enter Average Sales");
            return false;
        }


        if (TextUtils.isEmpty(mCreditCardSales)) {
            callErrorNotification(mTxtMoSales, "Enter Monthly Credit Card Sales");
            return false;
        }


        if (TextUtils.isEmpty(mBankName)) {
            callErrorNotification(mTxBankName, "Enter Valid Bank Name");
            return false;
        }


        if (TextUtils.isEmpty(mBankRoutingNumber)) {
            callErrorNotification(mTxBankRoutingNumber, "Enter Valid Routing Number");
            return false;
        }


        if (TextUtils.isEmpty(mBankAccountNumber)) {
            callErrorNotification(mTxBankAccountNumber, "Enter Valid Account Number");
            return false;
        }


        return true;
    }


    private void callErrorNotification(View view, String content) {
        ShowNotification.showErrorDialog(AccountInfoActivity.this, content);
        view.requestFocus();
    }
}

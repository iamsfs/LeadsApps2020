package com.ics.creditcardreader;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.ics.creditcardreader.helper.ShowNotification;
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

@EActivity(R.layout.activity_buisness_info)
public class BuisnessInfoActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;
    private Button mBtnNext;
    private RelativeLayout mRlContent;

    @ViewById(R.id.txtBusinessName)
    EditText mTxtBusinessName;


    @ViewById(R.id.txtStreetName)
    EditText mTxtStreetName;


    @ViewById(R.id.txtCity)
    EditText mTxtCity;

    @ViewById(R.id.txtState)
    EditText mTxtState;

    @ViewById(R.id.txtZipCode)
    EditText mTxtZipCode;

    @ViewById(R.id.txtPhoneNumber)
    EditText mTxtPhoneNumber;

    @ViewById(R.id.txtContent)
    EditText mTxtContent;

    @ViewById(R.id.txtFederal)
    EditText mTxtFedral;

    @StringArrayRes(R.array.owner_type_array)
    String[] mOwners;

    @ViewById(R.id.spinnerOwner)
    Spinner mSpinnerOwnershipType;

//    @ViewById(R.id.check_box_federal)
//    CheckBox mCheckBoxTax;


    private String mPhoneNumber = "";
    private String mTax = "";
    private String mOwnerType;
    private String mEmail;
    String taxStr = "";
    public static int staticNum = 0;
    public String PhoneNumberStr = "";

    @Extra
    ParseObject mCCRA;

    @Extra("Tocken_ID")
    String mTouchAbleId;

    @Extra
    ParseObject pricingObject;

    @Extra
    String objectId;


    public static void start(Context context, ParseObject query, String objectId, ParseObject pricingObject) {
        BuisnessInfoActivity_.intent(context)
                .mCCRA(query)
                .objectId(objectId)
                .pricingObject(pricingObject)
                .start();
    }


    @AfterViews
    void init() {


        ArrayAdapter<CharSequence> ownerAdapter = ArrayAdapter.createFromResource(
                this, R.array.owner_type_array, android.R.layout.simple_spinner_item);
        ownerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerOwnershipType.setAdapter(ownerAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");

        setDefaults();
        setListeners();

        setFilters();

        // setSeed();

    }

    private void setFilters() {
        InputFilter[] editFilters = mTxtState.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        mTxtState.setFilters(newFilters);
    }

    private void setDefaults() {
        if (mCCRA != null) {
            mTxtBusinessName.setText(mCCRA.get("BusinessName").toString());
            mTxtPhoneNumber.setText(mCCRA.get("Phone").toString());
        }
    }


    @Click(R.id.btnNext)
    void onNext() {
        if (validateForm()) {
            updateDataOnParse();
        }
    }

    private void updateDataOnParse(){
        progressDialog.show();

        String businessName = mTxtBusinessName.getText().toString().trim();
        String streetName = mTxtStreetName.getText().toString().trim();
        String city = mTxtCity.getText().toString().trim();
        String state = mTxtState.getText().toString().trim();
        String zipCode = mTxtZipCode.getText().toString().trim();
        mPhoneNumber = mTxtPhoneNumber.getText().toString();
        String content = mTxtContent.getText().toString().trim();


        final ParseObject BusinessInfo = new ParseObject("BusinessInfo");
        BusinessInfo.put("businessName", businessName);
        BusinessInfo.put("streetName", streetName);
        BusinessInfo.put("city", city);
        BusinessInfo.put("state", state);
        BusinessInfo.put("zipCode", zipCode);
        BusinessInfo.put("phoneNumber", mPhoneNumber);
        BusinessInfo.put("productDescription", content);
        BusinessInfo.put("federalTaxId", !mOwnerType.equalsIgnoreCase("Sole Proprietorship") ? mTax : "");
        BusinessInfo.put("businessType", mOwnerType);



        ParseQuery<ParseObject> query = ParseQuery.getQuery("CCRA");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject entity, ParseException e) {
                if (e == null) {
                    entity.put("businessInfo", BusinessInfo);
                    entity.saveInBackground();
                    progressDialog.dismiss();
                    startNewActivity();
                }
                else{
                    Toast.makeText(BuisnessInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setListeners() {
        mSpinnerOwnershipType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mOwnerType = parent.getItemAtPosition(position).toString();
                if (position != 0 && !mOwnerType.equalsIgnoreCase("Sole Proprietorship")) {
                    mTxtFedral.setVisibility(View.VISIBLE);
                } else {
                    mTxtFedral.setVisibility(View.GONE);
                    mTxtFedral.setText("");
                    mTax = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mTxtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (mPhoneNumber.length() < s.length()) {
//                    //s.clear();s.insert(0, s.toString().replaceAll("[\\W]", ""));
//                    switch (s.length()) {
//                        case 1:
//                            if (s.charAt(0) == '0' || s.charAt(0) == '1') {
//                                s.delete(0, 1);
//                            } else {
//                                //s.insert(0, "(");
//                            }
//                            break;
//                        case 3:
//                            s.insert(3, "-");
//                            break;
//                        case 7:
//                            s.insert(7, "-");
//                            break;
//                        case 10:
//                            //s.insert(9, "-");
//                            break;
//                    }
//                }


                if (s.length() == 1) {
                    if (s.charAt(0) == '0' || s.charAt(0) == '1') {
                        s.delete(0, 1);
                    }
                }

                if (s.length() > 3) {
                    if ((int) s.charAt(3) != 45) {
                        s.insert(3, "-");
                    }
                }

                if (s.length() > 7) {
                    if ((int) s.charAt(7) != 45) {
                        s.insert(7, "-");
                    }
                }

                mPhoneNumber = s.toString();
            }
        });

        mTxtFedral.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 2) {

                    if (s.charAt(1) == '0' || s.charAt(1) == '-') {
                        mTax = s.toString();
                        return;
                    }

                    if ((int) s.charAt(2) != 45) {
                        s.insert(2, "-");
                    }
                }
                mTax = s.toString();
            }
        });

    }

    private void startNewActivity() {

        String businessName = mTxtBusinessName.getText().toString().trim();
        String streetName = mTxtStreetName.getText().toString().trim();
        String city = mTxtCity.getText().toString().trim();
        String state = mTxtState.getText().toString().trim();
        String zipCode = mTxtZipCode.getText().toString().trim();
        mPhoneNumber = mTxtPhoneNumber.getText().toString();
        String content = mTxtContent.getText().toString().trim();
//        if (mCheckBoxTax.isChecked()) {
//            mTax = mTxtFedral.getText().toString();
//        }

        ParseObject BusinessInfo = new ParseObject("BusinessInfo");
        BusinessInfo.put("businessName", businessName);
        BusinessInfo.put("streetName", streetName);
        BusinessInfo.put("city", city);
        BusinessInfo.put("state", state);
        BusinessInfo.put("zipCode", zipCode);
        BusinessInfo.put("phoneNumber", mPhoneNumber);
        BusinessInfo.put("productDescription", content);
        BusinessInfo.put("federalTaxId", !mOwnerType.equalsIgnoreCase("Sole Proprietorship") ? mTax : "");
        BusinessInfo.put("businessType", mOwnerType);

        //Log.e("check", streetName);

        PrincipalInfoActivity.start(this, objectId, mCCRA, BusinessInfo, pricingObject);
    }

    private boolean validateForm() {

        if (TextUtils.isEmpty(mTxtBusinessName.getText().toString().trim())) {
            callErrorNotification(mTxtBusinessName, "Please enter business name");
            return false;
        }
        if (TextUtils.isEmpty(mTxtStreetName.getText().toString().trim())) {
            callErrorNotification(mTxtStreetName, "Please enter street name");
            return false;
        }

        if (TextUtils.isEmpty(mTxtCity.getText().toString().trim())) {
            callErrorNotification(mTxtCity, "Please enter city");
            return false;
        }

        if (TextUtils.isEmpty(mTxtState.getText().toString().trim())) {
            callErrorNotification(mTxtState, "Please enter state");
            return false;
        }

        if (TextUtils.isEmpty(mTxtZipCode.getText().toString().trim())) {
            callErrorNotification(mTxtZipCode, "Please enter zip code");
            return false;
        }

        if (mTxtZipCode.length() < 5) {
            callErrorNotification(mTxtZipCode, "Invalid zip code");
            return false;
        }


        if (TextUtils.isEmpty(mTxtPhoneNumber.getText().toString().trim())) {
            callErrorNotification(mTxtPhoneNumber, "Enter phone number");
            return false;
        } else {
            String regexStr = "^[+]?[01]?[- .]?(\\([2-9]\\d{2}\\)|[2-9]\\d{2})[- .]?\\d{3}[- .]?\\d{4}$";
            String number = mTxtPhoneNumber.getText().toString();
            if (number.length() < 10 || number.length() > 14 || !number.matches(regexStr)) {
                callErrorNotification(mTxtPhoneNumber, "Enter correct phone number");
                return false;
            }
        }

        if (TextUtils.isEmpty(mTxtContent.getText().toString().trim())) {
            callErrorNotification(mTxtContent, "Please enter products and services your business sells");
            return false;
        }


        if (mSpinnerOwnershipType.getSelectedItemPosition() == 0) {
            callErrorNotification(mSpinnerOwnershipType, "Please select business type");
            return false;
        }

        if (!mOwnerType.equalsIgnoreCase("Sole Proprietorship") && TextUtils.isEmpty(mTxtFedral.getText().toString().trim())) {
            callErrorNotification(mTxtContent, "Please Enter Federal Tax ID");
            return false;
        }

        if (!mOwnerType.equalsIgnoreCase("Sole Proprietorship") && (mTxtFedral.length() < 10 || mTax.indexOf("-") != 2)) {
            callErrorNotification(mTxtContent, "Invalid Tax ID");
            return false;
        }


        return true;
    }

    private void callErrorNotification(View view, String content) {
        ShowNotification.showErrorDialog(BuisnessInfoActivity.this, content);
        view.requestFocus();
    }
}

package com.ics.merchantaccount;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.ics.merchantaccount.helper.ShowNotification;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;

@EActivity(R.layout.activity_principal_info)
public class PrincipalInfoActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;

    @ViewById(R.id.cbPrincipal)
    CheckBox mCbPrincipal;


    @ViewById(R.id.txtAddress)
    EditText mTxtAddress;
    @ViewById(R.id.txtCity)
    EditText mTxtCity;
    @ViewById(R.id.txtState)
    EditText mTxtState;
    @ViewById(R.id.txtZipCode)
    EditText mTxtZipCode;
    @ViewById(R.id.txtMM)
    EditText mTxtMM;
    @ViewById(R.id.txtDD)
    EditText mTxtDD;
    @ViewById(R.id.txtYYY)
    EditText mTxtYYY;
    @ViewById(R.id.txt000)
    EditText mTxt000;
    @ViewById(R.id.txt00)
    EditText mTxt00;
    @ViewById(R.id.txt0000)
    EditText mTxt0000;
    @ViewById(R.id.txtDLState)
    EditText mTxtDLSTATE;
    @ViewById(R.id.txtLicenseNumber)
    EditText mTxtLicenseNumber;


    boolean isUncheck = false;
    private int mCurrentYear;

    private String mAddress;
    private String mCity;
    private String mState;
    private String mZipCode;
    private String mDob;
    private String mSsn;
    private String mLicenseNumber;
    private String mDLState;


    @Extra
    ParseObject CCRA;
    @Extra
    ParseObject mBusinessInfo;

    @Extra
    ParseObject pricingObject;

    @Extra
    String objectId;

    public static void start(Context context, String objectId, ParseObject CCRA, ParseObject businessInfo, ParseObject pricingObject) {
        PrincipalInfoActivity_.intent(context)
                .objectId(objectId)
                .CCRA(CCRA)
                .mBusinessInfo(businessInfo)
                .pricingObject(pricingObject)
                .start();
    }


    @Click(R.id.action_next)
    void onNext() {
        if (validateForm()) {
            updateDataOnParse();
        }
    }

    private void updateDataOnParse(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        final ParseObject principalInfo = new ParseObject("PrincipalInfo");
        principalInfo.put("city", mCity);
        principalInfo.put("zipCode", mZipCode);
        principalInfo.put("ssn", mSsn);
        principalInfo.put("state", mState);
        principalInfo.put("address", mAddress);
        principalInfo.put("dob", mDob);
        principalInfo.put("licenseNumber", mLicenseNumber);
        principalInfo.put("dlState", mDLState);



        ParseQuery<ParseObject> query = ParseQuery.getQuery(getResources().getString(R.string.source_and_class_name));
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject entity, ParseException e) {
                if (e == null) {
                    entity.put("principalInfo", principalInfo);
                    entity.saveInBackground();
                    progressDialog.dismiss();
                    startNewActivity();
                }
                else{
                    Toast.makeText(PrincipalInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Click(R.id.action_cancel)
    void onActionCancel() {
        onBackPressed();
    }

    private void requestFocusNext(EditText currentEt, final EditText nextEt, final int lengthCount) {
        currentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == lengthCount) {
                    nextEt.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setFilters(EditText editText) {
        InputFilter[] editFilters = editText.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        editText.setFilters(newFilters);

    }


    @AfterViews
    void setListeners() {
        requestFocusNext(mTxtMM, mTxtDD, 2);
        requestFocusNext(mTxtDD, mTxtYYY, 2);
        requestFocusNext(mTxtYYY, mTxt000, 4);
        requestFocusNext(mTxt000, mTxt00, 3);
        requestFocusNext(mTxt00, mTxt0000, 2);


        mTxtMM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTxtMM.length() >= 2) {
                    int month = Integer.parseInt(s.toString().trim());
                    if (month > 12 || month < 1) {

                        callErrorNotification(mTxtMM, "Month Should be between 1 and 12.");

                    }
                }
            }
        });

        mTxtDD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTxtDD.length() >= 2) {
                    int day = Integer.parseInt(s.toString().trim());
                    if (day > 31 || day < 1) {


                        callErrorNotification(mTxtDD, "Day Should be between 1 and 31.");

                    }
                }
            }
        });
        mCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        mTxtYYY.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTxtYYY.length() >= 4) {
                    int year = Integer.parseInt(s.toString().trim());
                    if (year > mCurrentYear || year < mCurrentYear - 100) {
                        callErrorNotification(mTxtYYY, "Enter correct year.");
                    }
                }
            }
        });


        mCbPrincipal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isUncheck = false;
                    if (mBusinessInfo != null) {
                        mTxtAddress.setText(mBusinessInfo.get("streetName").toString());
                        mTxtCity.setText(mBusinessInfo.get("city").toString());
                        mTxtState.setText(mBusinessInfo.get("state").toString());
                        mTxtZipCode.setText(mBusinessInfo.get("zipCode").toString());
                    }

                } else {
                    isUncheck = true;
                    mTxtAddress.setText("");
                    mTxtCity.setText("");
                    mTxtZipCode.setText("");
                    mTxtState.setText("");
                }
            }
        });

        setFilters(mTxtState);
        setFilters(mTxtDLSTATE);

    }

    private void startNewActivity() {
        ParseObject principalInfo = new ParseObject("PrincipalInfo");
        principalInfo.put("city", mCity);
        principalInfo.put("zipCode", mZipCode);
        principalInfo.put("ssn", mSsn);
        principalInfo.put("state", mState);
        principalInfo.put("address", mAddress);
        principalInfo.put("dob", mDob);
        principalInfo.put("licenseNumber", mLicenseNumber);
        principalInfo.put("dlState", mDLState);
        AccountInfoActivity.start(this,objectId , CCRA, mBusinessInfo, principalInfo, pricingObject);
    }

    private boolean validateForm() {

        mAddress = mTxtAddress.getText().toString().trim();
        mCity = mTxtCity.getText().toString().trim();
        mState = mTxtState.getText().toString().trim();
        mZipCode = mTxtZipCode.getText().toString().trim();
        String day = mTxtDD.getText().toString().trim();
        String month = mTxtMM.getText().toString().trim();
        String year = mTxtYYY.getText().toString().trim();
        String ssn3 = mTxt000.getText().toString().trim();
        String ssn2 = mTxt00.getText().toString().trim();
        String ssn4 = mTxt0000.getText().toString().trim();
        mDob = month + "/" + day + "/" + year;
        mSsn = ssn3 + "-" + ssn2 + "-" + ssn4;
        mLicenseNumber = mTxtLicenseNumber.getText().toString().trim();
        mDLState = mTxtDLSTATE.getText().toString().trim();

        if (TextUtils.isEmpty(mAddress)) {
            callErrorNotification(mTxtAddress, "Enter Address.");
            return false;
        }
        if (TextUtils.isEmpty(mCity)) {
            callErrorNotification(mTxtCity, "Enter City.");
            return false;
        }

        if (TextUtils.isEmpty(mState)) {
            callErrorNotification(mTxtState, "Enter State");
            return false;
        }

        if (TextUtils.isEmpty(mZipCode)) {
            callErrorNotification(mTxtZipCode, "Enter ZipCode");
            return false;
        }
        if (mZipCode.length() < 5) {
            callErrorNotification(mTxtZipCode, "Invalid ZipCode");
            return false;
        }

        if (TextUtils.isEmpty(mLicenseNumber)) {
            callErrorNotification(mTxtLicenseNumber, "Enter license number");
            return false;
        }

        if (TextUtils.isEmpty(mDLState)) {
            callErrorNotification(mTxtDLSTATE, "Enter driver license state");
            return false;
        }


        if (TextUtils.isEmpty(month)) {
            callErrorNotification(mTxtMM, "Enter Month");
            return false;
        }


        if (TextUtils.isEmpty(day)) {
            callErrorNotification(mTxtDD, "Enter Day");
            return false;
        }

        if (TextUtils.isEmpty(year)) {
            callErrorNotification(mTxtYYY, "Enter Year");
            return false;
        }

        if (Integer.parseInt(month) > 12 || Integer.parseInt(month) <= 0) {
            callErrorNotification(mTxtMM, "Month should be between 1 and 12");
            return false;
        }
        if (Integer.parseInt(day) > 31 || Integer.parseInt(day) <= 0) {
            callErrorNotification(mTxtDD, "Day should be between 1 and 31");
            return false;
        }
        if ((Integer.parseInt(year) >= mCurrentYear || Integer.parseInt(year) < mCurrentYear - 100)) {
            callErrorNotification(mTxtYYY, "Enter correct year");
            return false;
        }
        if (TextUtils.isEmpty(ssn3)) {
            callErrorNotification(mTxt000, "Enter SSN");
            return false;
        }
        if (TextUtils.isEmpty(ssn2)) {
            callErrorNotification(mTxt00, "Enter SSN");
            return false;
        }
        if (TextUtils.isEmpty(ssn4)) {
            callErrorNotification(mTxt0000, "Enter SSN");
            return false;
        }

        if (ssn3.equals("000") && ssn2.equals("00") && ssn4.equals("0000")) {
            callErrorNotification(mTxt000, "SSN invalid");
            return false;
        }
        if (ssn3.length() < 3 || ssn2.length() < 2 || ssn4.length() < 4) {
            callErrorNotification(mTxt000, "SSN invalid");
            return false;
        }


        return true;
    }


    private void callErrorNotification(View view, String content) {
        ShowNotification.showErrorDialog(PrincipalInfoActivity.this, content);
        view.requestFocus();
    }
}

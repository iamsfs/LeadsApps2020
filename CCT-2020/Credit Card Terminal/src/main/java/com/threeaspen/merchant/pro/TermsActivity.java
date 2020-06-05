package com.threeaspen.merchant.pro;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.threeaspen.merchant.pro.helper.ShowNotification;
import com.threeaspen.merchant.pro.helper.ShowTermsAndCondtionsDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_terms)
public class TermsActivity extends AppCompatActivity implements View.OnClickListener {

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;
    @ViewById(R.id.btnRefresh)
    Button mButtonRefresh;
//    @ViewById(R.id.scrollViewTerms)
//    ScrollView mScrollViewTerms;
//    @ViewById(R.id.txtTerms)
//    TextView mTextViewTerms;

    @ViewById(R.id.realtiveLayoutTermsHolder)
    RelativeLayout mRelativeLayoutTermsHolder;

    @ViewById(R.id.check_box_terms_one)
    CheckBox mCheckBoxTermsOne;

    @ViewById(R.id.check_box_terms_two)
    CheckBox mCheckBoxTermsTwo;

    @ViewById(R.id.txtViewAcceptTermsOneLink)
    TextView mTextViewTermsOne;

    @ViewById(R.id.txtViewAcceptTermsTwoLink)
    TextView mTextViewTermsTwo;

    @ViewById(R.id.btnNext)
    TextView mBtnNext;

    private String mTerms;
    private String mTermsPaySafe;
    private String mTermsPosPros;

    @Extra
    ParseObject CCRA;
    @Extra
    ParseObject mBusinessInfo;
    @Extra
    ParseObject principalInfo;
    @Extra
    ParseObject accountInfo;
    @Extra
    ParseObject pricingObject;


    @Extra
    String objectId;


    public static void start(Context context, String objectId, ParseObject CCRA, ParseObject businessInfo,
                             ParseObject principalInfo, ParseObject accountInfo, ParseObject pricingObject) {
        TermsActivity_.intent(context)
                .objectId(objectId)
                .CCRA(CCRA)
                .mBusinessInfo(businessInfo)
                .principalInfo(principalInfo)
                .accountInfo(accountInfo)
                .pricingObject(pricingObject)
                .start();
    }


    @AfterViews
    void init() {

        setListeners();

        //checkNet();

//        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mProgressBar.setVisibility(View.VISIBLE);
//                mButtonRefresh.setVisibility(View.GONE);
//                checkNet();
//            }
//        });


    }

    private void setListeners() {
        mTextViewTermsOne.setOnClickListener(this);
        mTextViewTermsTwo.setOnClickListener(this);
    }

    private void checkNet() {
        if (SignUpActivity.isNetworkAvailable(TermsActivity.this)) {
            callServer();
        } else {
            mButtonRefresh.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            //mScrollViewTerms.setVisibility(View.INVISIBLE);
            mRelativeLayoutTermsHolder.setVisibility(View.INVISIBLE);
            //mCheckBoxTermsOne.setVisibility(View.INVISIBLE);
            mBtnNext.setVisibility(View.INVISIBLE);
        }
    }

    @Click(R.id.btnNext)
    void onClick() {
        if (mCheckBoxTermsOne.isChecked() && mCheckBoxTermsTwo.isChecked()) {
            SignatureActivity.start(this, objectId, CCRA, mBusinessInfo, principalInfo, accountInfo, mTerms, pricingObject);
        } else {
            ShowNotification.showErrorDialog(TermsActivity.this, "Accept Both Terms & Conditions");
        }
    }

    @Click(R.id.action_cancel)
    void onActionCancel() {
        onBackPressed();
    }


    private void callServer() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TermsAndConditions");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.e("Terms", "Called");
                    ParseObject terms = objects.get(0);
                    mTerms = terms.get("terms_and_conditions").toString();
                    mTermsPaySafe = terms.get("terms_and_conditions_pay_safe").toString();
                    mTermsPosPros = terms.get("terms_and_conditions_pos_pros_content").toString();
                    //mTextViewTerms.setText(mTerms.replaceAll("\\s", " "));
                    mButtonRefresh.setVisibility(View.INVISIBLE);
                    //mScrollViewTerms.setVisibility(View.VISIBLE);
                    mRelativeLayoutTermsHolder.setVisibility(View.VISIBLE);
                    //mCheckBoxTermsOne.setVisibility(View.VISIBLE);
                    mBtnNext.setVisibility(View.VISIBLE);
                } else {
                    Log.e("Terms", e.getLocalizedMessage());
                    //mScrollViewTerms.setVisibility(View.INVISIBLE);
                    mRelativeLayoutTermsHolder.setVisibility(View.INVISIBLE);
                    //mCheckBoxTermsOne.setVisibility(View.INVISIBLE);
                    mBtnNext.setVisibility(View.INVISIBLE);
                    mButtonRefresh.setVisibility(View.VISIBLE);
                }
                mProgressBar.setVisibility(View.INVISIBLE);

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtViewAcceptTermsOneLink: {
                callTermsDialog(R.id.scrollViewDialogPaySafe);
                break;
            }

            case R.id.txtViewAcceptTermsTwoLink: {
                callTermsDialog(R.id.scrollViewDialog);
                break;
            }

            default: {

            }
        }


    }

    private void callTermsDialog(final int id) {
        mProgressBar.setVisibility(View.VISIBLE);
        mTextViewTermsOne.setOnClickListener(null);
        mTextViewTermsTwo.setOnClickListener(null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowTermsAndCondtionsDialog.showTerms(TermsActivity.this, id, CCRA, mProgressBar);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setListeners();
                    }
                }, 1000);
            }
        }, 1000);

    }
}

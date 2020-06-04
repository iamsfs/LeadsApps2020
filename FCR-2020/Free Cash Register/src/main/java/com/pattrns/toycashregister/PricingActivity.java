package com.pattrns.toycashregister;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pattrns.toycashregister.helper.ShowNotification;
import com.pattrns.toycashregister.utility.Utility;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class PricingActivity extends Activity implements OnClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = "CCR - Pricing";
    public static final String IS_SWIPED = "is_swiped";

    //private TextView pricing_apply;
    private TextView action_call, action_back;
    ProgressBar mProgressBar;
    Button mButtonRefresh;
//    private LinearLayout mLinearLayoutPricing;
//    private TextView mDebitRate;
//    private TextView mTransactionFee;
//    private TextView mSetUpFee;
//    private TextView mCardReader;
//    private TextView mAdditionalUsers;

    private LinearLayout mLinearLayoutPricingContainer;
    private TextView mTextViewKeyedDiscountRate;
    private TextView mTextViewKeyedMonthlyFee;
    private TextView mTextViewSwipedDiscountRate;
    private TextView mTextViewSwipedMonthlyFee;
    private TextView mTextViewCCRPrice;
    private TextView mTextViewOtherFees;

    private ParseObject mKeyedParseObject;
    private ParseObject mSwipedParseObject;
    private String keyedInfoText;
    private String swipedInfoText;


    private ImageView mImageViewCardReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);

//        if (pricing_apply == null) {
//            pricing_apply = (TextView) findViewById(R.id.pricing_apply);
//        }
        if (action_call == null) {
            action_call = (TextView) findViewById(R.id.action_call);
        }

        if (action_back == null) {
            action_back = (TextView) findViewById(R.id.action_cancel);
        }

        mButtonRefresh = (Button) findViewById(R.id.btnRefresh);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        mLinearLayoutPricing = (LinearLayout)findViewById(R.id.ll_pricing);
//        mDebitRate = (TextView)findViewById(R.id.txtDebitRate);
//        mTransactionFee = (TextView)findViewById(R.id.txtTransactionFee);
//        mSetUpFee = (TextView)findViewById(R.id.txtSetUpFee);
//        mCardReader = (TextView)findViewById(R.id.txtCardReader);
//        mAdditionalUsers = (TextView)findViewById(R.id.txtAdditionalUser);

        mLinearLayoutPricingContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
        mTextViewKeyedDiscountRate = (TextView) findViewById(R.id.txtViewDiscountValueKeyed);
        mTextViewKeyedMonthlyFee = (TextView) findViewById(R.id.txtViewMonthlyValueKeyed);
        mTextViewSwipedDiscountRate = (TextView) findViewById(R.id.txtViewDiscountValueSwiped);
        mTextViewSwipedMonthlyFee = (TextView) findViewById(R.id.txtViewMonthlyValueSwiped);
        mTextViewCCRPrice = (TextView) findViewById(R.id.txtViewCardReaderPrice);
        mTextViewOtherFees = findViewById(R.id.txtViewOtherFees);

        mImageViewCardReader = (ImageView) findViewById(R.id.imgViewReader);


        keyedInfoText = getResources().getString(R.string.string_keyed_info_text);
        swipedInfoText = getResources().getString(R.string.string_swiped_info_text);

        setListeners();

        checkNet();

        mButtonRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNet();
            }
        });


    }

    private void setListeners() {
        findViewById(R.id.imgViewKeyedSelect).setOnClickListener(this);
        findViewById(R.id.imgViewSwipedSelect).setOnClickListener(this);
        findViewById(R.id.realtiveLayoutKeyed).setOnClickListener(this);
        findViewById(R.id.realtiveLayoutSwiped).setOnClickListener(this);
        findViewById(R.id.imgViewKeyedInfo).setOnClickListener(this);
        findViewById(R.id.imgViewSwipedInfo).setOnClickListener(this);
    }

    private void checkNet() {
        if (SignUpActivity.isNetworkAvailable(PricingActivity.this)) {
            mProgressBar.setVisibility(View.VISIBLE);
            mButtonRefresh.setVisibility(View.GONE);
            callServer();
        } else {
            mButtonRefresh.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mLinearLayoutPricingContainer.setVisibility(View.GONE);
            //mLinearLayoutPricing.setVisibility(View.GONE);
        }
    }

    private void callServer() {
        ParseQuery<ParseObject> queryKeyed = ParseQuery.getQuery("Keyed");
        final ParseQuery<ParseObject> querySwiped = ParseQuery.getQuery("Swiped");
        final ParseQuery<ParseObject> queryPricing = ParseQuery.getQuery("Pricing");


        queryKeyed.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.e("Pricing", "Called");
                    final ParseObject keyedObject = objects.get(0);
                    mKeyedParseObject = keyedObject;
//                    mDebitRate.setText(keyedObject.get("debit_rate").toString());
//                    mTransactionFee.setText(keyedObject.get("transaction_fee").toString());
//                    mSetUpFee.setText(keyedObject.get("setup_fee").toString());
//                    mCardReader.setText(keyedObject.get("card_reader").toString());
//                    mAdditionalUsers.setText(keyedObject.get("additional_user").toString());
                    //mLinearLayoutPricing.setVisibility(View.VISIBLE);

                    querySwiped.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                final ParseObject swipedObject = objects.get(0);
                                mSwipedParseObject = swipedObject;


                                queryPricing.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        final ParseObject pricingObject = objects.get(0);


                                        if (e == null) {

                                            if (keyedObject != null && swipedObject != null && pricingObject != null) {
                                                callGlide(pricingObject.getParseFile("card_reader_image").getUrl());
                                                mButtonRefresh.setVisibility(View.GONE);
                                                mTextViewKeyedDiscountRate.setText("" + parseData(keyedObject, "credit_discount_rate") + "% + $" + parseData(keyedObject, "amex_txn_fee"));
                                                mTextViewKeyedMonthlyFee.setText("$" + parseData(keyedObject, "statement_fee"));
                                                mTextViewSwipedDiscountRate.setText("" + parseData(swipedObject, "credit_discount_rate") + "% + $" + parseData(swipedObject, "amex_txn_fee"));
                                                mTextViewSwipedMonthlyFee.setText("$" + parseData(swipedObject, "statement_fee"));
                                                mTextViewCCRPrice.setText("$" + parseData(swipedObject, "ccr_price") + " ea.");
                                                mProgressBar.setVisibility(View.GONE);
                                                mLinearLayoutPricingContainer.setVisibility(View.VISIBLE);
                                                mTextViewOtherFees.setVisibility(View.VISIBLE);
                                            }

                                        } else {
                                            Log.e("Pricing", e.getLocalizedMessage());
                                            mButtonRefresh.setVisibility(View.VISIBLE);
                                        }


                                    }
                                });


                            } else {
                                Log.e("Pricing", e.getLocalizedMessage());
                                mButtonRefresh.setVisibility(View.VISIBLE);
                            }
                        }
                    });


                } else {
                    Log.e("Pricing", e.getLocalizedMessage());
                    //mLinearLayoutPricing.setVisibility(View.GONE);
                    mButtonRefresh.setVisibility(View.VISIBLE);
                }

            }
        });


    }

    private void callGlide(String url) {
        Glide.with(this)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(mImageViewCardReader);

    }

    private String parseData(ParseObject data, String key) {
        double value = data.getDouble(key);
        String cc = String.valueOf(value);
        int index = cc.indexOf(".");

        return index == -1 ? cc : (cc.substring(index + 1).length() == 1 ? cc + "0" : cc);

        //return (value > 0.0 && value < 1.0) ? value + "0" : String.valueOf(value);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        pricing_apply.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                Intent i = new Intent(PricingActivity.this, SignUpActivity.class);
//                startActivity(i);
//            }
//        });
        action_call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Uri uri = Uri.parse("tel:18003353303");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        action_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PricingActivity.super.onBackPressed();
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.realtiveLayoutKeyed:
            case R.id.imgViewKeyedSelect: {
                checkKey(mKeyedParseObject);
                break;
            }

            case R.id.realtiveLayoutSwiped:
            case R.id.imgViewSwipedSelect: {
                mSwipedParseObject.add(IS_SWIPED, true);
                checkKey(mSwipedParseObject);
                break;
            }

            case R.id.imgViewKeyedInfo:
                ShowNotification.showErrorDialog(PricingActivity.this, keyedInfoText);
                break;

            case R.id.imgViewSwipedInfo:
                ShowNotification.showErrorDialog(PricingActivity.this, swipedInfoText);
                break;

        }
    }

    private void checkKey(ParseObject pricingObject) {

        if (Utility.getFlow(this) == 1) {


            String objectId = getIntent().getExtras().getString("objectId");
            ParseObject CCRA = (ParseObject) getIntent().getExtras().get(getString(R.string.source_and_class_name));
            ParseObject BusinessInfo = (ParseObject) getIntent().getExtras().get("BusinessInfo");
            ParseObject PrincipalInfo = (ParseObject) getIntent().getExtras().get("PrincipalInfo");
            ParseObject AccountInfo = (ParseObject) getIntent().getExtras().get("AccountInfo");

            TermsActivity.start(this, objectId, CCRA, BusinessInfo, PrincipalInfo, AccountInfo, pricingObject);


        } else if (Utility.getFlow(this) == 2) {
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.putExtra("pricingObject", pricingObject);
            startActivity(intent);
        }


    }
}

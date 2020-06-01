package com.ics.creditcardprocessing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.ics.creditcardprocessing.helper.GestureOverlayView;
import com.ics.creditcardprocessing.helper.ShowNotification;
import com.ics.creditcardprocessing.helper.pdf.CreatePDF;
import com.ics.creditcardprocessing.utility.Utility;
import com.ics.creditcardprocessing.webservices.IrsLeadsWebservice;
import com.ics.creditcardprocessing.webservices.impl.IrsLeadsWebserviceImpl;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_signature)
public class SignatureActivity extends AppCompatActivity {
     private static final String TAG = "CCR - SIGNATURE";
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


    private void updateDataOnParse(){
        mprogressDialog.setMessage("Please wait..");
        mprogressDialog.show();

        ParseFile parseFile = new ParseFile("Signature.png", Utility.loadByteArray(mGesture, 100, 100));
        mSignature = new ParseObject("Signature");
        mSignature.put("imageFile", parseFile);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("CCPA");
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

        pricingObject.remove(com.ics.creditcardprocessing.PricingActivity.IS_SWIPED);

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


    private void closeProgressDialog() {
        if (mprogressDialog != null) {
            mprogressDialog.dismiss();
        }
    }

}

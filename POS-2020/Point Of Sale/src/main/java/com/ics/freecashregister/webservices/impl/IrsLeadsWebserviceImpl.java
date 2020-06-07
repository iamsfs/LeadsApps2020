package com.ics.freecashregister.webservices.impl;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.ics.freecashregister.ApplicationClass;
import com.ics.freecashregister.webservices.ApiInterface;
import com.ics.freecashregister.webservices.IrsLeadsWebservice;
import com.parse.ParseObject;

import org.androidannotations.annotations.EBean;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@EBean
public class IrsLeadsWebserviceImpl implements IrsLeadsWebservice {


    @Override
    public void saveLeads(ParseObject mCCRA, ParseObject mPrincipalInfo, ParseObject mBusinessInfo, ParseObject mAccountInfo, @NonNull File signatureFile, @NonNull File signaturePDF, File achPDF, ParseObject pricingObject) {
        Retrofit retrofit = ApplicationClass.getMyApp().getRetrofitInstance();
        //creating our api
        ApiInterface api = retrofit.create(ApiInterface.class);

        //creating a call and calling the upload image method
        Call<ResponseBody> call = api.saveIRISLeads(ApiInterface.IRSL, createHashMap(mCCRA, mPrincipalInfo, mBusinessInfo, mAccountInfo, pricingObject),
                getFilePart("signatureImg", signatureFile, "multipart/form-data"),
                getFilePart("signaturePDF", signaturePDF, "application/pdf"),
                getFilePart("ach_pdf", achPDF, "application/pdf"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(ApplicationClass.getMyApp().getApplicationContext(), "Data saved Successfully...", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ApplicationClass.getMyApp().getApplicationContext(), "Data saved Error...", Toast.LENGTH_LONG).show();
            }
        });


    }

    private RequestBody createRequestBodyFromString(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private MultipartBody.Part getFilePart(String key, File file, String mediaType) {
        if (file == null)
            return null;
        RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), file);
        return MultipartBody.Part.createFormData(key, file.getName(), requestFile);

    }

    private Map<String, RequestBody> createHashMap(ParseObject mCCRA, ParseObject mPrincipalInfo, ParseObject mBusinessInfo, ParseObject mAccountInfo, ParseObject pricingObject) {
        Map<String, RequestBody> map = new HashMap<>();
        /*
        query.put("Name", getIntent().getStringExtra("full_name"));
        query.put("BusinessName", getIntent().getStringExtra("company"));
        query.put("Email", getIntent().getStringExtra("email_address"));
        query.put("Phone", getIntent().getStringExtra("mobile_phone"));
        query.put("Processing", getIntent().getStringExtra("card_processing"));
        query.put("source", getIntent().getStringExtra("source"));
        */

        map.put("First_Name", createRequestBodyFromString(mCCRA.getString("Name")));
        map.put("Date_Created", createRequestBodyFromString(new Date().toString()));
        map.put("Email", createRequestBodyFromString(mCCRA.getString("Email")));
        // map.put("Last_Name", "");
        String[] name_split = mCCRA.get("Name").toString().trim().split(" ");

//        for(int i=0;i<name_split.length;i++){
//            Log.e("check", name_split[i]);
//        }

//        if (name_split.length > 1) {
//            //Log.e("check", "if "+mCCRA.get("Name").toString().substring(name_split[0].length() + 1));
//            map.put("First_Name", createRequestBodyFromString(name_split[0]));
//            map.put("Last_Name", createRequestBodyFromString(mCCRA.get("Name").toString().substring(name_split[0].length() + 1)));
//
//
//            map.put("Contact_Last_Name", createRequestBodyFromString(mCCRA.get("Name").toString().substring(name_split[0].length() + 1)));
//            map.put("Contact_First_Name", createRequestBodyFromString(name_split[0]));
//        } else {
//            //Log.e("check", "else");
//            map.put("First_Name", createRequestBodyFromString(name_split[0]));
//
//            map.put("Contact_First_Name", createRequestBodyFromString(mCCRA.getString("Name")));
//        }

        map.put("First_Name", createRequestBodyFromString(mCCRA.get("Name").toString()));
        map.put("Last_Name", createRequestBodyFromString(mCCRA.get("LastName").toString()));

        map.put("Contact_First_Name", createRequestBodyFromString(mCCRA.get("Name").toString()));
        map.put("Contact_Last_Name", createRequestBodyFromString(mCCRA.get("LastName").toString()));


        map.put("Primary_Phone", createRequestBodyFromString(mCCRA.getString("Phone")));
        map.put("Secondary_Phone", createRequestBodyFromString(mCCRA.getString("Phone")));
        map.put("Currently_Processing", createRequestBodyFromString(mCCRA.getString("Processing")));
        map.put("source", createRequestBodyFromString(mCCRA.getString("source")));

        /*
        *
        *  principalInfo.put("city", mCity);
        principalInfo.put("zipCode", mZipCode);
        principalInfo.put("ssn", mSsn);
        principalInfo.put("state", mState);
        principalInfo.put("address", mAddress);
        principalInfo.put("dob", mDob);
        * */

        //map.put("Home_Address", createRequestBodyFromString(mPrincipalInfo.getString("address")));
        //map.put("Address:", createRequestBodyFromString(mPrincipalInfo.getString("address")));
        map.put("Date_of_Birth", createRequestBodyFromString(mPrincipalInfo.getString("dob")));
        map.put("Social_Security_Number", createRequestBodyFromString(mPrincipalInfo.getString("ssn")));


        /*
        ParseObject BusinessInfo = new ParseObject("BusinessInfo");
        BusinessInfo.put("businessName", businessName);
        BusinessInfo.put("streetName", streetName);
        BusinessInfo.put("city", city);
        BusinessInfo.put("state", state);
        BusinessInfo.put("zipCode", zipCode);
        BusinessInfo.put("phoneNumber", mPhoneNumber);
        BusinessInfo.put("productDescription", content);
        BusinessInfo.put("federalTaxId", mCheckBoxTax.isChecked() ? mTax : "");
        BusinessInfo.put("businessType", mOwnerType);
       */


        //Log.e("check", mBusinessInfo.getString("streetName"));

        map.put("Business_Name", createRequestBodyFromString(mBusinessInfo.getString("businessName")));
        map.put("DBA", createRequestBodyFromString(mBusinessInfo.getString("businessName")));
        map.put("Business_Address", createRequestBodyFromString(mBusinessInfo.getString("streetName")));
        map.put("Business_City", createRequestBodyFromString(mBusinessInfo.getString("city")));
        map.put("Business_State", createRequestBodyFromString(mBusinessInfo.getString("state")));
        map.put("Business_Zip", createRequestBodyFromString(mBusinessInfo.getString("zipCode")));
        map.put("Federal_Tax_ID", createRequestBodyFromString(mBusinessInfo.getString("federalTaxId")));
        map.put("Entity_Type", createRequestBodyFromString(mBusinessInfo.getString("businessType")));


        map.put("Corporate_Address", createRequestBodyFromString(mBusinessInfo.getString("streetName")));
        map.put("Corporate_City", createRequestBodyFromString(mBusinessInfo.getString("city")));
        map.put("Corporate_Zip", createRequestBodyFromString(mBusinessInfo.getString("zipCode")));
        map.put("Corporate_State", createRequestBodyFromString(mBusinessInfo.getString("state")));


        map.put("Owner_Address", createRequestBodyFromString(mPrincipalInfo.getString("address")));
        map.put("Owner_City", createRequestBodyFromString(mPrincipalInfo.getString("city")));
        map.put("Owner_Zip", createRequestBodyFromString(mPrincipalInfo.getString("zipCode")));
        map.put("Owner_State", createRequestBodyFromString(mPrincipalInfo.getString("state")));
        map.put("Owner_Phone", createRequestBodyFromString(mBusinessInfo.getString("phoneNumber")));

        map.put("campaign", createRequestBodyFromString("Android"));
        //map.put("source", createRequestBodyFromString("CCRA2019"));


      /*  map.put("Lead_ID", ""); //Skip
        map.put("Years_In_Business", ""); //Skip
        map.put("Monthly_Revenue_Deposits", ""); //Skip
        map.put("Refferal_Merchant", ""); //Skip */
        map.put("IPAddress", createRequestBodyFromString(ApplicationClass.getMyApp().getLocalIpAddress()));


        map.put("License_number", createRequestBodyFromString(mPrincipalInfo.getString("licenseNumber")));
        map.put("DL_State", createRequestBodyFromString(mPrincipalInfo.getString("dlState")));





        /*
        *
        * AccountInfo.put("sellType", mSellCustomersDropDown);
        AccountInfo.put("averageSell", mAverageSellCustomers);
        AccountInfo.put("estimatedCCSale", mCreditCardSales);
        AccountInfo.put("bankName", mBankName);
        AccountInfo.put("routingNumber", mBankRoutingNumber);
        AccountInfo.put("accountNumber", mBankAccountNumber);
        *
        * */

        map.put("Bank_Name", createRequestBodyFromString(mAccountInfo.getString("bankName")));
        map.put("Average_Sale_Per_Customer", createRequestBodyFromString(mAccountInfo.getString("averageSell")));
        map.put("Routing_#", createRequestBodyFromString(mAccountInfo.getString("routingNumber")));
        map.put("DDA_#", createRequestBodyFromString(mAccountInfo.getString("accountNumber")));
        map.put("Monthly_Processing_Limit", createRequestBodyFromString(mAccountInfo.getString("estimatedCCSale")));


        // map.put("Signature", (mAccountInfo.getString("estimatedCCSale")));


        //account.put("Products_&_Services_Sold", "");

        map.put("Products_&_Services_Sold", createRequestBodyFromString(mBusinessInfo.getString("productDescription")));


        //map.put("Test_Image", "");
        // map.put("Signature", "");
        // map.put("signatureImg", "");

//        map.put("credit_discount_rate", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("credit_discount_rate"))));
//        map.put("debit_discount_rate", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("debit_discount_rate"))));
//        map.put("mid_qualified_credit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("mid_qualified_credit"))));
//        map.put("mid_qualified_debit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("mid_qualified_debit"))));
//        map.put("non_qualified_credit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("non_qualified_credit"))));
//        map.put("non_qualified_debit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("non_qualified_debit"))));
//        map.put("mid_qualified_credit_val", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("mid_qualified_credit_val"))));
//        map.put("non_qualified_credit_val", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("non_qualified_credit_val"))));
//        map.put("amex_discount_rate", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("amex_discount_rate"))));
//        map.put("amex_txn_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("amex_txn_fee"))));
//        map.put("amex_mid_qualified_credit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("amex_mid_qualified_credit"))));
//        map.put("amex_esa_pass", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("amex_esa_pass"))));
//        map.put("amex_non_qualified_credit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("amex_non_qualified_credit"))));
//        map.put("amex_mid_qualified_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("amex_mid_qualified_fee"))));
//        map.put("amex_non_qualified_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("amex_non_qualified_fee"))));
//        map.put("amex_err_rate", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("amex_err_rate"))));
//        map.put("amex_cc_qualified_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("amex_cc_qualified_fee"))));
//        map.put("qualified_per_item_credit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("qualified_per_item_credit"))));
//        map.put("mid_qualified_per_item_credit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("mid_qualified_per_item_credit"))));
//        map.put("mid_qualified_per_item_debit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("mid_qualified_per_item_debit"))));
//        map.put("non_qualified_per_item_credit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("non_qualified_per_item_credit"))));
//        map.put("non_qualified_per_item_debit", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("non_qualified_per_item_debit"))));
//        map.put("debit_transaction_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("debit_transaction_fee"))));
//        map.put("debit_access_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("debit_access_fee"))));
//        map.put("return_transaction_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("return_transaction_fee"))));
//        map.put("electronic_avs_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("electronic_avs_fee"))));
//        map.put("ebt_per_item_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("ebt_per_item_fee"))));
//        map.put("qualified_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("qualified_fee"))));
//        map.put("mid_qualified_rate", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("mid_qualified_rate"))));
//        map.put("mid_qualified_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("mid_qualified_fee"))));
//        map.put("non_qualified_rate", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("non_qualified_rate"))));
//        map.put("non_qualified_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("non_qualified_fee"))));
//        map.put("err_rate", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("err_rate"))));
//        map.put("offline_debit_qualified_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("offline_debit_qualified_fee"))));
//        map.put("cc_qualified_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("cc_qualified_fee"))));
//        map.put("statement_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("statement_fee"))));
//        map.put("monthly_minimum", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("monthly_minimum"))));
//        map.put("charge_back_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("charge_back_fee"))));
//        map.put("batch_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("batch_fee"))));
//        map.put("retrieval_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("retrieval_fee"))));
//        map.put("annual_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("annual_fee"))));
//        map.put("gateway_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("gateway_fee"))));
//        map.put("wireless_monthly_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("wireless_monthly_fee"))));
//        map.put("tin_tfn_invalid", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("tin_tfn_invalid"))));
//        map.put("ach_reject_fees", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("ach_reject_fees"))));
//        map.put("early_termination_fee", createRequestBodyFromString(String.valueOf(pricingObject.getDouble("early_termination_fee"))));


        Set<String> keySet = pricingObject.keySet();
        String[] parseKeys = keySet.toArray(new String[keySet.size()]);
        for (String key : parseKeys) {
            Log.e("pricing", key + " " + pricingObject.getDouble(key));
            map.put(key, createRequestBodyFromString(String.valueOf(pricingObject.getDouble(key))));

        }


        Log.e("MAP ", map.toString());
        return map;
    }


}

package com.ics.creditcardreader.webservices;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface ApiInterface {

    String BASE_URL = "https://www.crmmas.com/";
    String IRSL = "https://www.crmmas.com/postleads/t/f673f0fc6465eedc220d0d13ec0126d8";
    String POSPROS = "https://crm.securedmerchantapp.com/api/leads";

    @Multipart
    @POST
    Call<ResponseBody> saveIRISLeads(@Url String url, @PartMap Map<String, RequestBody> info, @Part MultipartBody.Part signatureFile, @Part MultipartBody.Part signaturePdf,
                                     @Part MultipartBody.Part achPdf);


    @FormUrlEncoded
    @POST(POSPROS)
    Call<ResponseBody> pospros(@Field("name") String name,
                               @Field("source") String source,
                               @Field("dba") String dba, //Business name
                               @Field("phone") String phone,
                               @Field("url") String url,
                               @Field("utmSource") String utmSource,
                               @Field("utmMedium") String utmMedium,
                               @Field("utmCampaign") String utmCampaign,
                               @Field("utmTerm") String utmTerm,
                               @Field("utmContent") String utmContent,
                               @Field("note") String note,
                               @Field("isMobile") Boolean isMobile,
                               @Field("email") String email);
}

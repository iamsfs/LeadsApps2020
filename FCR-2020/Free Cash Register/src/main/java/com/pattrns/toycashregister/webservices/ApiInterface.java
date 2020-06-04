package com.pattrns.toycashregister.webservices;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface ApiInterface {

    String BASE_URL = "https://www.crmmas.com/";
    String IRSL = "https://www.crmmas.com/postleads/t/f673f0fc6465eedc220d0d13ec0126d8";

    @Multipart
    @POST
    Call<ResponseBody> saveIRISLeads(@Url String url, @PartMap Map<String, RequestBody> info, @Part MultipartBody.Part signatureFile, @Part MultipartBody.Part signaturePdf, @Part MultipartBody.Part achPdf);
}

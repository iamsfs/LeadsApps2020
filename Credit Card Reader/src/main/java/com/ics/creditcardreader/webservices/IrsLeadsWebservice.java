package com.ics.creditcardreader.webservices;

import android.support.annotation.NonNull;

import com.parse.ParseObject;

import java.io.File;

public interface IrsLeadsWebservice {

    void saveLeads(ParseObject mCCRA, ParseObject mPrincipalInfo, ParseObject mBusinessInfo, ParseObject mAccountInfo, @NonNull File file, @NonNull File pdf, File achFormPDF, ParseObject pricingObject);


}

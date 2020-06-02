package com.ics.merchantaccount.helper;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.text.HtmlCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ics.merchantaccount.R;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ShowTermsAndCondtionsDialog {

    public static void showTerms(Context context, int id, ParseObject details, ProgressBar progressBar) {
        final Dialog dialog = new Dialog(context,R.style.termsDialog);
        dialog.setContentView(R.layout.layout_show_terms_and_conditions);
        dialog.setCanceledOnTouchOutside(true);

        //final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBarDialog);

//        WebView webView = (WebView) dialog.findViewById(R.id.webViewDialog);
//
        //TextView textView = (TextView) dialog.findViewById(R.id.txtViewDialog);

        final ScrollView scrollView = (ScrollView) dialog.findViewById(id);
        scrollView.setVisibility(View.VISIBLE);

        if (id == R.id.scrollViewDialog) {
            TextView textView = dialog.findViewById(R.id.txtViewDialog);
            //HtmlCompat.fromHtml(context.getResources().getString(R.string.string_terms_pos_pros),HtmlCompat.FROM_HTML_MODE_LEGACY);
            textView.setText(HtmlCompat.fromHtml(context.getResources().getString(R.string.string_terms_pos_pros, details.getString("BusinessName"), details.getString("Name"), new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date())), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }

//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setDisplayZoomControls(false);
//        webView.getSettings().setBuiltInZoomControls(true);
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//
//                if (newProgress == 100) {
//                    progressBar.setVisibility(View.GONE);
//                }
//
//            }
//        });

        //textView.setText(content);

        dialog.show();

        progressBar.setVisibility(View.GONE);

        TextView close_btn = (TextView) dialog.findViewById(R.id.buttonDoneDialog);
        close_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                scrollView.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
    }


}

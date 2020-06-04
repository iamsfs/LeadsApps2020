package com.pattrns.toycashregister.helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.pattrns.toycashregister.R;

public class ShowNotification {
	
	 public static void showErrorDialog(Context context, String content){
         final Dialog dialog  = new Dialog(context, R.style.NotificationDialogTheme);
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
         dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
         dialog.setContentView(R.layout.layout_shownotification);
         dialog.setCanceledOnTouchOutside(true);

         TextView content_holder = (TextView) dialog.findViewById(R.id.shownotification_content);
         content_holder.setText(content);

         dialog.show();

         TextView close_btn = (TextView) dialog.findViewById(R.id.shownotification_ok);
         close_btn.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 dialog.dismiss();
             }
         });
	}
}
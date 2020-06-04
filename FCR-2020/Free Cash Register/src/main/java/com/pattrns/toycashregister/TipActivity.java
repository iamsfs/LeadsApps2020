package com.pattrns.toycashregister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.pattrns.toycashregister.helper.GestureOverlayView;

public class TipActivity extends Activity implements OnClickListener {

    private static final String TAG = "CCR - Tip";

    private TextView total_sale_amt, sign_clear, add_tip;
    private TextView action_next, action_cancel;
    private GestureOverlayView tip_sign;
    private Gesture mGesture;
    private static final float LENGTH_THRESHOLD = 12000.0f;

    private String sale_amt = "0.00";
    private Double total_amount = 0.00;
    private int selectedTip = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip);

        if (getIntent().hasExtra("sale_amount")) {
            sale_amt = getIntent().getStringExtra("sale_amount");
        }

        if (tip_sign == null) {
            tip_sign = (GestureOverlayView) findViewById(R.id.tip_sign);
        }
        if (action_next == null) {
            action_next = (TextView) findViewById(R.id.action_next);
        }

        if (action_cancel == null) {
            action_cancel = (TextView) findViewById(R.id.action_cancel);
        }

        if (add_tip == null) {
            add_tip = (TextView) findViewById(R.id.add_tip);
        }

        if (sign_clear == null) {
            sign_clear = (TextView) findViewById(R.id.sign_clear);
        }
        if (total_sale_amt == null) {
            total_sale_amt = (TextView) findViewById(R.id.total_sale_amt);
        }


        displayPopupTip(selectedTip);

    }


    @Override
    protected void onStart() {
        super.onStart();
        total_amount = Double.parseDouble(sale_amt);
        total_sale_amt.setText("$" + sale_amt);

        action_next.setOnClickListener(this);
        action_cancel.setOnClickListener(this);
        add_tip.setOnClickListener(this);

        //int winHeight = getWindowManager().getDefaultDisplay().getHeight();
        //int winWidth  = getWindowManager().getDefaultDisplay().getWidth();
        //tip_sign.setMinimumHeight(winHeight - (int)(winHeight / 1.5));
        //tip_sign.setMinimumWidth(winWidth - (winWidth / 10));
        tip_sign.setGestureColor(Color.BLACK);
        tip_sign.addOnGestureListener(newGestureListener);

        sign_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                tip_sign.clear(true);
            }
        });
    }

    private void displayPopupTip(int tip) {
        total_amount = (Double.parseDouble(sale_amt) + ((Double.parseDouble(sale_amt) * tip) / 100));
        total_sale_amt.setText(String.format("$%s + Tip:%d%s = $%.2f", sale_amt, tip, "%", total_amount));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_next:
                Intent i = new Intent(TipActivity.this, SuccessActivity.class);
                i.putExtra("sale_amount", String.valueOf(total_amount));
                startActivity(i);
                break;
            case R.id.action_cancel:
                onBackPressed();
                break;
            case R.id.add_tip:
                final CharSequence[] tips = {
                        getString(R.string.lbl_tip_0),
                        getString(R.string.lbl_tip_5),
                        getString(R.string.lbl_tip_10),
                        getString(R.string.lbl_tip_15),
                        getString(R.string.lbl_tip_20),
                        getString(R.string.lbl_tip_25),
                        getString(R.string.lbl_tip_30)};
                AlertDialog.Builder builder = new AlertDialog.Builder(TipActivity.this);
                builder.setTitle(R.string.lbl_tip_amount)
                        .setSingleChoiceItems(tips, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedTip = which * 5;
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                displayPopupTip(selectedTip);
                            }
                        }).create();
                builder.show();
                break;
        }
        Log.d(TAG, total_sale_amt.getText().toString());
    }

    private GestureOverlayView.OnGestureListener newGestureListener = new GestureOverlayView.OnGestureListener() {
        @Override
        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        }

        @Override
        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        }

        @Override
        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
            mGesture = overlay.getGesture();
            if (mGesture.getLength() < LENGTH_THRESHOLD) {
                overlay.clear(true);
            }
        }

        @Override
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
        }
    };
}

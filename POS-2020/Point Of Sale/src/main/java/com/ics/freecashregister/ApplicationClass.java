package com.ics.freecashregister;

import android.app.Application;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.ics.freecashregister.webservices.ApiInterface;
import com.parse.Parse;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.constraint.Constraints.TAG;

public class ApplicationClass extends Application {

    //------------Leads Apps Android for Back4apps ------------------
    public static final String APP_ID = "Wm6rlmXv53Ik1VlBTsnaMhQHDcEIreugsv5xVoCw";
    public static final String CLIENT_KEY = "t5t2bCnAsMHn9k3Mbr9E3cyA5QsbDApdWe167pO7";
    public static final String SERVER_URL = "https://parseapi.back4app.com/";
    private  Retrofit retrofit;
    private static ApplicationClass myApp;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        try {
            Parse.initialize(new Parse.Configuration.Builder(this)
                    .applicationId(APP_ID) // should correspond to APP_ID env variable
                    .clientKey(CLIENT_KEY)  // set explicitly unless clientKey is explicitly configured on Parse server
                    .server(SERVER_URL).build());

            Log.e("Success", "Server Connection success");
        } catch (Exception e) {
            Log.e("Error", "Server Connection error : " + e.toString());
        }


    }

    public static ApplicationClass getMyApp() {
        return myApp;
    }

    public  Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(ApiInterface.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i(TAG, "***** IP=" + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return "";
    }

    public static void toast(String message) {
        Toast.makeText(ApplicationClass.getMyApp().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}

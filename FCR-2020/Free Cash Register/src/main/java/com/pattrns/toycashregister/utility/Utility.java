package com.pattrns.toycashregister.utility;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.preference.PreferenceManager;

import com.pattrns.toycashregister.ApplicationClass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public final class Utility {
    private static Context context = ApplicationClass.getMyApp().getApplicationContext();
    public static String SIGNATURE_FILE = "Signature.png";
    private static final String FLOW_KEY = "FLOW_KEY";


    public static File getCacheFile(String fileName) {
        File file = new File(context.getCacheDir(), fileName);
        try {
            if (file.createNewFile())
                return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file;
    }

    public static File getFile(String fileName, Bitmap bitmap) {
        if (bitmap == null)
            return null;

        File file = new File(context.getCacheDir(), fileName);
        boolean hasFile = false;
        try {
            hasFile = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hasFile) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(bytes);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (file.exists())
            return file;

        return null;

    }

    public static String getPdfDocumentPath(Bitmap bitmap) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                bitmap.getWidth(), bitmap.getHeight(), 1
        ).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        canvas.drawBitmap(scaledBitmap, 0f, 0f, null);
        pdfDocument.finishPage(page);

        String path = context.getFilesDir() + "/signature.pdf";

        try {
            pdfDocument.writeTo(new FileOutputStream(path));
            //Log.e("check", path);
            return path;
        } catch (IOException e) {
            //Log.e("check", e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }

        return null;

    }

    public static void setFlow(Context context, int num) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(FLOW_KEY, num)
                .apply();
    }

    public static int getFlow(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(FLOW_KEY, 1);
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static byte[] loadByteArray(Gesture gesture, int width, int height) {

        Bitmap bitmap = gesture.toBitmap(width, height, 8, Color.BLACK);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();


    }


}

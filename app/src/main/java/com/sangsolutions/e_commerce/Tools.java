package com.sangsolutions.e_commerce;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Objects;


public class Tools {
    public static Bitmap ConvertToThumbnail(Bitmap bitmap) {
        final int THUMBSIZE = 100;
        return ThumbnailUtils.extractThumbnail(bitmap, THUMBSIZE, THUMBSIZE);
    }


    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }

    public static int calculateNoOfRows(Context context, float columnHeightDp) { // For example columnHeightDp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.heightPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnHeightDp + 0.5);
    }

    public static String readFile(Context context, String filename) throws FileNotFoundException {
        String contents = "";

        FileInputStream fis = new FileInputStream(new File(context.getExternalFilesDir(null), filename));
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.d("File", Objects.requireNonNull(e.getMessage()));
        } finally {
            contents = stringBuilder.toString();
        }
        return contents;
    }

    public static boolean writeToFile(String data, Context context, String filename) {
        try {
            FileWriter fileWriter = new FileWriter(context.getExternalFilesDir(null) + File.separator + filename);
            fileWriter.append(data);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            return false;
        }
    }

    public static boolean AddProductToWishList(JSONArray jsonArray, Context context) {
        try {
            FileWriter fileWriter = new FileWriter(context.getExternalFilesDir(null) + File.separator + "wishlist.json");
            fileWriter.append(jsonArray.toString());
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            //Log.e("Exception", "File write failed: " + e.toString());
            return false;
        }
    }

    public static String ReadWishListFile(Context context) {
        String contents = "";

        FileInputStream fis;
        try {
            fis = new FileInputStream(new File(context.getExternalFilesDir(null), "wishlist.json"));

            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (IOException e) {
                //Log.d("File", Objects.requireNonNull(e.getMessage()));
            } finally {
                contents = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
        return contents;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public static boolean isValidPhone(CharSequence target) {
        return (Patterns.PHONE.matcher(target).matches()&&!target.toString().isEmpty());
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        return preferences.getString("iId", "0");
    }

    public static boolean FireBaseCrashlyticsStatus(){
        //true if you want to return crash reports
        return false;
    }

    public static String GetCurrentDate(){
      Calendar c = Calendar.getInstance();
       return c.get(Calendar.YEAR)+"-"+(1 + c.get(Calendar.MONTH))+"-"+ c.get(Calendar.DAY_OF_MONTH);
    }

}


package com.sangsolutions.e_commerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {
   private EditText et_username, et_password;
    private CoordinatorLayout cl_main;
    private SharedPreferences.Editor editor;

    private void ShowSnackBar(String message, String color) {
        Snackbar.make(cl_main, message, 500).setBackgroundTint(Color.parseColor(color)).show();
    }

    public void Login(final String username, String password) {

        try {
            AndroidNetworking.get(URLs.GetLogin)
                    .addQueryParameter("LoginName",username)
                    .addQueryParameter("Password",CryptoHandler.getInstance().encrypt(password).trim())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("data", response.toString());
                            try {
                                JSONArray jsonArray = new JSONArray(response.getJSONArray("UserDetails").toString());
                                editor.putString("sUserName", new JSONObject(jsonArray.getJSONObject(0).toString()).getString("sUserName"));
                                editor.putString("iId", new JSONObject(jsonArray.getJSONObject(0).toString()).getString("iId"));
                                editor.apply();

                                ShowSnackBar(getString(R.string.login_successful), getString(R.string.color_success));

                                Handler handler = new Handler();
                                final Runnable r = new Runnable() {
                                    public void run() {
                                        startActivity(new Intent(MainActivity.this, Home.class));
                                        finish();
                                    }
                                };
                                handler.postDelayed(r, 500);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                FirebaseCrashlytics.getInstance().recordException(new Throwable("GetLogin\n"+e.getMessage()));
                                ShowSnackBar(getString(R.string.login_unsuccessful), getString(R.string.warning_color));
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("TAG", "onError: "+anError.getErrorDetail());
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("GetLogin\n"+anError.getErrorDetail()));
                            ShowSnackBar(getString(R.string.login_unsuccessful), getString(R.string.warning_color));
                        }
                    });
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(Tools.FireBaseCrashlyticsStatus());
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = preferences.edit();
        et_username = findViewById(R.id.username);
        et_password = findViewById(R.id.password);
        Button btn_login = findViewById(R.id.login);
        Button btn_register = findViewById(R.id.register);
        cl_main = findViewById(R.id.cl_main);
        TextView tv_forgot_password = findViewById(R.id.forgot_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_username.getText().toString().isEmpty()) {
                    et_username.setError(getString(R.string.enter_username));
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                } else if (et_password.getText().toString().isEmpty()) {
                    et_password.setError(getString(R.string.enter_password));
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                } else {
                    Login(et_username.getText().toString().trim(), et_password.getText().toString().trim());
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                intent.putExtra("edit", false);
                startActivity(intent);
                finish();
            }
        });

        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ForgotPassword.class));
            }
        });
    }
}
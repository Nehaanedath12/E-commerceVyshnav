package com.sangsolutions.e_commerce;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class PasswordReset extends AppCompatActivity {
EditText et_newPass,et_confirmPass;
ImageView img_newPassVisibility,img_confirmPassVisibility;
Button btn_Continue;
CoordinatorLayout cl_main;
    String phoneNumber="";
    private void ShowSnackBar(String message, String color) {
        Snackbar.make(cl_main, message, 500).setBackgroundTint(Color.parseColor(color)).show();
    }


    private void UploadNewPassword(String password,String phone){
        try {

            AndroidNetworking.get(URLs.PostPasswordChange+"?sPhone="+phone+"&password="+CryptoHandler.getInstance().encrypt(password))
                  /*  .addQueryParameter("sPhone",phone)
                    .addQueryParameter("password",CryptoHandler.getInstance().encrypt(password))*/
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("TAG", "onResponse: "+response);
                            if(response.equals("1")){
                                startActivity(new Intent(PasswordReset.this,MainActivity.class));
                                finishAffinity();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("TAG", "onError: "+anError.getResponse());
                        }
                    });
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

    }

    private void PasswordResetAlert(final String password, final String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Password")
                .setMessage("do you want to change the old password to this?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UploadNewPassword(password,phone);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent!=null) {
            phoneNumber = intent.getStringExtra("mobile");
        }else {
            finish();
        }
        setContentView(R.layout.activity_password_reset);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(Tools.FireBaseCrashlyticsStatus());



        et_newPass = findViewById(R.id.newpass);
        et_confirmPass = findViewById(R.id.confirmpass);
        img_newPassVisibility = findViewById(R.id.pass1_show);
        img_confirmPassVisibility = findViewById(R.id.pass2_show);
        btn_Continue = findViewById(R.id.Continue);
        cl_main = findViewById(R.id.cl_main);

        img_newPassVisibility.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        et_newPass.setTransformationMethod(new HideReturnsTransformationMethod());
                        break;
                    case MotionEvent.ACTION_UP:
                        et_newPass.setTransformationMethod(new PasswordTransformationMethod());
                        break;
                    default:
                        return false;
                }

                return true;
            }
        });


        img_confirmPassVisibility.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        et_confirmPass.setTransformationMethod(new HideReturnsTransformationMethod());
                        break;
                    case MotionEvent.ACTION_UP:
                        et_confirmPass.setTransformationMethod(new PasswordTransformationMethod());
                        break;
                    default:
                        return false;
                }

                return true;
            }
        });

        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(et_newPass.getText().toString().length()<6){
                    et_newPass.setError(getString(R.string.password_length_must_be));
                }else if(et_confirmPass.getText().toString().length()<6){
                    et_confirmPass.setError(getString(R.string.password_length_must_be));
                }else if(!et_newPass.getText().toString().trim().equals(et_confirmPass.getText().toString().trim())){
                    ShowSnackBar("New password and Confirm password need to be same",getString(R.string.warning_color));
                    Toast.makeText(PasswordReset.this, "password error", Toast.LENGTH_SHORT).show();
                }else {
                   PasswordResetAlert(et_newPass.getText().toString().trim(),phoneNumber);
                }
            }
        });


    }


}
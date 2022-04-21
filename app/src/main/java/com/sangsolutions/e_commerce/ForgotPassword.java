package com.sangsolutions.e_commerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.CountryAdapter.Country;
import com.sangsolutions.e_commerce.Adapter.CountryAdapter.CountryCodeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ForgotPassword extends AppCompatActivity {
private EditText et_mobile,et_otp;
private Spinner sp_country_code;
private List<Country> countryList;
private CountryCodeAdapter countryCodeAdapter;
private CoordinatorLayout cl_main;
private FirebaseMobileAuthenticationService firebaseMobileAuthenticationService;
private TextView tv_timer;
    private ImageView img_check;
private AlertDialog OTPAuthenticationDialog;
    private String mVerificationId="",mobile="";
Handler handler;
    private void ShowSnackBar(String message, String color) {
        Snackbar.make(cl_main, message, 500).setBackgroundTint(Color.parseColor(color)).show();
    }



    private void StartTimer(final String mobile){
        CountDownTimer countDownTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (tv_timer != null && OTPAuthenticationDialog.isShowing()) {
                    tv_timer.setBackground(null);
                    tv_timer.setTextColor(Color.BLACK);
                    tv_timer.setText(getString(R.string.seconds_remaining) + millisUntilFinished / 1000);
                }
            }

            public void onFinish() {
                if (tv_timer != null && OTPAuthenticationDialog.isShowing()) {
                    tv_timer.setText(R.string.resend_otp);
                    tv_timer.setTextColor(Color.WHITE);
                    tv_timer.setPadding(20, 20, 20, 20);
                    tv_timer.setBackground(ContextCompat.getDrawable(ForgotPassword.this, R.drawable.button_6));

                    if (tv_timer.getText().toString().equals(getString(R.string.resend_otp))) {

                        tv_timer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                StartTimer(mobile);
                                firebaseMobileAuthenticationService.initResendToken(mobile);
                            }
                        });
                    }
                }
            }
        }.start();
    }




    private void MobileVerificationAlert(String mobile){

        View view = LayoutInflater.from(this).inflate(R.layout.otp_verification_layout,null,false);
        et_otp = view.findViewById(R.id.et_otp);
        tv_timer = view.findViewById(R.id.time_left);
        img_check = view.findViewById(R.id.check);
        TextView tv_phone_number = view.findViewById(R.id.phone_no);
        Button btn_continue = view.findViewById(R.id.Continue);
        final Button btc_close = view.findViewById(R.id.close);

        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
        builder.setView(view)
                .setCancelable(false);

        OTPAuthenticationDialog = builder.create();
        OTPAuthenticationDialog.show();
        firebaseMobileAuthenticationService.initMobileAuthenticationFlow(mobile);
        StartTimer(mobile);

        tv_phone_number.setText(mobile);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_otp.getText().toString().isEmpty()) {
                    firebaseMobileAuthenticationService.validateTheCode(et_otp.getText().toString());
                }
                }
        });

        btc_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OTPAuthenticationDialog.dismiss();
            }
        });

    }


    private void MobileNumberConfirm(final String mobile){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.mobile_number_confirmation)
                .setMessage(getString(R.string.going_to_use_this_number_verify)+"\t\n"+mobile)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MobileVerificationAlert(mobile);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void LoadCountry(){
        countryList.clear();
        countryList.add(new Country("",getString(R.string.code),getString(R.string.select_country)));
        AndroidNetworking.get(URLs.getCountryDetails)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("CountryDetails"));
                            for(int i = 0 ; i<jsonArray.length();i++){
                                JSONObject jsonObject  = jsonArray.getJSONObject(i);
                                if(!jsonObject.getString("sName").equals(" ")){
                                    countryList.add(new Country(
                                            jsonObject.getString("iId"),
                                            jsonObject.getString("sCode"),
                                            jsonObject.getString("sName")
                                    ));

                                    if(jsonArray.length()==i+1){
                                        sp_country_code.setAdapter(countryCodeAdapter);
                                    }

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("country list\n"+e.getMessage()));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("country list",anError.getErrorBody());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("country list\n"+anError.getErrorDetail()));
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(Tools.FireBaseCrashlyticsStatus());
        et_mobile = findViewById(R.id.mobile_no);
        Button btn_next = findViewById(R.id.next);
        Button btn_close = findViewById(R.id.close);
        sp_country_code = findViewById(R.id.country_code);
        cl_main = findViewById(R.id.cl_main);

    countryList = new ArrayList<>();
    countryCodeAdapter = new CountryCodeAdapter(countryList,this);


        LoadCountry();

    btn_next.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(countryList.get(sp_country_code.getSelectedItemPosition()).getsCode().equals(getString(R.string.code))){
                et_mobile.setError(getString(R.string.select_code));
                ShowSnackBar(getString(R.string.error_in_given_information),getString(R.string.warning_color));
            }else if(!Tools.isValidPhone(et_mobile.getText().toString())){
                et_mobile.setError(getString(R.string.enter_a_valid_phone_number));
                ShowSnackBar(getString(R.string.error_in_given_information),getString(R.string.warning_color));
            }else {
                mobile = countryList.get(sp_country_code.getSelectedItemPosition()).getsCode()+et_mobile.getText().toString();
                MobileNumberConfirm(mobile);
            }

        }
    });


    btn_close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });


        firebaseMobileAuthenticationService = new FirebaseMobileAuthenticationService(this, new FirebaseMobileAuthenticationService.MobileAuthenticationListener() {
            @Override
            public void onTooManyRequestsFailure(@NonNull Exception e) {
                Log.d(TAG, "onTooManyRequestsFailure: "+e.getMessage());
                img_check.setImageResource(R.drawable.ic_close);
            }

            @Override
            public void onInvalidCredentialFailure(@NonNull Exception e) {
                Log.d(TAG, "onInvalidCredentialFailure: "+e.getMessage());
                img_check.setImageResource(R.drawable.ic_close);
            }

            @Override
            public void onInvalidPhoneNumber(@NonNull Exception e) {
                Log.d(TAG, "onInvalidPhoneNumber: "+e.getMessage());
                img_check.setImageResource(R.drawable.ic_close);

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
            }

            @Override
            public void onSignInComplete(FirebaseUser user) {
                img_check.setImageResource(R.drawable.ic_check);
                handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if (OTPAuthenticationDialog != null) {
                            if (OTPAuthenticationDialog.isShowing()) {
                                OTPAuthenticationDialog.dismiss();
                                Intent intent = new Intent(ForgotPassword.this,PasswordReset.class);
                                intent.putExtra("mobile",mobile);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                };
                handler.postDelayed(r, 1000);


            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                if (!Objects.requireNonNull(phoneAuthCredential.getSmsCode()).isEmpty()) {
                    if (et_otp != null)
                        et_otp.setText(phoneAuthCredential.getSmsCode());
                }
            }
        });
    }
}
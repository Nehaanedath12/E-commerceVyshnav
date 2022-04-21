package com.sangsolutions.e_commerce;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.CityAdapter.City;
import com.sangsolutions.e_commerce.Adapter.CityAdapter.CityAdapter;
import com.sangsolutions.e_commerce.Adapter.CountryAdapter.Country;
import com.sangsolutions.e_commerce.Adapter.CountryAdapter.CountryAdapter;
import com.sangsolutions.e_commerce.Adapter.CountryAdapter.CountryCodeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static android.content.ContentValues.TAG;

public class Register extends AppCompatActivity {
   private EditText et_name, et_lastName, et_mobile, et_phone, et_mail, et_address1, et_address2, et_username, et_password;
    private CoordinatorLayout cl_main;
   private Spinner sp_country,sp_city;
   private String selected_country = "", selected_city="";
   private CountryAdapter countryAdapter;
   private CountryCodeAdapter countryCodeAdapter;
   private CityAdapter cityAdapter;
   private List<Country> countryList;
   private List<City> cityList;
    private SharedPreferences.Editor editor;
   private JSONObject jsonObject;
   private EditText et_otp;
   private FirebaseAuth mAuth;
   private AlertDialog OTPAuthenticationDialog;
   private ImageView img_check;
   private TextView tv_timer;
    private PhoneAuthCredential credential;
   private String mVerificationId;
   private CountDownTimer countDownTimer;
   private Handler handler;
   private FirebaseMobileAuthenticationService firebaseMobileAuthenticationService;
   private Spinner sp_country_code;
   private PhoneAuthProvider.ForceResendingToken forceResendingToken;
   int country_selected_before=0;
    private boolean Edit = false;



    public void LoadDataForEdit(){
        AndroidNetworking.get(URLs.getUserDetails)
                .addQueryParameter("iUser",Tools.getUserId(Register.this))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: "+response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("getUserDetails"));
                            for(int i = 0 ; i < jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(!jsonObject.getString("sName").isEmpty())
                                et_name.setText(jsonObject.getString("sName"));

                                if(!jsonObject.getString("sLastName").isEmpty())
                                    et_lastName.setText(jsonObject.getString("sLastName"));

                                if(!jsonObject.getString("sUserName").isEmpty())
                                    et_username.setText(jsonObject.getString("sUserName"));

                                if(!jsonObject.getString("sPhoneNo").isEmpty())
                                    et_phone.setText(jsonObject.getString("sPhoneNo"));

                                if(!jsonObject.getString("sPassword").isEmpty()) {
                                    try {
                                        et_password.setText(CryptoHandler.getInstance().decrypt(jsonObject.getString("sPassword")));
                                    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(!jsonObject.getString("sEmail").isEmpty())
                                    et_mail.setText(jsonObject.getString("sEmail"));

                                if(!jsonObject.getString("sAddress1").isEmpty())
                                    et_address1.setText(jsonObject.getString("sAddress1"));

                                if(!jsonObject.getString("sAddress2").isEmpty())
                                    et_address2.setText(jsonObject.getString("sAddress2"));

                                if(!jsonObject.getString("iCountry").isEmpty()) {
                                    if (!jsonObject.getString("iCity").isEmpty()) {
                                        LoadCountry(jsonObject.getString("iCountry"), jsonObject.getString("iCity"));
                                    } else {
                                        LoadCountry(jsonObject.getString("iCountry"), "0");
                                    }
                                }




                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: "+anError.getErrorDetail());
                    }
                });

    }


   private void ShowSnackBar(String message, String color) {
        Snackbar.make(cl_main, message, 500).setBackgroundTint(Color.parseColor(color)).show();
    }

   private void SendOTP(String mobile, JSONObject jsonObject, PhoneAuthProvider.ForceResendingToken forceResendingToken){
        firebaseMobileAuthenticationService.initResendToken(mobile);
        firebaseMobileAuthenticationService.initMobileAuthenticationFlow(mobile);
       StartTimer(mobile,jsonObject);
   }

    private void StartTimer(final String PhoneNumber,final JSONObject jsonObject){
        countDownTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(tv_timer!=null&&OTPAuthenticationDialog.isShowing()) {
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
                    tv_timer.setBackground(ContextCompat.getDrawable(Register.this, R.drawable.button_6));

                    if(tv_timer.getText().toString().equals(getString(R.string.resend_otp))){

                        tv_timer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                firebaseMobileAuthenticationService.initResendToken(PhoneNumber);
                                SendOTP(PhoneNumber, jsonObject,forceResendingToken);
                            }
                        });
                    }
                }
            }
        }.start();
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
       if(et_otp!=null){
           et_otp.setText(credential.getSmsCode());
       }
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(img_check!=null&&OTPAuthenticationDialog.isShowing()){
                                img_check.setImageResource(R.drawable.ic_check);
                                countDownTimer.cancel();
                                tv_timer.setText("");
                            }


                            handler = new Handler();

                            final Runnable r = new Runnable() {
                                public void run() {
                                    if (OTPAuthenticationDialog != null) {
                                        if (OTPAuthenticationDialog.isShowing()) {
                                            OTPAuthenticationDialog.dismiss();
                                            if(jsonObject!=null)
                                                UploadUserData(jsonObject);
                                        }
                                    }
                                }
                            };
                            handler.postDelayed(r, 1000);

                            Log.d(TAG, "signInWithCredential:success");

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                if(img_check!=null&&OTPAuthenticationDialog.isShowing()){
                                    img_check.setImageResource(R.drawable.ic_close);
                                }
                            }
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void OTPVerificationAlert(JSONObject jsonObject){

        try {
            String mobile = jsonObject.getString("sMobNo");
            Log.d(TAG, "OTPVerificationAlert: "+mobile);

            View view = LayoutInflater.from(this).inflate(R.layout.otp_verification_layout,null,false);
            et_otp = view.findViewById(R.id.et_otp);
            tv_timer = view.findViewById(R.id.time_left);
            img_check = view.findViewById(R.id.check);
            TextView tv_phone_number = view.findViewById(R.id.phone_no);
            Button btn_continue = view.findViewById(R.id.Continue);
            Button btc_close = view.findViewById(R.id.close);

            SendOTP(mobile,jsonObject,forceResendingToken);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view)
                    .setCancelable(false);

            OTPAuthenticationDialog = builder.create();
            OTPAuthenticationDialog.show();

            if(OTPAuthenticationDialog.isShowing()){
                tv_phone_number.setText(countryList.get(sp_country_code.getSelectedItemPosition()).getsCode()+et_mobile.getText().toString());
            }

            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!et_otp.getText().toString().isEmpty()) {
                        if (mVerificationId == null) {
                            mVerificationId = "0";
                        }
                        credential = PhoneAuthProvider.getCredential(mVerificationId, et_otp.getText().toString());
                        signInWithPhoneAuthCredential(credential);
                    }else {
                        Toast.makeText(Register.this, R.string.enter_otp_please, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btc_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OTPAuthenticationDialog.dismiss();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void mobileNumberConfirm(String mobile){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.mobile_number_confirmation)
                .setMessage(getString(R.string.going_to_use_this_number_verify)+"\t\n"+mobile)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            /*if(!Objects.requireNonNull(preferences.getString("mobile", "")).isEmpty()) {
                                if (Objects.equals(preferences.getString("mobile", ""), jsonObject.getString("sMobNo"))) {
                                    if(!Objects.equals(preferences.getString("month", ""), String.valueOf(1+c.get(Calendar.MONTH)))) {
                                        OTPVerificationAlert(jsonObject);
                                    }else {
                                        UploadUserData(jsonObject);
                                    }
                                }else {
                                    OTPVerificationAlert(jsonObject);
                                }
                            }else {
                                OTPVerificationAlert(jsonObject);
                            }*/

                            OTPVerificationAlert(jsonObject);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }


    private void LoadCity(String iCountry, final String cityToSet){
        Log.d(TAG, "LoadCity: "+iCountry);
        cityList.clear();
        cityList.add(new City("","",getString(R.string.select_city)));
        AndroidNetworking.get(URLs.getCityDetails)
                .addQueryParameter("iCountry", iCountry)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("CityDetails"));
                            for(int i = 0 ; i<jsonArray.length();i++){
                                JSONObject jsonObject  = jsonArray.getJSONObject(i);
                                if(!jsonObject.getString("sName").equals(" ")){
                                    cityList.add(new City(
                                            jsonObject.getString("iId"),
                                            jsonObject.getString("sCode"),
                                            jsonObject.getString("sName")
                                    ));

                                    if(jsonArray.length()==i+1){
                                        sp_city.setAdapter(cityAdapter);
                                        for(int j = 0 ; j<cityList.size();j++) {
                                            if(cityList.get(j).getiId().equals(cityToSet)) {
                                                sp_city.setSelection(j);
                                            }
                                        }
                                    }

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("city list\n"+e.getMessage()));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("city list",anError.getErrorBody());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("city list\n"+anError.getErrorDetail()));
                    }
                });

        sp_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!adapterView.getItemAtPosition(i).toString().equals(getString(R.string.select_city))) {
                    selected_city = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void LoadCountry(final String countryToSet, final String cityToSet){
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
                                        sp_country.setAdapter(countryAdapter);
                                        sp_country_code.setAdapter(countryCodeAdapter);
                                        if(!countryToSet.equals("0")){
                                            for(int j = 0 ; j<countryList.size();j++){
                                                if(countryList.get(j).getiId().equals(countryToSet)){
                                                    sp_country.setSelection(j);
                                                }
                                            }
                                        }

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

        sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!adapterView.getItemAtPosition(i).toString().equals(getString(R.string.select_country))) {
                    if(country_selected_before!=i)
                    LoadCity(adapterView.getItemAtPosition(i).toString(),cityToSet);
                    selected_country = adapterView.getItemAtPosition(i).toString();
                }else {
                   // LoadCity("","");
                    selected_country = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void UploadUserData(final JSONObject jsonObject) {
        AndroidNetworking.post(URLs.PostUser)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("data", response);
                        if (response.equals("\"Created New User Successfully!!\"")||response.equals("\"Saved..!!\"")) {
                            //save date(MM/DD/YYYY) and phone number for further use
                            try {
                                editor.putString("mobile",jsonObject.getString("sMobNo")).apply();
                                editor.putString("date", Tools.GetCurrentDate()).apply();
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            ShowSnackBar(getString(R.string.registration_successful), getString(R.string.color_success));
                            Handler handler = new Handler();
                            final Runnable r = new Runnable() {
                                public void run() {
                                    if(!Edit) {
                                        startActivity(new Intent(Register.this, MainActivity.class));
                                    }
                                    finish();
                                }
                            };
                            handler.postDelayed(r, 500);

                        } else if (response.contains("\"Mobile Number Exists\"")) {
                            et_mobile.setError("This mobile number already registered");
                            ShowSnackBar(getString(R.string.registration_failed), getString(R.string.warning_color));

                        } else if (response.contains("\"Email Exists\"")) {
                            et_mail.setError("This email already registered");
                            ShowSnackBar(getString(R.string.registration_failed), getString(R.string.warning_color));

                        } else if (response.contains("\"UserName Exists\"")) {
                            et_username.setError("This Username already registered");
                            ShowSnackBar(getString(R.string.registration_failed), getString(R.string.warning_color));
                        } else {
                            ShowSnackBar(getString(R.string.registration_failed), getString(R.string.warning_color));

                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.toString());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("PostUser\n"+anError.getErrorDetail()));
                    }
                });
    }


    private void SendToRegister() {
        String name = "", username = "", password = "", last_name = "", mail = "", phone = "", mobile = "", address1 = "", address2 = "", country = "", city = "", pin = "";

        if (et_name.getText().toString().matches(".*\\d.*")) {
            et_name.setError(getString(R.string.name_should_not_have_number));
            ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
        } else {
            name = et_name.getText().toString().trim();
        }

        if (et_lastName.getText().toString().matches(".*\\d.*")) {
            et_lastName.setError(getString(R.string.last_name_should_not_have_numbers));
            ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
        } else {
            last_name = et_lastName.getText().toString().trim();
        }

        if (!Tools.isValidEmail(et_mail.getText().toString())) {
            et_mail.setError(getString(R.string.enter_a_valid_email));
            ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
        } else {
            mail = et_mail.getText().toString().trim();
        }

        if (!Tools.isValidPhone(et_mobile.getText().toString())) {
                et_mobile.setError(getString(R.string.enter_a_valid_phone_number));
                ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
        } else {
            mobile = countryList.get(sp_country_code.getSelectedItemPosition()).getsCode() + et_mobile.getText().toString();
            }

        if (!et_phone.getText().toString().isEmpty()) {
            if (!Tools.isValidPhone(et_phone.getText().toString())) {
                et_phone.setError(getString(R.string.enter_a_valid_mobile_number));
                ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
            } else {
                phone = et_phone.getText().toString().trim();
            }
        }
        password = et_password.getText().toString().trim();
        username = et_username.getText().toString().trim();
        address1 = et_address1.getText().toString().trim();
        address2 = et_address2.getText().toString().trim();
        country = selected_country;
        city = selected_city;


        JSONObject mainJsonObject = new JSONObject();

        try {
            if (!Edit) {
                mainJsonObject.put("iUser", 0);
            } else {
                mainJsonObject.put("iUser", Tools.getUserId(Register.this));
            }
            mainJsonObject.put("sName", name);
            mainJsonObject.put("sLastName", last_name);
            mainJsonObject.put("sUserName", username);
            mainJsonObject.put("sPassword", CryptoHandler.getInstance().encrypt(password).trim());
            mainJsonObject.put("sMobNo", mobile);
            mainJsonObject.put("sPhoneNo", phone);
            mainJsonObject.put("sEmail", mail);
            mainJsonObject.put("sAddress1", address1);
            mainJsonObject.put("sAddress2", address2);
            mainJsonObject.put("iCountry", country);
            mainJsonObject.put("iCity", city);


            jsonObject = mainJsonObject;

            mobileNumberConfirm(jsonObject.getString("sMobNo"));
            //UploadUserData(mainJsonObject);

        } catch (JSONException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(Tools.FireBaseCrashlyticsStatus());
        setContentView(R.layout.activity_register);
        et_name = findViewById(R.id.name);
        et_lastName = findViewById(R.id.last_name);
        et_username = findViewById(R.id.username);
        et_mobile = findViewById(R.id.mobile_no);
        et_phone = findViewById(R.id.phone_no);
        et_mail = findViewById(R.id.mail_id);
        et_address1 = findViewById(R.id.address_one);
        et_address2 = findViewById(R.id.address_two);
        sp_country = findViewById(R.id.country);
        sp_city = findViewById(R.id.city);
        et_password = findViewById(R.id.password);
        Button btn_register = findViewById(R.id.register);
        Button btn_Login = findViewById(R.id.login);
        cl_main = findViewById(R.id.cl_main);
        sp_country_code = findViewById(R.id.sp_country_code);
        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();

        countryList = new ArrayList<>();
        cityList = new ArrayList<>();
        countryAdapter = new CountryAdapter(countryList, Register.this);
        cityAdapter = new CityAdapter(cityList, Register.this);
        countryCodeAdapter = new CountryCodeAdapter(countryList, Register.this);


        Intent intent = getIntent();
        if (intent != null) {
            Edit = intent.getBooleanExtra("edit", false);
            if(Edit){
                LoadDataForEdit();
                btn_Login.setVisibility(View.INVISIBLE);
                btn_register.setText(R.string.continue_txt);
            }else {
                btn_Login.setVisibility(View.VISIBLE);
                btn_register.setText(R.string.register);
            }
        }


        if(!Edit)
        LoadCountry("0","0");

        SharedPreferences preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
        editor = preferences.edit();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_name.getText().toString().isEmpty()) {
                    et_name.setError(getString(R.string.enter_name));
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                } else if (et_mobile.getText().toString().isEmpty()) {
                    et_mobile.setError(getString(R.string.enter_mobile));
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                } else if (et_mail.getText().toString().isEmpty()) {
                    et_mail.setError(getString(R.string.enter_mail));
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                } else if (et_username.getText().toString().isEmpty()) {
                    et_username.setError(getString(R.string.enter_username));
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                } else if (et_password.getText().toString().isEmpty()||et_password.getText().toString().length()<6) {
                    if(et_password.getText().toString().length()<6) {
                        et_password.setError(getString(R.string.password_length_must_be));
                    }else {
                        et_password.setError(getString(R.string.enter_password));
                    }
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                } else if(countryList.get(sp_country_code.getSelectedItemPosition()).getsCode().equals(getString(R.string.code))) {
                    et_mobile.setError(getString(R.string.select_code));
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                }else {
                    SendToRegister();
                }


            }
        });


        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, MainActivity.class));
                finish();
            }
        });


        firebaseMobileAuthenticationService = new FirebaseMobileAuthenticationService(Register.this, new FirebaseMobileAuthenticationService.MobileAuthenticationListener() {
            @Override
            public void onTooManyRequestsFailure(@NonNull Exception e) {
                Log.d(TAG, "onTooManyRequestsFailure: "+e.getMessage());
            }

            @Override
            public void onInvalidCredentialFailure(@NonNull Exception e) {
                Log.d(TAG, "onInvalidCredentialFailure: "+e.getMessage());
            }

            @Override
            public void onInvalidPhoneNumber(@NonNull Exception e) {
                Log.d(TAG, "onInvalidPhoneNumber: "+e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
            forceResendingToken = token;
            }

            @Override
            public void onSignInComplete(FirebaseUser user) {

            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                if (!Objects.requireNonNull(phoneAuthCredential.getSmsCode()).isEmpty()) {
                    if (et_otp != null)
                        et_otp.setText(phoneAuthCredential.getSmsCode());
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }
        });


    }
}
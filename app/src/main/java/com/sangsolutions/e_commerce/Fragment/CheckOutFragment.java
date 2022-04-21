package com.sangsolutions.e_commerce.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.CityAdapter.City;
import com.sangsolutions.e_commerce.Adapter.CityAdapter.CityAdapter;
import com.sangsolutions.e_commerce.Adapter.CountryAdapter.Country;
import com.sangsolutions.e_commerce.Adapter.CountryAdapter.CountryAdapter;
import com.sangsolutions.e_commerce.Adapter.CountryAdapter.CountryCodeAdapter;
import com.sangsolutions.e_commerce.Adapter.ProductBeforeCheckOutAdapter.ProductBeforeCheckOut;
import com.sangsolutions.e_commerce.Adapter.ProductBeforeCheckOutAdapter.ProductBeforeCheckOutAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class CheckOutFragment extends Fragment {
    private   ProductBeforeCheckOutAdapter productBeforeCheckOutAdapter;
  private   List<ProductBeforeCheckOut> list;
  private   EditText et_name, et_lastName, et_mobile, et_phone, et_mail, et_address1, et_address2,et_pin;
  private   Spinner sp_country,sp_city;
    private   String cartJson;
  private   Handler handler;
  private   CoordinatorLayout cl_main;
  private   EditText et_otp;
  private   FirebaseAuth mAuth;
  private   AlertDialog OTPAuthenticationDialog;
  private   ImageView img_check;
  private   TextView tv_timer;
    private   PhoneAuthCredential credential;
  private   String mVerificationId;
  private   CountDownTimer countDownTimer;
  private   JSONObject jsonObject;
  private   Spinner sp_country_code;
  private   SharedPreferences preferences;
  private   SharedPreferences.Editor editor;
  private   CountryCodeAdapter countryCodeAdapter;
  private   CountryAdapter countryAdapter;
  private   List<Country> countryList;
  private   CityAdapter cityAdapter;
  private   List<City> cityList;
  private   String selected_country = "",selected_city="";

  private void ShowSnackBar(String message, String color) {
        Snackbar.make(cl_main, message, 500).setBackgroundTint(Color.parseColor(color)).show();
  }

    private void LoadCity(String iCountry){
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
                                            sp_country.setAdapter(countryAdapter);
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

      sp_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
             if(!adapterView.getItemAtPosition(i).toString().equals(getString(R.string.select_country))) {
                 LoadCity(adapterView.getItemAtPosition(i).toString());
                 selected_country = adapterView.getItemAtPosition(i).toString();
             }else {
                 LoadCity("");
                 selected_country = "";
             }
          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {

          }
      });
    }

    public void mobileNumberConfirm(String mobile){

      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(R.string.mobile_number_confirmation)
              .setMessage(getString(R.string.going_to_use_this_number_verify)+"\t\n"+mobile)
              .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      Log.d(TAG, "onClick: "+preferences.getString("date", ""));
                      try {
                      if(!Objects.requireNonNull(preferences.getString("mobile", "")).isEmpty()) {
                              if (Objects.equals(preferences.getString("mobile", ""), jsonObject.getString("sMobNo"))) {
                                  if(!Objects.equals(preferences.getString("date", ""),  Tools.GetCurrentDate())) {
                                      OTPVerificationAlert(jsonObject);
                                  }else {
                                      Upload(jsonObject);
                                  }
                              }else {
                                  OTPVerificationAlert(jsonObject);
                              }
                      }else {
                          OTPVerificationAlert(jsonObject);
                      }
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


    public void StartTimer(final String PhoneNumber,final JSONObject jsonObject){
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
                    tv_timer.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.button_6));

                    if(tv_timer.getText().toString().equals(getString(R.string.resend_otp))){

                        tv_timer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SendOTP(PhoneNumber, jsonObject);
                            }
                        });
                    }
                }
            }
        }.start();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
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
                                            Upload(jsonObject);
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



    public void SendOTP(String PhoneNumber, JSONObject jsonObject){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(PhoneNumber,120, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,mVerifyPhoneNumber);
        StartTimer(PhoneNumber,jsonObject);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerifyPhoneNumber = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            if(!Objects.requireNonNull(phoneAuthCredential.getSmsCode()).isEmpty()) {
                if(et_otp!=null)
                et_otp.setText(phoneAuthCredential.getSmsCode());
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getActivity(), R.string.faild_to_verify_please_check_mobile_number, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                if (Objects.requireNonNull(e.getLocalizedMessage()).contains("The format of the phone number provided is incorrect."))
                    Log.d(TAG, "onVerificationFailed: The format of the phone number provided is incorrect.");
                else
                    Log.d(TAG, "onVerificationFailed: Invalid credential");

            } else if (e instanceof FirebaseTooManyRequestsException) {
                Log.d(TAG, "onVerificationFailed: FirebaseTooManyRequestsException");
            } else {
                Log.d(TAG, "onVerificationFailed");
            }

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            /*mResendToken = forceResendingToken;*/



        }
    };

    @SuppressLint("SetTextI18n")
    public void OTPVerificationAlert(JSONObject jsonObject){

        try {
            String mobile = jsonObject.getString("sMobNo");

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.otp_verification_layout,null,false);
            et_otp = view.findViewById(R.id.et_otp);
            tv_timer = view.findViewById(R.id.time_left);
            img_check = view.findViewById(R.id.check);
            TextView tv_phone_number = view.findViewById(R.id.phone_no);
            Button btn_continue = view.findViewById(R.id.Continue);
            Button btc_close = view.findViewById(R.id.close);

            SendOTP(mobile,jsonObject);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        Toast.makeText(getActivity(), R.string.enter_otp_please, Toast.LENGTH_SHORT).show();
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


    private void SuccessAlert(String refNo) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.success_alert, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        TextView tv_message = view.findViewById(R.id.message);
        Button btn_ok = view.findViewById(R.id.ok);

        tv_message.setText(getString(R.string.order_placed)+"\n RefNO:"+refNo);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                ShowSnackBar(getString(R.string.order_placed), getString(R.string.color_success));
                handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                };
                handler.postDelayed(r, 500);
            }
        });


    }

    public void Upload(final JSONObject json) {
        AndroidNetworking.post(URLs.PostCart)
                .addJSONObjectBody(json)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        try {
                           JSONObject jsonObject = new JSONObject(response);

                           String refNo = jsonObject.getString("sRefNo");
                           if (!refNo.isEmpty()) {
                               editor.putString("mobile",json.getString("sMobNo")).apply();
                               editor.putString("date", Tools.GetCurrentDate()).apply();
                               File file = new File(requireActivity().getExternalFilesDir(null), "cart.json");
                               if (file.exists()) {
                                   if (file.delete()) {
                                       Log.d("cart file","deleted!");
                                   }
                               }
                               SuccessAlert(refNo);
                            }

                        } catch (JSONException e) {
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("PostCart\n"+e.getMessage()));
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("PostCart\n"+anError.getErrorDetail()));
                        Log.d("error", anError.toString());
                    }
                });

    }


    public void LoadProduct(String sJsonArray) {
        String fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName,
                sShortDescription, Qty;
        try {
            JSONArray jsonArray = new JSONArray(sJsonArray);
            if (jsonArray.length() > 0) {
                list.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    fPrice = jsonObject.getString("fPrice");
                    iId = jsonObject.getString("iId");
                    iSubCategory = jsonObject.getString("iSubCategory");
                    sAltLongDescription = jsonObject.getString("sAltLongDescription");
                    sAltName = jsonObject.getString("sAltName");
                    sAltShortDescription = jsonObject.getString("sAltShortDescription");
                    sCode = jsonObject.getString("sCode");
                    sImagePath = jsonObject.getString("sImagePath");
                    sLongDescription = jsonObject.getString("sLongDescription");
                    sName = jsonObject.getString("sName");
                    sShortDescription = jsonObject.getString("sShortDescription");
                    Qty = jsonObject.getString("Qty");

                    if (!iId.equals("0")) {
                        list.add(new ProductBeforeCheckOut(fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription, Qty));
                    }


                    if (jsonArray.length() == i + 1) {
                        productBeforeCheckOutAdapter.notifyDataSetChanged();
                    }

                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void SendCart() {
        boolean upload = true;
        String name = "", last_name = "", mail = "", phone = "", mobile = "", address1, address2, country , city , pin ;

        if (et_name.getText().toString().matches(".*\\d.*")) {
            et_name.setError(getString(R.string.name_should_not_have_number));
            ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
            upload = false;
        } else {
            name = et_name.getText().toString();
        }

        if (et_lastName.getText().toString().matches(".*\\d.*")) {
            et_lastName.setError(getString(R.string.last_name_should_not_have_numbers));
            ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
            upload = false;
        } else {
            last_name = et_lastName.getText().toString();
        }

        if (!Tools.isValidEmail(et_mail.getText().toString())) {
            et_mail.setError(getString(R.string.enter_a_valid_email));
            ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
            upload = false;
        } else {
            mail = et_mail.getText().toString();
        }

        if (!Tools.isValidPhone(et_mobile.getText().toString())) {
            et_mobile.setError(getString(R.string.enter_a_valid_phone_number));
            ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
            upload = false;
        } else {
            mobile =countryList.get(sp_country_code.getSelectedItemPosition()).getsCode()+et_mobile.getText().toString();
        }

        if (!et_phone.getText().toString().isEmpty()) {
            if (!Tools.isValidPhone(et_phone.getText().toString())) {
                et_phone.setError(getString(R.string.enter_a_valid_mobile_number));
                ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                upload = false;
            } else {
                phone = et_phone.getText().toString();
            }
        }

        address1 = et_address1.getText().toString();
        address2 = et_address2.getText().toString();
        country = selected_country;
        city = selected_city;
        pin = et_pin.getText().toString();

        JSONObject mainJsonObject = new JSONObject();
        if (!cartJson.isEmpty()) {

            try {
                mainJsonObject.put("sName", name);
                mainJsonObject.put("sLastName", last_name);
                mainJsonObject.put("iUserId", Tools.getUserId(requireActivity()));
                mainJsonObject.put("sMobNo", mobile);
                mainJsonObject.put("sPhoneNo", phone);
                mainJsonObject.put("sEmail", mail);
                mainJsonObject.put("sAddress1", address1);
                mainJsonObject.put("sAddress2", address2);
                mainJsonObject.put("iCountry", country);
                mainJsonObject.put("iCity", city);
                mainJsonObject.put("sPinCode", pin);


                JSONArray jsonArray = new JSONArray(cartJson);

                JSONArray newJsonArray = new JSONArray();


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject newJsonObject = new JSONObject();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    newJsonObject.put("iProduct", jsonObject.getString("iId"));
                    newJsonObject.put("fRate", jsonObject.getString("fPrice"));
                    newJsonObject.put("fQty", jsonObject.getString("Qty"));

                    newJsonArray.put(newJsonObject);

                    if (jsonArray.length() == i + 1) {
                        mainJsonObject.put("CartDetails", newJsonArray);
                    }
                }
                Log.d(TAG, "SendCart: "+mainJsonObject.toString());
                //before going to upload mobile number need to get verified every month that's not this month for that sharedPreference is used
                if (upload){

                    mobileNumberConfirm(mainJsonObject.getString("sMobNo"));

                    jsonObject = mainJsonObject;

                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.check_out_fragment, container, false);
        preferences = requireActivity().getSharedPreferences("phone", Context.MODE_PRIVATE);
        editor = preferences.edit();
        et_name = view.findViewById(R.id.name);
        et_lastName = view.findViewById(R.id.last_name);
        et_mobile = view.findViewById(R.id.mobile_no);
        et_phone = view.findViewById(R.id.phone_no);
        et_mail = view.findViewById(R.id.mail_id);
        et_address1 = view.findViewById(R.id.address_one);
        et_address2 = view.findViewById(R.id.address_two);
        sp_country = view.findViewById(R.id.country);
        sp_city = view.findViewById(R.id.city);
        et_pin = view.findViewById(R.id.pin);
        sp_country_code = view.findViewById(R.id.sp_country_code);
        Button aContinue = view.findViewById(R.id.Continue);
        cl_main = view.findViewById(R.id.cl_main);
        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();
        RecyclerView rv_product = view.findViewById(R.id.product);
        rv_product.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rv_product.setHasFixedSize(true);
        list = new ArrayList<>();
        countryList = new ArrayList<>();
        countryAdapter = new CountryAdapter(countryList,getActivity());
        countryCodeAdapter = new CountryCodeAdapter(countryList,getActivity());
        cityList = new ArrayList<>();
        cityAdapter = new CityAdapter(cityList,getActivity());

        LoadCountry();
        productBeforeCheckOutAdapter = new ProductBeforeCheckOutAdapter(getActivity(), list);
        rv_product.setAdapter(productBeforeCheckOutAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            cartJson = bundle.getString("cartJson");

            if (cartJson != null) {
                LoadProduct(cartJson);
            }
        }

        aContinue.setOnClickListener(new View.OnClickListener() {
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
                } else if (countryList.get(sp_country_code.getSelectedItemPosition()).getsCode().equals(getString(R.string.code))) {
                    et_mobile.setError(getString(R.string.select_code));
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                } else if (sp_country.getSelectedItemPosition() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.select_country), Toast.LENGTH_SHORT).show();
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                }else if(sp_city.getSelectedItemPosition()==0){
                    Toast.makeText(getActivity(), getString(R.string.select_city), Toast.LENGTH_SHORT).show();
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                }else if(et_address1.getText().toString().isEmpty()) {
                    et_address1.setError(getString(R.string.enter_address));
                    ShowSnackBar(getString(R.string.error_in_given_information), getString(R.string.warning_color));
                }else {
                    SendCart();
                }
            }
        });


        return view;
    }
}

package com.sangsolutions.e_commerce.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class suggestionsAndComplaints extends Fragment {
    String type ="";
    TextView tv_title;
    EditText et_name,et_mobile,et_mail,et_description;
    Button btn_continue;
    CoordinatorLayout cl_main;
    private void ShowSnackBar(String message, String color) {
        Snackbar.make(cl_main, message, 500).setBackgroundTint(Color.parseColor(color)).show();
    }
    public void UploadFeedBack(){
        String description="",mobile="",email="",name="";
        description = et_description.getText().toString();
        mobile = et_mobile.getText().toString();
        email = et_mail.getText().toString();
        name = et_name.getText().toString();
        if(!mobile.isEmpty()) {
            if (!Tools.isValidPhone(mobile)||mobile.length()<10||mobile.length()>12) {
                ShowSnackBar(getString(R.string.error_in_given_information),getString(R.string.warning_color));
                et_mobile.setError(getString(R.string.enter_a_valid_phone_number));
                return;
            }
        }
        if(!email.isEmpty()){
            if (!Tools.isValidEmail(email)){
                ShowSnackBar(getString(R.string.error_in_given_information),getString(R.string.warning_color));
                et_mail.setError(getString(R.string.enter_a_valid_email));
                return;
            }
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("iType",type);
            jsonObject.put("iUserId",Tools.getUserId(requireActivity()));
            jsonObject.put("iDate",Tools.GetCurrentDate());
            jsonObject.put("sName",name);
            jsonObject.put("sMobNo",mobile);
            jsonObject.put("sEmail",email);
            jsonObject.put("sFeedback",description);
            jsonObject.put("iStatus",0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(jsonObject.length()!=0)
        AndroidNetworking.post(URLs.PostFeedBack)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                if(response.equals("1")){
                    ShowSnackBar(getString(R.string.feedback_posted_thanks_for_your_feedback),getString(R.string.color_success));
                    Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT).show();

                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    };
                    handler.postDelayed(r, 500);


                }else {
                    ShowSnackBar(getString(R.string.failed),getString(R.string.warning_color));
                    Toast.makeText(getActivity(), R.string.failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Log.d(TAG, "onError: "+anError.getResponse());
                ShowSnackBar(getString(R.string.failed),getString(R.string.warning_color));
                FirebaseCrashlytics.getInstance().recordException(new Throwable("PostFeedBack\n"+anError.getResponse()));

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = LayoutInflater.from(getActivity()).inflate(R.layout.suggestions_and_complaints, container, false);
       Bundle bundle = getArguments();
       if(bundle!=null) {
          type = bundle.getString("type");

           tv_title = view.findViewById(R.id.title);
           et_name = view.findViewById(R.id.name);
           et_mobile = view.findViewById(R.id.mobile_no);
           et_mail = view.findViewById(R.id.mail_id);
           et_description = view.findViewById(R.id.description);
           btn_continue = view.findViewById(R.id.Continue);
           cl_main = view.findViewById(R.id.cl_main);

           assert type != null;
           if(type.equals("1")){
              tv_title.setText(getString(R.string.suggestion));
          }else {
               tv_title.setText(getString(R.string.complaints));
           }
       }else {
           requireActivity().getSupportFragmentManager().popBackStack();
       }



        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_description.getText().toString().isEmpty()){
                    et_description.setError(getString(R.string.enter_description));
                }else {
                    UploadFeedBack();
                }
            }
        });
        return view;
    }
}

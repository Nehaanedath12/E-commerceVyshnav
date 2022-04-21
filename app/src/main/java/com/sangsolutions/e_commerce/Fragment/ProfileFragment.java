package com.sangsolutions.e_commerce.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.sangsolutions.e_commerce.Home;
import com.sangsolutions.e_commerce.MainActivity;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Register;
import com.sangsolutions.e_commerce.Tools;

import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences.Editor editor;


    private void LogOut() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alert_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        TextView tv_title = view.findViewById(R.id.title);
        TextView tv_message = view.findViewById(R.id.message);
        Button btn_positive = view.findViewById(R.id.positive);
        Button btn_negative = view.findViewById(R.id.negative);


        tv_title.setText(getString(R.string.logout_));
        tv_message.setText(getString(R.string.do_you_want_to_logout));
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                editor.clear().apply();
                requireActivity().recreate();
            }
        });

    }

    public void GoToSuggestions(){
        Fragment fragment = new suggestionsAndComplaints();
        Bundle bundle = new Bundle();
        bundle.putString("type", "1");
        fragment.setArguments(bundle);
        FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment, fragment).addToBackStack("suggestionsAndComplaints").commit();
    }

    public void GoToComplaints(){
        Fragment fragment = new suggestionsAndComplaints();
        Bundle bundle = new Bundle();
        bundle.putString("type", "2");
        fragment.setArguments(bundle);
        FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment, fragment).addToBackStack("suggestionsAndComplaints").commit();
    }

    public void EditProfile(){
        Intent intent = new Intent(getActivity(), Register.class);
        intent.putExtra("edit",true);
        startActivity(intent);

    }

    public void SelectLanguage() {
        Fragment fragment = new LanguageSelectionFragment();
        FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment, fragment).addToBackStack("language").commit();
    }

    public void LoadPrivacyPolicy() {
        Fragment fragment = new PrivacyPolicyFragment();
        FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment, fragment).addToBackStack("privacy").commit();
    }

    public void OpenOrderList(){
        Fragment fragment = new OrderHistoryFragment();
        FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment, fragment).addToBackStack("orderList").commit();
    }

    private void LoadTrackOrder() {
        Fragment fragment = new orderTrackingFragment();
        FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment, fragment).addToBackStack("orderTrack").commit();
    }


    private void LoadTermsAndConditions() {
        Fragment fragment = new TermsAndConditionsFragment();
        FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment, fragment).addToBackStack("terms").commit();
    }


    @SuppressLint("CommitPrefEdits")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.profile_fragment, container, false);
        SharedPreferences preferences = requireActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = preferences.edit();
        CardView card_order_list = view.findViewById(R.id.card_order_list);
        CardView card_select_language = view.findViewById(R.id.card_select_language);
        CardView card_privacy_policy = view.findViewById(R.id.card_privacy_policy);
        CardView card_logout = view.findViewById(R.id.card_logout);
        CardView card_order_tracking = view.findViewById(R.id.card_order_tracking);
        CardView card_terms_and_conditions = view.findViewById(R.id.card_terms);
        CardView card_complaints = view.findViewById(R.id.card_complaints);
        CardView card_suggestion = view.findViewById(R.id.card_suggestion);

        card_order_list.setOnClickListener(this);
        card_select_language.setOnClickListener(this);
        card_privacy_policy.setOnClickListener(this);
        card_logout.setOnClickListener(this);
        card_order_tracking.setOnClickListener(this);
        card_terms_and_conditions.setOnClickListener(this);
        card_complaints.setOnClickListener(this);
        card_suggestion.setOnClickListener(this);

        TextView name = view.findViewById(R.id.name);
        TextView signin = view.findViewById(R.id.signin);
        TextView description = view.findViewById(R.id.description);
        ImageView img_edit = view.findViewById(R.id.edit);

        img_edit.setOnClickListener(this);

        if (!Objects.equals(preferences.getString("sUserName", ""), "")) {
            name.setText(preferences.getString("sUserName", ""));
            signin.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            card_logout.setVisibility(View.VISIBLE);
            img_edit.setVisibility(View.VISIBLE);
        } else {
            name.setText(getString(R.string.welcome_guest));
            signin.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            card_logout.setVisibility(View.GONE);
            img_edit.setVisibility(View.GONE);
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.card_order_list:
                OpenOrderList();
                break;

            case R.id.card_select_language:
                SelectLanguage();
                break;

            case R.id.card_privacy_policy:
                LoadPrivacyPolicy();
                break;
            case R.id.card_terms:
                LoadTermsAndConditions();
                break;
            case R.id.card_logout:
                LogOut();
                break;
            case R.id.card_order_tracking:
                LoadTrackOrder();
                break;
            case R.id.edit:
                EditProfile();
                break;
            case R.id.card_complaints:
                GoToComplaints();
                break;
            case R.id.card_suggestion:
                GoToSuggestions();
                break;
        }

    }



}

package com.sangsolutions.e_commerce.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.sangsolutions.e_commerce.Home;
import com.sangsolutions.e_commerce.R;

import java.util.Locale;
import java.util.Objects;

public class LanguageSelectionFragment extends Fragment {
    private  SharedPreferences preferences;
  private  SharedPreferences.Editor editor;
  private  LinearLayout ll_english, ll_arabic;

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        ((Home)requireActivity()).RemoveFragment();
        requireActivity().recreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.language_selection_fragment, container, false);
        preferences = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE);
        Button btn_language = view.findViewById(R.id.language);
        ll_english = view.findViewById(R.id.english);
        ll_arabic = view.findViewById(R.id.arabic);
        editor = preferences.edit();


        if (Objects.equals(preferences.getString("language", ""), "english")) {
            ll_english.setBackgroundColor(Color.rgb(103, 234, 103));
            ll_arabic.setBackgroundColor(Color.WHITE);
        } else {
            ll_english.setBackgroundColor(Color.WHITE);
            ll_arabic.setBackgroundColor(Color.rgb(103, 234, 103));
        }


        ll_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_english.setBackgroundColor(Color.rgb(103, 234, 103));
                ll_arabic.setBackgroundColor(Color.WHITE);
                editor.putString("language", "english").apply();
            }
        });

        ll_arabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_english.setBackgroundColor(Color.WHITE);
                ll_arabic.setBackgroundColor(Color.rgb(103, 234, 103));
                editor.putString("language", "arabic").apply();
            }
        });

        btn_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(preferences.getString("language", ""), "english")) {
                    setLocale("ar");
                } else {
                    setLocale("en");
                }
            }
        });
        return view;
    }
}

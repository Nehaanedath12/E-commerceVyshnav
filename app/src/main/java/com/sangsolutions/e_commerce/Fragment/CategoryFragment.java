package com.sangsolutions.e_commerce.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.ListCategoryAdapter.ListCategory;
import com.sangsolutions.e_commerce.Adapter.ListCategoryAdapter.ListCategoryAdapter;
import com.sangsolutions.e_commerce.Adapter.ListSubCategoryAdapter.ListSubCategory;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryFragment extends Fragment {
   private SharedPreferences preferences;
   private List<ListCategory> listCategory;
   private ListCategoryAdapter categoryAdapter;
   private RecyclerView categoryRecyclerView;

    public void LoadCategory() {
        AndroidNetworking.get(URLs.getCategory)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listCategory.clear();
                        String iId, sAltName, sName, sCode;
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("categoryDetails"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                iId = jsonObject.getString("iId");
                                sAltName = jsonObject.getString("sAltName");
                                sName = jsonObject.getString("sName");
                                sCode = jsonObject.getString("sCode");

                                listCategory.add(new ListCategory(iId, sAltName, sCode, sName));


                                if (jsonArray.length() == i + 1) {
                                    categoryAdapter.notifyDataSetChanged();
                                    categoryRecyclerView.setItemViewCacheSize(listCategory.size());
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getCategory\n"+e.getMessage()));
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorDetail());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("getCategory\n"+anError.getErrorDetail()));
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_fragment, container, false);
        preferences = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE);
        listCategory = new ArrayList<>();
        categoryAdapter = new ListCategoryAdapter(listCategory, getActivity());

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setAdapter(categoryAdapter);

        LoadCategory();

        categoryAdapter.setOnClickListener(new ListCategoryAdapter.OnClickListener() {
            @Override
            public void onSubCategoryItemClick(View view, ListSubCategory subCategory, int pos) {
                Fragment fragment = new ViewMoreFragment();
                Bundle bundle = new Bundle();
                bundle.putString("iId", subCategory.getiId());
                bundle.putString("type", "sub");
                if (Objects.equals(preferences.getString("language", ""), "english")) {
                    bundle.putString("title", subCategory.getsName());
                } else {
                    bundle.putString("title", subCategory.getsAltName());
                }
                fragment.setArguments(bundle);


                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("ViewSubCategory").commit();
            }
        });
        return view;
    }
}

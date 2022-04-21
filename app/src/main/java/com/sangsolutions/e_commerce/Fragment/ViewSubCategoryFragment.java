package com.sangsolutions.e_commerce.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.sangsolutions.e_commerce.Adapter.CategoryWiseProductAdapter.CategoryWiseProduct;
import com.sangsolutions.e_commerce.Adapter.SubCategoryAdapter.SubCategory;
import com.sangsolutions.e_commerce.Adapter.SubCategoryAdapter.SubCategoryAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewSubCategoryFragment extends Fragment {
  private  List<SubCategory> list;
  private  SubCategoryAdapter subCategoryAdapter;
    private  RecyclerView rv_sub_category;
    private  SharedPreferences preferences;

    private void LoadViewMore(String iId) {
        AndroidNetworking.get(URLs.getSubCategory)
                .addQueryParameter("iCategory", iId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String iId, sAltName, sCode, sName;

                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("SubcategoryDetails"));
                            list.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                iId = jsonObject.getString("iId");
                                sAltName = jsonObject.getString("sAltName");
                                sCode = jsonObject.getString("sCode");
                                sName = jsonObject.getString("sName");

                                if (!iId.equals("0")) {
                                    list.add(new SubCategory(iId, sAltName, sCode, sName));
                                }

                                if (jsonArray.length() == i + 1) {
                                    rv_sub_category.setAdapter(subCategoryAdapter);
                                }
                            }


                        } catch (JSONException e) {
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getSubCategory\n"+e.getMessage()));
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorDetail());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("getSubCategory\n"+anError.getErrorDetail()));
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_sub_category, container, false);
        list = new ArrayList<>();
        subCategoryAdapter = new SubCategoryAdapter(getActivity(), list);
        preferences = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE);

        TextView tv_title = view.findViewById(R.id.title);
        rv_sub_category = view.findViewById(R.id.rv_sub_category);
        rv_sub_category.setHasFixedSize(true);
        rv_sub_category.setLayoutManager(new LinearLayoutManager(getActivity()));
        Bundle bundle = getArguments();
        if (bundle != null) {
            String iId = bundle.getString("iId");
            tv_title.setText(bundle.getString("title"));
            LoadViewMore(iId);
        }

        subCategoryAdapter.setOnClickListener(new SubCategoryAdapter.OnClickListener() {
            @Override
            public void onProductItemClick(View view, CategoryWiseProduct product, int pos) {
                Fragment fragment = new ViewProductFragment();

                Bundle bundle = new Bundle();
                bundle.putString("iId", product.getiId());
                fragment.setArguments(bundle);


                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("view").commit();
            }

            @Override
            public void onSubCategoryItemClick(View view, SubCategory category, int pos) {
                Fragment fragment = new ViewMoreFragment();

                Bundle bundle = new Bundle();
                bundle.putString("type", "more");
                bundle.putString("iId", category.getiId());
                if (Objects.equals(preferences.getString("language", ""), "english")) {
                    bundle.putString("title", category.getsName());
                } else {
                    bundle.putString("title", category.getsAltName());
                }
                fragment.setArguments(bundle);

                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("view").commit();
            }

        });


        return view;


    }
}

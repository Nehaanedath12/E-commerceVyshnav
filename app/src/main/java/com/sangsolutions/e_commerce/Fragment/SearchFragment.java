package com.sangsolutions.e_commerce.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

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
import com.sangsolutions.e_commerce.Adapter.SearchProductAdapter.SearchProduct;
import com.sangsolutions.e_commerce.Adapter.SearchProductAdapter.SearchProductAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private List<SearchProduct> list;
  private SearchProductAdapter searchProductAdapter;
  private FrameLayout frame_error, frame_progress;


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void SearchProduct(String keyword) {
        frame_progress.setVisibility(View.VISIBLE);
        if (!keyword.equals("")) {
            AndroidNetworking.get(URLs.getSearchProductDetails)
                    .addQueryParameter("sText", keyword)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {

                            String fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName,
                                    sShortDescription;
                            try {
                                list.clear();
                                JSONArray jsonArray = new JSONArray(response.getString("productDetails"));

                                if (jsonArray.length() <= 0) {
                                    list.clear();
                                    frame_error.setVisibility(View.VISIBLE);
                                    frame_progress.setVisibility(View.GONE);
                                    searchProductAdapter.notifyDataSetChanged();
                                }

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

                                    list.add(new SearchProduct(iId, sName, sCode, sAltName, sImagePath, iSubCategory, fPrice, sShortDescription, sLongDescription, sAltShortDescription, sAltLongDescription));

                                    if (jsonArray.length() == i + 1) {
                                        frame_error.setVisibility(View.INVISIBLE);
                                        frame_progress.setVisibility(View.GONE);
                                        searchProductAdapter.notifyDataSetChanged();
                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                list.clear();
                                frame_error.setVisibility(View.VISIBLE);
                                frame_progress.setVisibility(View.GONE);
                                FirebaseCrashlytics.getInstance().recordException(new Throwable("getSearchProductDetails\n"+e.getMessage()));
                                searchProductAdapter.notifyDataSetChanged();
                            }


                        }

                        @Override
                        public void onError(ANError anError) {
                            frame_error.setVisibility(View.VISIBLE);
                            frame_progress.setVisibility(View.GONE);
                            searchProductAdapter.notifyDataSetChanged();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getSearchProductDetails\n"+anError.getErrorDetail()));
                            Log.d("error", anError.getErrorDetail());
                        }
                    });
        } else {
            list.clear();
            frame_error.setVisibility(View.VISIBLE);
            frame_progress.setVisibility(View.GONE);
            searchProductAdapter.notifyDataSetChanged();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_layout_fragment, container, false);
        list = new ArrayList<>();
        searchProductAdapter = new SearchProductAdapter(getActivity(), list);
        frame_error = view.findViewById(R.id.empty_frame);
        EditText et_search = view.findViewById(R.id.et_search);
        frame_progress = view.findViewById(R.id.progress_frame);
        //request keyboard to open without clicking edittext
        et_search.requestFocus();
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_search, InputMethodManager.SHOW_IMPLICIT);

        RecyclerView rv_search = view.findViewById(R.id.rv_search);
        rv_search.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_search.setHasFixedSize(true);
        rv_search.setAdapter(searchProductAdapter);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SearchProduct(editable.toString());
            }
        });


        searchProductAdapter.setOnClickListener(new SearchProductAdapter.OnClickListener() {
            @Override
            public void onProductItemClick(View view, SearchProduct searchProduct, int pos) {
                hideKeyboard(requireActivity());
                Fragment fragment = new ViewProductFragment();
                Bundle bundle = new Bundle();
                bundle.putString("iId", searchProduct.getiId());
                fragment.setArguments(bundle);
                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("view").commit();
            }
        });

        return view;
    }
}

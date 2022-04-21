package com.sangsolutions.e_commerce.Fragment;

import android.content.res.Configuration;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.NewArrivalAdapter.NewArrival;
import com.sangsolutions.e_commerce.Adapter.NewArrivalAdapter.NewArrivalAdapter;
import com.sangsolutions.e_commerce.Adapter.ViewMoreAdapter.ViewMore;
import com.sangsolutions.e_commerce.Adapter.ViewMoreAdapter.ViewMoreAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ViewMoreFragment extends Fragment {
  private  List<ViewMore> list;
  private  List<NewArrival> list2;
  private  ViewMoreAdapter viewMoreAdapter;
  private  NewArrivalAdapter newArrivalAdapter;
  private  RecyclerView rv_view_more;
  private  Bundle bundle;
  private  TextView tv_title;
  private  View view;
  private  String type = "";

    private void LoadViewMore(String iId) {
        AndroidNetworking.get(URLs.getProductSubCategoryWise)
                .addQueryParameter("iSubCategory", iId)
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

                                if (!iId.equals("0")) {
                                    list.add(new ViewMore(fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription));
                                }


                                if (jsonArray.length() == i + 1) {
                                    rv_view_more.setAdapter(viewMoreAdapter);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getProductSubCategoryWise\n"+e.getMessage()));
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorDetail());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("getProductSubCategoryWise\n"+anError.getMessage()));
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        rv_view_more = view.findViewById(R.id.rv_more);
        rv_view_more.setHasFixedSize(true);
        rv_view_more.setLayoutManager(new GridLayoutManager(getActivity(), Tools.calculateNoOfColumns(requireActivity(), 180)));

        assert type != null;
        if (type.equals("new")) {
            LoadViewMoreNewArrived();
        } else {
            String iId = bundle.getString("iId");
            LoadViewMore(iId);
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        if (bundle != null) {
            type = bundle.getString("type");
            tv_title.setText(bundle.getString("title"));

        }
    }

    private void LoadViewMoreNewArrived() {
        AndroidNetworking.get(URLs.getNewArrivals)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName,
                                sShortDescription;
                        try {
                            list2.clear();
                            JSONArray jsonArray = new JSONArray(response.getString("NewArrivalsdetails"));
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

                                if (!iId.equals("0")) {
                                    list2.add(new NewArrival(fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription));
                                }


                                if (jsonArray.length() == i + 1) {
                                    rv_view_more.setAdapter(newArrivalAdapter);
                                }

                            }

                        } catch (JSONException e) {
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("view more/getNewArrivals\n"+e.getMessage()));
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorDetail());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("view more/getNewArrivals\n"+anError.getErrorDetail()));
                    }
                });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        super.onResume();
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.view_more_fragment, container, false);
        list2 = new ArrayList<>();
        list = new ArrayList<>();
        viewMoreAdapter = new ViewMoreAdapter(getActivity(), list);
        newArrivalAdapter = new NewArrivalAdapter(getActivity(), list2, "big");
        tv_title = view.findViewById(R.id.title);
        bundle = getArguments();


        newArrivalAdapter.setOnClickListener(new NewArrivalAdapter.OnClickListener() {
            @Override
            public void onProductItemClick(View view, NewArrival newArrival, int pos) {
                Fragment fragment = new ViewProductFragment();

                Bundle bundle = new Bundle();
                bundle.putString("iId", newArrival.getiId());
                fragment.setArguments(bundle);


                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("view").commit();
            }

            @Override
            public void onWishlistAdded(View view) {

            }
        });


        viewMoreAdapter.setOnClickListener(new ViewMoreAdapter.OnClickListener() {
            @Override
            public void onProductItemClick(View view, ViewMore viewMore, int pos) {
                Fragment fragment = new ViewProductFragment();

                Bundle bundle = new Bundle();
                bundle.putString("iId", viewMore.getiId());
                fragment.setArguments(bundle);


                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("view").commit();
            }
        });


        return view;
    }
}

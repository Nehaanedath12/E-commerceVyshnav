package com.sangsolutions.e_commerce.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.Banner;
import com.sangsolutions.e_commerce.Adapter.CategoryAdapter.Category;
import com.sangsolutions.e_commerce.Adapter.CategoryAdapter.CategoryAdapter;
import com.sangsolutions.e_commerce.Adapter.CategoryWiseProductAdapter.CategoryWiseProduct;
import com.sangsolutions.e_commerce.Adapter.NewArrivalAdapter.NewArrival;
import com.sangsolutions.e_commerce.Adapter.NewArrivalAdapter.NewArrivalAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.URLs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {
   private ViewFlipper viewFlipperBanner;
   private TextView tv_new_arrival, tv_new_arrival_view_more;
   private List<Banner> listBanner;
    private SharedPreferences preferences;
   private NewArrivalAdapter newArrivalAdapter;
   private List<NewArrival> newArrivalList;

   private List<Category> listCategory;
   private CategoryAdapter categoryAdapter;

    private SwipeRefreshLayout swipe_layout;

    public void LoadBanner(final Context context) {
        AndroidNetworking.get(URLs.getBanner)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String iId, sImageLink, sName;
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("BannerDetails"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                iId = jsonObject.getString("iId");
                                sImageLink = jsonObject.getString("sImageLink");
                                sName = jsonObject.getString("sName");

                                if (!sImageLink.equals(" ")) {
                                    listBanner.add(new Banner(iId, sImageLink, sName));
                                }

                                if (jsonArray.length() == i + 1) {
                                    for (int j = 0; j < listBanner.size(); j++) {
                                        FlipImages(listBanner.get(j).getsImageLink(),j,context);
                                    }
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getBanner\n"+e.getMessage()));
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorDetail());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("getBanner\n"+anError.getErrorDetail()));

                    }
                });
    }

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

                                listCategory.add(new Category(iId, sAltName, sCode, sName));


                                if (jsonArray.length() == i + 1) {
                                    categoryAdapter.notifyDataSetChanged();
                                    swipe_layout.setRefreshing(false);
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
                        swipe_layout.setRefreshing(false);
                    }
                });
    }

    private void LoadNewArrival() {
        AndroidNetworking.get(URLs.getNewArrivals)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        newArrivalList.clear();
                        String fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName,
                                sShortDescription;
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("NewArrivalsdetails"));

                            if(jsonArray.length()==0){
                                tv_new_arrival.setText("");
                                tv_new_arrival_view_more.setText("");
                            }else {
                                tv_new_arrival.setText(getString(R.string.new_arrival));
                                tv_new_arrival_view_more.setText(getString(R.string.view_more));
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

                                if (!iId.equals("0")) {
                                    newArrivalList.add(new NewArrival(fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription));
                                }


                                if (jsonArray.length() == i + 1) {
                                    newArrivalAdapter.notifyDataSetChanged();
                                }

                            }

                        } catch (JSONException e) {
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getNewArrivals\n"+e.getMessage()));
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("getNewArrivals\n"+anError.getErrorDetail()));
                        Log.d("error", anError.getErrorDetail());
                    }
                });
    }

    public void FlipImages(String image, int i,Context context) {
        if (context != null){
            ImageView imageView = new ImageView(context);
            Picasso.get().load(image).error(R.drawable.place_holder).into(imageView);
            viewFlipperBanner.addView(imageView, i);
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LoadBanner(context);
        LoadCategory();
        LoadNewArrival();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_fragment, container, false);
        AndroidNetworking.initialize(requireActivity());
        preferences = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE);

        tv_new_arrival = view.findViewById(R.id.tv_new_arrival);
        tv_new_arrival_view_more = view.findViewById(R.id.tv_arrived_view_more);
        swipe_layout = view.findViewById(R.id.swipe_layout);




        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_layout.setRefreshing(true);
                LoadBanner(requireContext());
                LoadCategory();
                LoadNewArrival();
            }
        });

        EditText et_search = view.findViewById(R.id.search);
        tv_new_arrival.setText(R.string.new_arrival);
        tv_new_arrival_view_more.setText(getString(R.string.view_more));

        viewFlipperBanner = view.findViewById(R.id.view_flipper_banner);
        viewFlipperBanner.setFlipInterval(4000);
        viewFlipperBanner.setAutoStart(true);

        viewFlipperBanner.setInAnimation(getActivity(), android.R.anim.slide_in_left);
        viewFlipperBanner.setOutAnimation(getActivity(), android.R.anim.slide_out_right);

        listBanner = new ArrayList<>();


        listCategory = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getActivity(), listCategory);

        newArrivalList = new ArrayList<>();
        newArrivalAdapter = new NewArrivalAdapter(getActivity(), newArrivalList, "small");


        RecyclerView rv_new_arrival = view.findViewById(R.id.rv_new_arrival);
        rv_new_arrival.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        rv_new_arrival.setAdapter(newArrivalAdapter);


        RecyclerView categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setAdapter(categoryAdapter);

        tv_new_arrival_view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ViewMoreFragment();
                Bundle bundle = new Bundle();
                bundle.putString("type", "new");
                bundle.putString("title", getString(R.string.new_arrival));
                fragment.setArguments(bundle);


                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("viewMore").commit();
            }
        });


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
                LoadCategory();
                LoadNewArrival();
            }

        });

        categoryAdapter.setOnClickListener(new CategoryAdapter.OnClickListener() {
            @Override
            public void onProductItemClick(View view, CategoryWiseProduct product, int pos) {
                Fragment fragment = new ViewProductFragment();

                Bundle bundle = new Bundle();
                bundle.putString("iId", product.getiId());
                if (Objects.equals(preferences.getString("language", ""), "english")) {
                    bundle.putString("title", product.getsName());
                } else {
                    bundle.putString("title", product.getsAltName());
                }
                fragment.setArguments(bundle);


                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("view").commit();
            }

            @Override
            public void onCategoryItemClick(View view, Category category, int pos) {
                Fragment fragment = new ViewSubCategoryFragment();

                Bundle bundle = new Bundle();
                bundle.putString("iId", category.getiId());
                if (Objects.equals(preferences.getString("language", ""), "english")) {
                    bundle.putString("title", category.getsName());
                } else {
                    bundle.putString("title", category.getsAltName());
                }

                fragment.setArguments(bundle);


                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("ViewSubCategory").commit();
            }

            @Override
            public void onWishlistAdded(View view) {
                LoadCategory();
                LoadNewArrival();
            }
        });

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SearchFragment();
                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("Search").commit();
            }
        });

        return view;
    }


}

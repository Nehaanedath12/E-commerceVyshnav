package com.sangsolutions.e_commerce.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.ProductImagesAdapter.ProductImages;
import com.sangsolutions.e_commerce.Adapter.ProductImagesAdapter.ProductImagesAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewProductFragment extends Fragment {
   private    TextView tv_name, tv_price;
               // ImageView img_image;
   private    ViewPager2 viewPager2;
    private    SharedPreferences preferences;
   private    String fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName,
                sShortDescription;
    private    TabLayout tabLayout;
   private    View descriptionview;
   private    Bundle bundle;
   private    JSONArray jsonArray;
   private    CoordinatorLayout cl_main;
   private    ImageView wishlist;
    private    List<ProductImages> list;
   private    ProductImagesAdapter imagesAdapter;

    public void LoadImages(String pId){

        AndroidNetworking.get(URLs.GetProductImage)
                .addQueryParameter("iProduct",pId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("data",response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("ProductImages"));
                            for(int i = 0 ; i<jsonArray.length();i++){
                                final String IId,iImageId,sImagePath;
                                try  {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    IId = jsonObject.getString("IId");
                                    iImageId = jsonObject.getString("iImageId");
                                    sImagePath = jsonObject.getString("sImagePath");
                                    list.add(new ProductImages(IId,iImageId,sImagePath));

                                    if(response.length()==i+1){
                                        viewPager2.setAdapter(imagesAdapter);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("GetProductImage\n"+e.getMessage()));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    Log.d("error",anError.toString());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("GetProductImage\n"+anError.getErrorDetail()));
                    }
                });
    }



    public boolean CheckProductInsideWishlist(String iId) {
        String sJsonArray = Tools.ReadWishListFile(requireActivity());
        try {
            JSONArray fileJsonArray = new JSONArray(sJsonArray);
            for (int i = 0; i < fileJsonArray.length(); i++) {
                JSONObject jsonObject = fileJsonArray.getJSONObject(i);
                if (jsonObject.getString("iId").equals(iId)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            return false;
        }


        return false;
    }

    public boolean AddOrRemoveFromWishList(ImageView wish_img) {

        JSONArray newJsonArray = new JSONArray();
        JSONObject newJsonObject = new JSONObject();
        if (jsonArray != null) {
            try {
                newJsonObject.put("fPrice", jsonArray.getJSONObject(0).getString("fPrice"));
                newJsonObject.put("iId", jsonArray.getJSONObject(0).getString("iId"));
                newJsonObject.put("iSubCategory", jsonArray.getJSONObject(0).getString("iSubCategory"));
                newJsonObject.put("sAltLongDescription", jsonArray.getJSONObject(0).getString("sAltLongDescription"));
                newJsonObject.put("sAltName", jsonArray.getJSONObject(0).getString("sAltName"));
                newJsonObject.put("sAltShortDescription", jsonArray.getJSONObject(0).getString("sAltShortDescription"));
                newJsonObject.put("sCode", jsonArray.getJSONObject(0).getString("sCode"));
                newJsonObject.put("sImagePath", jsonArray.getJSONObject(0).getString("sImagePath"));
                newJsonObject.put("sLongDescription", jsonArray.getJSONObject(0).getString("sLongDescription"));
                newJsonObject.put("sShortDescription", jsonArray.getJSONObject(0).getString("sShortDescription"));
                newJsonObject.put("sName", jsonArray.getJSONObject(0).getString("sName"));
                newJsonObject.put("Qty", "1");

                newJsonArray.put(newJsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            File file = new File(requireActivity().getExternalFilesDir(null), "wishlist.json");
            if (file.exists()) {
                String sJsonArray = Tools.ReadWishListFile(requireActivity());
                try {
                    JSONArray fileJsonArray = new JSONArray(sJsonArray);
                    if (!CheckProductInsideWishlist(jsonArray.getJSONObject(0).getString("iId"))) {
                        fileJsonArray.put(newJsonArray.getJSONObject(0));
                        if (Tools.AddProductToWishList(fileJsonArray, requireActivity())) {
                            ShowSnackBar(getString(R.string.added_to_wishlist), getString(R.string.color_success));
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        RemoveFromWishList(jsonArray.getJSONObject(0).getString("iId"), wish_img);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }


            } else {
                if (Tools.AddProductToWishList(newJsonArray, requireActivity())) ;
                {
                    ShowSnackBar(getString(R.string.added_to_wishlist), getString(R.string.color_success));
                    return true;
                }

            }
        }
        return false;
    }

    public boolean RemoveFromWishList(String iId, ImageView wish_img) {
        String sJsonArray = Tools.ReadWishListFile(requireActivity());
        int indexToRemove = -1;
        try {
            JSONArray fileJsonArray = new JSONArray(sJsonArray);
            for (int i = 0; i < fileJsonArray.length(); i++) {
                if (fileJsonArray.getJSONObject(i).getString("iId").equals(iId)) {
                    indexToRemove = i;
                }

                if (fileJsonArray.length() == i + 1) {
                    if (indexToRemove != -1) {
                        fileJsonArray.remove(indexToRemove);
                        if (Tools.AddProductToWishList(fileJsonArray, requireActivity())) {
                            wish_img.setImageResource(R.drawable.ic_love_big);
                            ShowSnackBar(getString(R.string.remove_from_wishlist), getString(R.string.warning_color));
                            return true;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    ;

    public void ShowSnackBar(String message, String color) {
        Snackbar.make(cl_main, message, 500).setBackgroundTint(Color.parseColor(color)).show();
    }


    public boolean CheckProductInsideCart(JSONArray jsonArray) {
        File file = new File(requireActivity().getExternalFilesDir(null), "cart.json");
        if (file.exists()) {

            try {
                String sJsonArray = Tools.readFile(requireActivity(), "cart.json");
                JSONArray fileJsonArray = new JSONArray(sJsonArray);


                if (fileJsonArray.length() > 0) {

                    try {


                        for (int i = 0; i < fileJsonArray.length(); i++) {

                            if (jsonArray.getJSONObject(0).getString("iId").equals(fileJsonArray.getJSONObject(i).getString("iId"))) {
                                Log.d("data", jsonArray.getJSONObject(0).getString("iId") + ":" + fileJsonArray.getJSONObject(i).getString("iId"));

                                ShowSnackBar(getString(R.string.product_is_already_in_your_cart), getString(R.string.warning_color));

                                return true;
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }
        return false;
    }

    public void AddToFile(JSONArray jsonArray) {

        File file = new File(requireActivity().getExternalFilesDir(null), "cart.json");
        if (file.exists()) {

            try {
                String sJsonArray = Tools.readFile(requireActivity(), "cart.json");
                JSONArray fileJsonArray = new JSONArray(sJsonArray);


                if (fileJsonArray.length() > 0) {

                    if (!CheckProductInsideCart(jsonArray)) {
                        fileJsonArray.put(jsonArray.getJSONObject(0));

                        if (Tools.writeToFile(fileJsonArray.toString(), requireActivity(), "cart.json")) {
                            ShowSnackBar(getString(R.string.added_to_cart), getString(R.string.color_success));

                        }
                    }


                } else {

                    if (Tools.writeToFile(jsonArray.toString(), requireActivity(), "cart.json")) {

                        ShowSnackBar(getString(R.string.added_to_cart), getString(R.string.color_success));
                    } else {
                        ShowSnackBar(getString(R.string.failed_to_add_product_into_cart), getString(R.string.warning_color));
                    }
                }
            } catch (FileNotFoundException | JSONException e) {
                e.printStackTrace();
            }

        } else {

            if (Tools.writeToFile(jsonArray.toString(), requireActivity(), "cart.json")) {

                ShowSnackBar(getString(R.string.added_to_cart), getString(R.string.color_success));
            } else {
                ShowSnackBar(getString(R.string.failed_to_add_product_into_cart), getString(R.string.warning_color));
            }

        }


    }

    public void AddToCart() {
        JSONArray newJsonArray = new JSONArray();
        JSONObject newJsonObject = new JSONObject();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject JsonObject = null;
            try {
                JsonObject = jsonArray.getJSONObject(i);

                try {
                    newJsonObject.put("iId", JsonObject.getString("iId"));
                    newJsonObject.put("sName", JsonObject.getString("sName"));
                    newJsonObject.put("sCode", JsonObject.getString("sCode"));
                    newJsonObject.put("sAltName", JsonObject.getString("sAltName"));
                    newJsonObject.put("sImagePath", JsonObject.getString("sImagePath"));
                    newJsonObject.put("iSubCategory", JsonObject.getString("iSubCategory"));
                    newJsonObject.put("fPrice", JsonObject.getString("fPrice"));
                    newJsonObject.put("sShortDescription", JsonObject.getString("sShortDescription"));
                    newJsonObject.put("sLongDescription", JsonObject.getString("sLongDescription"));
                    newJsonObject.put("sAltShortDescription", JsonObject.getString("sAltShortDescription"));
                    newJsonObject.put("sAltLongDescription", JsonObject.getString("sAltLongDescription"));
                    newJsonObject.put("Qty", "1");

                    newJsonArray.put(newJsonObject);

                    if (jsonArray.length() == i + 1) {
                        AddToFile(newJsonArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void InflateLayoutDescription(String inflateType) {

        TextView description = descriptionview.findViewById(R.id.description);
        if (inflateType.equals("det")) {

            if (Objects.equals(preferences.getString("language", ""), "english")) {
                description.setText(sLongDescription);
            } else {
                description.setText(sAltLongDescription);
            }
        } else {
            description.setText("");
        }


    }

    private void LoadLoadProduct(String pId) {
        AndroidNetworking.get(URLs.getProductDetails)
                .addQueryParameter("iProduct", pId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            jsonArray = new JSONArray(response.getString("Productdetails"));

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

                                if (jsonArray.length() == i + 1) {
                                    /*if (!sImagePath.isEmpty()) {
                                        Picasso.get().load(sImagePath).placeholder(R.drawable.place_holder).error(R.drawable.place_holder).into(img_image);
                                    } else {
                                        Picasso.get().load(R.drawable.place_holder).placeholder(R.drawable.place_holder).into(img_image);
                                    }*/
                                    LoadImages(iId);

                                    tv_price.setText(fPrice + getString(R.string.currency));
                                    if (Objects.equals(preferences.getString("language", ""), "english")) {
                                        tv_name.setText(sName);
                                    } else {
                                        tv_name.setText(sAltName);
                                    }

                                    if (CheckProductInsideWishlist(iId)) {
                                        wishlist.setImageResource(R.drawable.ic_love_red_big);
                                    } else {
                                        wishlist.setImageResource(R.drawable.ic_love_big);
                                    }

                                    Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                                    if (tabLayout.getSelectedTabPosition() == 0) {
                                        InflateLayoutDescription("det");
                                    } else {
                                        InflateLayoutDescription("rev");
                                    }
                                }


                            }

                        } catch (JSONException e) {
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getProductDetails\n"+e.getMessage()));
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorDetail());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("getProductDetails\n"+anError.getErrorDetail()));
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        if (bundle != null) {
            String iId = bundle.getString("iId");
            LoadLoadProduct(iId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_product_fragment, container, false);
        descriptionview = LayoutInflater.from(getActivity()).inflate(R.layout.description_layout, null, false);
        preferences = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE);
        tv_name = view.findViewById(R.id.name);
       // img_image = view.findViewById(R.id.image);
        tv_price = view.findViewById(R.id.price);
        Button exit = view.findViewById(R.id.exit);
        tabLayout = view.findViewById(R.id.tab);
        Button add_to_cart = view.findViewById(R.id.add_to_cart);
        cl_main = view.findViewById(R.id.cl_main);
        wishlist = view.findViewById(R.id.wishlist);
        ImageView img_backward = view.findViewById(R.id.img_backward);
        ImageView img_forward = view.findViewById(R.id.img_forward);
        list = new ArrayList<>();
        imagesAdapter = new ProductImagesAdapter(getActivity(),list);

        viewPager2 = view.findViewById(R.id.viewPager2);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        if(Objects.equals(preferences.getString("language", ""), "english")) {
            viewPager2.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }else {
            viewPager2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);

        img_backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.size()!=0){
                    int CurrentPos = viewPager2.getCurrentItem();
                    if(CurrentPos!=0){
                        viewPager2.setCurrentItem(CurrentPos-1,true);
                    }
                }
            }
        });

        img_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.size()!=0){
                    int CurrentPos = viewPager2.getCurrentItem();
                    if(CurrentPos!=list.size()){
                        viewPager2.setCurrentItem(CurrentPos+1,true);
                    }


                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (Objects.requireNonNull(tab.getText()).toString().equals(getString(R.string.details))) {
                    InflateLayoutDescription("det");
                } else if (Objects.requireNonNull(tab.getText()).toString().equals(getString(R.string.review))) {
                    InflateLayoutDescription("rev");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        RelativeLayout rl_description = view.findViewById(R.id.description);
        rl_description.addView(descriptionview);



        bundle = getArguments();


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jsonArray != null) {
                    AddToCart();
                }
            }
        });

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AddOrRemoveFromWishList(wishlist)) {
                    wishlist.setImageResource(R.drawable.ic_love_red_big);
                }

            }
        });

        return view;
    }
}

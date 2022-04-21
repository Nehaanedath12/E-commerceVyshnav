package com.sangsolutions.e_commerce.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.sangsolutions.e_commerce.Adapter.WishListAdapter.WishList;
import com.sangsolutions.e_commerce.Adapter.WishListAdapter.WishListAdapter;
import com.sangsolutions.e_commerce.Home;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Tools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WishListFragment extends Fragment {
  private WishListAdapter wishListAdapter;
  private List<WishList> list;
  private RecyclerView rv_wish_list;
  private FrameLayout empty_frame;
  private SharedPreferences preferences;
    private CoordinatorLayout cl_main;


    public void ItemRemoveAlert(final WishList wishList) {
        View dialogView = getLayoutInflater().inflate(R.layout.item_remove_bottom_sheet, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
        dialog.setContentView(dialogView);
        dialog.show();

        Button btn_no, btn_remove;
        TextView tv_name, tv_description;
        ImageView image;

        btn_no = dialogView.findViewById(R.id.no);
        btn_remove = dialogView.findViewById(R.id.remove);
        tv_name = dialogView.findViewById(R.id.name);
        tv_description = dialogView.findViewById(R.id.description);
        image = dialogView.findViewById(R.id.image);

        if (Objects.equals(preferences.getString("language", ""), "english")) {
            tv_name.setText(wishList.getsName());
            tv_description.setText(wishList.getsShortDescription());
        } else {
            tv_name.setText(wishList.getsAltName());
            tv_description.setText(wishList.getsAltShortDescription());
        }
        if (!wishList.getsImagePath().isEmpty()) {
            Picasso.get().load(wishList.getsImagePath()).placeholder(R.drawable.place_holder).error(R.drawable.place_holder).into(image);
        } else {
            Picasso.get().load(R.drawable.place_holder).into(image);
        }


        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RemoveFromWishList(wishList.getiId())) {
                    ShowSnackBar(getString(R.string.item_removed_from_wishlist), getString(R.string.warning_color));
                }
                dialog.dismiss();
            }
        });


    }

    public boolean CheckCart(String iId) {

        String sJsonArray = null;
        try {
            sJsonArray = Tools.readFile(requireActivity(), "cart.json");
            JSONArray fileJsonArray = new JSONArray(sJsonArray);

            for (int i = 0; i < fileJsonArray.length(); i++) {
                Log.d("data", fileJsonArray.getJSONObject(i).getString("iId") + ":" + iId);
                if (iId.equals(fileJsonArray.getJSONObject(i).getString("iId"))) {
                    return true;
                }
            }


        } catch (FileNotFoundException | JSONException e) {
            e.printStackTrace();
        }


        return false;
    }

    public boolean AddToCart(WishList wishList) {
        File file = new File(requireActivity().getExternalFilesDir(null), "cart.json");

        JSONObject newJsonObject = new JSONObject();
        JSONArray newJsonArray = new JSONArray();
        try {
            newJsonObject.put("iId", wishList.getiId());
            newJsonObject.put("sName", wishList.getsName());
            newJsonObject.put("sCode", wishList.getsAltName());
            newJsonObject.put("sAltName", wishList.getsAltName());
            newJsonObject.put("sImagePath", wishList.getsImagePath());
            newJsonObject.put("iSubCategory", wishList.getiSubCategory());
            newJsonObject.put("fPrice", wishList.getfPrice());
            newJsonObject.put("sShortDescription", wishList.getsShortDescription());
            newJsonObject.put("sLongDescription", wishList.getsAltLongDescription());
            newJsonObject.put("sAltShortDescription", wishList.getsAltShortDescription());
            newJsonObject.put("sAltLongDescription", wishList.getsAltLongDescription());
            newJsonObject.put("Qty", "1");

            newJsonArray.put(newJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (file.exists()) {
            String sJsonArray = null;
            try {
                sJsonArray = Tools.readFile(requireActivity(), "cart.json");
                JSONArray fileJsonArray = new JSONArray(sJsonArray);

                if (fileJsonArray.length() > 0) {


                    if (!CheckCart(wishList.getiId())) {
                        fileJsonArray.put(newJsonObject);

                        if (Tools.writeToFile(fileJsonArray.toString(), requireActivity(), "cart.json")) {

                            ShowSnackBar(getString(R.string.added_to_cart), getString(R.string.color_success));
                            return true;
                        } else {
                            ShowSnackBar(getString(R.string.failed_to_add_product_into_cart), getString(R.string.warning_color));
                            return false;
                        }
                    } else {
                        ShowSnackBar(getString(R.string.product_already_in_your_cart), getString(R.string.warning_color));
                        return false;
                    }

                } else {
                    if (Tools.writeToFile(newJsonArray.toString(), requireActivity(), "cart.json")) {

                        ShowSnackBar(getString(R.string.added_to_cart), getString(R.string.color_success));
                        return true;
                    } else {
                        ShowSnackBar(getString(R.string.failed_to_add_product_into_cart), getString(R.string.warning_color));
                        return false;
                    }
                }


            } catch (FileNotFoundException | JSONException e) {
                e.printStackTrace();
                return false;
            }


        } else {
            if (Tools.writeToFile(newJsonArray.toString(), requireActivity(), "cart.json")) {

                ShowSnackBar(getString(R.string.added_to_cart), getString(R.string.color_success));
                return true;
            } else {
                ShowSnackBar(getString(R.string.failed_to_add_product_into_cart), getString(R.string.warning_color));
                return false;
            }
        }

    }

    public void LoadWishList() {
        File file = new File(requireActivity().getExternalFilesDir(null), "wishlist.json");
        if (file.exists()) {
            try {
                String sJsonArray = Tools.ReadWishListFile(requireActivity());
                JSONArray jsonArray = new JSONArray(sJsonArray);

                if (jsonArray.length() == 0) {
                    empty_frame.setVisibility(View.VISIBLE);
                    wishListAdapter.notifyDataSetChanged();
                }

                try {
                    list.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String fPrice = jsonObject.getString("fPrice");
                        String iId = jsonObject.getString("iId");
                        String iSubCategory = jsonObject.getString("iSubCategory");
                        String sAltLongDescription = jsonObject.getString("sAltLongDescription");
                        String sAltName = jsonObject.getString("sAltName");
                        String sAltShortDescription = jsonObject.getString("sAltShortDescription");
                        String sCode = jsonObject.getString("sCode");
                        String sImagePath = jsonObject.getString("sImagePath");
                        String sLongDescription = jsonObject.getString("sLongDescription");
                        String sName = jsonObject.getString("sName");
                        String sShortDescription = jsonObject.getString("sShortDescription");
                        String qty = jsonObject.getString("Qty");

                        if (!iId.equals("0")) {
                            list.add(new WishList(fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription, qty));
                        }


                        if (jsonArray.length() == i + 1) {
                            empty_frame.setVisibility(View.INVISIBLE);
                            wishListAdapter.notifyDataSetChanged();
                            rv_wish_list.setItemViewCacheSize(list.size());
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    empty_frame.setVisibility(View.VISIBLE);
                }


            } catch (Exception e) {
                e.printStackTrace();
                empty_frame.setVisibility(View.VISIBLE);
            }

        } else {
            empty_frame.setVisibility(View.VISIBLE);
        }
    }

    public boolean RemoveFromWishList(String iId) {
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
                            list.remove(indexToRemove);
                            wishListAdapter.notifyDataSetChanged();
                            LoadWishList();
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

    @Override
    public void onResume() {
        super.onResume();
        LoadWishList();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.wish_list_fragment, container, false);
        preferences = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE);
        list = new ArrayList<>();
        cl_main = view.findViewById(R.id.cl_main);
        empty_frame = view.findViewById(R.id.empty_frame);

        Button btn_home = view.findViewById(R.id.add_wish);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Home) requireActivity()).getViewPager().setCurrentItem(0);
            }
        });


        wishListAdapter = new WishListAdapter(getActivity(), list);
        rv_wish_list = view.findViewById(R.id.wishlist);
        rv_wish_list = view.findViewById(R.id.rv_wish_list);
        rv_wish_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_wish_list.setAdapter(wishListAdapter);


        LoadWishList();

        wishListAdapter.setOnClickListener(new WishListAdapter.OnClickListener() {
            @Override
            public void onProductItemClick(View view, WishList wishList, int pos) {
                Fragment fragment = new ViewProductFragment();
                Bundle bundle = new Bundle();
                bundle.putString("iId", wishList.getiId());
                fragment.setArguments(bundle);
                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("view").commit();
            }

            @Override
            public void onCartButtonClick(View view, WishList wishList, int pos) {
                if (AddToCart(wishList)) {
                    RemoveFromWishList(wishList.getiId());
                }
            }

            @Override
            public void onRemoveButtonClick(View view, WishList wishList, int pos) {
                ItemRemoveAlert(wishList);
            }
        });

        return view;
    }
}

package com.sangsolutions.e_commerce.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sangsolutions.e_commerce.Adapter.CartAdapter.Cart;
import com.sangsolutions.e_commerce.Adapter.CartAdapter.CartAdapter;
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

public class CartFragment extends Fragment {
    private CartAdapter cartAdapter;
    private List<Cart> list;
    private RecyclerView rv_cart;
    private SharedPreferences preferences;
    private FrameLayout empty_frame;
    private String fPrice;
    private String iId;
    private String iSubCategory;
    private String sAltLongDescription;
    private String sAltName;
    private String sAltShortDescription;
    private String sCode;
    private String sImagePath;
    private String sLongDescription;
    private String sName;
    private String sShortDescription;
    private CoordinatorLayout cl_main;
    private FloatingActionButton fab_check_out;
    private Handler handler;

    @Override
    public void onDetach() {
        super.onDetach();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }


    public void GoHome() {
        ((Home) requireActivity()).getViewPager().setCurrentItem(0);
    }

    public void CheckCart() {
        LoadCart();
    }

    public void ItemRemoveAlert(final int pos, Cart cart) {
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
            tv_name.setText(cart.getsName());
            tv_description.setText(cart.getsShortDescription());
        } else {
            tv_name.setText(cart.getsAltName());
            tv_description.setText(cart.getsAltShortDescription());
        }
        if (!cart.getsImagePath().isEmpty()) {
            Picasso.get().load(cart.getsImagePath()).placeholder(R.drawable.place_holder).error(R.drawable.place_holder).into(image);
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
                if (RemoveProduct(pos)) {
                    cartAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });


    }

    public boolean CheckProductInsideWishlist(String iId) {
        String sJsonArray = Tools.ReadWishListFile(requireActivity());
        try {
            JSONArray fileJsonArray = new JSONArray(sJsonArray);
            Log.d("file json", fileJsonArray.toString());
            for (int i = 0; i < fileJsonArray.length(); i++) {
                JSONObject jsonObject = fileJsonArray.getJSONObject(i);
                if (jsonObject.getString("iId").equals(iId)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }


        return false;
    }

    public boolean AddtoWishList(Cart cart, int pos) {

        JSONArray newJsonArray = new JSONArray();
        JSONObject newJsonObject = new JSONObject();
        try {
            newJsonObject.put("fPrice", cart.getfPrice());
            newJsonObject.put("iId", cart.getiId());
            newJsonObject.put("iSubCategory", cart.getiSubCategory());
            newJsonObject.put("sAltLongDescription", cart.getsAltLongDescription());
            newJsonObject.put("sAltName", cart.getsAltName());
            newJsonObject.put("sAltShortDescription", cart.getsAltShortDescription());
            newJsonObject.put("sCode", cart.getsCode());
            newJsonObject.put("sImagePath", cart.getsImagePath());
            newJsonObject.put("sLongDescription", cart.getsLongDescription());
            newJsonObject.put("sShortDescription", cart.getsShortDescription());
            newJsonObject.put("sName", cart.getsName());
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
                if (!CheckProductInsideWishlist(cart.getiId())) {
                    fileJsonArray.put(newJsonArray.getJSONObject(0));
                    if (Tools.AddProductToWishList(fileJsonArray, requireActivity())) {

                        if (RemoveProduct(pos)) {
                            ShowSnackBar(getString(R.string.added_to_wishlist), getString(R.string.color_success));
                        }

                        return true;
                    } else {
                        return false;
                    }
                } else {
                    ShowSnackBar(getString(R.string.already_exist_in_wishlist), getString(R.string.warning_color));
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

        return false;
    }

    public void LoadCart() {
        list.clear();
        fab_check_out.setVisibility(View.INVISIBLE);
        empty_frame.setVisibility(View.VISIBLE);
        File file = new File(requireActivity().getExternalFilesDir(null), "cart.json");
        if (file.exists()) {
            try {
                String sJsonArray = Tools.readFile(requireActivity(), "cart.json");
                if (!sJsonArray.isEmpty()) {
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(sJsonArray);
                    try {
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
                            String qty = jsonObject.getString("Qty");

                            if (!iId.equals("0")) {
                                list.add(new Cart(fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription, qty));
                            }


                            if (jsonArray.length() == i + 1) {
                                empty_frame.setVisibility(View.INVISIBLE);
                                fab_check_out.setVisibility(View.VISIBLE);
                                cartAdapter.notifyDataSetChanged();
                                rv_cart.setItemViewCacheSize(list.size());
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        empty_frame.setVisibility(View.VISIBLE);
                        cartAdapter.notifyDataSetChanged();
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
                empty_frame.setVisibility(View.VISIBLE);
                cartAdapter.notifyDataSetChanged();
            }

        } else {
            cartAdapter.notifyDataSetChanged();
            empty_frame.setVisibility(View.VISIBLE);
        }
    }


    public void ShowSnackBar(String message, String color) {

        Snackbar.make(cl_main, message, 500).setBackgroundTint(Color.parseColor(color)).show();

    }

    public boolean UpdateProduct(List<Cart> list) {
        boolean status = false;
        File file = new File(requireActivity().getExternalFilesDir(null), "cart.json");
        if (file.exists()) {
            try {

                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < list.size(); i++) {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("fPrice", list.get(i).getfPrice());
                    jsonObject.put("iId", list.get(i).getiId());
                    jsonObject.put("iSubCategory", list.get(i).getiSubCategory());
                    jsonObject.put("sAltLongDescription", list.get(i).getsAltLongDescription());
                    jsonObject.put("sAltName", list.get(i).getsAltName());
                    jsonObject.put("sAltShortDescription", list.get(i).getsAltShortDescription());
                    jsonObject.put("sCode", list.get(i).getsCode());
                    jsonObject.put("sImagePath", list.get(i).getsImagePath());
                    jsonObject.put("sLongDescription", list.get(i).getsLongDescription());
                    jsonObject.put("sShortDescription", list.get(i).getsShortDescription());
                    jsonObject.put("sName", list.get(i).getsName());
                    jsonObject.put("Qty", list.get(i).getQty());

                    jsonArray.put(jsonObject);

                    if (list.size() == i + 1) {
                        status = Tools.writeToFile(jsonArray.toString(), requireActivity(), "cart.json");

                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return status;
    }

    public boolean RemoveProduct(int pos) {
        boolean status = false;
        File file = new File(requireActivity().getExternalFilesDir(null), "cart.json");
        if (file.exists()) {
            try {
                String sJsonArray = Tools.readFile(requireActivity(), "cart.json");
                if (!sJsonArray.isEmpty()) {
                    JSONArray jsonArray = null;
                    jsonArray = new JSONArray(sJsonArray);
                    jsonArray.remove(pos);

                    status = Tools.writeToFile(jsonArray.toString(), requireActivity(), "cart.json");
                    if (status) {
                        list.remove(pos);
                        ShowSnackBar(getString(R.string.item_removed_from_cart), getString(R.string.warning_color));
                        LoadCart();
                    }
                }
            } catch (JSONException | FileNotFoundException e) {
                e.printStackTrace();
            }


        }
        return status;
    }

    public void UpdateCount(int count, int qty, float price, TextView tv, EditText et_qty, Cart cart, int pos) {
        int sum = 0;
        if (!et_qty.getText().toString().equals("0")) {

            try {
                if (qty == 1 && count == -1) {
                    Log.d("count", "count error");
                } else {
                    sum = count + qty;
                    et_qty.setText(String.valueOf(sum));

                    Log.d("data", String.valueOf(sum));
                    fPrice = cart.getfPrice();
                    iId = cart.getiId();
                    iSubCategory = cart.getiSubCategory();
                    sAltLongDescription = cart.getsAltLongDescription();
                    sAltName = cart.getsAltName();
                    sAltShortDescription = cart.getsAltShortDescription();
                    sCode = cart.getsCode();
                    sImagePath = cart.getsImagePath();
                    sLongDescription = cart.getsLongDescription();
                    sName = cart.getsName();
                    sShortDescription = cart.getsShortDescription();
                    list.set(pos, new Cart(fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription, String.valueOf(sum)));
                    if (UpdateProduct(list)) {
                        Log.d("updated", String.valueOf(sum));
                    }
                    tv.setText((sum * price) + getString(R.string.currency));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


        }


    }

    @Override
    public void onResume() {
        super.onResume();
        LoadCart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.cart_fragment, container, false);
        preferences = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE);
        list = new ArrayList<>();
        cl_main = view.findViewById(R.id.cl_main);
        empty_frame = view.findViewById(R.id.empty_frame);
        fab_check_out = view.findViewById(R.id.check_out);

        fab_check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new File(requireActivity().getExternalFilesDir(null), "cart.json").exists()) {
                    try {
                        JSONArray jsonArray = new JSONArray(Tools.readFile(requireActivity(), "cart.json"));
                        if (jsonArray.length() > 0) {
                            Fragment fragment = new CheckOutFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("cartJson", jsonArray.toString());
                            fragment.setArguments(bundle);
                            FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                            tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                            tx.replace(R.id.fragment, fragment).addToBackStack("CheckOut").commit();
                        }
                    } catch (JSONException | FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }

        });


        Button btn_home = view.findViewById(R.id.add_cart);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoHome();
            }
        });


        cartAdapter = new CartAdapter(getActivity(), list);
        rv_cart = view.findViewById(R.id.rv_cart);
        rv_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_cart.setAdapter(cartAdapter);


        cartAdapter.setOnClickListener(new CartAdapter.OnClickListener() {
            @Override
            public void onProductItemClick(View view, Cart cart, int pos) {
                Fragment fragment = new ViewProductFragment();
                Bundle bundle = new Bundle();
                bundle.putString("iId", cart.getiId());
                fragment.setArguments(bundle);
                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("view").commit();
            }

            @Override
            public void onWishListItemClick(View view, Cart cart, int pos) {
                if (AddtoWishList(cart, pos)) {
                    cartAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onRemoveProductItemClick(View view, Cart cart, int pos) {

                ItemRemoveAlert(pos, cart);

            }

            @Override
            public void onAddItemClick(View view, Cart cart, EditText et_qty, int pos, TextView tv) {
                UpdateCount(1, Integer.parseInt(et_qty.getText().toString()), Float.parseFloat(cart.getfPrice()), tv, et_qty, cart, pos);
            }

            @Override
            public void onRemoveItemClick(View view, Cart cart, EditText et_qty, int pos, TextView tv) {
                UpdateCount(-1, Integer.parseInt(et_qty.getText().toString()), Float.parseFloat(cart.getfPrice()), tv, et_qty, cart, pos);
            }
        });

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                CheckCart();
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);

        return view;
    }
}


package com.sangsolutions.e_commerce.Adapter.SubCategoryAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.CategoryWiseProductAdapter.CategoryWiseProduct;
import com.sangsolutions.e_commerce.Adapter.CategoryWiseProductAdapter.CategoryWiseProductAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    private Context context;
    private List<SubCategory> list;
    private SharedPreferences preferences;
    private OnClickListener onClickListener;

    public SubCategoryAdapter(Context context, List<SubCategory> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sub_category_item, parent, false);
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final SubCategory subCategory = list.get(position);
        holder.text_view_more.setText(context.getString(R.string.view_more));
        if (Objects.equals(preferences.getString("language", ""), "english")) {
            holder.text_title.setText(list.get(position).getsName());
        } else {
            holder.text_title.setText(list.get(position).getsAltName());
        }

        holder.text_view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        final List<CategoryWiseProduct> list2 = new ArrayList<>();
        final CategoryWiseProductAdapter categoryWiseProductAdapter = new CategoryWiseProductAdapter(context, list2, "big");
        holder.rv_sub_category.setLayoutManager(new GridLayoutManager(context, Tools.calculateNoOfColumns(context, 180)));
        holder.rv_sub_category.setHasFixedSize(true);

        AndroidNetworking.get(URLs.getProductSubCategoryWise)
                .addQueryParameter("iSubCategory", list.get(position).getiId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription;
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("productDetails"));

                            list2.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                iId = jsonObject.getString("iId");
                                fPrice = jsonObject.getString("fPrice");
                                iSubCategory = jsonObject.getString("iSubCategory");
                                sAltLongDescription = jsonObject.getString("sAltLongDescription");
                                sAltName = jsonObject.getString("sAltName");
                                sAltShortDescription = jsonObject.getString("sAltShortDescription");
                                sCode = jsonObject.getString("sCode");
                                sImagePath = jsonObject.getString("sImagePath");
                                sLongDescription = jsonObject.getString("sLongDescription");
                                sName = jsonObject.getString("sName");
                                sShortDescription = jsonObject.getString("sShortDescription");


                                list2.add(new CategoryWiseProduct(fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription));


                                if (jsonArray.length() == i + 1) {
                                    holder.rv_sub_category.setAdapter(categoryWiseProductAdapter);
                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getProductSubCategoryWise list\n"+e.getMessage()));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorDetail());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("getProductSubCategoryWise list\n"+anError.getErrorDetail()));
                    }
                });


        categoryWiseProductAdapter.setOnClickListener(new CategoryWiseProductAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, CategoryWiseProduct product, int pos) {
                onClickListener.onProductItemClick(view, product, position);
            }

            @Override
            public void onWishlistAdded(View view) {

            }
        });

        holder.text_view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onSubCategoryItemClick(view, subCategory, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface OnClickListener {
        void onProductItemClick(View view, CategoryWiseProduct product, int pos);

        void onSubCategoryItemClick(View view, SubCategory category, int pos);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_sub_category;
        TextView text_title;
        TextView text_view_more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_sub_category = itemView.findViewById(R.id.rv_sub_category);
            text_title = itemView.findViewById(R.id.tv_new_arrival);
            text_view_more = itemView.findViewById(R.id.tv_view_more);
        }
    }
}




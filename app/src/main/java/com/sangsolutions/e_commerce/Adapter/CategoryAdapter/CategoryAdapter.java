package com.sangsolutions.e_commerce.Adapter.CategoryAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.CategoryWiseProductAdapter.CategoryWiseProduct;
import com.sangsolutions.e_commerce.Adapter.CategoryWiseProductAdapter.CategoryWiseProductAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> list;
    private SharedPreferences preferences;
    private OnClickListener onClickListener;


    public CategoryAdapter(Context context, List<Category> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.ViewHolder holder, final int position) {
        final Category category = list.get(position);
        holder.text_view_more.setText(R.string.view_more);
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
        final CategoryWiseProductAdapter categoryWiseProductAdapter = new CategoryWiseProductAdapter(context, list2, "small");
        holder.rv_category.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        holder.rv_category.setAdapter(categoryWiseProductAdapter);
        AndroidNetworking.get(URLs.getProducts)
                .addQueryParameter("iCategory", list.get(position).getiId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        list2.clear();
                        String fPrice, iId, iSubCategory, sAltLongDescription, sAltName, sAltShortDescription, sCode, sImagePath, sLongDescription, sName, sShortDescription;
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("productDetails"));
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
                                   categoryWiseProductAdapter.notifyDataSetChanged();
                                }

                            }

                        } catch (JSONException e) {
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getProducts\n"+e.getMessage()));
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("getProducts\n"+anError.getErrorDetail()));
                        Log.d("error", anError.getErrorDetail());
                    }
                });


        categoryWiseProductAdapter.setOnClickListener(new CategoryWiseProductAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, CategoryWiseProduct product, int pos) {
                onClickListener.onProductItemClick(view, product, position);
            }

            @Override
            public void onWishlistAdded(View view) {
                onClickListener.onWishlistAdded(view);
            }
        });

        holder.text_view_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onCategoryItemClick(view, category, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onProductItemClick(View view, CategoryWiseProduct product, int pos);

        void onCategoryItemClick(View view, Category category, int pos);

        void onWishlistAdded(View view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_category;
        TextView text_title;
        TextView text_view_more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_category = itemView.findViewById(R.id.rv_category);
            text_title = itemView.findViewById(R.id.tv_new_arrival);
            text_view_more = itemView.findViewById(R.id.tv_view_more);
        }
    }
}

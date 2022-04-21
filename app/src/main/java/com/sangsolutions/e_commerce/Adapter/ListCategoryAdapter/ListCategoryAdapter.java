package com.sangsolutions.e_commerce.Adapter.ListCategoryAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.ListSubCategoryAdapter.ListSubCategory;
import com.sangsolutions.e_commerce.Adapter.ListSubCategoryAdapter.ListSubCategoryAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ListCategoryAdapter extends RecyclerView.Adapter<ListCategoryAdapter.ViewHolder> {
    private Context context;
    private List<ListCategory> list;
    private SharedPreferences preferences;
    private OnClickListener onClickListener;
    private ListCategory expandListCategory;
     boolean expand = false,animate = false;
    RecyclerView mRecyclerView;


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public ListCategoryAdapter(List<ListCategory> listCategory, Context context) {
        this.context = context;
        this.list = listCategory;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }



    private void expandItem(ViewHolder holder,boolean expand,boolean animate){
        final Animation animation_expand = AnimationUtils.loadAnimation(context, R.anim.rv_expand);
        final Animation animation_contract = AnimationUtils.loadAnimation(context, R.anim.rv_contract);
if(animate) {
    if (!expand) {
        holder.rv_sub_category.startAnimation(animation_contract);
        holder.rv_sub_category.setVisibility(View.GONE);
        holder.img_expand.setImageResource(R.drawable.ic_expand_more);
    } else {
        holder.rv_sub_category.startAnimation(animation_expand);
        holder.rv_sub_category.setVisibility(VISIBLE);
        holder.img_expand.setImageResource(R.drawable.ic_expand_less);
    }
}

    }



    @NonNull
    @Override
    public ListCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_category_item, parent, false);
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListCategoryAdapter.ViewHolder holder, final int position) {

        final ListCategory category = list.get(position);

        expandItem(holder,category==expandListCategory,false);

        if (Objects.equals(preferences.getString("language", ""), "english")) {
            holder.text_title.setText(list.get(position).getsName());
        } else {
            holder.text_title.setText(list.get(position).getsAltName());
        }
        holder.img_expand.setImageResource(R.drawable.ic_expand_more);



        final List<ListSubCategory> list2 = new ArrayList<>();
        final ListSubCategoryAdapter listSubCategoryAdapter = new ListSubCategoryAdapter(context, list2);
        holder.rv_sub_category.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        holder.rv_sub_category.setHasFixedSize(true);
        holder.rv_sub_category.setAdapter(listSubCategoryAdapter);

        holder.rv_sub_category.setVisibility(VISIBLE);

        AndroidNetworking.get(URLs.getSubCategory)
                .addQueryParameter("iCategory", list.get(position).getiId())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        list2.clear();
                        String iId, sAltName, sCode, sName;
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("SubcategoryDetails"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                iId = jsonObject.getString("iId");
                                sAltName = jsonObject.getString("sAltName");
                                sCode = jsonObject.getString("sCode");
                                sName = jsonObject.getString("sName");


                                list2.add(new ListSubCategory(iId, sAltName, sCode, sName));


                                if (jsonArray.length() == i + 1) {
                                    listSubCategoryAdapter.notifyDataSetChanged();
                                    holder.rv_sub_category.setVisibility(View.GONE);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getSubCategory\n"+e.getMessage()));
                            holder.rv_sub_category.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorDetail());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("getSubCategory\n"+anError.getErrorDetail()));
                        holder.rv_sub_category.setVisibility(GONE);
                    }
                });


        listSubCategoryAdapter.setOnClickListener(new ListSubCategoryAdapter.OnClickListener() {
            @Override
            public void onSubCategoryItemClick(View view, ListSubCategory subCategory, int pos) {
                onClickListener.onSubCategoryItemClick(view, subCategory, pos);
            }
        });


        holder.rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(expandListCategory == null){
                expandItem(holder, expand =true,animate = true);
                expandListCategory = category;
            }else if(expandListCategory == category){
                expandItem(holder, expand = false ,animate =true);
                expandListCategory = null;
            }else {

                int expandedModelPosition = list.indexOf(expandListCategory);
                ViewHolder oldViewHolder = (ViewHolder) mRecyclerView.findViewHolderForAdapterPosition(expandedModelPosition);

                if(oldViewHolder!=null){
                    expandItem(oldViewHolder, expand = false,animate = true);
                }

                expandItem(holder, expand = true, animate = true);
                expandListCategory = category;
            }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onSubCategoryItemClick(View view, ListSubCategory subCategory, int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_sub_category;
        TextView text_title;
        ImageView img_expand;
        RelativeLayout rl_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_sub_category = itemView.findViewById(R.id.rv_category);
            text_title = itemView.findViewById(R.id.tv_new_arrival);
            img_expand = itemView.findViewById(R.id.img_expand);
            rl_main = itemView.findViewById(R.id.rl_main);
        }
    }
}

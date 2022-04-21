package com.sangsolutions.e_commerce.Adapter.ListSubCategoryAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.e_commerce.R;

import java.util.List;
import java.util.Objects;

public class ListSubCategoryAdapter extends RecyclerView.Adapter<ListSubCategoryAdapter.ViewHolder> {

    private Context context;
    private List<ListSubCategory> list;
    private SharedPreferences preferences;
    private OnClickListener onClickListener;

    public ListSubCategoryAdapter(Context context, List<ListSubCategory> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_sub_category_item, parent, false);
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSubCategoryAdapter.ViewHolder holder, final int position) {
        final ListSubCategory subCategory = list.get(position);

        if (Objects.equals(preferences.getString("language", ""), "english")) {
            holder.sub_category.setText(list.get(position).getsName());
        } else {
            holder.sub_category.setText(list.get(position).getsAltName());
        }

        holder.card_main.setOnClickListener(new View.OnClickListener() {
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
        void onSubCategoryItemClick(View view, ListSubCategory subCategory, int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sub_category;
        CardView card_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card_main = itemView.findViewById(R.id.card_main);
            sub_category = itemView.findViewById(R.id.sub_category);
        }
    }
}

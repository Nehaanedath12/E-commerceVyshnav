package com.sangsolutions.e_commerce.Adapter.ProductImagesAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.e_commerce.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductImagesAdapter extends RecyclerView.Adapter<ProductImagesAdapter.ViewHolder> {
 private Context context;
 private List<ProductImages> list;

    public ProductImagesAdapter(Context context, List<ProductImages> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_image_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!list.get(position).getsImagePath().isEmpty()) {
            Picasso.get().load(list.get(position).getsImagePath()).placeholder(R.drawable.place_holder).error(R.drawable.place_holder).into(holder.image);
        } else {
            Picasso.get().load(R.drawable.place_holder).placeholder(R.drawable.place_holder).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        image = itemView.findViewById(R.id.image);

        }
    }
}

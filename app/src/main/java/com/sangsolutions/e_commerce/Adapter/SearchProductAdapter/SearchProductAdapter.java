package com.sangsolutions.e_commerce.Adapter.SearchProductAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.e_commerce.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ViewHolder> {
    private Context context;
    private List<SearchProduct> list;
    private SharedPreferences preferences;
    private OnClickListener onClickListener;


    public SearchProductAdapter(Context context, List<SearchProduct> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_product_item, parent, false);
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final SearchProduct searchProduct = list.get(position);
        holder.tv_price.setText(list.get(position).getfPrice() + context.getString(R.string.currency));
        if (Objects.equals(preferences.getString("language", ""), "english")) {
            holder.tv_name.setText(list.get(position).getsName());
            holder.tv_description.setText(list.get(position).getsShortDescription());
        } else {
            holder.tv_name.setText(list.get(position).getsAltName());
            holder.tv_description.setText(list.get(position).getsAltShortDescription());
        }
        if (!list.get(position).getsImagePath().isEmpty()) {
            Picasso.get().load(list.get(position).getsImagePath()).placeholder(R.drawable.place_holder).error(R.drawable.place_holder).into(holder.img_image);
        } else {
            Picasso.get().load(R.drawable.place_holder).into(holder.img_image);
        }

        holder.card_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onProductItemClick(view, searchProduct, position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface OnClickListener {
        void onProductItemClick(View view, SearchProduct searchProduct, int pos);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_price, tv_description;
        ImageView img_image;
        CardView card_product;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.name);
            img_image = itemView.findViewById(R.id.image);
            card_product = itemView.findViewById(R.id.card);
            tv_price = itemView.findViewById(R.id.price);
            tv_description = itemView.findViewById(R.id.description);
        }
    }
}

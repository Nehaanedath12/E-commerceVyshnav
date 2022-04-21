package com.sangsolutions.e_commerce.Adapter.WishListAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.e_commerce.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {
    private Context context;
    private List<WishList> list;
    private SharedPreferences preferences;
    private OnClickListener onClickListener;

    public WishListAdapter(Context context, List<WishList> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wish_list_item, parent, false);
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.ViewHolder holder, final int position) {
        final WishList wishList = list.get(position);
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onProductItemClick(view, wishList, position);
            }
        });

        holder.btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onCartButtonClick(view, wishList, position);
            }
        });

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onRemoveButtonClick(view, wishList, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onProductItemClick(View view, WishList wishList, int pos);

        void onCartButtonClick(View view, WishList wishList, int pos);

        void onRemoveButtonClick(View view, WishList wishList, int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button btn_remove, btn_add_to_cart;
        ImageView img_image;
        TextView tv_name, tv_description, tv_price;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.name);
            tv_description = itemView.findViewById(R.id.description);
            tv_price = itemView.findViewById(R.id.price);
            cardView = itemView.findViewById(R.id.card_wish);
            img_image = itemView.findViewById(R.id.image);
            btn_remove = itemView.findViewById(R.id.remove);
            btn_add_to_cart = itemView.findViewById(R.id.cart);


        }
    }
}

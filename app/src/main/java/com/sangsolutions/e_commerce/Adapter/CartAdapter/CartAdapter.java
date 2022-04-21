package com.sangsolutions.e_commerce.Adapter.CartAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.e_commerce.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    List<Cart> list;
    SharedPreferences preferences;
    private OnClickListener onClickListener;


    public CartAdapter(Context context, List<Cart> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Cart cart = list.get(position);
        holder.et_qty.setText(list.get(position).getQty());
        holder.tv_price.setText(list.get(position).getfPrice() + context.getString(R.string.currency));
        try {
            holder.tv_price_after.setText((Integer.parseInt(list.get(position).getfPrice()) * Integer.parseInt(list.get(position).getQty())) + context.getString(R.string.currency));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
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
                onClickListener.onProductItemClick(view, cart, position);
            }
        });

        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onWishListItemClick(view, cart, position);
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onRemoveProductItemClick(view, cart, position);
            }
        });

        holder.img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onAddItemClick(view, cart, holder.et_qty, position, holder.tv_price_after);
            }
        });

        holder.img_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onRemoveItemClick(view, cart, holder.et_qty, position, holder.tv_price_after);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface OnClickListener {
        void onProductItemClick(View view, Cart cart, int pos);

        void onWishListItemClick(View view, Cart cart, int pos);

        void onRemoveProductItemClick(View view, Cart cart, int pos);

        void onAddItemClick(View view, Cart cart, EditText edit, int pos, TextView tv);

        void onRemoveItemClick(View view, Cart cart, EditText edit, int pos, TextView tv);


    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_price, tv_description, tv_price_after;
        ImageView img_image, img_add, img_remove;
        CardView card_product;
        Button wishlist, remove;
        EditText et_qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.name);
            img_image = itemView.findViewById(R.id.image);
            card_product = itemView.findViewById(R.id.card_cart);
            tv_price = itemView.findViewById(R.id.price);
            tv_description = itemView.findViewById(R.id.description);
            img_remove = itemView.findViewById(R.id.img_remove);
            img_add = itemView.findViewById(R.id.img_add);
            wishlist = itemView.findViewById(R.id.wishlist);
            remove = itemView.findViewById(R.id.remove);
            tv_price_after = itemView.findViewById(R.id.after_price);
            et_qty = itemView.findViewById(R.id.qty);
        }
    }
}

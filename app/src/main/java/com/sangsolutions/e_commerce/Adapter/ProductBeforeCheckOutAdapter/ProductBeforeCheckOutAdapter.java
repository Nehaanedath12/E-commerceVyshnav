package com.sangsolutions.e_commerce.Adapter.ProductBeforeCheckOutAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.e_commerce.Adapter.CartAdapter.Cart;
import com.sangsolutions.e_commerce.Adapter.CartAdapter.CartAdapter;
import com.sangsolutions.e_commerce.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class ProductBeforeCheckOutAdapter extends RecyclerView.Adapter<ProductBeforeCheckOutAdapter.ViewHolder> {

    private Context context;
    private List<ProductBeforeCheckOut> list;
    private SharedPreferences preferences;
    private CartAdapter.OnClickListener onClickListener;


    public ProductBeforeCheckOutAdapter(Context context, List<ProductBeforeCheckOut> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(CartAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_before_check_out_item, parent, false);
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tv_qty.setText(list.get(position).getQty() + context.getString(R.string.item));
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


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface OnClickListener {
        void onProductItemClick(View view, Cart cart, int pos);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_qty, tv_description, tv_price_after;
        ImageView img_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.name);
            img_image = itemView.findViewById(R.id.image);
            tv_qty = itemView.findViewById(R.id.qty);
            tv_description = itemView.findViewById(R.id.description);
            tv_price_after = itemView.findViewById(R.id.after_price);
        }
    }

}

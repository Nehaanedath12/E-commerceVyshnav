package com.sangsolutions.e_commerce.Adapter.OrderTrakingAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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

public class OrderTrackingAdapter extends RecyclerView.Adapter<OrderTrackingAdapter.ViewHolder> {
    private Context context;
    private List<OrderTracking> list;
    private OnClickListener onClickListener;
    private SharedPreferences preferences;
    public OrderTrackingAdapter(Context context, List<OrderTracking> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public OrderTrackingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.order_history_details_item,parent,false);
    preferences = context.getSharedPreferences("language",Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderTrackingAdapter.ViewHolder holder, final int position) {
        final OrderTracking orderTracking = list.get(position);
        if(Objects.equals(preferences.getString("language", ""), "english")) {
            holder.tv_product.setText(list.get(position).getProduct());
            holder.tv_description.setText(list.get(position).getsShortDescription());
        }else {
            holder.tv_product.setText(list.get(position).getsAltName());
            holder.tv_description.setText(list.get(position).getsAltShortDescription());
        }
        if (!list.get(position).getsImagePath().isEmpty()) {
            Picasso.get().load(list.get(position).getsImagePath()).error(R.drawable.place_holder).into(holder.img_image);

        } else {
            Picasso.get().load(R.drawable.place_holder).placeholder(R.drawable.place_holder).into(holder.img_image);
        }

        holder.tv_qty.setText(list.get(position).getfQty()+context.getString(R.string.items));
        holder.tv_rate.setText(list.get(position).getfRate()+context.getString(R.string.currency));
        
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onOrderListItemClick(view, orderTracking, position);
            }
        });
    }

    public interface OnClickListener {
        void onOrderListItemClick(View view, OrderTracking orderTracking, int pos);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tv_product,tv_description,tv_qty,tv_rate;
        ImageView img_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_product = itemView.findViewById(R.id.name);
            tv_description = itemView.findViewById(R.id.description);
            img_image  = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.card_main);
            tv_qty = itemView.findViewById(R.id.qty);
            tv_rate = itemView.findViewById(R.id.price);
        }
    }
}

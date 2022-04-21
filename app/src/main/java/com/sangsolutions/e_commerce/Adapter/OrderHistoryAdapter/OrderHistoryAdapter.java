package com.sangsolutions.e_commerce.Adapter.OrderHistoryAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.sangsolutions.e_commerce.R;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private Context context;
    private List<OrderHistory> list;
    private OnClickListener onClickListener;

    public OrderHistoryAdapter(Context context, List<OrderHistory> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_history_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final OrderHistory orderHistory = list.get(position);
        holder.tv_ref_no.setText("Ref no:"+list.get(position).getsRefNo());

        holder.tv_date.setText(list.get(position).getOrderDate());
        holder.tv_status.setText(list.get(position).getDeliveryStatus());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onOrderHistoryItemClick(view, orderHistory, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnClickListener {
        void onOrderHistoryItemClick(View view, OrderHistory orderHistory, int pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ref_no,tv_status,tv_date;
        CardView cardView;
         public ViewHolder(@NonNull View itemView) {
            super(itemView);
             tv_status = itemView.findViewById(R.id.status);
             tv_date = itemView.findViewById(R.id.date);
             cardView = itemView.findViewById(R.id.card_main);
             tv_ref_no = itemView.findViewById(R.id.ref_no);

         }
    }
}

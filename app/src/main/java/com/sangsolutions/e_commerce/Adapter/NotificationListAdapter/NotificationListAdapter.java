package com.sangsolutions.e_commerce.Adapter.NotificationListAdapter;

import android.app.Notification;
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

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    List<NotificationList> list;
    Context context;
    onItemClick onItemClick;

    public NotificationListAdapter(List<NotificationList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }

    public void setOnClickListener(onItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final NotificationList notificationList = list.get(position);
        holder.tv_ref_no.setText(list.get(position).getsRefNo());
        holder.tv_message.setText(list.get(position).getsMessage());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onItemClickListener(view,notificationList,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface onItemClick{
        void onItemClickListener(View view,NotificationList notificationList,int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
    CardView card;
    TextView tv_ref_no,tv_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            tv_ref_no = itemView.findViewById(R.id.ref_no);
            tv_message = itemView.findViewById(R.id.message);
        }
    }
}

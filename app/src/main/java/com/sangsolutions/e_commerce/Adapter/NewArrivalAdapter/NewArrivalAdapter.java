package com.sangsolutions.e_commerce.Adapter.NewArrivalAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Tools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class NewArrivalAdapter extends RecyclerView.Adapter<NewArrivalAdapter.ViewHolder> {
    private Context context;
    private List<NewArrival> list;
    private SharedPreferences preferences;
    private String type;
    private OnClickListener onClickListener;

    public NewArrivalAdapter(Context context, List<NewArrival> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    public boolean CheckProductInsideWishlist(String iId) {
        String sJsonArray = Tools.ReadWishListFile(context);
        try {
            JSONArray fileJsonArray = new JSONArray(sJsonArray);
            //Log.d("file json", fileJsonArray.toString());
            for (int i = 0; i < fileJsonArray.length(); i++) {
                JSONObject jsonObject = fileJsonArray.getJSONObject(i);
                if (jsonObject.getString("iId").equals(iId)) {
                    return true;
                }
            }
        } catch (JSONException e) {
         //   e.printStackTrace();
            return false;
        }


        return false;
    }

    public boolean AddOrRemoveFromWishList(NewArrival newArrival, ImageView wish_img) {

        JSONArray newJsonArray = new JSONArray();
        JSONObject newJsonObject = new JSONObject();

        try {
            newJsonObject.put("fPrice", newArrival.getfPrice());
            newJsonObject.put("iId", newArrival.getiId());
            newJsonObject.put("iSubCategory", newArrival.getiSubCategory());
            newJsonObject.put("sAltLongDescription", newArrival.getsAltLongDescription());
            newJsonObject.put("sAltName", newArrival.getsAltName());
            newJsonObject.put("sAltShortDescription", newArrival.getsAltShortDescription());
            newJsonObject.put("sCode", newArrival.getsCode());
            newJsonObject.put("sImagePath", newArrival.getsImagePath());
            newJsonObject.put("sLongDescription", newArrival.getsLongDescription());
            newJsonObject.put("sShortDescription", newArrival.getsShortDescription());
            newJsonObject.put("sName", newArrival.getsName());
            newJsonObject.put("Qty", "1");

            newJsonArray.put(newJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        File file = new File(context.getExternalFilesDir(null), "wishlist.json");
        if (file.exists()) {
            String sJsonArray = Tools.ReadWishListFile(context);
            try {
                JSONArray fileJsonArray = new JSONArray(sJsonArray);
                if (!CheckProductInsideWishlist(newArrival.getiId())) {
                    fileJsonArray.put(newJsonArray.getJSONObject(0));
                    if (Tools.AddProductToWishList(fileJsonArray, context)) {
                        Toast.makeText(context, context.getString(R.string.added_to_wishlist), Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    RemoveFromWishList(newArrival.getiId(), wish_img);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }


        } else {
            if (Tools.AddProductToWishList(newJsonArray, context)) ;
            {
                Toast.makeText(context, context.getString(R.string.added_to_wishlist), Toast.LENGTH_SHORT).show();
                return true;
            }

        }
        return false;
    }

    ;

    public boolean RemoveFromWishList(String iId, ImageView wish_img) {
        String sJsonArray = Tools.ReadWishListFile(context);
        int indexToRemove = -1;
        try {
            JSONArray fileJsonArray = new JSONArray(sJsonArray);
            for (int i = 0; i < fileJsonArray.length(); i++) {
                if (fileJsonArray.getJSONObject(i).getString("iId").equals(iId)) {
                    indexToRemove = i;
                }

                if (fileJsonArray.length() == i + 1) {
                    if (indexToRemove != -1) {
                        fileJsonArray.remove(indexToRemove);
                        if (Tools.AddProductToWishList(fileJsonArray, context)) {
                            wish_img.setImageResource(R.drawable.ic_love_gray);
                            return true;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (type.equals("small")) {
            view = LayoutInflater.from(context).inflate(R.layout.product1_item, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.product_big_item, parent, false);
        }
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final NewArrival newArrival = list.get(position);

        holder.tv_price.setText(list.get(position).getfPrice() + context.getString(R.string.currency));
        if (CheckProductInsideWishlist(list.get(position).getiId())) {
            holder.wishlist.setImageResource(R.drawable.ic_love_red);
        } else {
            holder.wishlist.setImageResource(R.drawable.ic_love_gray);
        }


        if (Objects.equals(preferences.getString("language", ""), "english")) {
            holder.tv_name.setText(list.get(position).getsName());
        } else {
            holder.tv_name.setText(list.get(position).getsAltName());
        }
        if (!list.get(position).getsImagePath().isEmpty()) {
            Picasso.get().load(list.get(position).getsImagePath()).error(R.drawable.place_holder).into(holder.img_image);
        } else {
            Picasso.get().load(R.drawable.place_holder).into(holder.img_image);
        }

        holder.card_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onProductItemClick(view, newArrival, position);
            }
        });


        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onClickListener.onWishlistAdded(view);
                if (AddOrRemoveFromWishList(newArrival, holder.wishlist)) {
                    holder.wishlist.setImageResource(R.drawable.ic_love_red);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        if (type.equals("small")) {
            int size = 2 + Tools.calculateNoOfColumns(context, 103);
            return Math.min(list.size(), size);
        } else {
            return list.size();
        }
    }


    public interface OnClickListener {
        void onProductItemClick(View view, NewArrival newArrival, int pos);

        void onWishlistAdded(View view);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_price;
        ImageView img_image;
        CardView card_product;
        ImageView wishlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.name);
            img_image = itemView.findViewById(R.id.image);
            card_product = itemView.findViewById(R.id.card);
            tv_price = itemView.findViewById(R.id.price);
            wishlist = itemView.findViewById(R.id.wish);
        }
    }
}

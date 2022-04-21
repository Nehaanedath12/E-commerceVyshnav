package com.sangsolutions.e_commerce.Adapter.ViewMoreAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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

public class ViewMoreAdapter extends RecyclerView.Adapter<ViewMoreAdapter.ViewHolder> {
    private Context context;
    private List<ViewMore> list;
    private SharedPreferences preferences;
    private OnClickListener onClickListener;


    public ViewMoreAdapter(Context context, List<ViewMore> list) {
        this.context = context;
        this.list = list;
    }

    public boolean CheckProductInsideWishlist(String iId) {
        String sJsonArray = Tools.ReadWishListFile(context);
        try {
            JSONArray fileJsonArray = new JSONArray(sJsonArray);
            for (int i = 0; i < fileJsonArray.length(); i++) {
                JSONObject jsonObject = fileJsonArray.getJSONObject(i);
                if (jsonObject.getString("iId").equals(iId)) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }


        return false;
    }

    public boolean AddOrRemoveFromWishList(ViewMore viewMore, ImageView wish_img) {

        JSONArray newJsonArray = new JSONArray();
        JSONObject newJsonObject = new JSONObject();

        try {
            newJsonObject.put("fPrice", viewMore.getfPrice());
            newJsonObject.put("iId", viewMore.getiId());
            newJsonObject.put("iSubCategory", viewMore.getiSubCategory());
            newJsonObject.put("sAltLongDescription", viewMore.getsAltLongDescription());
            newJsonObject.put("sAltName", viewMore.getsAltName());
            newJsonObject.put("sAltShortDescription", viewMore.getsAltShortDescription());
            newJsonObject.put("sCode", viewMore.getsCode());
            newJsonObject.put("sImagePath", viewMore.getsImagePath());
            newJsonObject.put("sLongDescription", viewMore.getsLongDescription());
            newJsonObject.put("sShortDescription", viewMore.getsShortDescription());
            newJsonObject.put("sName", viewMore.getsName());
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
                if (!CheckProductInsideWishlist(viewMore.getiId())) {
                    fileJsonArray.put(newJsonArray.getJSONObject(0));
                    if (Tools.AddProductToWishList(fileJsonArray, context)) {
                        Toast.makeText(context, context.getString(R.string.added_to_wishlist), Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    RemoveFromWishList(viewMore.getiId(), wish_img);
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
        View view = LayoutInflater.from(context).inflate(R.layout.product_big_item, parent, false);
        preferences = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ViewMore viewMore = list.get(position);
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
            Picasso.get().load(list.get(position).getsImagePath()).placeholder(R.drawable.place_holder).error(R.drawable.place_holder).into(holder.img_image);
        } else {
            Picasso.get().load(R.drawable.place_holder).into(holder.img_image);
        }

        holder.card_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onProductItemClick(view, viewMore, position);
            }
        });

        holder.wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (AddOrRemoveFromWishList(viewMore, holder.wishlist)) {
                    holder.wishlist.setImageResource(R.drawable.ic_love_red);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        int size = 2 + Tools.calculateNoOfColumns(context, 103);
        return Math.min(list.size(), size);
    }


    public interface OnClickListener {
        void onProductItemClick(View view, ViewMore viewMore, int pos);

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

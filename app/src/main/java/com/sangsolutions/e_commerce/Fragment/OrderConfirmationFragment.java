package com.sangsolutions.e_commerce.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.OrderConfirmationAdapter.OrderConfirmation;
import com.sangsolutions.e_commerce.Adapter.OrderConfirmationAdapter.OrderConfirmationAdapter;
import com.sangsolutions.e_commerce.Adapter.OrderHistroryDetailsAdapter.OrderHistoryDetails;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Singleton.OrderHistorySingleton;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderConfirmationFragment extends Fragment {
    OrderConfirmationAdapter orderConfirmationAdapter;
    List<OrderConfirmation> list;
    RecyclerView rv_order_confirm;
    Button btn_continue;
    SharedPreferences preferences;
    String orderId;

    public void UpdateOrder(){
        int position = 0;
        List<OrderHistoryDetails> listOrderHistory = OrderHistorySingleton.getInstance().getList();
        if(listOrderHistory!=null&&!listOrderHistory.isEmpty()){
           JSONObject mainJsonObject = new JSONObject();
           if(list.size()>0) {
               try {
                   mainJsonObject.put("iTransId", orderId);
                   mainJsonObject.put("sName", "");
                   mainJsonObject.put("sLastName", "");
                   mainJsonObject.put("iUserId", Tools.getUserId(requireActivity()));
                   mainJsonObject.put("sMobNo", listOrderHistory.get(position).getsMobNo());
                   mainJsonObject.put("sPhoneNo", "");
                   mainJsonObject.put("sPinCode", "");
                   mainJsonObject.put("sEmail", "");
                   mainJsonObject.put("sAddress1", listOrderHistory.get(position).getsAddress1());
                   mainJsonObject.put("sAddress2", listOrderHistory.get(position).getsAddress2());
                   mainJsonObject.put("iCountry", "");
                   mainJsonObject.put("iCity", "");

                   JSONArray jsonArray = new JSONArray();
                   for (int i = 0; i < list.size(); i++) {
                       JSONObject jsonObject = new JSONObject();
                       jsonObject.put("iProduct", list.get(i).getiId());
                       jsonObject.put("fRate", list.get(i).getfPrice());
                       jsonObject.put("fQty", list.get(i).getQty());

                       jsonArray.put(jsonObject);
                   }

                   mainJsonObject.put("CartDetails", jsonArray);

                   AndroidNetworking.post(URLs.PostCart)
                           .addJSONObjectBody(mainJsonObject)
                           .setPriority(Priority.MEDIUM)
                           .build()
                           .getAsString(new StringRequestListener() {
                               @Override
                               public void onResponse(String response) {
                                   Log.d("data", response);
                                   try {
                                       JSONObject jsonObject = new JSONObject(response);
                                       if (!jsonObject.getString("sRefNo").isEmpty()) {
                                           Toast.makeText(getActivity(), getString(R.string.done), Toast.LENGTH_SHORT).show();
                                           requireActivity().getSupportFragmentManager().popBackStack();
                                       }

                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }


                               }

                               @Override
                               public void onError(ANError anError) {
                                   Log.d("data", anError.toString());
                               }
                           });


               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
        }
    }

    public void UpdateCount(int count, int qty, float price, TextView tv, EditText et_qty, OrderConfirmation orderConfirmation, int pos) {
        int sum = 0;
        if (!et_qty.getText().toString().equals("0")) {

            try {
                if (qty == 1 && count == -1) {
                    Log.d("count", "count error");
                } else {
                    sum = count + qty;
                    et_qty.setText(String.valueOf(sum));
                    String fPrice = orderConfirmation.getfPrice();
                    String iId = orderConfirmation.getiId();
                    String sAltLongDescription = orderConfirmation.getsAltLongDescription();
                    String sAltName = orderConfirmation.getsAltName();
                    String sAltShortDescription = orderConfirmation.getsAltShortDescription();
                    String sImagePath = orderConfirmation.getsImagePath();
                    String sLongDescription = orderConfirmation.getsLongDescription();
                    String sName = orderConfirmation.getsName();
                    String sShortDescription = orderConfirmation.getsShortDescription();
                    list.set(pos, new OrderConfirmation(String.valueOf(sum * Float.parseFloat(fPrice)),orderConfirmation.getBeforePrice(),orderConfirmation.getBeforeQty(),iId,sAltLongDescription,sAltName,sAltShortDescription,sImagePath,sLongDescription,sName,sShortDescription,String.valueOf(sum)));

                    tv.setText((sum * price) + getString(R.string.currency));
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


        }


    }


    public void ItemRemoveAlert(final int pos, OrderConfirmation OrderConfirmation) {
        View dialogView = getLayoutInflater().inflate(R.layout.item_remove_bottom_sheet, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
        dialog.setContentView(dialogView);
        dialog.show();

        Button btn_no, btn_remove;
        TextView tv_name, tv_description;
        ImageView image;

        btn_no = dialogView.findViewById(R.id.no);
        btn_remove = dialogView.findViewById(R.id.remove);
        tv_name = dialogView.findViewById(R.id.name);
        tv_description = dialogView.findViewById(R.id.description);
        image = dialogView.findViewById(R.id.image);

        if (Objects.equals(preferences.getString("language", ""), "english")) {
            tv_name.setText(OrderConfirmation.getsName());
            tv_description.setText(OrderConfirmation.getsShortDescription());
        } else {
            tv_name.setText(OrderConfirmation.getsAltName());
            tv_description.setText(OrderConfirmation.getsAltShortDescription());
        }
        if (!OrderConfirmation.getsImagePath().isEmpty()) {
            Picasso.get().load(OrderConfirmation.getsImagePath()).placeholder(R.drawable.place_holder).error(R.drawable.place_holder).into(image);
        } else {
            Picasso.get().load(R.drawable.place_holder).into(image);
        }


        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    list.remove(pos);
                    orderConfirmationAdapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });


    }

    public void LoadOrderItems(final String RefNo) {
        AndroidNetworking.get(URLs.GetOrderHistoryDetails)
                .addQueryParameter("iCustomer", Tools.getUserId(requireActivity()))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        list.clear();
                        String iId,Product,sRefNo, sAltName, sShortDescription, sLongDescription, sAltLongDescription, sAltShortDescription, DeliveryStatus, sImagePath, fQty, fRate;
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("OrderHistoryDetails"));

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                Product = jsonObject.getString("Product");
                                iId = jsonObject.getString("iProduct");
                                fQty = jsonObject.getString("fQty");
                                fRate = jsonObject.getString("fRate");
                                sAltLongDescription = jsonObject.getString("sAltLongDescription");
                                sAltName = jsonObject.getString("sAltName");
                                sAltShortDescription = jsonObject.getString("sAltShortDescription");
                                sLongDescription = jsonObject.getString("sLongDescription");
                                sShortDescription = jsonObject.getString("sShortDescription");
                                sImagePath = jsonObject.getString("sImagePath");
                                sRefNo = jsonObject.getString("sRefNo");
                                if(RefNo.equals(sRefNo)) {
                                    list.add(new OrderConfirmation(fRate,fRate,fQty,iId,sAltLongDescription,sAltName,sAltShortDescription,sImagePath,sLongDescription,Product,sShortDescription,fQty));
                                    orderConfirmationAdapter.notifyDataSetChanged();
                                }
                                if(jsonArray.length()==i+1){
                                    rv_order_confirm.setItemViewCacheSize(list.size());
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorBody());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("GetOrderHistoryDetails\n" + anError.getErrorDetail()));
                    }
                });

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.confirm_layout_fragment, container, false);
        btn_continue = view.findViewById(R.id.Continue);
        preferences = requireActivity().getSharedPreferences("language", Context.MODE_PRIVATE);
        list = new ArrayList<>();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String sRefNo = bundle.getString("sRefNo");
            orderId = bundle.getString("orderId");
            if (sRefNo != null && !sRefNo.isEmpty()) LoadOrderItems(sRefNo);
            if (orderId == null || orderId.isEmpty()) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        }
        orderConfirmationAdapter = new OrderConfirmationAdapter(getActivity(), list);
        rv_order_confirm = view.findViewById(R.id.rv_order);
        rv_order_confirm.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv_order_confirm.setAdapter(orderConfirmationAdapter);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateOrder();
            }
        });

        orderConfirmationAdapter.setOnClickListener(new OrderConfirmationAdapter.OnClickListener() {
            @Override
            public void onProductItemClick(View view, OrderConfirmation orderConfirmation, int pos) {

            }

            @Override
            public void onRemoveProductItemClick(View view, OrderConfirmation orderConfirmation, int pos) {
                ItemRemoveAlert(pos, orderConfirmation);
            }

            @Override
            public void onAddItemClick(View view, OrderConfirmation orderConfirmation, EditText edit, int pos, TextView tv) {
                UpdateCount(1, Integer.parseInt(edit.getText().toString()), Float.parseFloat(orderConfirmation.getfPrice()), tv, edit, orderConfirmation, pos);
            }

            @Override
            public void onRemoveItemClick(View view, OrderConfirmation orderConfirmation, EditText edit, int pos, TextView tv) {
                UpdateCount(-1, Integer.parseInt(edit.getText().toString()), Float.parseFloat(orderConfirmation.getfPrice()), tv, edit, orderConfirmation, pos);
            }
        });
        return view;
    }
}

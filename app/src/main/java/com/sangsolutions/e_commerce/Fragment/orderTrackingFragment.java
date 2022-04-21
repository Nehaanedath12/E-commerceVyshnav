package com.sangsolutions.e_commerce.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.OrderTrakingAdapter.OrderTracking;
import com.sangsolutions.e_commerce.Adapter.OrderTrakingAdapter.OrderTrackingAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class orderTrackingFragment extends Fragment {
 private List<OrderTracking> list;
 private OrderTrackingAdapter orderTrackingAdapter;
    private EditText et_ref_number;
    private FrameLayout empty_frame;
 private LinearLayout ll_2;
private TextView tv_ref_no,tv_address1,tv_address2,tv_order_date,tv_status;
    private void loadOrderHistoryDetails(final String ref_no) {
        ll_2.setVisibility(View.INVISIBLE);
        AndroidNetworking.get(URLs.GetTrackOrder)
                .addQueryParameter("sOrderNo", ref_no)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        list.clear();
                        String ContactPerson,DeliveryStatus,OrderDate,Product,fQty,fRate,sAddress1,sAddress2,sAltLongDescription,sAltName,sAltShortDescription,sLongDescription,sMobNo,sRefNo,sShortDescription,sImagePath="";
                        try {
                            JSONArray jsonArray = new JSONArray(response.getString("TrackOrder"));

                            if(jsonArray.length()==0){
                                empty_frame.setVisibility(View.VISIBLE);
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ContactPerson = jsonObject.getString("ContactPerson");
                                DeliveryStatus = jsonObject.getString("DeliveryStatus");
                                OrderDate = jsonObject.getString("OrderDate");
                                Product = jsonObject.getString("Product");
                                fQty = jsonObject.getString("fQty");
                                fRate = jsonObject.getString("fRate");
                                sAddress1 = jsonObject.getString("sAddress1");
                                sAddress2 = jsonObject.getString("sAddress2");
                                sAltLongDescription = jsonObject.getString("sAltLongDescription");
                                sAltName = jsonObject.getString("sAltName");
                                sAltShortDescription = jsonObject.getString("sAltShortDescription");
                                sLongDescription = jsonObject.getString("sLongDescription");
                                sMobNo = jsonObject.getString("sMobNo");
                                sRefNo = jsonObject.getString("sRefNo");
                                sShortDescription = jsonObject.getString("sShortDescription");
                                sImagePath = jsonObject.getString("sImagePath");




                                list.add(new OrderTracking(ContactPerson,DeliveryStatus,OrderDate,Product,fQty,fRate,sAddress1,sAddress2,sAltLongDescription,sAltName,sAltShortDescription,sLongDescription,sMobNo,sRefNo,sShortDescription,sImagePath));

                                if(list.size()>0){
                                    ll_2.setVisibility(View.VISIBLE);
                                }
                                if (jsonArray.length() == i + 1) {

                                    if(list.get(0).getsAddress1().isEmpty()&&list.get(0).getsAddress2().isEmpty()) {
                                        tv_address1.setText("N/A");
                                        tv_address2.setText("");
                                    }else {
                                        tv_address1.setText(list.get(0).getsAddress1());
                                        tv_address2.setText(list.get(0).getsAddress2());
                                    }
                                    if(list.get(0).getOrderDate().isEmpty()){
                                        tv_order_date.setText("N/A");
                                    }else {
                                        tv_order_date.setText(list.get(0).getOrderDate());
                                    }

                                    if(list.get(0).getsRefNo().isEmpty()){
                                        tv_ref_no.setText("N/A");
                                    }else {
                                        tv_ref_no.setText(list.get(0).getsRefNo());
                                    }

                                    if(list.get(0).getDeliveryStatus().isEmpty()){
                                        tv_status.setText("N/A");
                                    }else {
                                        tv_status.setText(list.get(0).getDeliveryStatus());
                                    }

                                    orderTrackingAdapter.notifyDataSetChanged();
                                    empty_frame.setVisibility(View.INVISIBLE);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("GetTrackOrder\n"+e.getMessage()));
                            empty_frame.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("data", anError.getErrorBody());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("GetTrackOrder\n"+anError.getErrorDetail()));
                        empty_frame.setVisibility(View.VISIBLE);
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.order_tracking_fragment,container,false);
        list = new ArrayList<>();
        orderTrackingAdapter = new OrderTrackingAdapter(getActivity(),list);
        RecyclerView rv_order_tracking = view.findViewById(R.id.rv_order_tracking);
        et_ref_number = view.findViewById(R.id.ref_no);
        Button btn_load = view.findViewById(R.id.load);
        ll_2 = view.findViewById(R.id.ll_2);
        tv_ref_no = view.findViewById(R.id.ref_number);
        tv_address1 = view.findViewById(R.id.address_one);
        tv_address2 = view.findViewById(R.id.address_two);
        tv_order_date = view.findViewById(R.id.order_date);
        tv_status = view.findViewById(R.id.status);

        empty_frame = view.findViewById(R.id.empty_frame);
        rv_order_tracking.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_order_tracking.setAdapter(orderTrackingAdapter);

        empty_frame.setVisibility(View.VISIBLE);
        ll_2.setVisibility(View.INVISIBLE);
        orderTrackingAdapter.setOnClickListener(new OrderTrackingAdapter.OnClickListener() {
            @Override
            public void onOrderListItemClick(View view, OrderTracking orderTracking, int pos) {

            }
        });


        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_ref_number.getText().toString().isEmpty()){
                    loadOrderHistoryDetails(et_ref_number.getText().toString());
                }
            }
        });



        return view;
    }


}

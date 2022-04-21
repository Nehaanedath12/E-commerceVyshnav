package com.sangsolutions.e_commerce.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.OrderHistroryDetailsAdapter.OrderHistoryDetails;
import com.sangsolutions.e_commerce.Adapter.OrderHistroryDetailsAdapter.OrderHistoryDetailsAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Singleton.OrderHistorySingleton;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class OrderHistoryDetailsFragment extends Fragment {
    private OrderHistoryDetailsAdapter orderHistoryDetailsAdapter;
 private List<OrderHistoryDetails> list;
 private FrameLayout empty_frame;
 private LinearLayout ll_1;
 private TextView tv_ref_no,tv_address1,tv_address2,tv_order_date, tv_status;
    private ImageView img_edit;
    String sRefNo ="";


    private void PostAlertUpdate(String iId){
        AndroidNetworking.get(URLs.PostAlertUpdate)
                .addQueryParameter("iId",iId)
                .addQueryParameter("iStatus","1")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: "+anError.getResponse());
                    }
                });
    }

    private void LoadValueForReference(JSONObject json,String loadValue){
        list.clear();
        String ContactPerson,DeliveryStatus,OrderDate,Product,fQty,fRate,sAddress1,sAddress2,sAltLongDescription,sAltName,sAltShortDescription,sLongDescription,sMobNo,sRefNo,sShortDescription,sImagePath="";
        try {
            JSONArray jsonArray = new JSONArray(json.getString("OrderHistoryDetails"));

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


                if(sRefNo.equals(loadValue)) {
                    list.add(new OrderHistoryDetails(sRefNo,ContactPerson,sMobNo,sAddress1,sAddress2,OrderDate,Product,fQty,fRate,sAltName,sShortDescription,sLongDescription,sAltLongDescription,sAltShortDescription,DeliveryStatus,sImagePath));
                }
                if (jsonArray.length() == i + 1) {
                    OrderHistorySingleton.getInstance().setList(list);
                    ll_1.setVisibility(View.VISIBLE);
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

                    orderHistoryDetailsAdapter.notifyDataSetChanged();
                    if(list.size()<=0){
                        empty_frame.setVisibility(View.VISIBLE);
                    }else {
                        empty_frame.setVisibility(View.INVISIBLE);
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(new Throwable("GetOrderHistoryDetails\n"+e.getMessage()));
            empty_frame.setVisibility(View.VISIBLE);
        }
    }

    private void LoadValueFromNotification(JSONObject json){
        list.clear();
        String ContactPerson = "",DeliveryStatus,fRate,OrderDate,Product,fQty,sAddress1,sAddress2,sAltLongDescription,sAltName,sAltShortDescription,sLongDescription,sMobNo,sRefNo,sShortDescription,sImagePath="";

        try {
            sAddress1 = json.getString("sAddress1");
            sAddress2 = json.getString("sAddress2");
            OrderDate = json.getString("OrderDate");
            sRefNo = json.getString("sRefNo");
            sMobNo = json.getString("sMobNo");

        ll_1.setVisibility(View.VISIBLE);
        if(sAddress1.isEmpty()&&sAddress2.isEmpty()) {
            tv_address1.setText("N/A");
            tv_address2.setText("");
        }else {
            tv_address1.setText(sAddress1);
            tv_address2.setText(sAddress2);
        }
        if(OrderDate.isEmpty()){
            tv_order_date.setText("N/A");
        }else {
            tv_order_date.setText(OrderDate);
        }

        if(sRefNo.isEmpty()){
            tv_ref_no.setText("N/A");
        }else {
            tv_ref_no.setText(sRefNo);
        }

       /* if(list.get(0).getDeliveryStatus().isEmpty()){
            tv_status.setText("N/A");
        }else {
            tv_status.setText(list.get(0).getDeliveryStatus());
        }*/


       String productDetails = json.getString("productDetails");

       JSONArray jsonArray = new JSONArray(productDetails);
       for(int i = 0 ; i< jsonArray.length();i++){
           JSONObject jsonObject = jsonArray.getJSONObject(i);

           Product = jsonObject.getString("Product");
           fQty = jsonObject.getString("fQty");
           fRate = jsonObject.getString("fRate");
           sAltLongDescription = jsonObject.getString("sAltLongDescription");
           sAltName = jsonObject.getString("sAltName");
           sAltShortDescription = jsonObject.getString("sAltShortDescription");
           sLongDescription = jsonObject.getString("sLongDescription");
           sShortDescription = jsonObject.getString("sShortDescription");
           DeliveryStatus = jsonObject.getString("DeliveryStatus");
           sImagePath = jsonObject.getString("sImagePath");

            list.add(new OrderHistoryDetails(sRefNo,ContactPerson,sMobNo,sAddress1,sAddress2,OrderDate,Product,fQty,fRate,sAltName,sShortDescription,sLongDescription,sAltLongDescription,sAltShortDescription,DeliveryStatus,sImagePath));




           if(i+1==jsonArray.length()){

               if(list.get(0).getDeliveryStatus().isEmpty()){
                   tv_status.setText("N/A");
               }else {
                   tv_status.setText(list.get(0).getDeliveryStatus());
               }

               orderHistoryDetailsAdapter.notifyDataSetChanged();
               if(list.size()<=0){
                   empty_frame.setVisibility(View.VISIBLE);
               }else {
                   empty_frame.setVisibility(View.INVISIBLE);
               }
           }

       }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void LoadHistory(final String loadValue, final int load) {
        empty_frame.setVisibility(View.VISIBLE);
        ll_1.setVisibility(View.INVISIBLE);
        String url,Parameter,value;
        if(load == 1){
            url = URLs.GetOrderHistoryDetails;
            Parameter = "iCustomer";
            value =Tools.getUserId(requireActivity());
        }else{
            url = URLs.GetAlertDetails;
            Parameter = "iOrder";
            value =loadValue;
        }
        AndroidNetworking.get(url)
                .addQueryParameter(Parameter,value)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("data",response.toString());
                        if(load == 1 ) {
                            LoadValueForReference(response, loadValue);
                        }else {
                            LoadValueFromNotification(response);
                        }
                        }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", anError.getErrorBody());
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("GetOrderHistoryDetails\n" + anError.getErrorDetail()));
                        empty_frame.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void EditButton(final String orderId) {
        img_edit.setVisibility(View.VISIBLE);
        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderHistorySingleton.getInstance().setList(list);
                Fragment fragment = new OrderConfirmationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("sRefNo", list.get(0).getsRefNo());
                bundle.putString("orderId", orderId);
                fragment.setArguments(bundle);
                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("orderConfirmation").commit();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.histroy_details_fragment, container, false);
        RecyclerView rv_order_history_details = view.findViewById(R.id.rv_order_history_details);
        empty_frame = view.findViewById(R.id.empty_frame);
        rv_order_history_details.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rv_order_history_details.setHasFixedSize(true);
        ll_1 = view.findViewById(R.id.rl_1);
        tv_ref_no = view.findViewById(R.id.ref_no);
        tv_address1 = view.findViewById(R.id.address_one);
        tv_address2 = view.findViewById(R.id.address_two);
        tv_order_date = view.findViewById(R.id.order_date);
        tv_status = view.findViewById(R.id.status);
        img_edit = view.findViewById(R.id.edit);
        img_edit.setVisibility(View.INVISIBLE);
        empty_frame.setVisibility(View.VISIBLE);
        ll_1.setVisibility(View.INVISIBLE);
        list = new ArrayList<>();
        orderHistoryDetailsAdapter = new OrderHistoryDetailsAdapter(getActivity(), list);
        rv_order_history_details.setAdapter(orderHistoryDetailsAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            sRefNo = bundle.getString("refno");
            final String orderId = bundle.getString("OrderId");
            String iId = bundle.getString("iId");
            if (sRefNo != null) {
                LoadHistory(sRefNo, 1);
            } else if (orderId != null) {
                LoadHistory(orderId, 2);
                EditButton(orderId);
                assert iId != null;
                if (!iId.isEmpty()) {
                    PostAlertUpdate(iId);
                }


            }


        }


        orderHistoryDetailsAdapter.setOnClickListener(new OrderHistoryDetailsAdapter.OnClickListener() {
            @Override
            public void onOrderListItemClick(View view, OrderHistoryDetails orderHistoryDetails, int pos) {

            }
        });


        return view;
    }


}

package com.sangsolutions.e_commerce.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.OrderHistoryAdapter.OrderHistory;
import com.sangsolutions.e_commerce.Adapter.OrderHistoryAdapter.OrderHistoryAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment {
    private List<OrderHistory> list;
 private OrderHistoryAdapter orderHistoryAdapter;
 private FrameLayout empty_frame;
private void LoadOrderHistory(){
    empty_frame.setVisibility(View.VISIBLE);
    AndroidNetworking.get(URLs.GetOrderHistory)
    .addQueryParameter("iCustomer",Tools.getUserId(requireActivity()))
    .setPriority(Priority.MEDIUM)
    .build()
     .getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    list.clear();
                    String sRefNo,ContactPerson,sMobNo,sAddress1,sAddress2,OrderDate,DeliveryStatus;
                    try {
                        JSONArray jsonArray = new JSONArray(response.getString("OrderHistory"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            sRefNo = jsonObject.getString("sRefNo");
                            ContactPerson = jsonObject.getString("ContactPerson");
                            sMobNo = jsonObject.getString("sMobNo");
                            sAddress1 = jsonObject.getString("sAddress1");
                            sAddress2 = jsonObject.getString("sAddress2");
                            OrderDate = jsonObject.getString("OrderDate");
                            DeliveryStatus = jsonObject.getString("DeliveryStatus");

                                list.add(new OrderHistory(sRefNo,ContactPerson,sMobNo,sAddress1,sAddress2,OrderDate,DeliveryStatus));

                            if (jsonArray.length() == i + 1) {
                                if(list.size()>0) {
                                    empty_frame.setVisibility(View.INVISIBLE);
                                }
                                orderHistoryAdapter.notifyDataSetChanged();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        FirebaseCrashlytics.getInstance().recordException(new Throwable("GetOrderHistory\n"+e.getMessage()));
                        empty_frame.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(ANError anError) {
                Log.d("error",anError.getErrorBody());
                    FirebaseCrashlytics.getInstance().recordException(new Throwable("GetOrderHistory\n"+anError.getErrorDetail()));
                    empty_frame.setVisibility(View.VISIBLE);

                }
            });

}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = LayoutInflater.from(requireActivity()).inflate(R.layout.order_history_fragment,container,false);
        RecyclerView rv_order_history = view.findViewById(R.id.rv_order_history);
        empty_frame = view.findViewById(R.id.empty_frame);
        empty_frame.setVisibility(View.VISIBLE);
        rv_order_history.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rv_order_history.setHasFixedSize(true);
        list = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(getActivity(),list);
        rv_order_history.setAdapter(orderHistoryAdapter);
        LoadOrderHistory();

        orderHistoryAdapter.setOnClickListener(new OrderHistoryAdapter.OnClickListener() {
            @Override
            public void onOrderHistoryItemClick(View view, OrderHistory orderHistory, int pos) {
                Fragment fragment = new OrderHistoryDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("refno",orderHistory.getsRefNo());
                fragment.setArguments(bundle);
                FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("orderListDetails").commit();
            }
        });

        return view;
    }
}

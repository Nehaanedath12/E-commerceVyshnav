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
import com.sangsolutions.e_commerce.Adapter.NotificationListAdapter.NotificationList;
import com.sangsolutions.e_commerce.Adapter.NotificationListAdapter.NotificationListAdapter;
import com.sangsolutions.e_commerce.R;
import com.sangsolutions.e_commerce.Tools;
import com.sangsolutions.e_commerce.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class NotificationFragment extends Fragment {
RecyclerView rv_notification;
List<NotificationList> list;
NotificationListAdapter notificationListAdapter;
FrameLayout empty_frame;


    public void GotoOrderHistoryDetails(int OrderId,String iId){
        Fragment fragment = new OrderHistoryDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("OrderId", String.valueOf(OrderId));
        bundle.putString("iId", iId);
        fragment.setArguments(bundle);
        FragmentTransaction tx = requireActivity().getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment, fragment).addToBackStack("orderListDetails").commit();
    }


    public void LoadNotification() {
        empty_frame.setVisibility(View.VISIBLE);
        if (!Tools.getUserId(requireActivity()).equals("0"))
            AndroidNetworking.get(URLs.getOrderConfirmAlert)
                    .addQueryParameter("iUser", Tools.getUserId(requireActivity()))
                    .addQueryParameter("iStatus", "1")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            list.clear();
                            try {
                                JSONArray jsonArray = new JSONArray(response.getString("getConfirmDetails"));

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String iId = jsonObject.getString("iId");
                                    String iOrderId = jsonObject.getString("iOrderId");
                                    String sMessage = jsonObject.getString("sMessage");
                                    String sRefNo = jsonObject.getString("sRefNo");
                                    list.add(new NotificationList(iId,iOrderId,sMessage,sRefNo));

                                    if(jsonArray.length()==i+1){
                                        empty_frame.setVisibility(View.GONE);
                                        notificationListAdapter.notifyDataSetChanged();
                                    }
                                }

                            } catch (JSONException e) {
                                empty_frame.setVisibility(View.VISIBLE);
                                e.printStackTrace();
                                FirebaseCrashlytics.getInstance().recordException(new Throwable("getOrderConfirmAlert\n"+e.getMessage()));
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            empty_frame.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onError: " + anError.getResponse());
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getOrderConfirmAlert\n"+anError.getErrorDetail()));
                        }
                    });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = LayoutInflater.from(getActivity()).inflate(R.layout.notifaction_fragmnet,container,false);
       list = new ArrayList<>();
       notificationListAdapter = new NotificationListAdapter(list,getActivity());
        empty_frame = view.findViewById(R.id.empty_frame);
        empty_frame.setVisibility(View.VISIBLE);
       rv_notification = view.findViewById(R.id.rv_notification);
       rv_notification.setLayoutManager(new LinearLayoutManager(getActivity()));
       rv_notification.setAdapter(notificationListAdapter);

        LoadNotification();


        notificationListAdapter.setOnClickListener(new NotificationListAdapter.onItemClick() {
            @Override
            public void onItemClickListener(View view, NotificationList notificationList, int position) {
                GotoOrderHistoryDetails(Integer.parseInt(notificationList.getiOrderId()),notificationList.getiId());
            }
        });
       return view;
    }
}

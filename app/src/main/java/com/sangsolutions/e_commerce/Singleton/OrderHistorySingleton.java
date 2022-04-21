package com.sangsolutions.e_commerce.Singleton;

import com.sangsolutions.e_commerce.Adapter.OrderHistroryDetailsAdapter.OrderHistoryDetails;

import java.util.ArrayList;
import java.util.List;

public class OrderHistorySingleton {
    private static OrderHistorySingleton orderHistorySingleton;
    public List<OrderHistoryDetails> list = new ArrayList<>();

    private OrderHistorySingleton(){}

    public static OrderHistorySingleton getInstance(){
        if(orderHistorySingleton ==null){
            orderHistorySingleton = new OrderHistorySingleton();
        }
        return orderHistorySingleton;
    }

    public List<OrderHistoryDetails> getList(){return list;}
    public void setList(List<OrderHistoryDetails> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}

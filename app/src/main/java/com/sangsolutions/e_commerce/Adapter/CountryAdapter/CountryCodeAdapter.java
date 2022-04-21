package com.sangsolutions.e_commerce.Adapter.CountryAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sangsolutions.e_commerce.R;

import java.util.List;

public class CountryCodeAdapter extends BaseAdapter {

    List<Country> list;
    Context context;

    public CountryCodeAdapter(List<Country> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.country_city_item,viewGroup,false);
        TextView tv_title = view.findViewById(R.id.tv_city_or_country);
        tv_title.setText(list.get(i).getsCode());
        return view;
    }
}

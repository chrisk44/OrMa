package com.ceid.orma;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class OrderFragment extends Fragment{
    View fragmentView;
    ListView listView;
    Table table;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        setRetainInstance(true);
        fragmentView = inflater.inflate(R.layout.order_layout, container, false);
        listView = fragmentView.findViewById(R.id.order_list_view);

        if(table==null){
            Log.e("OrderFragment", "table is null");
            return fragmentView;
        }

        OrderAdapter adapter = new OrderAdapter((MainActivity)getActivity(), R.layout.order_item_tile, table.order);
        adapter.setNotifyOnChange(true);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Toast.makeText(getActivity(), "Clicked on item " + position, Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                Toast.makeText(getActivity(), "Long clicked on item " + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return fragmentView;
    }

    @Override
    public void onStart(){
        super.onStart();

        ((MainActivity)getActivity()).toolbarMenu.findItem(R.id.done_icon).setVisible(true);
        ((MainActivity)getActivity()).toolbarMenu.findItem(R.id.settings_icon).setVisible(false);
    }
    @Override
    public void onStop(){
        super.onStop();

        ((MainActivity)getActivity()).toolbarMenu.findItem(R.id.done_icon).setVisible(false);
        ((MainActivity)getActivity()).toolbarMenu.findItem(R.id.settings_icon).setVisible(true);
    }

    int onDoneClicked(MainActivity mainActivity){

        return 0;
    }

    OrderFragment setTable(Table t){
        this.table = t;
        return this;
    }

}

package com.ceid.orma;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

enum Status{ TABLE_IDLE, TABLE_RESERVED, TABLE_FREE, TABLE_CALLING, TABLE_TO_PAY, TABLE_ATTENTION }

public class Table{
    private String id;
    private int count;
    private Status status;

    private View view;
    private TextView id_view, count_view;
    private ImageView alert_icon, call_icon, pay_icon, reserved_icon;

    Order order;

    Table(String id, int count, View v, Order order){
        this.id = id;
        this.count = count;
        this.view = v;
        this.order = order;

        id_view = view.findViewById(R.id.table_id_tv);
        count_view = view.findViewById(R.id.table_count_tv);
        alert_icon = view.findViewById(R.id.alert_icon);
        call_icon = view.findViewById(R.id.call_icon);
        pay_icon = view.findViewById(R.id.pay_icon);
        reserved_icon = view.findViewById(R.id.reserved_icon);

        id_view.setText(id);
        count_view.setText(Integer.toString(count));

        this.view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentTransaction ft = ((MainActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment_view, new OrderFragment().setTable(Table.this));
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }
        });

        setStatus(Status.TABLE_FREE);
    }

    void setStatus(Status s){
        this.status = s;

        // Clear all icons
        alert_icon.setVisibility(View.GONE);
        call_icon.setVisibility(View.GONE);
        pay_icon.setVisibility(View.GONE);
        reserved_icon.setVisibility(View.GONE);

        // Enable the right ones
        switch(status){
            case TABLE_TO_PAY:
                pay_icon.setVisibility(View.VISIBLE);
                break;

            case TABLE_ATTENTION:
                call_icon.setVisibility(View.VISIBLE);
                alert_icon.setVisibility(View.VISIBLE);
                break;

            case TABLE_RESERVED:
                reserved_icon.setVisibility(View.VISIBLE);
                break;

            case TABLE_CALLING:
                call_icon.setVisibility(View.VISIBLE);
                break;
        }
    }

    boolean hasPeople(){
        return status!=Status.TABLE_FREE && status!=Status.TABLE_RESERVED;
    }
}

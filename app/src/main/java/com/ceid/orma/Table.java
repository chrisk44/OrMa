package com.ceid.orma;

import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.ceid.orma.OverviewFragment.STYLE_BOTH;
import static com.ceid.orma.OverviewFragment.STYLE_ICONS;
import static com.ceid.orma.OverviewFragment.STYLE_INSIDE;
import static com.ceid.orma.OverviewFragment.STYLE_OUTSIDE;
import static com.ceid.orma.OverviewFragment.table_icon_style;

enum Status{ TABLE_IDLE, TABLE_RESERVED, TABLE_FREE, TABLE_CALLING, TABLE_TO_PAY, TABLE_ATTENTION }

class Table{
    private String id;
    private int count;
    private Status status;

    private View view;
    private TextView id_view, count_view;
    private ImageView alert_icon, call_icon, pay_icon, reserved_icon;
    private ImageView table_normal, table_gray, table_red, table_green, table_blue;

    Order order;

    Table(String id, int count, View v, Order order){
        this.id = id;
        this.count = count;
        this.view = v;
        this.order = order;

        id_view = view.findViewById(R.id.table_id_tv);
        count_view = view.findViewById(R.id.table_count_tv);

        // Icons
        alert_icon = view.findViewById(R.id.alert_icon);
        call_icon = view.findViewById(R.id.call_icon);
        pay_icon = view.findViewById(R.id.pay_icon);
        reserved_icon = view.findViewById(R.id.reserved_icon);

        // Tables
        table_normal = view.findViewById(R.id.table_icon);
        table_gray = view.findViewById(R.id.table_gray);
        table_red = view.findViewById(R.id.table_red);
        table_green = view.findViewById(R.id.table_green);
        table_blue = view.findViewById(R.id.table_blue);

        id_view.setText(id);
        count_view.setText(String.format(view.getContext().getResources().getConfiguration().locale, "%d", count));

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

        // Clear all icons and tables
        alert_icon.setVisibility(View.GONE);
        call_icon.setVisibility(View.GONE);
        pay_icon.setVisibility(View.GONE);
        reserved_icon.setVisibility(View.GONE);
        table_gray.setVisibility(View.GONE);
        table_red.setVisibility(View.GONE);
        table_green.setVisibility(View.GONE);
        table_blue.setVisibility(View.GONE);

        switch(table_icon_style){
            case STYLE_ICONS:
                table_normal.setVisibility(View.GONE);
                break;

            case STYLE_INSIDE:
                table_gray.setImageResource(R.drawable.table_icon_gray_inside);
                table_red.setImageResource(R.drawable.table_icon_red_inside);
                table_green.setImageResource(R.drawable.table_icon_green_inside);
                table_blue.setImageResource(R.drawable.table_icon_blue_inside);
                break;

            case STYLE_OUTSIDE:
                table_gray.setImageResource(R.drawable.table_icon_gray_outside);
                table_red.setImageResource(R.drawable.table_icon_red_outside);
                table_green.setImageResource(R.drawable.table_icon_green_outside);
                table_blue.setImageResource(R.drawable.table_icon_blue_outside);
                break;

            case STYLE_BOTH:
                table_gray.setImageResource(R.drawable.table_icon_gray_both);
                table_red.setImageResource(R.drawable.table_icon_red_both);
                table_green.setImageResource(R.drawable.table_icon_green_both);
                table_blue.setImageResource(R.drawable.table_icon_blue_both);
                break;
        }

        // Enable the right ones
        switch(status){
            case TABLE_TO_PAY:
                if(table_icon_style==STYLE_ICONS) pay_icon.setVisibility(View.VISIBLE);
                else table_green.setVisibility(View.VISIBLE);
                break;

            case TABLE_ATTENTION:
                if(table_icon_style==STYLE_ICONS){
                    call_icon.setVisibility(View.VISIBLE);
                    alert_icon.setVisibility(View.VISIBLE);
                }else{
                    table_red.setVisibility(View.VISIBLE);
                }
                break;

            case TABLE_RESERVED:
                if(table_icon_style==STYLE_ICONS) reserved_icon.setVisibility(View.VISIBLE);
                else table_gray.setVisibility(View.VISIBLE);
                break;

            case TABLE_CALLING:
                if(table_icon_style==STYLE_ICONS) call_icon.setVisibility(View.VISIBLE);
                else table_blue.setVisibility(View.VISIBLE);
                break;
        }
    }

    boolean hasPeople(){
        return status!=Status.TABLE_FREE && status!=Status.TABLE_RESERVED;
    }
}

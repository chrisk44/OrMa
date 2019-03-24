package com.ceid.orma;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import static com.ceid.orma.Order.getStringFromType;

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

        OrderAdapter adapter = new OrderAdapter(table.order);
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

    OrderFragment setTable(Table t){
        this.table = t;
        return this;
    }

    class OrderAdapter extends ArrayAdapter<OrderItem>{
        Order order;
//        public OrderAdapter(@NonNull Context context, int resource){
//            super(context, resource);
//        }
        OrderAdapter(Order order){
            super(getActivity(), R.layout.order_item_tile);
            if(getActivity()==null){
                Log.e("OrderFragment", "getActivity() is null");
            }
            this.order = order;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent){
            // get a view to work with
            View itemview = convertView;
            if(itemview==null){
                itemview = getLayoutInflater().inflate(R.layout.order_item_tile, parent, false);
            }

            // find the item to work with
            final OrderItem current = order.items.get(position);

            TextView type_view = itemview.findViewById(R.id.item_type_tv);
            TextView extra_view = itemview.findViewById(R.id.item_details_tv);
            TextView price_view = itemview.findViewById(R.id.item_price_tv);
            final TextView count_view = itemview.findViewById(R.id.item_count_tv);
            Button decr_but = itemview.findViewById(R.id.decr_but);
            Button incr_but = itemview.findViewById(R.id.incr_but);

            type_view.setText(getStringFromType(current.type));
            extra_view.setText(current.extra);
            count_view.setText(String.format(getResources().getConfiguration().locale, "%d", current.count));
            price_view.setText(String.format(getResources().getConfiguration().locale, "%.1f0", current.price_per_item));

            decr_but.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    current.count--;
                    count_view.setText(String.format(getResources().getConfiguration().locale, "%d", current.count));
                }
            });
            incr_but.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    current.count++;
                    count_view.setText(String.format(getResources().getConfiguration().locale, "%d", current.count));
                }
            });

            return itemview;
        }

        @Override
        public int getCount(){
            return order.items.size();
        }
    }
}

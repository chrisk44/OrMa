package com.ceid.orma;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import static com.ceid.orma.Product.getStringFromType;

class OrderAdapter extends ArrayAdapter<Product>{
    private Order order;
    private MainActivity mainActivity;
    private int resource;
    OrderAdapter(@NonNull MainActivity mainActivity, int resource, Order o){
        super(mainActivity, resource);
        this.order = o;
        this.mainActivity = mainActivity;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        // get a view to work with
        View itemview = convertView;
        if(itemview==null){
            itemview = mainActivity.getLayoutInflater().inflate(resource, parent, false);
        }

        // find the item to work with
        final Product current = order.products.get(position);

        final TextView count_view = itemview.findViewById(R.id.item_count_tv);
        TextView type_view = itemview.findViewById(R.id.item_type_tv);
        TextView extra_view = itemview.findViewById(R.id.item_details_tv);
        TextView price_view = itemview.findViewById(R.id.item_price_tv);
        final Button decr_but = itemview.findViewById(R.id.decr_but);
        Button incr_but = itemview.findViewById(R.id.incr_but);

        type_view.setText(getStringFromType(current.type));
        extra_view.setText(current.extra);
        count_view.setText(String.format(mainActivity.getResources().getConfiguration().locale, "%d", current.count));
        price_view.setText(String.format(mainActivity.getResources().getConfiguration().locale, "%.1f0", current.price));

        decr_but.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                current.count--;
                count_view.setText(String.format(mainActivity.getResources().getConfiguration().locale, "%d", current.count));
                if(current.count==0) decr_but.setEnabled(false);
            }
        });
        incr_but.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(current.count==0) decr_but.setEnabled(true);
                current.count++;
                count_view.setText(String.format(mainActivity.getResources().getConfiguration().locale, "%d", current.count));
            }
        });

        return itemview;
    }
    @Override
    public int getCount(){
        return order.products.size();
    }
}

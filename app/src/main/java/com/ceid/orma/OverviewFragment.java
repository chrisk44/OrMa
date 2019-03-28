package com.ceid.orma;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.ceid.orma.MainActivity.r;
import static com.ceid.orma.Order.Type.TYPE_GIN;
import static com.ceid.orma.Order.Type.TYPE_LIQUOR;
import static com.ceid.orma.Order.Type.TYPE_RUM;
import static com.ceid.orma.Order.Type.TYPE_VODKA;
import static com.ceid.orma.Order.Type.TYPE_WHISKEY;

public class OverviewFragment extends Fragment{
    static final int STYLE_INSIDE = 0, STYLE_OUTSIDE = 1, STYLE_BOTH = 2, STYLE_ICONS = 3;
    static final int table_icon_style = STYLE_INSIDE;

    View fragmentView;
    ArrayList<Table> tables;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        setRetainInstance(true);
        fragmentView = inflater.inflate(R.layout.overview_layout, container, false);
        tables = new ArrayList<>();

        // Normally would build the overview here

        // For testing
        tables.add(new Table("A1", 3, fragmentView.findViewById(R.id.tb1), getRandomOrder()));
        tables.add(new Table("A2", 4, fragmentView.findViewById(R.id.tb2), getRandomOrder()));
        tables.add(new Table("A3", 3, fragmentView.findViewById(R.id.tb22), getRandomOrder()));
        tables.add(new Table("B1", 3, fragmentView.findViewById(R.id.tb3), getRandomOrder()));
        tables.add(new Table("B2", 4, fragmentView.findViewById(R.id.tb4), getRandomOrder()));
        tables.add(new Table("B3", 3, fragmentView.findViewById(R.id.tb42), getRandomOrder()));
        tables.add(new Table("C1", 5, fragmentView.findViewById(R.id.tb5), getRandomOrder()));
        tables.add(new Table("D1", 5, fragmentView.findViewById(R.id.tb6), getRandomOrder()));

        for(Table t : tables){
            switch(r.nextInt()%4){
                case 0:
                    t.setStatus(Status.TABLE_CALLING);
                    break;

                case 1:
                    t.setStatus(Status.TABLE_ATTENTION);
                    break;

                case 2:
                    t.setStatus(Status.TABLE_RESERVED);
                    break;

                case 3:
                    t.setStatus(Status.TABLE_TO_PAY);
                    break;
            }
        }

        return fragmentView;
    }

    // For testing
    Order getRandomOrder(){
        Order ord = new Order();

        Order.Type item_types[] = {
                TYPE_VODKA,
                TYPE_GIN,
                TYPE_WHISKEY,
                TYPE_LIQUOR,
                TYPE_RUM
        };
        String extras[] = {
                "",
                "No ice",
                "1 ice",
                "Cola",
                "Lemon"
        };
        int count = 1 + Math.abs(r.nextInt()%7);
        for(int j=0; j<count; j++){
            ord.addItem(
                    new Product(
                            item_types[Math.abs(r.nextInt()%item_types.length)],
                            extras[Math.abs(r.nextInt()%extras.length)],
                            1+Math.abs(r.nextInt()%3),
                            4+Math.abs(r.nextInt()%5)
                    )
            );
        }

        return ord;
    }
}

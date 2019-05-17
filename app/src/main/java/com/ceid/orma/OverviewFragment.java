package com.ceid.orma;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

import static com.ceid.orma.MainActivity.r;


public class OverviewFragment extends Fragment{
    View fragmentView;
    ConstraintLayout mainLayout;
    ArrayList<Table> tables;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        setRetainInstance(true);
        fragmentView = inflater.inflate(R.layout.overview_layout, container, false);
        mainLayout = fragmentView.findViewById(R.id.main_overview_layout);
        tables = new ArrayList<>();

        // Normally would build the overview on mainLayout here

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

        Product.ProductType item_types[] = {
                Product.ProductType.PRODUCT_FOOD,
                Product.ProductType.PRODUCT_DRINK,
                Product.ProductType.PRODUCT_OTHER
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
                            "id", "description",
                            extras[Math.abs(r.nextInt()%extras.length)],
                            4+Math.abs(r.nextInt()%5),
                            item_types[Math.abs(r.nextInt()%item_types.length)],
                            false,
                            0.0
                    )
            );
        }

        return ord;
    }
}

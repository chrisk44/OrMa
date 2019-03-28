package com.ceid.orma;

import java.util.ArrayList;

class Order{
    enum Type {
        TYPE_VODKA,
        TYPE_GIN,
        TYPE_WHISKEY,
        TYPE_LIQUOR,
        TYPE_RUM
    }
    static String getStringFromType(Type type){
        switch(type){
            case TYPE_VODKA:
                return "Stolichnaya";

            case TYPE_GIN:
                return "Gordon's Gin";

            case TYPE_LIQUOR:
                return "Jagermeister";

            case TYPE_RUM:
                return "Bacardi";

            case TYPE_WHISKEY:
                return "Jack Daniel's";
        }

        return null;
    }

    ArrayList<Product> items = new ArrayList<>();

    Order(){}

    void addItem(Product item){
        items.add(item);
    }

    float getTotal(){
        float total = 0.0f;

        for(Product i : items){
            total += i.getPrice();
        }

        return total;
    }

}


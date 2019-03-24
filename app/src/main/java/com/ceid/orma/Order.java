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
                return "Vodka";

            case TYPE_GIN:
                return "Gin";

            case TYPE_LIQUOR:
                return "Liquor";

            case TYPE_RUM:
                return "Rum";

            case TYPE_WHISKEY:
                return "Whiskey";
        }

        return null;
    }

    ArrayList<OrderItem> items = new ArrayList<>();

    Order(){
    }

    void addItem(OrderItem item){
        items.add(item);
    }

    float getTotal(){
        float total = 0.0f;

        for(OrderItem i : items){
            total += i.getPrice();
        }

        return total;
    }

}

class OrderItem{

    int count;
    float price_per_item;
    Order.Type type;
    String extra;

    OrderItem(Order.Type type, String extra, int count, float price_per){
        this.type = type;
        this.extra = extra;
        this.count = count;
        this.price_per_item = price_per;
    }

    float getPrice(){ return count*price_per_item; }
}

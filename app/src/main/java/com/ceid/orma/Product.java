package com.ceid.orma;

class Product{
    int count;
    float price_per_item;
    Order.Type type;
    String extra;

    Product(Order.Type type, String extra, int count, float price_per){
        this.type = type;
        this.extra = extra;
        this.count = count;
        this.price_per_item = price_per;
    }

    float getPrice(){ return count*price_per_item; }
}

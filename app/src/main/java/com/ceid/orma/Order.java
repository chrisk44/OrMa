package com.ceid.orma;

import java.util.ArrayList;
import java.util.Date;

class Order{
    private long id;
    private double balance;
    private Date create_time;
    private Date delivery_time;
    private Date pay_time;
    private Table table;
    ArrayList<Product> products = new ArrayList();
    private ArrayList<Boolean> product_paid = new ArrayList();
    //private ArrayList<Waiter> waiters = new ArrayList();
    //private PrepArea assigned_prepArea;

    Order(){}

    void addItem(Product item){
        products.add(item);
    }

    float getTotal(){
        float total = 0.0f;

        for(Product i : products){
            total += i.getPrice();
        }

        return total;
    }

}


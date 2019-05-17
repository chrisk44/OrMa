package com.ceid.orma;

class Product{
    enum ProductType{ PRODUCT_DRINK, PRODUCT_FOOD, PRODUCT_OTHER };

    double price;
    String id;
    String description;
    String extra;
    ProductType type;
    boolean has_alcohol;
    double time_to_prepare;

    int count = 1;

    Product(String id, String description, String extra, double price, ProductType type, boolean has_alcohol, double time_to_prepare){
        this.id = id;
        this.description = description;
        this.extra = extra;
        this.price = price;
        this.type = type;
        this.has_alcohol = has_alcohol;
        this.time_to_prepare = time_to_prepare;
    }

    double getPrice(){ return price; }

    static String getStringFromType(ProductType type){
        switch(type){
            case PRODUCT_DRINK:
                return "Drink";

            case PRODUCT_FOOD:
                return "Food";

            case PRODUCT_OTHER:
                return "Other";
        }

        return null;
    }
}

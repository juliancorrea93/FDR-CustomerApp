package com.correajulian.demo;

public class Item {
    public Item(String item_name, String item_price) {
        name = item_name;
        price = Float.parseFloat(item_price);
    }

    public String getPrice() {
        Float f = this.price;
        return f.toString();
    }

    public String getName() {
        return this.name;
    }

    private String name;
    private float price;
}

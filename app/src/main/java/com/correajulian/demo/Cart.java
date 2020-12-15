package com.correajulian.demo;

import java.util.ArrayList;

public class Cart {
    /**
     * Constructor for cart
     * @param items ArrayList comprised of items assumed to have at least one element
     * @param rest_name
     */
    public Cart(ArrayList<Item> items, String rest_name) {
        for (Item i : items) {
            items_ordered.add(i.getName());
            total.add(Float.parseFloat(i.getPrice()));
        }
        restaurant = rest_name;
    }

    /**
     * Converts the order to String
     * @return order string
     */
    public String getOrderString() {
        String tmp = "";
        for (String s : items_ordered) {
            tmp += s + ", ";
        }
        return tmp;
    }

    /**
     * Gets the grand total for the order
     * @return float value of grand total
     */
    public float getOrderTotal() {
        float tmp = 0;
        for (float f : total) {
            tmp += f;
        }
        return tmp;
    }

    /**
     * Gets restaurant name for the order
     * @return String with restaurant name
     */
    public String getRestaurantName() {
        return this.restaurant;
    }

    private ArrayList<String> items_ordered = new ArrayList<>();
    private ArrayList<Float> total = new ArrayList<>();
    private String restaurant;
}

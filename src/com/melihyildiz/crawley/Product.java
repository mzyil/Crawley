package com.melihyildiz.crawley;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by YILDIZ on 24.08.2016.
 */
public class Product {
    public String id;
    public String link;
    public ArrayList<String> options;
    public String imageLink;
    public String price;
    public String price2;
    public String description;
    public HashSet<Category> categories = new HashSet<>();
    /**
     * id is the last element of split("-")
     * options are in ul.colorSelect (as li elements)
     * imageLink is div.product>div.ye2-1>div.ye2-11>a>img
     * price is div.product(data-price)
     * price2 is (price * 100)/(div.gncl>span:last * 118)
     * description is the same as product name (may need to omit some words like best)
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof Product) && ((Product)o).id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

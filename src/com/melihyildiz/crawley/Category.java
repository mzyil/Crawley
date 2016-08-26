package com.melihyildiz.crawley;

import java.util.ArrayList;

/**
 * Created by YILDIZ on 24.08.2016.
 *
 */
public class Category extends Parsable {
    public String id;
    public String link;
    public String name;
    public ArrayList<String> products;

    public Category(String link) {
        this.link = link;
        this.id = link.substring(link.lastIndexOf('-') + 1);
        products = new ArrayList<>();
    }

    /**
     * title tag contains the name (split " | ")
     * .product items are product info containers (create Product objects)
     * .next contains the anchor for next page link (parse current and fetch next)
     */

    @Override
    public boolean equals(Object o) {
        return (o instanceof Category) && ((Category) o).id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public Runnable parse() {
        return () -> {

        };
    }
}

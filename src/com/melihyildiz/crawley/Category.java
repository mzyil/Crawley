package com.melihyildiz.crawley;

import java.util.HashSet;

/**
 * Created by YILDIZ on 24.08.2016.
 *
 */
public class Category {
    public String id;
    public String link;
    public String name;
    public HashSet<Product> products;
    public String htmlContent;

    public Category(String link) {
        this.link = link;
        this.id = link.substring(link.lastIndexOf('-') + 1);
    }

    /**
     * title tag contains the name (split " | ")
     * .product items are product info containers (create Product objects)
     * .next contains anchor for next page link (parse current and fetch next)
     */

    @Override
    public boolean equals(Object o) {
        return (o instanceof Category) && ((Category) o).id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

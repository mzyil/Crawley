package com.melihyildiz.crawley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by YILDIZ on 24.08.2016.
 */
public class Category extends Parsable implements Runnable {
    public String id;
    public String link;
//    public String name;
//    public int type = 0; // 0 marka - 1 kategori

    public Category(String link) {
        this.link = link;
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
        return this;
    }

    @Override
    public void run() {
        Document doc = null;
        /*****
         * first page
         *****
         */
        try {
            doc = Jsoup.connect(link).ignoreHttpErrors(true).timeout(0).get();
        } catch (IOException e) {
            System.out.println("Cannot be retrieved: " + link + " Cause: " + e.getMessage() + " and " + e.getLocalizedMessage());
            return;
        }
        Elements products = doc.select("div.product");
        products.stream().filter(product -> !Crawley.crawlData.containsKey(product.attr("data-id"))).forEach(product -> {
            Crawley.relation.add(new String[]{this.id, product.attr("data-id")});
            parentExecutor.execute(new Product(product).parse());
        });
        /*****
         * next pages
         *****
         */
        Elements next = doc.select("div.next");
        if (!next.isEmpty() && !next.first().select("a").isEmpty()) {
            String nextLink = Crawley.siteUrl + next.first().select("a").first().attr("href");
            Category category = new Category(nextLink);
            category.id = this.id;
            category.parentExecutor = this.parentExecutor;
            parentExecutor.execute(category.parse());
        } else {
            String pageCount = link.substring(link.lastIndexOf('/') + 1);
            if (pageCount.equals(this.id)) {
                String splitted[] = link.split("/");
                pageCount = splitted[splitted.length - 2];
            }
            System.out.println("Page Count: " + this.id + ", " + pageCount);
        }
    }
}

package com.melihyildiz.crawley;

import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Created by YILDIZ on 24.08.2016.
 */
public class Product extends Parsable implements Runnable {
    public String id;
    public Element htmlContent;
    public String link;
    public ArrayList<String> options;
    public String imageLink;
    public String price;
    public String price2;
    public String description;

    public Product(Element htmlContent) {
        this.htmlContent = htmlContent;
        this.id = htmlContent.attr("data-id");
    }
    /**
     * id is the last element of link.split("-")
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

    @Override
    public Runnable parse() {
        return this;
    }

    @Override
    public void run() {
        this.link = htmlContent.select("div.ye2-132").first().select("a").first().attr("href");
        Crawley.crawlData.put(id, this);
    }
}

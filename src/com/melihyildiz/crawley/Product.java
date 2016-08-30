package com.melihyildiz.crawley;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by YILDIZ on 24.08.2016.
 */
public class Product {
    public Element htmlContent;
    @Expose
    public String id;
    @Expose
    public String link;
    @Expose
    public ArrayList<String[]> options;
    @Expose
    public String imageLink;
    @Expose
    public String price;
    @Expose
    public String name;

    public void parseFields(Element htmlContent) {
        this.htmlContent = htmlContent;
        options = new ArrayList<>();
        this.id = htmlContent.attr("data-id");
        this.link = htmlContent.select("div.ye2-132").first().select("a").first().attr("href");
        Elements options = htmlContent.select("ul.colorSelect>li");
        this.options.addAll(options.stream().map(option -> new String[]{option.attr("data-value"), option.text()}).collect(Collectors.toList()));
        this.imageLink = htmlContent.select("div.product>div.ye2-1>div.ye2-11>a>img").first().attr("src");
        this.price = htmlContent.attr("data-price");
        this.name = htmlContent.select("div.ye2-12>a").first().text();
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
        return (o instanceof Product) && ((Product) o).id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return (new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()).toJson(this);
    }
}

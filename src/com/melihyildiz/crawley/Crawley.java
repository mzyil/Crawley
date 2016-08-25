package com.melihyildiz.crawley;

import sitemapper.parser.core.SiteMapParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by YILDIZ on 18.08.2016.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Crawley {
    private static Crawley ourInstance = new Crawley();
    private String siteUrl = "http://www.akilliphone.com/";
    private String storage = "akilli";
    private String robotName = "Crawley";
    private HashSet<String> sitemaps = new HashSet<>();
    private Set<Category> categories = Collections.synchronizedSet(new HashSet<Category>());

    public Crawley() {
    }

    public static void main(String[] args) throws Exception {
        Crawley crawley = getInstance();
        crawley.getSitemaps();
        crawley.parseSitemaps();
        System.out.println("Done");
    }

    public static Crawley getInstance() {
        return ourInstance;
    }

    private void parseSitemaps() throws Exception {
        for (String sitemap : sitemaps) {
            String sitemapName = sitemap.substring(sitemap.lastIndexOf('/') + 1, sitemap.lastIndexOf('.')).replaceAll("\\d", "");
            if(!(sitemapName.equals("xmlmarkalar") && sitemap.equals("xmlkategoriler"))){
                continue;
            }
            String sitemapTempLoc = storage + File.separator + sitemap.substring(sitemap.lastIndexOf('/') + 1, sitemap.length());

            Fetcher fetcher = new Fetcher(sitemap);
            fetcher.run();
            String xmlContent = fetcher.result;
            PrintStream printStream = new PrintStream(new FileOutputStream(sitemapTempLoc));
            printStream.print(xmlContent);
            printStream.close();

            SiteMapParser parser = new SiteMapParser(sitemapTempLoc);
            categories.addAll(parser.getUrlSet().getSet().stream().map(category -> new Category(category.getLoc())).collect(Collectors.toList()));
        }
    }

    private void getSitemaps() {
        String txt = getTxt();
        String[] lines = txt.split("\n");
        for (String line : lines) {
            if (line.startsWith("Sitemap:")) {
                sitemaps.add(line.split(": ")[1]);
            }
        }
    }

    private String getTxt() {
        Fetcher fetcher = new Fetcher(siteUrl + "robots.txt");
        return fetcher.fetch();
    }
}

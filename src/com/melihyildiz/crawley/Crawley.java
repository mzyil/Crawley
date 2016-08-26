package com.melihyildiz.crawley;

import sitemapper.parser.beans.Url;
import sitemapper.parser.core.SiteMapParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by YILDIZ on 18.08.2016.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Crawley {
    private static Crawley ourInstance = new Crawley();
    private static Map<String, Parsable> crawlData = new ConcurrentHashMap<>();
    private String siteUrl;
    private String robotName;
    private HashSet<String> sitemaps;
    private ArrayList<String> initialCrawlQueue;
    private ExecutorService executorService;

    public Crawley() {
        siteUrl = "http://www.akilliphone.com/";
        robotName = "akilli";
        sitemaps = new HashSet<>();
        initialCrawlQueue = new ArrayList<>();
        executorService = Executors.newFixedThreadPool(50);
    }

    public static void main(String[] args) throws Exception {
        Crawley crawley = getInstance();
        crawley.getSitemaps();
        crawley.parseSitemaps();
        crawley.createTasks();
        System.out.println("Done");
    }

    public static Crawley getInstance() {
        return ourInstance;
    }

    private void createTasks() {
        for (String link : initialCrawlQueue) {
            Category category = new Category(link);
            category.parentExecutor = this.executorService;
            executorService.execute(category.parse());
        }
    }

    private void parseSitemaps() throws Exception {
        for (String sitemap : sitemaps) {
            String sitemapName = sitemap.substring(sitemap.lastIndexOf('/') + 1, sitemap.lastIndexOf('.')).replaceAll("\\d", "");
            if ((sitemapName.equals("xmlmarkalar") || sitemap.equals("xmlkategoriler"))) {
                String sitemapTempLoc = robotName + File.separator + sitemap.substring(sitemap.lastIndexOf('/') + 1, sitemap.length());

                Fetcher fetcher = new Fetcher(sitemap);
                fetcher.run();
                String xmlContent = fetcher.result;
                PrintStream printStream = new PrintStream(new FileOutputStream(sitemapTempLoc));
                printStream.print(xmlContent);
                printStream.close();

                SiteMapParser parser = new SiteMapParser(sitemapTempLoc);
                initialCrawlQueue.addAll(parser.getUrlSet().getSet().stream().map(Url::getLoc).collect(Collectors.toList()));
            }
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

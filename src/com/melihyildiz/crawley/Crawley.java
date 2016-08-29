package com.melihyildiz.crawley;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import sitemapper.parser.beans.Url;
import sitemapper.parser.core.SiteMapParser;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by YILDIZ on 18.08.2016.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Crawley extends ThreadPoolExecutor {
    private static final int maxThread = 50;
    public static Map<String, Parsable> crawlData = new ConcurrentHashMap<>();
    public static List<String[]> relation = Collections.synchronizedList(new ArrayList<>());
    public static String siteUrl = "http://www.akilliphone.com/";
    private static Crawley ourInstance = new Crawley();
    private int executing = 0;
    private String robotName;
    private HashSet<String> sitemaps;
    private ArrayList<String> initialCrawlQueue;
    private ExecutorService executorService;

    public Crawley() {
        super(maxThread, maxThread, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        robotName = "akilli";
        sitemaps = new HashSet<>();
        initialCrawlQueue = new ArrayList<>();
        executorService = this;
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Crawley crawley = getInstance();
        crawley.getSitemaps();
        crawley.parseSitemaps();
        crawley.executeTasks();
        crawley.waitForExecuted();
        crawley.saveData();
        System.out.println("Done. In: " + (System.currentTimeMillis() - start) + " seconds.");
    }

    public static Crawley getInstance() {
        return ourInstance;
    }

    private void saveData() throws IOException {
        Gson factory = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        factory.toJson(crawlData, new FileWriter(robotName + "/products.json"));
        factory.toJson(relation, new FileWriter(robotName + "/realation.json"));
    }

    private void executeTasks() {
        int inc = 0;
        for (String link : initialCrawlQueue) {
            String id = link.substring(link.lastIndexOf('-') + 1);
            String listLink = Crawley.siteUrl + "/productlist/index/" + id + "/0";
            int type = link.contains("/markalar/") ? 0 : 1;
            if (type == 0) {
                listLink = Crawley.siteUrl + "/productlist/brandlist/" + id + "/0/" + id;
            }
            Category category = new Category(listLink);
            category.parentExecutor = this.executorService;
            category.id = id;
            executorService.execute(category.parse());
            if (inc < 3) {
                inc++;
            } else {
                break;
            }
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

    @Override
    public synchronized void execute(Runnable command) {
        executing++;
        super.execute(command);
    }


    @Override
    protected synchronized void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        executing--;
        notifyAll();
    }

    public synchronized void waitForExecuted() throws InterruptedException {
        while (executing > 0)
            wait();
        this.shutdown();
    }
}

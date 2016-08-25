package com.melihyildiz.crawley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("StatementWithEmptyBody")
class Fetcher implements Runnable {
    private static boolean parsingComplete = true;
    private static int instanceCount = 0;

    String result;
    private String urlAddress;

    Fetcher(String urlAddress) {
        int maxInstanceCount = 50;
        while(getInstanceCount() >= maxInstanceCount){
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        this.urlAddress = urlAddress;
        incInstanceCount();
    }

    public void run() {
        URL u;
        StringBuilder resultB = new StringBuilder();
        try {
            u = new URL(urlAddress);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(u.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                resultB.append(inputLine);
                resultB.append('\n');
            }
            in.close();
            result = resultB.toString();
            parsingComplete = false;
        } catch (MalformedURLException mue) {
            System.out.println("Ouch - a MalformedURLException happened.");
            mue.printStackTrace();
            System.exit(1);
        } catch (IOException ioe) {
            System.out.println("Oops- an IOException happened.");
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    String fetch() {
        Thread fetcherThread = new Thread(this);
        fetcherThread.start();
        while (parsingComplete) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
        decInstanceCount();
        return result;
    }

    private synchronized void incInstanceCount() {
        instanceCount++;
        notify();
    }

    private synchronized void decInstanceCount() {
        instanceCount--;
        notify();
    }

    private synchronized int getInstanceCount() {
        return instanceCount;
    }
}
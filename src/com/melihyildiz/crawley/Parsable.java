package com.melihyildiz.crawley;

import java.util.concurrent.ExecutorService;

/**
 * Created by YILDIZ on 26.08.2016.
 */
public abstract class Parsable {
    public ExecutorService parentExecutor;

    public abstract Runnable parse();
}

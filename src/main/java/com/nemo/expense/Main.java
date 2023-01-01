package com.nemo.expense;

import com.nemo.expense.api.Server;
import com.nemo.expense.dagger.Components;
import com.nemo.expense.dagger.DaggerComponents;
import com.nemo.expense.database.util.DatabaseFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Components component = DaggerComponents.create();
        doFirstTimeStuffs(component);
        log.info("Starting server....");
        Server server = component.getServer();
        server.prepareApiPath();
        server.start();
    }

    private static void doFirstTimeStuffs(Components component) {
        ForkJoinPool.commonPool().execute(() -> {
            DatabaseFiller databaseFiller = component.getDatabaseFiller();
            databaseFiller.fillDefaultCategoriesIfNeeded();
        });
    }
}

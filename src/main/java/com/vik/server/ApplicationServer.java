package com.vik.server;

import com.sun.net.httpserver.HttpServer;
import com.vik.Application;
import com.vik.common.Logger;
import com.vik.common.LoggerImpl;
import com.vik.server.handlers.*;
import com.vik.service.InitType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Main application server
 */
public class ApplicationServer {
    Logger logger = new LoggerImpl();
    public static boolean initialized = false;

    public void init(InitType initType) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            server.createContext("/owners", new OwnerHttpHandler());
            server.createContext("/accounts", new AccountsHttpHandler());
            server.createContext("/cards", new CardsHttpHandler());
            server.createContext("/cardsByOwner", new CardsByOwnerHttpHandler());
            server.createContext("/balance", new BalanceHttpHandler());
            server.createContext("/contractors", new ContractorsHttpHandler());
            server.createContext("/contractorsIncome", new ContractorIncomeHttpHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();
            logger.write("Server started on port 8080");
            initialized = true;
            if (initType == InitType.DEV) {
                Application.application();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            logger.write("Server start error");
        }
    }
}

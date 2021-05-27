package com.vik.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.vik.common.Logger;
import com.vik.common.LoggerImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BankHttpHandler implements HttpHandler {
    Logger logger = new LoggerImpl();

    @Override
    public void handle(HttpExchange exchange){
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetRequest(exchange);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                handlePostRequest(exchange);
            }
        } catch (IOException e) {
            logger.write("IO Exception");
            try {
                errorMessage(exchange);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void errorMessage(HttpExchange exchange) throws IOException {
        String resultOut = "Wrong json object format";
        OutputStream out = exchange.getResponseBody();
        exchange.sendResponseHeaders(400, resultOut.length());
        out.write(resultOut.getBytes(StandardCharsets.UTF_8));
        out.flush();

        out.close();
    }

    public abstract void handleGetRequest(HttpExchange exchange) throws IOException;

    public abstract void handlePostRequest(HttpExchange exchange) throws IOException;
}

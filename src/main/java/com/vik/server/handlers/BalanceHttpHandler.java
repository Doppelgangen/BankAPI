package com.vik.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.vik.common.QueryMapper;
import com.vik.dao.AccountDAO;
import com.vik.dao.AccountDAOImpl;
import com.vik.models.Account;
import com.vik.models.Income;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handler for /balance. Receives an object JSON Income {"id":long, "balance":BigDecimal, "income":BigDecimal}.
 * If received format is different returns String "Wrong json object format" with code 400.
 */
public class BalanceHttpHandler extends BankHttpHandler {
    AccountDAO accountDAO = new AccountDAOImpl();

    /**
     * Returns the balance of an account provided by id, if id is incorrect or is absent in DB returns String "Wrong id"
     *
     * @param exchange id = long
     * @throws IOException
     */
    @Override
    public void handleGetRequest(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryMapper.queryToMap(exchange.getRequestURI().getQuery());
        ObjectMapper objectMapper = new ObjectMapper();
        Income income = new Income();
        income.setId(Long.parseLong(params.get("id")));
        String resultOut = "Wrong id";

        Account account = accountDAO.getAccountById(income.getId());

        if (account.getId() != 0L) {
            income.setCurrentBalance(account.getBalance());
            income.setAccountNumber(account.getAccNumber());
            resultOut = objectMapper.writeValueAsString(income);
        }

        OutputStream out = exchange.getResponseBody();
        exchange.sendResponseHeaders(200, resultOut.length());
        out.write(resultOut.getBytes(StandardCharsets.UTF_8));
        out.flush();

        out.close();
    }

    /**
     * Adds an income to the balance of an account
     * Parameter authentication should be valid to process operation
     * @param exchange parameters:"authentication"="token" body: Income{"id":long, "income":BigDecimal}
     * @throws IOException
     */
    @Override
    public void handlePostRequest(HttpExchange exchange) throws IOException {
        String resultOut = "Wrong id";
        InputStream requestBody = exchange.getRequestBody();

        Map<String, String> params = QueryMapper.queryToMap(exchange.getRequestURI().getQuery());
        if (params == null) {
            resultOut = "Invalid parameters";
            exchange.sendResponseHeaders(401, resultOut.length());
        } else {
            String confirmation = params.get("authentication");

            if (confirmation.equals("token")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Income income = objectMapper.readValue(requestBody, Income.class);
                if (income.getIncome().compareTo(new BigDecimal(0)) <= 0) {
                    resultOut = "Income should be positive";
                    exchange.sendResponseHeaders(401, resultOut.length());
                }

                Account account = accountDAO.getAccountById(income.getId());
                if (account.getId() != 0L && (income.getIncome().compareTo(new BigDecimal(0)) > 0)) {
                    accountDAO.addToBalance(account.getAccNumber(), income.getIncome());
                    account = accountDAO.getAccountById(account.getId());
                    income.setCurrentBalance(account.getBalance());
                    income.setAccountNumber(account.getAccNumber());
                    resultOut = objectMapper.writeValueAsString(income);
                    exchange.sendResponseHeaders(200, resultOut.length());
                } else if (account.getId() == 0L) {
                    exchange.sendResponseHeaders(404, resultOut.length());
                }
            } else {
                resultOut = "Not confirmed";
                exchange.sendResponseHeaders(401, resultOut.length());
            }
        }
        OutputStream out = exchange.getResponseBody();
        out.write(resultOut.getBytes(StandardCharsets.UTF_8));
        out.flush();

        requestBody.close();
        out.close();
    }
}

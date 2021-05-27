package com.vik.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.vik.common.QueryMapper;
import com.vik.dao.AccountDAO;
import com.vik.dao.AccountDAOImpl;
import com.vik.dao.OwnerDAO;
import com.vik.dao.OwnerDAOImpl;
import com.vik.models.Account;
import com.vik.models.Income;
import com.vik.models.Owner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handler for /contractorsIncome. Receives an object Account {"id": long}
 * If received format is different returns String "Wrong json object format" with code 400.
 */
public class ContractorIncomeHttpHandler extends BankHttpHandler {

    /**
     * Returns contractors for an owner by his id
     *
     * @param exchange Owner{"id" : long}
     * @throws IOException
     */
    @Override
    public void handleGetRequest(HttpExchange exchange) throws IOException {
        new ContractorsHttpHandler().handleGetRequest(exchange);
    }

    /**
     * Adds income to first account of a contractor by his id
     * Parameter authentication should be valid to process operation
     * @param exchange parameters:"authentication"="token" body: Income{"id" (of owner) : long, "income" : BigDecimal}
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
                if (income.getIncome().compareTo(new BigDecimal("0")) > 0) {
                    OwnerDAO ownerDAO = new OwnerDAOImpl();
                    AccountDAO accountDAO = new AccountDAOImpl();
                    Owner contractor = ownerDAO.getOwnerById(income.getId());
                    if (ownerDAO.isOwnerInDB(contractor)) {
                        accountDAO.getAccountsOnOwner(contractor);
                        if (!contractor.getAccounts().isEmpty()) {
                            accountDAO.addToBalance(contractor.getAccounts().get(0).getAccNumber(), income.getIncome());
                            Account account = accountDAO.getAccountById(contractor.getAccounts().get(0).getId());
                            resultOut = objectMapper.writeValueAsString(account.getOwner());
                            exchange.sendResponseHeaders(200, resultOut.length());
                        } else {
                            resultOut = "Accounts not found";
                            exchange.sendResponseHeaders(404, resultOut.length());
                        }
                    } else {
                        exchange.sendResponseHeaders(404, resultOut.length());
                    }
                } else {
                    resultOut = "Income should be positive";
                    exchange.sendResponseHeaders(400, resultOut.length());
                }
            } else {
                resultOut = "Not confirmed";
                exchange.sendResponseHeaders(401, resultOut.length());
            }
        }
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(resultOut.getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        requestBody.close();
        outputStream.close();
    }
}

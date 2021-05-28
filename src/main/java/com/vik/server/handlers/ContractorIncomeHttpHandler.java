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
     * Transfer funds from owner's account with enough balance to contractor's first account
     * @param exchange parameters:"authentication"="token" body:
     *                 Income{"id" (of owner) : long, "target"(id of contractor):long, "income" : BigDecimal}
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

//            Check for authentication
            if (confirmation.equals("token")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Income income = objectMapper.readValue(requestBody, Income.class);
//                Check for positive transfer
                if (income.getIncome().compareTo(new BigDecimal("0")) > 0) {
                    OwnerDAO ownerDAO = new OwnerDAOImpl();
                    AccountDAO accountDAO = new AccountDAOImpl();
                    Owner owner = ownerDAO.getOwnerById(income.getId());
                    Owner contractor = ownerDAO.getOwnerById(income.getTarget());
//                    Check if contractor and owner in DB
                    if (ownerDAO.isOwnerInDB(contractor) && ownerDAO.isOwnerInDB(owner)) {
                        accountDAO.getAccountsOnOwner(owner);
                        accountDAO.getAccountsOnOwner(contractor);
//                        Check if contractor and owner have accounts
                        if (!contractor.getAccounts().isEmpty() && !owner.getAccounts().isEmpty()) {
                            int sourceAccountId = -1;
//                            Check if owner have an account with enough balance
                            for (int i = 0; i < owner.getAccounts().size(); i++){
                                if (owner.getAccounts().get(i).getBalance().compareTo(income.getIncome()) > 0){
                                    sourceAccountId = i;
                                    break;
                                }
                            }
                            if (sourceAccountId == -1)
                            {
                                resultOut = "Not enough funds for transfer";
                                exchange.sendResponseHeaders(403, resultOut.length());
                            } else {
                                accountDAO
                                        .transfer(owner.getAccounts().get(sourceAccountId)
                                                , contractor.getAccounts().get(0), income.getIncome());
                                Account account = accountDAO.getAccountById(contractor.getAccounts().get(0).getId());
                                resultOut = objectMapper.writeValueAsString(account.getOwner());
                                exchange.sendResponseHeaders(200, resultOut.length());
                            }
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
                exchange.sendResponseHeaders(400, resultOut.length());
            }
        }
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(resultOut.getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        requestBody.close();
        outputStream.close();
    }
}

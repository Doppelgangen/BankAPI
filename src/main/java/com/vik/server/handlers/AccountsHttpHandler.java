package com.vik.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.vik.common.QueryMapper;
import com.vik.dao.AccountDAOImpl;
import com.vik.dao.OwnerDAOImpl;
import com.vik.models.Account;
import com.vik.models.Owner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handler for /accounts. Receives an object Owner {"id": long, "name": String}
 * If received format is different returns String "Wrong json object format" with code 400.
 */
public class AccountsHttpHandler extends BankHttpHandler {
    OwnerDAOImpl ownerDAO = new OwnerDAOImpl();
    AccountDAOImpl accountDAO = new AccountDAOImpl();

    /**
     * Returns account by account's data starting from its owner, if id 0 or not found returns String "Wrong id"
     *
     * @param exchange id = long
     * @throws IOException
     */
    @Override
    public void handleGetRequest(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryMapper.queryToMap(exchange.getRequestURI().getQuery());
        ObjectMapper objectMapper = new ObjectMapper();
        Account account = new Account();
        String resultOut = "";

        if (params == null) {
            resultOut = "Parameters not set";
            exchange.sendResponseHeaders(400, resultOut.length());

//            Check parameters for id
        } else {
            boolean isIdSet = (params.get("id") != null) && !params.get("id").equals("");
            boolean isAccNumberSet = (params.get("accNumber") != null) && !params.get("accNumber").equals("");

            if (isIdSet) {
                account.setId(Long.parseLong(params.get("id")));
//                Check account in DB by ID
                if (accountDAO.isAccountInDB(account)) {
                    account = accountDAO.getAccountById(account.getId());
//                    returns owner
                    resultOut = objectMapper.writeValueAsString(account.getOwner());
                    exchange.sendResponseHeaders(200, resultOut.length());

//                  Sets response if id not found
                } else {
                    resultOut = "Wrong id";
                    exchange.sendResponseHeaders(404, resultOut.length());
                }

//                  Checks account in DB by account number
            } else if (isAccNumberSet) {
                account.setAccNumber(params.get("accNumber"));
                account = accountDAO.getAccountByAccNumber(account.getAccNumber(), false);

//                if an account is not found
                if (account.getId() == 0L) {
                    resultOut = "Wrong account number";
                    exchange.sendResponseHeaders(404, resultOut.length());
                } else {
//                if an account found returns it's owner
                    resultOut = objectMapper.writeValueAsString(account.getOwner());
                    exchange.sendResponseHeaders(200, resultOut.length());
                }
            } else {
                resultOut = "Wrong parameters";
                exchange.sendResponseHeaders(400, resultOut.length());
            }
        }
//        Check for parameters

        OutputStream out = exchange.getResponseBody();
        out.write(resultOut.getBytes(StandardCharsets.UTF_8));
        out.flush();

        out.close();
    }

    /**
     * Creates an account for provided JSON owner by his id, if id is absent in DB returns String "Wrong id"
     *
     * @param exchange Owner{"id":long, "name":String}
     * @throws IOException
     */
    @Override
    public void handlePostRequest(HttpExchange exchange) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream requestBody = exchange.getRequestBody();
        Owner owner = objectMapper.readValue(requestBody, Owner.class);

        String resultOut = "Wrong id";
        if (ownerDAO.isOwnerInDB(owner)) {
            Account account = accountDAO.createNewAccountByOwner(owner);
            resultOut = objectMapper.writeValueAsString(account.getOwner());
            exchange.sendResponseHeaders(200, resultOut.length());
        } else {
            exchange.sendResponseHeaders(404, resultOut.length());
        }

        OutputStream out = exchange.getResponseBody();
        out.write(resultOut.getBytes(StandardCharsets.UTF_8));
        out.flush();

        requestBody.close();
        out.close();
    }
}

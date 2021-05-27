package com.vik.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.vik.common.QueryMapper;
import com.vik.dao.AccountDAO;
import com.vik.dao.AccountDAOImpl;
import com.vik.dao.CardDAO;
import com.vik.dao.CardDAOImpl;
import com.vik.models.Account;
import com.vik.models.Card;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handler for /cards. Receives an object Account {"id": long}
 * If received format is different returns String "Wrong json object format" with code 400.
 */
public class CardsHttpHandler extends BankHttpHandler {
    CardDAO cardDAO = new CardDAOImpl();
    AccountDAO accountDAO = new AccountDAOImpl();

    /**
     * Returns filled card by provided card's id
     *
     * @param exchange id = long
     * @throws IOException
     */
    @Override
    public void handleGetRequest(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryMapper.queryToMap(exchange.getRequestURI().getQuery());
        ObjectMapper objectMapper = new ObjectMapper();
        Card card = new Card();
        card.setId(Long.parseLong(params.get("id")));
        String resultOut = "Wrong id";
        if (cardDAO.isCardInDB(card)) {
            card = cardDAO.getCardById(card.getId());
            resultOut = objectMapper.writeValueAsString(card.getAccount().getOwner());
            exchange.sendResponseHeaders(200, resultOut.length());
        } else {
            exchange.sendResponseHeaders(404, resultOut.length());
        }

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(resultOut.getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        outputStream.close();
    }

    /**
     * Publishes a new card for provided account and returns filled account with new card,
     * if account id is not in db returns String "Wrong id".
     *
     * @param exchange Account{"id":long}
     * @throws IOException
     */
    @Override
    public void handlePostRequest(HttpExchange exchange) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream requestBody = exchange.getRequestBody();
        Account account = objectMapper.readValue(requestBody, Account.class);
        String resultOut = "Wrong id";
        if (accountDAO.isAccountInDB(account)) {
            Card card = cardDAO.publishNewCard(account);
            resultOut = objectMapper.writeValueAsString(card);
            exchange.sendResponseHeaders(200, resultOut.length());
        } else {
            exchange.sendResponseHeaders(404, resultOut.length());
        }
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(resultOut.getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        requestBody.close();
        outputStream.close();
    }
}
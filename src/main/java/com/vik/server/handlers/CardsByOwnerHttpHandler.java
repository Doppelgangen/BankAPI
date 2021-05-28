package com.vik.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.vik.common.QueryMapper;
import com.vik.dao.CardDAO;
import com.vik.dao.CardDAOImpl;
import com.vik.dao.OwnerDAO;
import com.vik.dao.OwnerDAOImpl;
import com.vik.models.Card;
import com.vik.models.Owner;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Handler for /cardsByOwner. Receives an object Owner {"id": long}
 * If received format is different returns String "Wrong json object format" with code 400.
 */
public class CardsByOwnerHttpHandler extends BankHttpHandler {
    OwnerDAO ownerDAO = new OwnerDAOImpl();
    CardDAO cardDAO = new CardDAOImpl();

    /**
     * Returns list of all cards by provided owner's id
     *
     * @param exchange id = long
     * @throws IOException
     */
    @Override
    public void handleGetRequest(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryMapper.queryToMap(exchange.getRequestURI().getQuery());
        ObjectMapper objectMapper = new ObjectMapper();
        Owner owner = new Owner();
        owner.setId(Long.parseLong(params.get("id")));
        String resultOut = "Wrong id";
        if (ownerDAO.isOwnerInDB(owner)) {
            owner = ownerDAO.getOwnerById(owner.getId());
            List<Card> cards = cardDAO.getAllCardsOfOwner(owner);
            resultOut = objectMapper.writeValueAsString(cards);
            exchange.sendResponseHeaders(200, resultOut.length());
        } else {
            exchange.sendResponseHeaders(404, resultOut.length());
        }

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(resultOut.getBytes(StandardCharsets.UTF_8));

        outputStream.flush();
        outputStream.close();
    }

    @Override
    public void handlePostRequest(HttpExchange exchange) throws IOException {
    }
}
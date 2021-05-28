package com.vik.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.vik.common.QueryMapper;
import com.vik.dao.AccountDAOImpl;
import com.vik.dao.OwnerDAOImpl;
import com.vik.models.Owner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Handler for /owners. Receives an object Owner {"id":long, "name":String}
 * If received format is different returns String "Wrong json object format" with code 400.
 */
public class OwnerHttpHandler extends BankHttpHandler {
    OwnerDAOImpl ownerDAO = new OwnerDAOImpl();
    AccountDAOImpl accountDAO = new AccountDAOImpl();

    /**
     * Reads an id and returns an owner with this id,
     * If id is set to 0: get all owners,
     * If id is absent but name is set returns owner with this name
     * In case if id is absent in DB returns String "Wrong id"
     *
     * @param exchange id = long
     * @throws IOException
     */
    @Override
    public void handleGetRequest(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryMapper.queryToMap(exchange.getRequestURI().getQuery());
        ObjectMapper objectMapper = new ObjectMapper();
        Owner owner = new Owner();
        String resultOut = "";
        if (params == null) {
            resultOut = "No parameters";
            exchange.sendResponseHeaders(400, resultOut.length());
        } else {
            boolean idIsSet = (params.get("id") != null) && !params.get("id").equals("");
            boolean nameIsSet = (params.get("name") != null) && !params.get("name").equals("");

//        Check for parameters
            if (idIsSet) {
                long l = Long.parseLong(params.get("id"));
                owner.setId(l);
            } else if (nameIsSet) {
                owner.setName(params.get("name"));
            } else {
                resultOut = "Parameters not set";
                exchange.sendResponseHeaders(400, resultOut.length());
            }

//        Check if a parameter id is set to 0 to display all owners
            if (idIsSet) {
                if (owner.getId() == 0L) {
                    List<Owner> owners = ownerDAO.getAllOwners();
                    resultOut = objectMapper.writeValueAsString(owners);
                    exchange.sendResponseHeaders(200, resultOut.length());
                }

//                If id is not 0 check DB for such id
                if (owner.getId() != 0L) {
                    if (ownerDAO.isOwnerInDB(owner)) {
                        owner = ownerDAO.getOwnerById(owner.getId());
                        accountDAO.getAccountsOnOwner(owner);
                        resultOut = objectMapper.writeValueAsString(owner);
                        exchange.sendResponseHeaders(200, resultOut.length());
                    } else {
                        resultOut = "Wrong id";
                        exchange.sendResponseHeaders(404, resultOut.length());
                    }
                }

//        Check DB by owner's name
            } else if (nameIsSet) {
                if (!owner.getName().equals("")) {
                    owner = ownerDAO.getOwnerByName(owner.getName());
                    if (owner.getId() == 0L) {
                        resultOut = "Wrong name";
                        exchange.sendResponseHeaders(404, resultOut.length());
                    } else {
                        accountDAO.getAccountsOnOwner(owner);
                        resultOut = objectMapper.writeValueAsString(owner);
                        exchange.sendResponseHeaders(200, resultOut.length());
                    }
                }
            }
        }
        OutputStream out = exchange.getResponseBody();
        out.write(resultOut.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

    /**
     * Creates a new owner with provided name
     * Returns JSON with owner
     * If owner's name is not unique then returns last occurrence in DB
     *
     * @param exchange Owner{"id":long, "name":String}
     * @throws IOException
     */
    @Override
    public void handlePostRequest(HttpExchange exchange) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream requestBody = exchange.getRequestBody();

        Owner owner = objectMapper.readValue(requestBody, Owner.class);
        if (ownerDAO.persistOwner(owner))
            owner = ownerDAO.getOwnerByName(owner.getName());

        String resultOut = objectMapper.writeValueAsString(owner);

        OutputStream out = exchange.getResponseBody();
        exchange.sendResponseHeaders(200, resultOut.length());
        out.write(resultOut.getBytes(StandardCharsets.UTF_8));
        out.flush();

        requestBody.close();
        out.close();
    }
}

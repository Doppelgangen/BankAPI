package com.vik.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.vik.common.QueryMapper;
import com.vik.dao.ContractorDAO;
import com.vik.dao.ContractorsDAOImpl;
import com.vik.dao.OwnerDAO;
import com.vik.dao.OwnerDAOImpl;
import com.vik.models.Contractor;
import com.vik.models.Owner;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Handler for /contractors. Receives an object Account {"id": long}
 * If received format is different returns String "Wrong json object format" with code 400.
 */
public class ContractorsHttpHandler extends BankHttpHandler {
    ContractorDAO contractorDAO = new ContractorsDAOImpl();
    OwnerDAO ownerDAO = new OwnerDAOImpl();

    /**
     * Returns contractors of owner
     *
     * @param exchange Owner {"id" :long}
     * @throws IOException
     */
    @Override
    public void handleGetRequest(HttpExchange exchange) throws IOException {
        Map<String, String> params = QueryMapper.queryToMap(exchange.getRequestURI().getQuery());
        ObjectMapper objectMapper = new ObjectMapper();
        String resultOut = "Wrong id";
        Contractor contractor = new Contractor();
        contractor.setOwner_id(Long.parseLong(params.get("id")));
        Owner owner = new Owner();
        owner.setId(contractor.getOwner_id());
        if (ownerDAO.isOwnerInDB(owner)) {
            contractor = contractorDAO.getContractorsByOwner(owner);
            resultOut = objectMapper.writeValueAsString(contractor);
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
     * For an owner ads a contractor
     *
     * @param exchange [long (owner_id), long(contractor_id)]
     * @throws IOException
     */
    @Override
    public void handlePostRequest(HttpExchange exchange) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        String resultOut = "Wrong id or contractor already added";
        InputStream requestBody = exchange.getRequestBody();

        List<Integer> ids = objectMapper.readValue(requestBody, List.class);
        Owner o1 = new Owner();
        o1.setId(ids.get(0).longValue());
        Owner o2 = new Owner();
        o2.setId(ids.get(1).longValue());
        if (contractorDAO.addContractor(o1, o2)) {
            Contractor contractor = contractorDAO.getContractorsByOwner(o1);
            resultOut = objectMapper.writeValueAsString(contractor);
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

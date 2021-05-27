package com.vik.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vik.dao.OwnerDAO;
import com.vik.dao.OwnerDAOImpl;
import com.vik.models.Owner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler {
    OwnerDAO ownerDAO = new OwnerDAOImpl();

    public String templateRequest(String Url, Long ownerId) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "1");
        Owner owner = ownerDAO.getOwnerById(ownerId);
        ObjectMapper mapper = new ObjectMapper();

        URL url = new URL(Url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("GET");
        con.setDoOutput(true);

        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        String par = getParametersString(parameters);
        out.writeBytes(par);

//        OutputStream out = con.getOutputStream();
//        mapper.writeValue(out, owner);
        out.flush();
        out.close();

        InputStream inputStream = con.getInputStream();
        owner = mapper.readValue(inputStream, Owner.class);
        String output = mapper.writeValueAsString(owner);
        inputStream.close();
        return output;
    }

    private String getParametersString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()){
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString  = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}

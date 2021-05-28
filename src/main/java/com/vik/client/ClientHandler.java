package com.vik.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vik.dao.OwnerDAO;
import com.vik.dao.OwnerDAOImpl;
import com.vik.models.Account;
import com.vik.models.Income;
import com.vik.models.Owner;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler {

    public String sendGetById(String URL, Long id) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URL);
        stringBuilder.append("?id=");
        stringBuilder.append(id);
        String fullURL = stringBuilder.toString();
        HttpClient httpClient = HttpClientBuilder.create().build();

        try {
            HttpGet request = new HttpGet(fullURL);
            request.addHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String sendGetWithParameter(String URL, String parameterName, String parameter) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URL);
        stringBuilder.append("?");
        stringBuilder.append(parameterName);
        stringBuilder.append("=");
        stringBuilder.append(parameter);
        String fullURL = stringBuilder.toString();
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpGet request = new HttpGet(fullURL);
            request.addHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String sendPostOwner(String URL, Long id, String name){
        Owner owner = new Owner();
        owner.setId(id);
        owner.setName(name);

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            ObjectMapper mapper = new ObjectMapper();
            HttpPost request = new HttpPost(URL);
            String JSON_STRING = mapper.writeValueAsString(owner);
            request.addHeader("content-type", "application/json");
            request.setEntity(new StringEntity(JSON_STRING, ContentType.APPLICATION_JSON));

            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String sendPostIncomeWithAuthentication(String URL, Long id, BigDecimal amount, String authentication){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(URL);
        stringBuilder.append("?authentication=");
        stringBuilder.append(authentication);
        String fullURL = stringBuilder.toString();
        Income income = new Income();
        income.setId(id);
        income.setIncome(amount);

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            ObjectMapper mapper = new ObjectMapper();
            HttpPost request = new HttpPost(URL);
            String JSON_STRING = mapper.writeValueAsString(income);
            request.addHeader("content-type", "application/json");
            request.setEntity(new StringEntity(JSON_STRING, ContentType.APPLICATION_JSON));

            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String sendPostAccount(String URL, Long id){
        Account account = new Account();
        account.setId(id);

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            ObjectMapper mapper = new ObjectMapper();
            HttpPost request = new HttpPost(URL);
            String JSON_STRING = mapper.writeValueAsString(account);
            request.addHeader("content-type", "application/json");
            request.setEntity(new StringEntity(JSON_STRING, ContentType.APPLICATION_JSON));

            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String sendPostListLong(String URL, Long id1, Long id2){
        List<Long> longs = new ArrayList<>();
        longs.add(id1);
        longs.add(id2);

        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            ObjectMapper mapper = new ObjectMapper();
            HttpPost request = new HttpPost(URL);
            String JSON_STRING = mapper.writeValueAsString(longs);
            request.addHeader("content-type", "application/json");
            request.setEntity(new StringEntity(JSON_STRING, ContentType.APPLICATION_JSON));

            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String postBeatify(String URL, Object object){
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            ObjectMapper mapper = new ObjectMapper();
            HttpPost request = new HttpPost(URL);
            String JSON_STRING = mapper.writeValueAsString(object);
            request.addHeader("content-type", "application/json");
            request.setEntity(new StringEntity(JSON_STRING, ContentType.APPLICATION_JSON));

            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

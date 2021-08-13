package com.lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.lucidworks.fusion.connector.exception.RequestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The class where the http requests are made
 */
@Slf4j
public class DrupalHttpClient {

    private final OkHttpClient client;
    private final OkHttpClient loginClient;
    private String cookie = "";

    public DrupalHttpClient() {
        this.client = new OkHttpClient.Builder()
            .followRedirects(true)
            .build();
        this.loginClient = new OkHttpClient.Builder()
            .followRedirects(false)
            .build();
    }

    /**
     * Get the content from an url and return it as a String
     *
     * @param url
     * @return The content from specified url
     */
    public String getContent(String url) {
        log.debug("Making content request to url=" + url);
        try {
            Request.Builder requestBuilder = new Request.Builder()
                .url(url);
            if(!Strings.isNullOrEmpty(cookie)){
                requestBuilder.addHeader("Cookie", cookie);
            }
            Request request = requestBuilder.build();

            Call call = client.newCall(request);
            try(Response response = call.execute()){
                if(response.isSuccessful()){
                    return response.body().string();
                }
                throw new RuntimeException("Unsuccessful content request to url=" + url + ", response status code=" + response.code());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while making content request to url=" + url, e);
        }
    }

    /**
     * Login request method
     *
     * @param url
     * @param username
     * @param password
     * @return
     */
    public String doLogin(String url, String username, String password) {
        log.info("Entering the login request method...");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String requestBody = objectMapper.writeValueAsString(prepareLoginBody(username, password));
            RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), requestBody);
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder()
                .addQueryParameter("_format", "json");
            Request request = new Request.Builder()
                .url(urlBuilder.build())
                .post(body)
                .build();
            Call call = loginClient.newCall(request);
            try(Response response = call.execute()){
                if(!response.isSuccessful() && response.code() != 302){
                    throw new RuntimeException("Unsuccessful request to url=" + url + " / status code=" + response.code());
                }
                String rawCookie = response.header("Set-Cookie");
                if(!Strings.isNullOrEmpty(rawCookie)){
                    cookie = rawCookie.split(";")[0];
                } else {
                    throw new RuntimeException("Authentication failed: no response cookie.");
                }
                return response.toString();
            }
        } catch (IOException e) {
            throw new RequestException("Error logging-in", e);
        }
    }

    private static Map<String, String> prepareLoginBody(String username, String password) {
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("name", username);
        loginBody.put("pass", password);
        return loginBody;
    }
}

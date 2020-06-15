package com.lucidworks.fusion.connector.service;

import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public class DrupalOkHttp {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient okHttpClient = new OkHttpClient();

    public DrupalOkHttp() {
    }

    public String getDrupalContent(String url, DrupalLoginResponse drupalLoginResponse) {
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/vnd.api+json")
                .addHeader("Authorization", "Bearer YAWrsYFxfCYqwyQi9yFgh_HtB-yy921SpCBUqailhuA")
                .build();

        try {
            Response response = okHttpClient.newCall(getRequest).execute();

            if (!response.isSuccessful()) {
                return null;
            }

            String responseBody = response.body().string();
            response.body().close();

            return responseBody;

        } catch (IOException exception) {
            return null;
        }
    }

    public ResponseBody login(String url, DrupalLoginRequest drupalLoginRequest) {
        RequestBody requestBody = RequestBody.create(drupalLoginRequest.getJson(), JSON);

        Request loginRequest = new Request.Builder()
                .url(url + "user/login?_format=json")
                .post(requestBody)
                .build();

        try {
            Response response = okHttpClient.newCall(loginRequest).execute();

            return response.body();
        } catch (IOException exception) {
            //wrong
        }

        return null;
    }
}

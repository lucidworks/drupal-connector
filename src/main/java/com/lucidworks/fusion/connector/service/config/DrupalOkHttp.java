package com.lucidworks.fusion.connector.service.config;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public class DrupalOkHttp {

    private final OkHttpClient okHttpClient = new OkHttpClient();

    public DrupalOkHttp() {}

    public ResponseBody getDrupalContent(String url) {
        Request getRequest = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = okHttpClient.newCall(getRequest).execute();

            if (!response.isSuccessful()) {
                return null;
            }

            return response.body();

        } catch (IOException exception) {
            return null;
        }
    }
}

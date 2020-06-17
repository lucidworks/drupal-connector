package com.lucidworks.fusion.connector.service;

import com.lucidworks.fusion.connector.exception.RequestException;
import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

@Slf4j
public class DrupalOkHttp {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient okHttpClient = new OkHttpClient();

    public DrupalOkHttp() {
    }

    /**
     * Get the content from the url provided
     *
     * @param url
     * @param drupalLoginResponse
     * @return
     */
    public String getDrupalContent(String url, DrupalLoginResponse drupalLoginResponse) {
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/vnd.api+json")
                //.addHeader("Authorization", drupalLoginResponse.getAuthorization())
                .build();

        try {
            Response response = okHttpClient.newCall(getRequest).execute();

            if (!response.isSuccessful()) {
                log.error("Unable to get the response from okHttpClient request!");
                return null;
            }

            String responseBody = response.body().string();
            response.body().close();

            return responseBody;

        } catch (IOException exception) {
            throw new RequestException("There was an error on getting the content from Drupal.", exception);
        }
    }

    /**
     * Login request
     *
     * @param url                The page where the login is requested
     * @param drupalLoginRequest Object that contains the user's credentials
     * @return
     */
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
            throw new RequestException("There was an error when trying to login the user: " + drupalLoginRequest.getName(), exception);
        }
    }
}

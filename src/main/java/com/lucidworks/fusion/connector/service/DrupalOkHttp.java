package com.lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.exception.RequestException;
import com.lucidworks.fusion.connector.exception.ServiceException;
import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

@Slf4j
public class DrupalOkHttp {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String FORMAT = "?_format=json";
    private static final String CRSF_TOKEN = "&crsf_token=";
    private OkHttpClient okHttpClient;
    private ObjectMapper mapper;

    public DrupalOkHttp(ObjectMapper objectMapper) {
        okHttpClient = new OkHttpClient.Builder().build();
        this.mapper = objectMapper;
    }

    /**
     * Get the content from the url provided
     *
     * @param url
     * @param drupalLoginResponse
     * @return
     */
    public String getDrupalContent(String url, DrupalLoginResponse drupalLoginResponse) {
        String cookies = drupalLoginResponse.getCookie() != null ? drupalLoginResponse.getCookie() : "";
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/vnd.api+json")
                .addHeader("Cookies", cookies)
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
    public DrupalLoginResponse loginResponse(String url, DrupalLoginRequest drupalLoginRequest) {
        RequestBody requestBody =
                FormBody.create(drupalLoginRequest.getJson(), JSON);

        String loginUrl = url + FORMAT;

        Request loginRequest = new Request.Builder()
                .url(loginUrl)
                .post(requestBody)
                .build();

        String loginResponse, cookie;
        try {
            Response response = okHttpClient.newCall(loginRequest).execute();

            loginResponse = response.body().string();
            cookie = response.headers().get("Set-Cookie").split(";")[0];
        } catch (IOException exception) {
            throw new RequestException("There was an error when trying to login the user: " + drupalLoginRequest.getName(), exception);
        }

        DrupalLoginResponse drupalLoginResponse;
        try {
            drupalLoginResponse = mapper.readValue(loginResponse, DrupalLoginResponse.class);
        } catch (IOException e) {
            throw new ServiceException("Failed to get the loginResponse from login request.", e);
        }
        drupalLoginResponse.setCookie(cookie);

        log.info("User: {} logged in", drupalLoginRequest.getName());
        return drupalLoginResponse;
    }

    /**
     * Logout function
     *
     * @param url
     * @param drupalLoginResponse
     * @return true if the code from logout request is 200 or 403
     */
    public boolean logout(String url, DrupalLoginResponse drupalLoginResponse) {
        String logoutUrl = url + FORMAT + CRSF_TOKEN + drupalLoginResponse.getCsrfToken();

        Request logoutRequest = new Request.Builder()
                .url(logoutUrl)
                .addHeader("Cookies", drupalLoginResponse.getCookie())
                .build();

        try {
            Response response = okHttpClient.newCall(logoutRequest).execute();
            //200 OK or 403 Forbidden is success
            return response.code() == 200 || response.code() == 403;
        } catch (IOException e) {
            throw new ServiceException("Failed to logout", e);
        }
    }
}

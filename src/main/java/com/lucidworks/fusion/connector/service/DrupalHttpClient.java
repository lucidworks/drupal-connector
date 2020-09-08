package com.lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.exception.RequestException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The class where the http requests are made
 */
@Slf4j
public class DrupalHttpClient {

    private String cookie = "";

    /**
     * Get the content from an url and return it as a String
     *
     * @param url
     * @return The content from specified url
     */
    public String getContent(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            boolean redirect = false;
            con.setRequestMethod("GET");

            if (!cookie.isEmpty()) {
                con.setRequestProperty("Cookie", cookie);
            }

            int responseCode = con.getResponseCode();

            // 3xx is redirect
            if (responseCode != HttpURLConnection.HTTP_OK) {
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                        || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                        || responseCode == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            if (redirect) {
                // get redirect url from "location" header field
                String newUrl = con.getHeaderField("Location");

                // open the new connection again
                con = (HttpURLConnection) new URL(newUrl).openConnection();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (IOException e) {
            return null;
            //TODO throw an error when it will be logged in Fusion Log Viewer
            //throw new RequestException("Error on the HTTP GET request that takes the content from a URL.", e);
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
            byte[] loginBody = requestBody.getBytes(StandardCharsets.UTF_8);

            URL obj = new URL(url + "?_format=json");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setFixedLengthStreamingMode(loginBody.length);

            // Send the request body
            try (DataOutputStream os = new DataOutputStream(con.getOutputStream())) {
                os.write(loginBody);
                os.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // Success
                // Read from the response
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader bf = new BufferedReader(new InputStreamReader(
                        con.getInputStream()))) {
                    String line;
                    while ((line = bf.readLine()) != null)
                        stringBuilder.append(line);
                }

                // Save cookie for future GET Requests
                cookie = con.getHeaderField("Set-Cookie") != null ?
                        con.getHeaderField("Set-Cookie").split(";")[0] : "";

                log.info("Successful login for user " + username);
                return stringBuilder.toString();
            } else {
                log.error("Unsuccessful login request, Status-Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            throw new RequestException("Error on the login request... ", e);
        }
    }

    private static Map<String, String> prepareLoginBody(String username, String password) {
        Map<String, String> loginBody = new HashMap<>();

        loginBody.put("name", username);
        loginBody.put("pass", password);

        return loginBody;
    }
}

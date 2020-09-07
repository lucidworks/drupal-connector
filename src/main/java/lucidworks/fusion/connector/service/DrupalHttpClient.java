package lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lucidworks.fusion.connector.exception.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class DrupalHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(DrupalHttpClient.class);

    private String cookie = "";

    /**
     * Get the content from url as String
     *
     * @param url
     * @return The content from specified url
     */
    public String getContent(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            if (!cookie.isEmpty()) {
                con.setRequestProperty("Cookie", cookie);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                logger.error("Error on the request getting the content. Status-Code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            throw new RequestException("Error on the HTTP GET request that takes the content from a URL.", e);
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
        logger.info("Entering the login request method...");
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

                logger.info("Successful login for user " + username);
                return stringBuilder.toString();
            } else {
                logger.error("Unsuccessful login request, Status-Code: " + responseCode);
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

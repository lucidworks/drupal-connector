package com.lucidworks.fusion.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalOkHttp;
import com.lucidworks.fusion.connector.service.DrupalUserService;

import java.util.Map;

public class Runner {

    public static void main(String[] args) {
        String baseUrl = "http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com";
        ObjectMapper mapper = new ObjectMapper();

        DrupalOkHttp drupalOkHttp = new DrupalOkHttp(mapper);
        ContentService contentService = new ContentService(mapper);
        DrupalUserService drupalUserService = new DrupalUserService(drupalOkHttp);

        DrupalLoginResponse drupalLoginResponse = drupalUserService.login(baseUrl + "/user/login", "authenticated", "authenticated");

        ConnectorService connectorService = new ConnectorService(baseUrl + "/en/fusion", new DrupalLoginResponse(), contentService, mapper);

        Map<String, String> response = connectorService.prepareDataToUpload();

        response.forEach((currentUrl, content) -> {
            System.out.println(currentUrl);
            //System.out.println(content);
        });

        System.out.println("Logout is successful: " + drupalUserService.logout(baseUrl + "/user/logout", drupalLoginResponse));
    }
}

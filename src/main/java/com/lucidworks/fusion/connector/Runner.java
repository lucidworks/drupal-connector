package com.lucidworks.fusion.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalOkHttp;

import java.util.Map;

public class Runner {

    public static void main(String[] args) {
        String baseUrl = "http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com";
        ObjectMapper mapper = new ObjectMapper();

        DrupalOkHttp drupalOkHttp = new DrupalOkHttp(mapper);
        ContentService contentService = new ContentService(mapper);

        DrupalLoginRequest drupalLoginRequest = new DrupalLoginRequest("authenticated", "authenticated");

        DrupalLoginResponse drupalLoginResponse = drupalOkHttp.loginResponse(baseUrl + "/user/login", drupalLoginRequest);

        ConnectorService connectorService = new ConnectorService(baseUrl + "/en/fusion/node/article", new DrupalLoginResponse(), contentService, mapper);

        Map<String, String> response = connectorService.prepareDataToUpload();

        Map<String, TopLevelJsonapi> topLevelJsonapiMap = contentService.getTopLevelJsonapiDataMap();

        topLevelJsonapiMap.forEach((url, data) -> {
            System.out.println(data.toString());
        });

        //System.out.println("Logout is successful: " + drupalOkHttp.logout(baseUrl + "/user/logout", drupalLoginResponse));
    }
}

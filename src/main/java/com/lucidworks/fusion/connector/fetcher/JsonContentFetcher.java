package com.lucidworks.fusion.connector.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.config.ContentConfig;
import com.lucidworks.fusion.connector.exception.ServiceException;
import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalOkHttp;
import com.lucidworks.fusion.connector.util.DataUtil;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fetch the content from Drupal's pages
 */
@Slf4j
public class JsonContentFetcher implements ContentFetcher {

    private final ContentConfig connectorConfig;
    private ContentService contentService;
    private ConnectorService connectorService;
    private ObjectMapper objectMapper;
    private DrupalOkHttp drupalOkHttp;
    private DrupalLoginResponse drupalLoginResponse;

    @Inject
    public JsonContentFetcher(
            ContentConfig connectorConfig
    ) {
        this.connectorConfig = connectorConfig;
        this.objectMapper = new ObjectMapper();
        this.drupalOkHttp = new DrupalOkHttp(objectMapper);
        this.contentService = new ContentService(objectMapper);
        this.drupalLoginResponse = getDrupalLoginResponse();
        this.connectorService = new ConnectorService(getDrupalContentEntryUrl(), this.drupalLoginResponse, contentService, objectMapper);
    }

    @Override
    public FetchResult fetch(FetchContext fetchContext) {

        Map<String, TopLevelJsonapi> topLevelJsonapiMap = new HashMap<>();
        Map<String, String> contentMap = new HashMap<>();

        try {
            contentMap = connectorService.prepareDataToUpload();

            topLevelJsonapiMap = contentService.getTopLevelJsonapiDataMap();

        } catch (ServiceException e) {
            String message = "Failed to parse content from Drupal!";
            log.error(message, e);
            fetchContext.newError(fetchContext.getFetchInput().getId())
                    .withError(message)
                    .emit();
        }

        if (contentMap.keySet().size() == topLevelJsonapiMap.keySet().size()) {

            Map<String, Object> objectMap = DataUtil.generateObjectMap(topLevelJsonapiMap);

            topLevelJsonapiMap.forEach((url, data) -> {
                fetchContext.newDocument(url)
                        .fields(field -> {
                            field.setString("url", url);
                            field.setLong("lastUpdated", ZonedDateTime.now().toEpochSecond());
                            field.merge(objectMap);
                        })
                        .emit();
            });
        } else {
            String message = "Failed to store all Drupal Content.";
            log.error(message);
            fetchContext.newError(fetchContext.getFetchInput().getId())
                    .withError(message)
                    .emit();
        }

        logout();

        return fetchContext.newResult();
    }

    private DrupalLoginResponse getDrupalLoginResponse() {
        String username = connectorConfig.properties().getUsername();
        String password = connectorConfig.properties().getPassword();


        if (username != null && !username.isEmpty() &&
                password != null && !password.isEmpty()) {
            DrupalLoginRequest drupalLoginRequest = new DrupalLoginRequest(username, password);

            drupalLoginResponse = drupalOkHttp.loginResponse(getDrupalLoginUrl(), drupalLoginRequest);

            return drupalLoginResponse;
        } else {
            return new DrupalLoginResponse();
        }
    }

    private boolean logout() {
        return drupalOkHttp.logout(getDrupalLogoutUrl(), drupalLoginResponse);
    }

    private String getDrupalUrl() {
        return connectorConfig.properties().getUrl();
    }

    private String getDrupalContentEntryUrl() {
        return getDrupalUrl() + connectorConfig.properties().getDrupalContentEntryPath();
    }

    private String getDrupalLoginUrl() {
        return getDrupalUrl() + connectorConfig.properties().getLoginPath();
    }

    private String getDrupalLogoutUrl() {
        return getDrupalUrl() + connectorConfig.properties().getLogoutPath();
    }
}

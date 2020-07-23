package com.lucidworks.fusion.connector.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.config.ContentConfig;
import com.lucidworks.fusion.connector.exception.ServiceException;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalHttpClient;
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
    private DrupalHttpClient drupalHttpClient;

    @Inject
    public JsonContentFetcher(
            ContentConfig connectorConfig
    ) {
        this.connectorConfig = connectorConfig;
        this.objectMapper = new ObjectMapper();
        this.drupalHttpClient = new DrupalHttpClient();
        this.contentService = new ContentService(objectMapper);
        this.connectorService = new ConnectorService(getDrupalContentEntryUrl(), contentService, drupalHttpClient);
    }

    @Override
    public FetchResult fetch(FetchContext fetchContext) {

        Map<String, TopLevelJsonapi> topLevelJsonapiMap = new HashMap<>();
        Map<String, String> contentMap = new HashMap<>();

        doLogin();

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

            Map<String, Map<String, Object>> objectMap = DataUtil.generateObjectMap(topLevelJsonapiMap);

            for (String key : objectMap.keySet()) {
                Map<String, Object> pageContentMap = objectMap.get(key);
                fetchContext.newDocument(key)
                        .fields(field -> {
                            field.setString("url", key);
                            field.setLong("lastUpdated", ZonedDateTime.now().toEpochSecond());
                            field.merge(pageContentMap);
                        })
                        .emit();
            }
        } else {
            String message = "Failed to store all Drupal Content.";
            log.error(message);
            fetchContext.newError(fetchContext.getFetchInput().getId())
                    .withError(message)
                    .emit();
        }

        return fetchContext.newResult();
    }

    private void doLogin() {
        String username = connectorConfig.properties().getUsername(), password = connectorConfig.properties().getPassword();
        if (username != null && password != null) {
            drupalHttpClient.doLogin(getDrupalLoginUrl(), username, password);
        }
    }

    private String normalizeUrl(String initialUrl) {
        String normalizedUrl = initialUrl.endsWith("/") ?
                initialUrl.substring(0, initialUrl.length() - 1) : initialUrl;

        return normalizedUrl;
    }

    private String getDrupalUrl() {
        return connectorConfig.properties().getUrl();
    }

    private String getDrupalContentEntryUrl() {
        return normalizeUrl(getDrupalUrl()) + normalizeUrl(connectorConfig.properties().getDrupalContentEntryPath());
    }

    private String getDrupalLoginUrl() {
        return normalizeUrl(getDrupalUrl()) + normalizeUrl(connectorConfig.properties().getLoginPath());
    }

    private String getDrupalLogoutUrl() {
        return normalizeUrl(getDrupalUrl()) + normalizeUrl(connectorConfig.properties().getLogoutPath());
    }
}

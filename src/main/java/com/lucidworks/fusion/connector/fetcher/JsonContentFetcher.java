package com.lucidworks.fusion.connector.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.config.ContentConfig;
import com.lucidworks.fusion.connector.exception.ServiceException;
import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.FetchInput;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalOkHttp;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.ZonedDateTime;
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
        try {
            FetchInput input = fetchContext.getFetchInput();
            Map<String, Object> metaData = input.getMetadata();

            Map<String, String> contentMap = connectorService.prepareDataToUpload();

            contentMap.forEach((url, content) -> {

                fetchContext.newCandidate(url)
                        .metadata(m -> {
                            m.setString("content", content.substring(content.length() / 2));
                        }).emit();

                fetchContext.newDocument(input.getId())
                        .fields(f -> {
                            f.setString("content_s", (String) metaData.get("content"));
                            f.setLong("lastUpdatedEntry_l", ZonedDateTime.now().toEpochSecond());
                        })
                        .emit();
            });

            logout();
        } catch (ServiceException e) {
            String message = "Failed to parse content from Drupal!";
            log.error(message, e);
            fetchContext.newError(fetchContext.getFetchInput().getId())
                    .withError(message)
                    .emit();
        }

        return fetchContext.newResult();
    }

    private DrupalLoginResponse getDrupalLoginResponse() {
        String username = connectorConfig.properties().getUsername();
        String password = connectorConfig.properties().getPassword();

        DrupalLoginRequest drupalLoginRequest = new DrupalLoginRequest(username, password);

        drupalLoginResponse = drupalOkHttp.loginResponse(getDrupalLoginUrl(), drupalLoginRequest);

        return drupalLoginResponse;
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

package com.lucidworks.fusion.connector.fetcher;

import com.lucidworks.fusion.connector.config.ContentConfig;
import com.lucidworks.fusion.connector.content.DrupalContent;
import com.lucidworks.fusion.connector.content.DrupalContentEntry;
import com.lucidworks.fusion.connector.exception.ServiceException;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.FetchInput;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Fetch the content from Drupal's pages
 */
@Slf4j
public class JsonContentFetcher implements ContentFetcher {

    private final static String LAST_JOB_RUN_DATE_TIME = "lastJobRunDateTime";
    private final static String ENTRY_LAST_UPDATED = "lastUpdatedEntry";

    private final ContentConfig connectorConfig;
    private ContentService contentService;
    private ConnectorService connectorService;

    @Inject
    public JsonContentFetcher(
            ContentConfig connectorConfig,
            ContentService contentService
    ) {
        this.connectorConfig = connectorConfig;
        this.contentService = contentService;
        connectorService = new ConnectorService(getDrupalContentEntryUrl(), null, contentService);
    }

    private String getDrupalUrl() {
        return connectorConfig.properties().getUrl();
    }

    private String getDrupalContentEntryUrl() {
        return getDrupalUrl() + connectorConfig.properties().getDrupalContentEntryPath();
    }

    private DrupalLoginResponse getDrupalLoginResponse() {
        String username = connectorConfig.properties().getUsername();
        String password = connectorConfig.properties().getPassword();

        DrupalLoginResponse drupalLoginResponse = contentService.login(getDrupalUrl(), username, password);

        return drupalLoginResponse;

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
        } catch (ServiceException e) {
            String message = "Failed to parse content from Drupal!";
            log.error(message, e);
            fetchContext.newError(fetchContext.getFetchInput().getId())
                    .withError(message)
                    .emit();
        }

        return fetchContext.newResult();
    }

    private void emitDrupalCandidates(DrupalContent feed, FetchContext fetchContext, long lastJobRunDateTime) {
        Map<String, DrupalContentEntry> entryMap = feed.getEntries();
        entryMap.forEach((id, entry) -> {
            fetchContext.newCandidate(id)
                    .metadata(m -> {
                        m.setString("content", entry.getContent());
                        // add last time when entry was modified
                        m.setLong(ENTRY_LAST_UPDATED, entry.getLastUpdated());
                        // add 'lastJobRunDateTime'.
                        m.setLong(LAST_JOB_RUN_DATE_TIME, lastJobRunDateTime);
                    })
                    .emit();
        });
    }
}

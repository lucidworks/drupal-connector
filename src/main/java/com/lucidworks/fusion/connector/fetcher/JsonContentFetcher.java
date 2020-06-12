package com.lucidworks.fusion.connector.fetcher;

import com.lucidworks.fusion.connector.config.ContentConfig;
import com.lucidworks.fusion.connector.content.DrupalContent;
import com.lucidworks.fusion.connector.content.DrupalContentEntry;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.FetchInput;
import com.lucidworks.fusion.connector.service.ContentService;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Map;

public class JsonContentFetcher implements ContentFetcher {

    private final static String LAST_JOB_RUN_DATE_TIME = "lastJobRunDateTime";
    private final static String ENTRY_LAST_UPDATED = "lastUpdatedEntry";

    private final ContentConfig connectorConfig;
    private ContentService contentService;

    @Inject
    public JsonContentFetcher(
            ContentConfig connectorConfig,
            ContentService contentService
    ) {
        this.connectorConfig = connectorConfig;
        this.contentService = contentService;
    }

    @Override
    public FetchResult fetch(FetchContext fetchContext) {
        FetchInput input = fetchContext.getFetchInput();
        Map<String, Object> metaData = input.getMetadata();
        long lastJobRunDateTime = 0;

        String url = connectorConfig.properties().getUrl();
        String username = connectorConfig.properties().getUsername();
        String password = connectorConfig.properties().getPassword();

        DrupalLoginResponse drupalLoginResponse = contentService.login(url, username, password);

        DrupalContent drupalContent = contentService.getDrupalContent(url, drupalLoginResponse);

        emitDrupalCandidates(drupalContent, fetchContext, lastJobRunDateTime);

        //Emit document
        drupalContent.getEntries().forEach((id, entry) -> {
                    fetchContext.newDocument(input.getId())
                            .fields(f -> {
                                f.setString("content_s", (String) metaData.get("content"));
                                f.setLong("lastUpdatedEntry_l", ZonedDateTime.now().toEpochSecond());
                                // adding more fields with random values.
                                f.merge(drupalContent.getMapWithObject());
                            })
                            .emit();
                }
        );
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

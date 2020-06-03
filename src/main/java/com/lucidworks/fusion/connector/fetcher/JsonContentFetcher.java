package com.lucidworks.fusion.connector.fetcher;

import com.google.common.collect.ImmutableMap;
import com.lucidworks.fusion.connector.config.ContentConfig;
import com.lucidworks.fusion.connector.content.*;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.FetchInput;
import com.lucidworks.fusion.connector.service.config.DrupalOkHttp;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;


public class JsonContentFetcher implements ContentFetcher {

    private final static String LAST_JOB_RUN_DATE_TIME = "lastJobRunDateTime";
    private final static String ENTRY_LAST_UPDATED = "lastUpdatedEntry";

    private final DrupalOkHttp drupalOkHttp;

    @Inject
    public JsonContentFetcher(
            DrupalOkHttp drupalOkHttp
    ) {
        this.drupalOkHttp = drupalOkHttp;
    }

    @Override
    public FetchResult fetch(FetchContext fetchContext) {
        FetchInput input = fetchContext.getFetchInput();
        Map<String, Object> metaData = input.getMetadata();
        long lastJobRunDateTime = 0;

        DrupalContent drupalContent = getDrupalContent();

        emitDrupalCandidates(drupalContent,fetchContext,lastJobRunDateTime);

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

    private DrupalContent getDrupalContent() {
        ImmutableMap.Builder<String, DrupalContentEntry> builder = ImmutableMap.builder();

        String url = "http://s5ece25faf2e8c4kc8tnpvvh.devcloud.acquia-sites.com/jsonapi";
        ResponseBody responseBody = drupalOkHttp.getDrupalContent(url);

        try {
            DrupalContentEntry drupalContentEntry = new DrupalContentEntry(url, responseBody.string(), ZonedDateTime.now().toEpochSecond());
            builder.put(url, drupalContentEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DrupalContent(builder.build());

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

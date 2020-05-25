package com.lucidworks.fusion.connector.fetcher;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.lucidworks.fusion.connector.config.FeedConfig;
import com.lucidworks.fusion.connector.content.Content;
import com.lucidworks.fusion.connector.content.ContentEntry;
import com.lucidworks.fusion.connector.content.ContentGenerator;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.FetchInput;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;

import static com.lucidworks.fusion.connector.plugin.api.validation.constants.FetchInputValidatorConstants.NOT_MODIFIED;

public class DrupalContentFetcher implements ContentFetcher {

    private final static String CHECKPOINT_PREFIX = "checkpoint";
    private final static String LAST_JOB_RUN_DATE_TIME = "lastJobRunDateTime";
    private final static String ENTRY_LAST_UPDATED = "lastUpdatedEntry";
    private final static String ENTRY_INDEX_START = "entryIndexStart";
    private final static String ENTRY_INDEX_END = "entryIndexEnd";

    private final FeedConfig connectorConfig;
    private final ContentGenerator generator;

    @Inject
    public DrupalContentFetcher(
            FeedConfig connectorConfig,
            ContentGenerator generator
    ) {
        this.connectorConfig = connectorConfig;
        this.generator = generator;
    }

    @Override
    public FetchResult fetch(FetchContext fetchContext) {
        FetchInput input = fetchContext.getFetchInput();
        Map<String, Object> metaData = input.getMetadata();
        if (!input.hasId() || input.getId().startsWith(CHECKPOINT_PREFIX)) {
            long currentJobRunDateTime = Instant.now().toEpochMilli();
            long lastJobRunDateTime = 0;
            if (metaData.containsKey(LAST_JOB_RUN_DATE_TIME)) {
                // extract the lastJobRunDateTime from the checkpoint coming from crawlDb
                // it represents the last time a job was run (previous to this current crawl)
                lastJobRunDateTime = (Long) metaData.get(LAST_JOB_RUN_DATE_TIME);
            }

            Content content = generator.generateContent(getEntryIndexStart(input), getEntryIndexEnd(input));
            emitCandidates(content, fetchContext, lastJobRunDateTime);
            emitCheckpoint(
                    fetchContext,
                    currentJobRunDateTime,
                    getEntryIndexStart(input),
                    getEntryIndexEnd(input)
            );
        } else {
            processFeedEntry(fetchContext, input, metaData);
        }
        return fetchContext.newResult();
    }

    private void emitCandidates(Content feed, FetchContext fetchContext, long lastJobRunDateTime) {
        Map<String, ContentEntry> entryMap = feed.getEntries();
        entryMap.forEach((id, entry) -> {
            fetchContext.newCandidate(entry.getId())
                    .metadata(m -> {
                        m.setString("title", entry.getTitle());
                        // add last time when entry was modified
                        m.setLong(ENTRY_LAST_UPDATED, entry.getLastUpdated());
                        // add 'lastJobRunDateTime'.
                        m.setLong(LAST_JOB_RUN_DATE_TIME, lastJobRunDateTime);
                    })
                    .emit();
        });
    }

    private void emitCheckpoint(FetchContext fetchContext, long currentJobRunDateTime, int entryIndexStart, int entryIndexEnd) {
        // For feed-connector purposes, we need to update the checkpoint per crawl
        // Reason is to update the lastJobRunDateTime with the current crawl time.
        fetchContext.newCheckpoint(CHECKPOINT_PREFIX)
                .metadata(m -> {
                    m.setLong(LAST_JOB_RUN_DATE_TIME, currentJobRunDateTime);
                    m.setString("requestInfoId", fetchContext.getRequestInfo().getId());
                    m.setInteger(ENTRY_INDEX_START,
                            entryIndexStart); // needed when generating entries (entries are not get from a json file)
                    m.setInteger(ENTRY_INDEX_END,
                            entryIndexEnd); // needed when generating entries (  entries are not get from a json file)
                })
                .emit();
    }

    private void processFeedEntry(FetchContext fetchContext, FetchInput input, Map<String, Object> metaData) {
        long lastJobRunDateTime = (Long) metaData.get(LAST_JOB_RUN_DATE_TIME);
        long entryLastUpdated = (Long) metaData.get(ENTRY_LAST_UPDATED);
        // when candidate was emitted, it was provided 'lastJobRunDateTime'. it will be compared with 'lastUpdatedEntry'
        // If true it means an entry was updated after the last time a job was run, the emit the item as document in order to be re-indexed.
        if (entryLastUpdated > lastJobRunDateTime) {
            fetchContext.newDocument(input.getId())
                    .fields(f -> {
                        f.setString("title_s", (String) metaData.get("title"));
                        f.setLong("lastUpdatedEntry_l", entryLastUpdated);
                        // adding more fields with random values.
                        f.merge(generator.generateFieldsMap());
                    })
                    .emit();
        } else {
            // if false, it means the entry was not modified.
            // We need to emit it as unmodified to update the 'modifiedAt_l' in crawlDb item, it is mandatory otherwise the
            // purge process will remove unmodified items.
            // logger.info("unmodified {}", input.getId());
            fetchContext.newSkip(input.getId())
                    .withConditions(Sets.newHashSet(NOT_MODIFIED))
                    .emit();
        }
    }

    private int getEntryIndexEnd(FetchInput fetchInput) {
        if (!Strings.isNullOrEmpty(connectorConfig.properties().url())) {
            return 0;
        } else if (!fetchInput.hasId()) {
            return connectorConfig.properties().generateProperties().entriesTotal();
        }
        return Integer.valueOf(fetchInput.getMetadata().get(ENTRY_INDEX_END).toString()) +
                connectorConfig.properties().generateProperties().entriesToAddIncremental();
    }

    private int getEntryIndexStart(FetchInput fetchInput) {
        if (!Strings.isNullOrEmpty(connectorConfig.properties().url()) || !fetchInput.hasId()) {
            return 0;
        }
        return Integer.valueOf(fetchInput.getMetadata().get(ENTRY_INDEX_START).toString()) +
                connectorConfig.properties().generateProperties().entriesToRemoveIncremental();
    }
}

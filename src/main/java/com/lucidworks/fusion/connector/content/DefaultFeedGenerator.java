package com.lucidworks.fusion.connector.content;

import com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

public class DefaultFeedGenerator implements ContentGenerator {

    @Override
    public Map<String, Object> generateFieldsMap() {
        return ImmutableMap.<String, Object>builder()
                .put("abc_lola1", UUID.randomUUID().toString())
                .put("xyz_lola2", UUID.randomUUID().toString())
                .put("qwe_lola3", UUID.randomUUID().toString())
                .put("dfg_lola4", UUID.randomUUID().toString())
                .put("ghy_s", UUID.randomUUID().toString())
                .put("uik_s", UUID.randomUUID().toString())
                .put("awf_s", UUID.randomUUID().toString())
                .put("tyu_s", UUID.randomUUID().toString())
                .build();
    }

    @Override
    public Content generateContent(int entryIndexStart, int entryIndexEnd) {
        ImmutableMap.Builder<String, ContentEntry> builder = ImmutableMap.builder();
        // generating entries from 'entryIndexStart'.
        // Entries not generated before 'entryIndexStart' will not be emitted as candidates, this will simulate when
        // entries are removed from the Feed
        IntStream.range(entryIndexStart, entryIndexEnd).asLongStream().forEach(index -> {
            builder.put(
                    String.valueOf(index),
                    new ContentEntry(String.valueOf(index), UUID.randomUUID().toString(), Instant.now().toEpochMilli())
            );
        });
        return new Content(builder.build());
    }
}

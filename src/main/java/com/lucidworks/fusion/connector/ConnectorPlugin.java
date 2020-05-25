package com.lucidworks.fusion.connector;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.lucidworks.fusion.connector.config.FeedConfig;
import com.lucidworks.fusion.connector.content.ContentGenerator;
import com.lucidworks.fusion.connector.content.DefaultFeedGenerator;
import com.lucidworks.fusion.connector.fetcher.DrupalContentFetcher;
import com.lucidworks.fusion.connector.plugin.api.plugin.ConnectorPluginProvider;

public class ConnectorPlugin implements ConnectorPluginProvider {

    @Override
    public com.lucidworks.fusion.connector.plugin.api.plugin.ConnectorPlugin get() {
        Module fetchModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ContentGenerator.class)
                        .to(DefaultFeedGenerator.class)
                        .asEagerSingleton();
            }
        };

        return com.lucidworks.fusion.connector.plugin.api.plugin.ConnectorPlugin.builder(FeedConfig.class)
                .withFetcher("content", DrupalContentFetcher.class, fetchModule)
                .build();
    }
}

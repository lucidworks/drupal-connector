package com.lucidworks.fusion.connector;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.lucidworks.fusion.connector.config.ContentConfig;
import com.lucidworks.fusion.connector.fetcher.JsonContentFetcher;
import com.lucidworks.fusion.connector.plugin.api.plugin.ConnectorPluginProvider;

public class ConnectorPlugin implements ConnectorPluginProvider {

    @Override
    public com.lucidworks.fusion.connector.plugin.api.plugin.ConnectorPlugin get() {
        Module fetchModule = new AbstractModule() {
            @Override
            protected void configure() {

            }
        };

        return com.lucidworks.fusion.connector.plugin.api.plugin.ConnectorPlugin.builder(ContentConfig.class)
                .withFetcher("content", JsonContentFetcher.class, fetchModule)
                .build();
    }
}

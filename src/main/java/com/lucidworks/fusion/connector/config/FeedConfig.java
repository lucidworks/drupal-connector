package com.lucidworks.fusion.connector.config;

import com.lucidworks.fusion.connector.plugin.api.config.ConnectorConfig;
import com.lucidworks.fusion.connector.plugin.api.config.ConnectorPluginProperties;
import com.lucidworks.fusion.schema.SchemaAnnotations.Property;
import com.lucidworks.fusion.schema.SchemaAnnotations.RootSchema;
import com.lucidworks.fusion.schema.SchemaAnnotations.StringSchema;

@RootSchema(
        title = "Drupal 8 Connector",
        description = "A Drupal 8 connector",
        category = "Drupal8"
)
public interface FeedConfig extends ConnectorConfig<FeedConfig.Properties> {

    /**
     * Connector specific settings
     */
    @Property(
            title = "Properties",
            required = true
    )
    Properties properties();

    /**
     * Connector specific settings
     */
    interface Properties extends ConnectorPluginProperties, GenerateConfig {

        @Property(
                title = "Content URL",
                description = "Content URL location. If empty, the connector will generate entries (see 'Generate Properties')"
        )
        @StringSchema
        String url();

    }

}

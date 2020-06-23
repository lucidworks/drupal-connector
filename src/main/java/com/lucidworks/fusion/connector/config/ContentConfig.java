package com.lucidworks.fusion.connector.config;

import com.lucidworks.fusion.connector.plugin.api.config.ConnectorConfig;
import com.lucidworks.fusion.connector.plugin.api.config.ConnectorPluginProperties;
import com.lucidworks.fusion.schema.SchemaAnnotations.Property;
import com.lucidworks.fusion.schema.SchemaAnnotations.RootSchema;
import com.lucidworks.fusion.schema.SchemaAnnotations.StringSchema;

@RootSchema(
        title = "Drupal 8 - Java Connector",
        description = "A Drupal 8 connector",
        category = "Drupal8"
)
public interface ContentConfig extends ConnectorConfig<ContentConfig.Properties> {

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
    interface Properties extends ConnectorPluginProperties {

        @Property(
                title = "Drupal URL",
                description = "Page URL.",
                required = true,
                order = 1
        )
        @StringSchema
        String getUrl();

        @Property(
                title = "Username for login",
                description = "Username to login into drupal to be able to fetch content from it.",
                order = 2
        )
        @StringSchema
        String getUsername();

        @Property(
                title = "Password for login",
                description = "Password to login into drupal to be able to fetch content from it.",
                order = 3
        )
        @StringSchema(encrypted = true)
        String getPassword();

        @Property(
                title = "Login path",
                description = "Login path.",
                required = true,
                order = 4
        )
        @StringSchema(defaultValue = "/user/login")
        String getLoginPath();

        @Property(
                title = "Logout path",
                description = "Logout path.",
                required = true,
                order = 5
        )
        @StringSchema(defaultValue = "/user/logout")
        String getLogoutPath();

        @Property(
                title = "Drupal Content entry path",
                description = "Drupal Content entry path from where the crawling should start.",
                required = true,
                order = 6
        )
        @StringSchema(defaultValue = "/en/fusion")
        String getDrupalContentEntryPath();

    }

}

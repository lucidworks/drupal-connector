package lucidworks.fusion.connector.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lucidworks.fusion.connector.service.ContentService;
import lucidworks.fusion.connector.util.DataUtil;
import lucidworks.fusion.connector.service.DrupalHttpClient;
import lucidworks.fusion.connector.config.ContentConfig;
import lucidworks.fusion.connector.exception.ServiceException;
import lucidworks.fusion.connector.model.TopLevelJsonapi;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import lucidworks.fusion.connector.service.ConnectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fetch the content from Drupal's pages
 */
public class JsonContentFetcher implements ContentFetcher {

    private static final Logger logger = LoggerFactory.getLogger(JsonContentFetcher.class);
    private final ContentConfig connectorConfig;
    private ContentService contentService;
    private ConnectorService connectorService;
    private ObjectMapper objectMapper;
    private DrupalHttpClient drupalHttpClient;

    @Inject
    public JsonContentFetcher(
            ContentConfig connectorConfig
    ) {
        this.connectorConfig = connectorConfig;
        this.objectMapper = new ObjectMapper();
        this.drupalHttpClient = new DrupalHttpClient();
        this.contentService = new ContentService(objectMapper);
        this.connectorService = new ConnectorService(getDrupalContentEntryUrl(), contentService, drupalHttpClient);
    }

    @Override
    public FetchResult fetch(FetchContext fetchContext) {

        Map<String, TopLevelJsonapi> topLevelJsonapiMap = new HashMap<>();
        Map<String, String> contentMap = new HashMap<>();

        doLogin();

        try {

            logger.info("Start crawling process...");

            contentMap = connectorService.prepareDataToUpload();
            topLevelJsonapiMap = contentService.getTopLevelJsonapiDataMap();

            logger.info("Crawling process finished with success...");

        } catch (ServiceException e) {
            String message = "Failed to parse content from Drupal!";

            logger.error(message, e);

            fetchContext.newError(fetchContext.getFetchInput().getId())
                    .withError(message)
                    .emit();
        }

        if (contentMap.keySet().size() == topLevelJsonapiMap.keySet().size()) {
            logger.info("Start normalization process...");

            Map<String, Map<String, Object>> objectMap = DataUtil.generateObjectMap(topLevelJsonapiMap);

            logger.info("Normalization process finished with success...");


            logger.info("Start indexing content, documents to be indexed ", objectMap.keySet().size());

            for (String key : objectMap.keySet()) {
                Map<String, Object> pageContentMap = objectMap.get(key);
                long epochSeconds = ZonedDateTime.now().toEpochSecond();
                fetchContext.newDocument(key)
                        .fields(field -> {
                            field.setString("url", key);
                            field.setLong("lastUpdated", epochSeconds);
                            field.merge(pageContentMap);
                        })
                        .emit();

                logger.info("Emit document id: {} lastUpdated: ", key, epochSeconds);
            }

            logger.info("Indexing finished with success");
        } else {
            String message = "Failed to store all Drupal Content.";

            logger.error(message);

            fetchContext.newError(fetchContext.getFetchInput().getId())
                    .withError(message)
                    .emit();
        }

        return fetchContext.newResult();
    }

    private void doLogin() {
        String username = connectorConfig.properties().getUsername(), password = connectorConfig.properties().getPassword();
        if (username != null && password != null) {
            logger.info("Try to login with provided user and password into CMS {} ", username);

            drupalHttpClient.doLogin(getDrupalLoginUrl(), username, password);

            logger.info("Login successfully {} ", username);
        }
    }

    private String normalizeUrl(String initialUrl) {
        String normalizedUrl = initialUrl.endsWith("/") ?
                initialUrl.substring(0, initialUrl.length() - 1) : initialUrl;

        return normalizedUrl;
    }

    private String getDrupalUrl() {
        return connectorConfig.properties().getUrl();
    }

    private String getDrupalContentEntryUrl() {
        return normalizeUrl(getDrupalUrl()) + normalizeUrl(connectorConfig.properties().getDrupalContentEntryPath());
    }

    private String getDrupalLoginUrl() {
        return normalizeUrl(getDrupalUrl()) + normalizeUrl(connectorConfig.properties().getLoginPath());
    }

    private String getDrupalLogoutUrl() {
        return normalizeUrl(getDrupalUrl()) + normalizeUrl(connectorConfig.properties().getLogoutPath());
    }
}

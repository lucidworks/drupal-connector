package com.lucidworks.fusion.connector.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.lucidworks.fusion.connector.config.ContentConfig;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import com.lucidworks.fusion.connector.plugin.api.fetcher.result.FetchResult;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.ContentFetcher;
import com.lucidworks.fusion.connector.plugin.api.fetcher.type.content.FetchInput;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalHttpClient;
import com.lucidworks.fusion.connector.util.DataUtil;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Fetch the content from Drupal's pages
 */
@Slf4j
public class JsonContentFetcher implements ContentFetcher {

  private final ContentConfig connectorConfig;
  private ContentService contentService;
  private ObjectMapper objectMapper;
  private DrupalHttpClient drupalHttpClient;

  private Set<String> visitedUrls = Sets.newHashSet();
  private boolean loggedIn = false;

  @Inject
  public JsonContentFetcher(
      ContentConfig connectorConfig
  ) {
    this.connectorConfig = connectorConfig;
    this.objectMapper = new ObjectMapper();
    this.drupalHttpClient = new DrupalHttpClient();
    this.contentService = new ContentService(objectMapper);
  }

  @Override
  public FetchResult fetch(FetchContext fetchContext) {
    if (!loggedIn) {
      doLogin();
    }

    FetchInput input = fetchContext.getFetchInput();
    if (!input.hasId()) {
      fetchContext.newCandidate(getDrupalContentEntryUrl()).emit();
      return fetchContext.newResult();
    }
    String url = input.getId();
    String content = drupalHttpClient.getContent(url);
    TopLevelJsonapi topLevelJsonapi;
    try {
      topLevelJsonapi = objectMapper.readValue(content, TopLevelJsonapi.class);
    } catch (IOException e) {
      fetchContext.newError(url, "Unable to parse top-level JSON data");
      return fetchContext.newResult();
    }
    Map<String, Map<String, Object>> objectMap = DataUtil.generateObjectMap(
        Collections.singletonMap(url, topLevelJsonapi)
    );
    for (String key : objectMap.keySet()) {
      Map<String, Object> pageContentMap = objectMap.get(key);
      fetchContext.newDocument(key)
          .fields(field -> {
            field.setString("url", key);
            field.setLong("lastUpdated", ZonedDateTime.now().toEpochSecond());
            field.merge(pageContentMap);
          })
          .emit();
      visitedUrls.add(key);
    }
    List<String> links = contentService
        .collectLinksFromDrupalContent(url, content)
        .stream()
        .filter(link -> !visitedUrls.contains(link))
        .collect(Collectors.toList());
    links.forEach(link -> {
      fetchContext.newCandidate(link).emit();
      visitedUrls.add(link);
    });
    return fetchContext.newResult();
  }

  private void doLogin() {
    String username = connectorConfig.properties().getUsername(), password = connectorConfig.properties().getPassword();
    if (username != null && password != null) {
      drupalHttpClient.doLogin(getDrupalLoginUrl(), username, password);
      this.loggedIn = true;
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

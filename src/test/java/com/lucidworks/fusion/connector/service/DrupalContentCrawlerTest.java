package com.lucidworks.fusion.connector.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class DrupalContentCrawlerTest {

    private static final String URL = "http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion";
    private static final int TOTAL = 5;

    @Mock
    private DrupalContentCrawler drupalContentCrawler;

    @Mock
    private ContentService contentService;

    @Mock
    private DrupalHttpClient drupalHttpClient;

    private MockitoSession mockitoSession;

    @Before
    public void setUp() {

        mockitoSession = Mockito.mockitoSession()
                .initMocks(this)
                .strictness(Strictness.LENIENT)
                .startMocking();
        MockitoAnnotations.initMocks(this);

        drupalContentCrawler = new DrupalContentCrawler(URL, contentService, drupalHttpClient);
    }

    @After
    public void tearDown() {
        mockitoSession.finishMocking();
    }

    @Test
    public void testStartCrawling() {
        when(drupalHttpClient.getContent(anyString())).thenReturn(prepareDrupalContent());
        when(contentService.collectLinksFromDrupalContent(anyString(), anyString())).thenReturn(prepareListWithExpectedLinks());
        drupalContentCrawler.startCrawling();

        List<String> links = new ArrayList<>();
        links.addAll(drupalContentCrawler.getVisitedUrls().keySet());

        List<String> expectedLinks = prepareListWithExpectedLinks();

        assertEquals(TOTAL, links.size());
        assertEquals(expectedLinks.get(0), links.get(0));
        assertEquals(expectedLinks.get(1), links.get(1));
        assertEquals(expectedLinks.get(2), links.get(2));
        assertEquals(expectedLinks.get(3), links.get(3));
        assertEquals(expectedLinks.get(4), links.get(4));

    }

    private String prepareDrupalContent() {
        return "{\"jsonapi\":" +
                "{\"version\":\"1.0\",\"meta\":" +
                "{\"links\":{\"self\":{\"href\":\"http://jsonapi.org/format/1.0/\"}}}}," +
                "\"data\":[" +
                "{\"type\":\"node--page\",\"id\":\"e0274815-10ba-4b41-b65f-2e0c918dbe09\"," +
                "\"attributes\":{\"drupal_internal__nid\":86},\"" +
                "relationships\":{\"node_type\":{\"data\":{\"type\":\"node_type--node_type\",\"id\":\"c142b6c7-af65-4fb5-8fdc-51bdf2b72e92\"}," +
                "\"links\":{\"related\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page/e0274815-10ba-4b41-b65f-2e0c918dbe09/node_type?resourceVersion=id%3A176\"}," +
                "\"self\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page/e0274815-10ba-4b41-b65f-2e0c918dbe09/relationships/node_type?resourceVersion=id%3A176\"}}}," +
                "\"revision_uid\":{\"data\":{\"type\":\"user--user\",\"id\":\"bfbb1d33-18aa-4726-98e2-3be439022479\"}," +
                "\"links\":{\"related\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page/e0274815-10ba-4b41-b65f-2e0c918dbe09/revision_uid?resourceVersion=id%3A176\"}}}}}" +
                "]," +
                "\"links\":{\"node--article\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/article\"}," +
                "\"node--page\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page\"}," +
                "\"self\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion\"}}}";
    }

    private List<String> prepareListWithExpectedLinks() {
        List<String> expectedLinks = new ArrayList<>();
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page/e0274815-10ba-4b41-b65f-2e0c918dbe09/revision_uid?resourceVersion=id%3A176");
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/article");
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion");
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page/e0274815-10ba-4b41-b65f-2e0c918dbe09/node_type?resourceVersion=id%3A176");
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page");

        return expectedLinks;
    }

    @Test(expected = RuntimeException.class)
    public void testStartCrawling_throwException() {
        when(drupalHttpClient.getContent(any(String.class))).thenReturn(null);
        doNothing().when(contentService).collectLinksFromDrupalContent(URL, prepareDrupalContent());
        drupalContentCrawler.startCrawling();
    }

}

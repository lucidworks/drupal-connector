package lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ContentServiceTest {

    private static final int TOTAL = 5;

    private ContentService contentService;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        contentService = new ContentService(mapper);
    }

    @Test
    public void testCollectLinksFromDrupalContent() {
        String url = "http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion";
        String content = prepareContent();
        List<String> expectedLinks = prepareListWithExpectedLinks();
        List<String> links = contentService.collectLinksFromDrupalContent(url, content);

        assertEquals(TOTAL, links.size());
        assertEquals(expectedLinks.get(0), links.get(0));
        assertEquals(expectedLinks.get(1), links.get(1));
        assertEquals(expectedLinks.get(2), links.get(2));
        assertEquals(expectedLinks.get(3), links.get(3));
        assertEquals(expectedLinks.get(4), links.get(4));
    }

    private List<String> prepareListWithExpectedLinks() {
        List<String> expectedLinks = new ArrayList<>();
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page/e0274815-10ba-4b41-b65f-2e0c918dbe09/revision_uid?resourceVersion=id%3A176");
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page/e0274815-10ba-4b41-b65f-2e0c918dbe09/node_type?resourceVersion=id%3A176");
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/article");
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page");
        expectedLinks.add("http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe");

        return expectedLinks;
    }

    private String prepareContent() {
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
                "\"node--recipe\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe\"}," +
                "\"self\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion\"}}}";
    }

    @Test(expected = RuntimeException.class)
    public void testCollectLinksFromDrupalContent_throwException() {
        String url = "http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion";
        String content = prepareWrongContent();
        contentService.collectLinksFromDrupalContent(url, content);
    }

    private String prepareWrongContent() {
        return "{\"wrongTag\":" +
                "{\"version\":\"1.0\",\"meta\":" +
                "{\"links\":{\"self\":{\"href\":\"http://jsonapi.org/format/1.0/\"}}}}," +
                "\"data\":[]," +
                "\"links\":{\"node--article\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/article\"}," +
                "\"node--page\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page\"}," +
                "\"node--recipe\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe\"}," +
                "\"self\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion\"}}}";
    }

}

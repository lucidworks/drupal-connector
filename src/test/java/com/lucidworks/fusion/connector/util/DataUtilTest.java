package com.lucidworks.fusion.connector.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.exception.ServiceException;
import com.lucidworks.fusion.connector.model.TopLevelJsonApiData;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DataUtilTest {

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testTopLevelJsonApiFields() {
        String content = prepareContent();
        Map<String, TopLevelJsonapi> topLevelJsonapiMap = new HashMap<>();
        Map<String, Object> objectMap = new HashMap<>();

        TopLevelJsonapi topLevelJsonapi;

        try {
            topLevelJsonapi = mapper.readValue(content, TopLevelJsonapi.class);

        } catch (IOException e) {
            try {
                topLevelJsonapi = mapper.readValue(content, TopLevelJsonApiData.class);
            } catch (IOException ex) {
                throw new ServiceException("The mapper was unable to read the content!", ex);
            }
        }

        topLevelJsonapiMap.put("object", topLevelJsonapi);
        objectMap.putAll(DataUtil.generateObjectMap(topLevelJsonapiMap));

        assertEquals("e0274815-10ba-4b41-b65f-2e0c918dbe09", objectMap.get("data_id"));
        assertEquals(true, objectMap.get("attribute_defaultLangcode"));
        assertEquals("{self=LinkHref(href=http://self)}", objectMap.get("data_links"));
        assertEquals("2020-06-16T07:56:29+00:00", objectMap.get("attribute_revision_timestamp"));
        assertEquals("{content_translation_source=und, content_translation_outdated=false, created=2020-06-16T07:56:29+00:00, title=About Umami}", objectMap.get("attribute_fields"));
        assertEquals(true, objectMap.get("attribute_status"));
        assertEquals("86", objectMap.get("attribute_drupal_internal__nid"));
        assertEquals("en", objectMap.get("attribute_langcode"));
        assertEquals("Meta(Links=null, omitted={detail=Detail, links={help={href=https://www.drupal.org}}})", objectMap.get("meta"));
        assertEquals("node--page", objectMap.get("data_type"));
        assertEquals(false, objectMap.get("attribute_revision_translation_affected"));
        assertEquals("{self=LinkHref(href=http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page)}", objectMap.get("links"));
        assertEquals("176", objectMap.get("attribute_drupal_internal__vid"));
        assertEquals("No revisionLog", objectMap.get("attribute_revision_log"));
        assertEquals("2020-06-16T07:56:29+00:00", objectMap.get("attribute_changed"));
        assertEquals("Path(alias=/about-umami, pid=501, langcode=en)", objectMap.get("attribute_path"));
        assertEquals("No Extra Info", objectMap.get("included"));
        assertEquals("{node_type=RelationshipFields(data={type=node_type--node_type, id=c142b6c7-af65-4fb5-8fdc-51bdf2b72e92}, Links={related=LinkHref(href=http://self), self=LinkHref(href=http://url1)})}", objectMap.get("relationships_fields"));
        assertEquals("No Errors", objectMap.get("errors"));

    }

    private String prepareContent() {
        return "{\"jsonapi\":{}," +
                "\"data\":[{\"type\":\"node--page\"," +
                "\"id\":\"e0274815-10ba-4b41-b65f-2e0c918dbe09\",\"" +
                "links\":{\"self\":{\"href\":\"http://self\"}}," +
                "\"attributes\":{\"drupal_internal__nid\":86,\"drupal_internal__vid\":176," +
                "\"langcode\":\"en\",\"revision_timestamp\":\"2020-06-16T07:56:29+00:00\"," +
                "\"revision_log\":null,\"status\":true,\"title\":\"About Umami\"," +
                "\"created\":\"2020-06-16T07:56:29+00:00\",\"changed\":\"2020-06-16T07:56:29+00:00\"," +
                "\"default_langcode\":true,\"revision_translation_affected\":null," +
                "\"path\":{\"alias\":\"/about-umami\",\"pid\":501,\"langcode\":\"en\"}," +
                "\"content_translation_source\":\"und\",\"content_translation_outdated\":false}," +
                "\"relationships\":{\"node_type\":{\"data\":{\"type\":\"node_type--node_type\"," +
                "\"id\":\"c142b6c7-af65-4fb5-8fdc-51bdf2b72e92\"}," +
                "\"links\":{\"related\":{\"href\":\"http://self\"}," +
                "\"self\":{\"href\":\"http://url1\"}}}}}]," +
                "\"meta\":{\"omitted\":{\"detail\":\"Detail\"," +
                "\"links\":{\"help\":{\"href\":\"https://www.drupal.org\"}}}}," +
                "\"links\":{\"self\":{\"href\":\"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/page\"}}}";
    }

}

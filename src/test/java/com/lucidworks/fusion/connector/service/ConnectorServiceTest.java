package com.lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class ConnectorServiceTest {

    private static final String URL = "http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion";

    @Mock
    private ConnectorService connectorService;

    @Mock
    private DrupalContentCrawler drupalContentCrawler;

    @Mock
    private ContentService contentService;

    @Mock
    private DrupalLoginResponse drupalLoginResponse;

    @Mock
    private ObjectMapper mapper;

    private MockitoSession mockitoSession;

    @Before
    public void setUp() {

        mockitoSession = Mockito.mockitoSession()
                .initMocks(this)
                .strictness(Strictness.LENIENT)
                .startMocking();
        MockitoAnnotations.initMocks(this);

        connectorService = new ConnectorService(URL, drupalLoginResponse, contentService, mapper);
    }

    @After
    public void tearDown() {
        mockitoSession.finishMocking();
    }

    @Test
    public void testPrepareDataToUpload() {
        when(drupalContentCrawler.isProcessFinished()).thenReturn(false);
        doNothing().when(drupalContentCrawler).startCrawling();
        Map<String, String> dataMap = connectorService.prepareDataToUpload();

        assertEquals(1, dataMap.size());
        assertEquals(true, dataMap.containsKey(URL));

    }

    @Test(expected = RuntimeException.class)
    public void testPrepareDataToUpload_ThrowException() {
        doThrow().when(drupalContentCrawler).startCrawling();
    }

}

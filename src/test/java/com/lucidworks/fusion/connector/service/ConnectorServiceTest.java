package com.lucidworks.fusion.connector.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

public class ConnectorServiceTest {

    @Mock
    private ConnectorService connectorService;

    @Mock
    private DrupalContentCrawler drupalContentCrawler;

    private MockitoSession mockitoSession;

    @Before
    public void setUp() {

        mockitoSession = Mockito.mockitoSession()
                .initMocks(this)
                .strictness(Strictness.LENIENT)
                .startMocking();
        MockitoAnnotations.initMocks(this);

    }

    @After
    public void tearDown() {
        mockitoSession.finishMocking();
    }

    @Test
    public void testPrepareDataToUpload() {
        when(drupalContentCrawler.isProcessFinished()).thenReturn(false);
        doNothing().when(drupalContentCrawler).startCrawling();
        connectorService.prepareDataToUpload();
    }

    @Test(expected = RuntimeException.class)
    public void testPrepareDataToUpload_ThrowException() {
        doThrow().when(drupalContentCrawler).startCrawling();
    }

}

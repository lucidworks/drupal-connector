package lucidworks.fusion.connector.service;

import lucidworks.fusion.connector.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Connector Service resolve the data for Fusion Fetcher
 */
public class ConnectorService {


    private static final Logger logger = LoggerFactory.getLogger(ConnectorService.class);
    private final DrupalContentCrawler drupalContentCrawler;
    private boolean isProcessStarted = false;

    public ConnectorService(String drupalUrl, ContentService contentService, DrupalHttpClient drupalHttpClient) {
        this.drupalContentCrawler = new DrupalContentCrawler(drupalUrl, contentService, drupalHttpClient);
    }

    /**
     * Prepare data to upload will wait until the crawling process is done and then return the result of it
     *
     * @return dataToUpload
     */
    public Map<String, String> prepareDataToUpload() {
        logger.info("Method prepareDataToUpload...");

        Map<String, String> dataToUpload;

        try {
            if (!isProcessStarted || !drupalContentCrawler.isProcessFinished()) {
                drupalContentCrawler.startCrawling();
                isProcessStarted = true;
            }
        } catch (ServiceException e) {
            throw new ServiceException("There was an error on the crawling process!", e);
        }

        while (!drupalContentCrawler.isProcessFinished()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.error("Retrying to call the crawling method...");
                return prepareDataToUpload();
            }
        }

        logger.info("Crawling process is finished.");
        dataToUpload = drupalContentCrawler.getVisitedUrls();

        return dataToUpload;
    }

}

package lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lucidworks.fusion.connector.exception.ServiceException;
import lucidworks.fusion.connector.model.*;
import lucidworks.fusion.connector.service.ConnectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Content Service fetch the content from Drupal
 */
public class ContentService {


    private static final Logger logger = LoggerFactory.getLogger(ConnectorService.class);
    private static final String SELF_LINK = "self";
    private static final String HTML_LINK = "html";

    private final ObjectMapper mapper;
    private Map<String, TopLevelJsonapi> topLevelJsonapiDataMap;

    @Inject
    public ContentService(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
        topLevelJsonapiDataMap = new HashMap<>();
    }

    /**
     * Collect all the links inside a page
     *
     * @param content The entire content from a web page
     * @return List with all the links found
     */
    public List<String> collectLinksFromDrupalContent(String url, String content) {
        logger.info("Enter collectLinksFromDrupalContent method...");

        List<String> links = new ArrayList<>();
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

        if (topLevelJsonapi.getData() != null) {
            topLevelJsonapiDataMap.put(url, topLevelJsonapi);
            List<Data> dataList = Arrays.asList(topLevelJsonapi.getData());

            dataList.stream()
                    .filter(data -> data.getRelationships() != null)
                    //.parallel()
                    .forEach(data -> {
                        Collection<RelationshipFields> relationshipFields = data.getRelationships().getFields().values();
                        relationshipFields.forEach(fields -> {
                            fields.getLinks().forEach((linkTag, linkHref) -> {
                                if (!linkTag.equals(SELF_LINK) && !linkTag.equals(HTML_LINK)) {
                                    links.add(linkHref.getHref());
                                }
                            });
                        });
                    });
        }

        if (topLevelJsonapi.getLinks() != null || !topLevelJsonapi.getLinks().isEmpty()) {
            topLevelJsonapiDataMap.put(url, topLevelJsonapi);
            topLevelJsonapi.getLinks().forEach((linkTag, linkHref) -> {
                if (!linkTag.equals(SELF_LINK)) {
                    links.add(linkHref.getHref());
                }
            });
        }

        return links;
    }

    public Map<String, TopLevelJsonapi> getTopLevelJsonapiDataMap(){
        return  topLevelJsonapiDataMap;
    }
}

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
import static org.junit.Assert.assertTrue;

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
        TopLevelJsonapi topLevelJsonapi;

        try {
            topLevelJsonapi = mapper.readValue(content, TopLevelJsonapi.class);

        } catch (IOException e) {
            try {
                topLevelJsonapi = mapper.readValue(content, TopLevelJsonapi.class);
            } catch (IOException ex) {
                throw new ServiceException("The mapper was unable to read the content!", ex);
            }
        }

        topLevelJsonapiMap.put("object", topLevelJsonapi);
        Map<String, Map<String, Object>> allObjectsMap = DataUtil.generateObjectMap(topLevelJsonapiMap);

        assertEquals(2, allObjectsMap.size());

        for (String key : allObjectsMap.keySet()) {
            assertTrue(allObjectsMap.get(key).get("html.link").equals(key));
        }
    }

    private String prepareContent() {
        return "{\n" +
                "  \"jsonapi\": {\n" +
                "    \"version\": \"1.0\",\n" +
                "    \"meta\": {\n" +
                "      \"links\": {\n" +
                "        \"self\": {\n" +
                "          \"href\": \"http://jsonapi.org/format/1.0/\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"type\": \"node--recipe\",\n" +
                "      \"id\": \"ce732d0e-721a-41a2-9f7e-fa758f75c50d\",\n" +
                "      \"links\": {\n" +
                "        \"html\": {\n" +
                "          \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/recipes/deep-mediterranean-quiche\"\n" +
                "        },\n" +
                "        \"self\": {\n" +
                "          \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d?resourceVersion=id%3A6\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"attributes\": {\n" +
                "        \"drupal_internal__nid\": 1,\n" +
                "        \"drupal_internal__vid\": 6,\n" +
                "        \"langcode\": \"en\",\n" +
                "        \"revision_timestamp\": \"2020-06-16T07:56:29+00:00\",\n" +
                "        \"revision_log\": null,\n" +
                "        \"status\": true,\n" +
                "        \"title\": \"Deep mediterranean quiche\",\n" +
                "        \"created\": \"2020-06-16T07:56:29+00:00\",\n" +
                "        \"changed\": \"2020-06-16T07:56:29+00:00\",\n" +
                "        \"promote\": true,\n" +
                "        \"sticky\": false,\n" +
                "        \"default_langcode\": true,\n" +
                "        \"revision_translation_affected\": null,\n" +
                "        \"moderation_state\": \"published\",\n" +
                "        \"path\": {\n" +
                "          \"alias\": \"/recipes/deep-mediterranean-quiche\",\n" +
                "          \"pid\": 331,\n" +
                "          \"langcode\": \"en\"\n" +
                "        },\n" +
                "        \"content_translation_source\": \"und\",\n" +
                "        \"content_translation_outdated\": false,\n" +
                "        \"field_cooking_time\": 30,\n" +
                "        \"field_difficulty\": \"medium\",\n" +
                "        \"field_ingredients\": [\n" +
                "          \"For the pastry:\",\n" +
                "          \"280g plain flour\",\n" +
                "          \"140g butter\",\n" +
                "          \"Cold water\",\n" +
                "          \"For the filling:\",\n" +
                "          \"1 onion\",\n" +
                "          \"2 garlic cloves\",\n" +
                "          \"Half a courgette\",\n" +
                "          \"450ml soya milk\",\n" +
                "          \"500g grated parmesan\",\n" +
                "          \"2 eggs\",\n" +
                "          \"200g sun dried tomatoes\",\n" +
                "          \"100g feta\"\n" +
                "        ],\n" +
                "        \"field_number_of_servings\": 8,\n" +
                "        \"field_preparation_time\": 40,\n" +
                "        \"field_recipe_instruction\": {\n" +
                "          \"value\": \"<ol>\\n  <li>Preheat the oven to 400°F/200°C. Starting with the pastry; rub the flour and butter together in a bowl until crumbling like breadcrumbs. Add water, a little at a time, until it forms a dough.</li>\\n  <li>Roll out the pastry on a floured board and gently spread over your tin. Place in the fridge for 20 minutes before blind baking for a further 10.</li>\\n  <li>Whilst the pastry is cooling, chop and gently cook the onions, garlic and courgette.</li>\\n  <li>In a large bowl, add the soya milk, half the parmesan, and the eggs. Gently mix.</li>\\n  <li>Once the pastry is cooked, spread the onions, garlic and sun dried tomatoes over the base and pour the eggs mix over. Sprinkle the remaining parmesan and careful lay the feta over the top. Bake for 30 minutes or until golden brown.</li>\\n</ol>\\n\",\n" +
                "          \"format\": \"basic_html\",\n" +
                "          \"processed\": \"<ol><li>Preheat the oven to 400°F/200°C. Starting with the pastry; rub the flour and butter together in a bowl until crumbling like breadcrumbs. Add water, a little at a time, until it forms a dough.</li>\\n<li>Roll out the pastry on a floured board and gently spread over your tin. Place in the fridge for 20 minutes before blind baking for a further 10.</li>\\n<li>Whilst the pastry is cooling, chop and gently cook the onions, garlic and courgette.</li>\\n<li>In a large bowl, add the soya milk, half the parmesan, and the eggs. Gently mix.</li>\\n<li>Once the pastry is cooked, spread the onions, garlic and sun dried tomatoes over the base and pour the eggs mix over. Sprinkle the remaining parmesan and careful lay the feta over the top. Bake for 30 minutes or until golden brown.</li>\\n</ol>\"\n" +
                "        },\n" +
                "        \"field_summary\": {\n" +
                "          \"value\": \"An Italian inspired quiche with sun dried tomatoes and courgette. A perfect light meal for a summer's day.\",\n" +
                "          \"format\": \"basic_html\",\n" +
                "          \"processed\": \"<p>An Italian inspired quiche with sun dried tomatoes and courgette. A perfect light meal for a summer's day.</p>\\n\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"relationships\": {\n" +
                "        \"node_type\": {\n" +
                "          \"data\": {\n" +
                "            \"type\": \"node_type--node_type\",\n" +
                "            \"id\": \"0bec7ecc-ba15-4907-9011-d6b476329e78\"\n" +
                "          },\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/node_type?resourceVersion=id%3A6\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/relationships/node_type?resourceVersion=id%3A6\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"revision_uid\": {\n" +
                "          \"data\": {\n" +
                "            \"type\": \"user--user\",\n" +
                "            \"id\": \"a3ded394-12db-4e25-8d70-37fd96ccaf89\"\n" +
                "          },\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/revision_uid?resourceVersion=id%3A6\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/relationships/revision_uid?resourceVersion=id%3A6\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"uid\": {\n" +
                "          \"data\": {\n" +
                "            \"type\": \"user--user\",\n" +
                "            \"id\": \"a3ded394-12db-4e25-8d70-37fd96ccaf89\"\n" +
                "          },\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/uid?resourceVersion=id%3A6\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/relationships/uid?resourceVersion=id%3A6\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"field_media_image\": {\n" +
                "          \"data\": {\n" +
                "            \"type\": \"media--image\",\n" +
                "            \"id\": \"7e43e064-dbd4-4245-8541-22d63c8cdaa5\"\n" +
                "          },\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/field_media_image?resourceVersion=id%3A6\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/relationships/field_media_image?resourceVersion=id%3A6\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"field_recipe_category\": {\n" +
                "          \"data\": [\n" +
                "            {\n" +
                "              \"type\": \"taxonomy_term--recipe_category\",\n" +
                "              \"id\": \"f9cd39e6-5e8a-4ec7-83b0-edfa0ad74c14\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/field_recipe_category?resourceVersion=id%3A6\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/relationships/field_recipe_category?resourceVersion=id%3A6\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"field_tags\": {\n" +
                "          \"data\": [\n" +
                "            {\n" +
                "              \"type\": \"taxonomy_term--tags\",\n" +
                "              \"id\": \"1ee898a9-da19-46c7-b7cf-02e20c666c64\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"type\": \"taxonomy_term--tags\",\n" +
                "              \"id\": \"ba07f0e3-ef59-4feb-bf8a-fb9914782992\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/field_tags?resourceVersion=id%3A6\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/ce732d0e-721a-41a2-9f7e-fa758f75c50d/relationships/field_tags?resourceVersion=id%3A6\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }," +
                "\n" +
                "    {\n" +
                "      \"type\": \"node--recipe\",\n" +
                "      \"id\": \"6671c26c-e325-42a5-b501-d2cdddb612a8\",\n" +
                "      \"links\": {\n" +
                "        \"html\": {\n" +
                "          \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/node/956\"\n" +
                "        },\n" +
                "        \"self\": {\n" +
                "          \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8?resourceVersion=id%3A1441\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"attributes\": {\n" +
                "        \"drupal_internal__nid\": 956,\n" +
                "        \"drupal_internal__vid\": 1441,\n" +
                "        \"langcode\": \"en\",\n" +
                "        \"revision_timestamp\": \"2020-06-26T13:03:33+00:00\",\n" +
                "        \"revision_log\": null,\n" +
                "        \"status\": true,\n" +
                "        \"title\": \"Vegan chocolate and nut brownies\",\n" +
                "        \"created\": \"2020-06-26T13:02:20+00:00\",\n" +
                "        \"changed\": \"2020-06-26T13:03:33+00:00\",\n" +
                "        \"promote\": true,\n" +
                "        \"sticky\": false,\n" +
                "        \"default_langcode\": true,\n" +
                "        \"revision_translation_affected\": true,\n" +
                "        \"moderation_state\": \"published\",\n" +
                "        \"path\": {\n" +
                "          \"alias\": null,\n" +
                "          \"pid\": null,\n" +
                "          \"langcode\": \"en\"\n" +
                "        },\n" +
                "        \"content_translation_source\": \"und\",\n" +
                "        \"content_translation_outdated\": false,\n" +
                "        \"field_cooking_time\": 23,\n" +
                "        \"field_difficulty\": \"hard\",\n" +
                "        \"field_ingredients\": [\n" +
                "          \"6 tbsp sunflower oil\",\n" +
                "          \"80g vegan dark chocolate\",\n" +
                "          \"170g plain flour\",\n" +
                "          \"1 tsp baking powder\"\n" +
                "        ],\n" +
                "        \"field_number_of_servings\": 4,\n" +
                "        \"field_preparation_time\": 45,\n" +
                "        \"field_recipe_instruction\": {\n" +
                "          \"value\": \"<ol>\\r\\n\\t<li>Use a little of the sunflower oil to grease an 8 inch square baking tin (or similar size) and line the tin with greaseproof paper.</li>\\r\\n\\t<li>Preheat the oven to 350°F/180°C.</li>\\r\\n\\t<li>Break approximately 1/3rd of the chocolate bar off and chop into small pieces. Roughly chop 2/3rds of the pecan nuts and mix together with the chopped chocolate. Set aside.</li>\\r\\n\\t<li>For finishing the brownies, chop or crush the remaining pecan nuts and walnuts, mix together and set aside.</li>\\r\\n\\t<li>Melt the remaining chocolate by bringing a couple inches of water to the boil in a small saucepan that is suitably sized for holding a heatproof bowl in the pan opening. Do not allow the bottom of the heatproof bowl to touch the water. Place the chocolate into the bowl to melt, stirring occasionally to ensure the chocolate has fully melted. Once melted, set aside and allow to cool slightly.</li>\\r\\n\\t<li>Whilst the chocolate is melting, begin to sieve the plain flour, coconut flour, and cocoa powder into a large mixing bowl and mix. Once mixed, stir in the baking powder and sugar.</li>\\r\\n\\t<li>Once the chocolate has cooled a little, begin to slowly stir the vanilla essence, sunflower oil, soya milk, and melted chocolate into the flour and cocoa mix.</li>\\r\\n\\t<li>Now stir in the previously chopped chocolate and pecan nuts, ensuring they are stirred evenly into the mixture.</li>\\r\\n\\t<li>Pour the mixture into the baking tin and spread evenly with a spatula.</li>\\r\\n\\t<li>Sprinkle the chopped pecan nuts and walnuts across the top and bake in the centre of the oven for 18 to 23 minutes.</li>\\r\\n\\t<li>Remove from the oven and allow to cool for 45 minutes. Carefully use the edges of the greaseproof paper to lift the brownie out of the tin and place onto a chopping board. With a sharp knife, gently cut into evenly sized pieces.</li>\\r\\n\\t<li>Serve on their own or with some vegan cream or ice cream.</li>\\r\\n</ol>\\r\\n\",\n" +
                "          \"format\": \"basic_html\",\n" +
                "          \"processed\": \"<ol><li>Use a little of the sunflower oil to grease an 8 inch square baking tin (or similar size) and line the tin with greaseproof paper.</li>\\n<li>Preheat the oven to 350°F/180°C.</li>\\n<li>Break approximately 1/3rd of the chocolate bar off and chop into small pieces. Roughly chop 2/3rds of the pecan nuts and mix together with the chopped chocolate. Set aside.</li>\\n<li>For finishing the brownies, chop or crush the remaining pecan nuts and walnuts, mix together and set aside.</li>\\n<li>Melt the remaining chocolate by bringing a couple inches of water to the boil in a small saucepan that is suitably sized for holding a heatproof bowl in the pan opening. Do not allow the bottom of the heatproof bowl to touch the water. Place the chocolate into the bowl to melt, stirring occasionally to ensure the chocolate has fully melted. Once melted, set aside and allow to cool slightly.</li>\\n<li>Whilst the chocolate is melting, begin to sieve the plain flour, coconut flour, and cocoa powder into a large mixing bowl and mix. Once mixed, stir in the baking powder and sugar.</li>\\n<li>Once the chocolate has cooled a little, begin to slowly stir the vanilla essence, sunflower oil, soya milk, and melted chocolate into the flour and cocoa mix.</li>\\n<li>Now stir in the previously chopped chocolate and pecan nuts, ensuring they are stirred evenly into the mixture.</li>\\n<li>Pour the mixture into the baking tin and spread evenly with a spatula.</li>\\n<li>Sprinkle the chopped pecan nuts and walnuts across the top and bake in the centre of the oven for 18 to 23 minutes.</li>\\n<li>Remove from the oven and allow to cool for 45 minutes. Carefully use the edges of the greaseproof paper to lift the brownie out of the tin and place onto a chopping board. With a sharp knife, gently cut into evenly sized pieces.</li>\\n<li>Serve on their own or with some vegan cream or ice cream.</li>\\n</ol>\"\n" +
                "        },\n" +
                "        \"field_summary\": {\n" +
                "          \"value\": \"<p>Scrumptious vegan chocolate brownies that are rich, fudgy, and nutty. These delights have a surprise hint of coconut making them the perfect indulgence. Serve warm with a little vanilla dairy-free ice cream!</p>\\r\\n\",\n" +
                "          \"format\": \"basic_html\",\n" +
                "          \"processed\": \"<p>Scrumptious vegan chocolate brownies that are rich, fudgy, and nutty. These delights have a surprise hint of coconut making them the perfect indulgence. Serve warm with a little vanilla dairy-free ice cream!</p>\\n\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"relationships\": {\n" +
                "        \"node_type\": {\n" +
                "          \"data\": {\n" +
                "            \"type\": \"node_type--node_type\",\n" +
                "            \"id\": \"0bec7ecc-ba15-4907-9011-d6b476329e78\"\n" +
                "          },\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/node_type?resourceVersion=id%3A1441\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/relationships/node_type?resourceVersion=id%3A1441\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"revision_uid\": {\n" +
                "          \"data\": {\n" +
                "            \"type\": \"user--user\",\n" +
                "            \"id\": \"37444321-dc8f-434f-871e-be7a9a9ecd44\"\n" +
                "          },\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/revision_uid?resourceVersion=id%3A1441\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/relationships/revision_uid?resourceVersion=id%3A1441\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"uid\": {\n" +
                "          \"data\": {\n" +
                "            \"type\": \"user--user\",\n" +
                "            \"id\": \"37444321-dc8f-434f-871e-be7a9a9ecd44\"\n" +
                "          },\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/uid?resourceVersion=id%3A1441\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/relationships/uid?resourceVersion=id%3A1441\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"field_media_image\": {\n" +
                "          \"data\": {\n" +
                "            \"type\": \"media--image\",\n" +
                "            \"id\": \"ef0d2ae6-4ae3-41c5-90aa-ace022c53b46\"\n" +
                "          },\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/field_media_image?resourceVersion=id%3A1441\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/relationships/field_media_image?resourceVersion=id%3A1441\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"field_recipe_category\": {\n" +
                "          \"data\": [\n" +
                "            {\n" +
                "              \"type\": \"taxonomy_term--recipe_category\",\n" +
                "              \"id\": \"8b2ffca7-e7b5-4151-94f5-5595044326ab\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/field_recipe_category?resourceVersion=id%3A1441\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/relationships/field_recipe_category?resourceVersion=id%3A1441\"\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"field_tags\": {\n" +
                "          \"data\": [\n" +
                "            {\n" +
                "              \"type\": \"taxonomy_term--tags\",\n" +
                "              \"id\": \"bed81a5a-ec6c-41a3-9ec0-a7cc2e3d19e9\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"links\": {\n" +
                "            \"related\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/field_tags?resourceVersion=id%3A1441\"\n" +
                "            },\n" +
                "            \"self\": {\n" +
                "              \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe/6671c26c-e325-42a5-b501-d2cdddb612a8/relationships/field_tags?resourceVersion=id%3A1441\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"links\": {\n" +
                "    \"self\": {\n" +
                "      \"href\": \"http://s5ee7c4bb7c413wcrxueduzw.devcloud.acquia-sites.com/en/fusion/node/recipe\"\n" +
                "    }\n" +
                "  }" +
                "  }";
    }
}

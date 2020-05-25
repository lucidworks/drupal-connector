package com.lucidworks.fusion.connector.content;

import java.util.Map;

public interface ContentGenerator {

    Map<String, Object> generateFieldsMap();

    Content generateContent(int entryIndexStart, int entryIndexEnd);

}

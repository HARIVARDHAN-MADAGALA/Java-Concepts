package org.example.concepts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDiffrence {

    public static void main(String[] args) throws JsonProcessingException {

        String json1 = "{\"id\":101,\"name\":\"Phanindra\",\"age\":28}";
        String json2 = "{\"iqd\":101,\"name\":\"Phanindra\",\"age\":22}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node1 = mapper.readTree(json1);
        JsonNode node2 = mapper.readTree(json2);

        node1.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            if (!node2.has(key))
                System.out.println(key + " : only in json1 = " + entry.getValue());
            else if (!node2.get(key).equals(entry.getValue()))
                System.out.println(key + " : json1 = " + entry.getValue() + " | json2 = " + node2.get(key));
        });

        node2.fieldNames().forEachRemaining(key -> {
            if (!node1.has(key))
                System.out.println(key + " : only in json2 = " + node2.get(key));
        });
    }
}

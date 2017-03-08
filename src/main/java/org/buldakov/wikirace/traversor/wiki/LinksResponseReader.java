package org.buldakov.wikirace.traversor.wiki;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class LinksResponseReader {

    private final ObjectMapper mapper;

    public LinksResponseReader() {
        mapper = new ObjectMapper();
    }

    public LinksResponse read(String bytes, LinkType type) throws IOException {
        Map<String, String> result = new HashMap<>();
        JsonNode tree = mapper.readTree(bytes);
        JsonNode query = tree.get("query");
        JsonNode pages = query.get("pages");
        for (Iterator<Map.Entry<String, JsonNode>> it = pages.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> page = it.next();
            String title = page.getValue().get("title").asText();
            JsonNode links = page.getValue().get(type.getType());
            if (links != null) {
                for (JsonNode link : links) {
                    result.put(link.get("title").asText(), title);
                }
            }
        }
        Optional<String> next = Optional.ofNullable(tree.get("continue"))
                .map(node -> node.get(type.getPrefix() + "continue"))
                .map(JsonNode::asText)
                .filter(StringUtils::isNotBlank);
        return new LinksResponse(result, next);
    }
}

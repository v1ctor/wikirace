package org.buldakov.wikirace.traversor.wiki;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

class LinksResponse {
    private final Map<String, String> links;
    private final Optional<String> con;

    private static LinksResponse EMPTY = new LinksResponse();

    private LinksResponse() {
        this.links = Collections.emptyMap();
        this.con = Optional.empty();
    }

    LinksResponse(Map<String, String> links, Optional<String> con) {
        this.links = links;
        this.con = con;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public Optional<String> getCon() {
        return con;
    }

    public static LinksResponse empty() {
        return EMPTY;
    }
}

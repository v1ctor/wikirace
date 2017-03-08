package org.buldakov.wikirace.traversor.wiki;

public enum LinkType {

    FROM("links", "pl"),
    TO("linkshere", "lh"),
    ;

    private final String type;
    private final String prefix;

    LinkType(String type, String prefix) {
        this.type = type;
        this.prefix = prefix;
    }

    public String getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }
}

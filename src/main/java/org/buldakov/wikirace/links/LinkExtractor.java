package org.buldakov.wikirace.links;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.List;

public class LinkExtractor {

    public List<String> getLinks(String document, String baseUri) {
        Document doc = Jsoup.parse(document, baseUri);
        LinkCollectVisitor visitor = new LinkCollectVisitor();
        new NodeTraversor(visitor).traverse(doc);

        return visitor.getLinks();
    }

    public static class LinkCollectVisitor implements NodeVisitor {

        private final List<String> links;

        public LinkCollectVisitor() {
            this.links = new ArrayList<>();
        }

        @Override
        public void head(Node node, int depth) {
            if (!(node instanceof Element)) {
                return;
            }
            Element element = (Element) node;
            if (element.tagName().equalsIgnoreCase("a") && element.hasAttr("href")) {
                links.add(element.absUrl("href"));
            }
        }

        @Override
        public void tail(Node node, int depth) {

        }

        public List<String> getLinks() {
            return links;
        }
    }

}

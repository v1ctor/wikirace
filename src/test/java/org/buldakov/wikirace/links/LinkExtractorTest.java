package org.buldakov.wikirace.links;

import org.buldakov.wikirace.LinkExtractor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class LinkExtractorTest {

    @Test
    public void getLinksTest() {
        List<String> links = new LinkExtractor().getLinks("<html><a href=\"http://wikipedia.com/\"/></html>",
                "http://wikipedia.com/");
        Assert.assertEquals(Collections.singletonList("http://wikipedia.com/"), links);
    }

    @Test
    public void getRelativeLinksTest() {
        List<String> links = new LinkExtractor().getLinks("<html><a href=\"/wiki/Matrix\"/></html>",
                "http://wikipedia.com/");
        Assert.assertEquals(Collections.singletonList("http://wikipedia.com/wiki/Matrix"), links);
    }

}
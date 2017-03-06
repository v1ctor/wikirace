package org.buldakov.wikirace.links;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class LinkExtractorTest {

    @Test
    public void getLinksTest() {
        List<String> links = new LinkExtractor().getLinks("<html><a href=\"http://wikipedia.com/\"/></html>");
        Assert.assertEquals(Collections.singletonList("http://wikipedia.com/"), links);
    }

}
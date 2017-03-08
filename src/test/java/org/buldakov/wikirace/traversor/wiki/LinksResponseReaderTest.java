package org.buldakov.wikirace.traversor.wiki;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class LinksResponseReaderTest {

    @Test
    public void read() throws Exception {
        String json = "{\n" +
                "\"continue\": {\n" +
                "\"plcontinue\": \"736|0|Alfred_Kastler\",\n" +
                "\"continue\": \"||\"\n" +
                "},\n" +
                "\"query\": {\n" +
                "\"pages\": {\n" +
                "\"736\": {\n" +
                "\"pageid\": 736,\n" +
                "\"ns\": 0,\n" +
                "\"title\": \"Albert Einstein\",\n" +
                "\"links\": [\n" +
                "{\n" +
                "\"ns\": 0,\n" +
                "\"title\": \"Albert Einstein Peace Prize\"\n" +
                "},\n" +
                "{\n" +
                "\"ns\": 0,\n" +
                "\"title\": \"Albert Einstein World Award of Science\"\n" +
                "}" +
                "]\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}";
        LinksResponse response = new LinksResponseReader().read(json.getBytes(), LinkType.FROM);
        Assert.assertEquals(response.getCon(), Optional.of("736|0|Alfred_Kastler"));
        Assert.assertEquals(response.getLinks(), new HashSet<>(Arrays.asList("Albert Einstein World Award of Science",
                "Albert Einstein Peace Prize")));
    }

}
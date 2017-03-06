package org.buldakov.wikirace.loader;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.buldakov.wikirace.links.LinkExtractor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PageLoader {

    private final String baseUri;
    private final OkHttpClient client;
    private final HttpUrl endpoint;

    public PageLoader(String baseUri) {
        this.baseUri = baseUri;
        this.client = new OkHttpClient();
        this.endpoint = HttpUrl.parse(baseUri);
    }

    public List<HttpUrl> getLinks(String path) {
        Request request = new Request.Builder()
                .url(endpoint.newBuilder().addPathSegment(path).build())
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Error: " + response);
        }
        LinkExtractor extractor = new LinkExtractor();
        List<HttpUrl> links = extractor.getLinks(response.body().string(), baseUri)
                .stream().map(HttpUrl::parse)
                .filter(url -> url.pathSize() > 1)
                .filter(url -> url.host().equals(endpoint.host()))
                .filter(url -> url.scheme().equals(endpoint.scheme()))
                .filter(url -> url.port() == endpoint.port())
                .collect(Collectors.toList());

        return links;
    }
}

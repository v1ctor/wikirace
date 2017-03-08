package org.buldakov.wikirace.traversor.wiki;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WikiClient {

    private static final Logger logger = LoggerFactory.getLogger(WikiClient.class);
    private static final String NAMESPACE = "0|14";

    private final LinksResponseReader reader;
    private final OkHttpClient client;
    private final HttpUrl endpoint;
    private final boolean verbose;

    public WikiClient(HttpUrl hostname, boolean verbose) {
        this.endpoint = hostname.newBuilder().addPathSegments("w/api.php").build();
        this.verbose = verbose;
        this.client = new OkHttpClient();
        reader = new LinksResponseReader();
    }

    public Map<String, String> getFromLinks(List<String> pages) {
        return getAllLinks(pages, LinkType.FROM);
    }

    public Map<String, String> getToLinks(List<String> pages) {
        return getAllLinks(pages, LinkType.TO);
    }

    public Map<String, String> getAllLinks(List<String> pages, LinkType type) {
        StopWatch watch = new StopWatch();
        watch.start();
        Map<String, String> links = new HashMap<>();
        LinksResponse result = LinksResponse.empty();
        do {
            result = getLinks(pages, type, result.getCon());
            links.putAll(result.getLinks());
        } while (result.getCon().isPresent());
        watch.stop();
        if (verbose) {
            logger.info("Download the resources {} {}ms", pages, watch.getTime());
        }
        return links;
    }

    private LinksResponse getLinks(List<String> pages, LinkType type, Optional<String> con) {

        HttpUrl.Builder builder = endpoint.newBuilder()
                .addQueryParameter("titles", String.join("|", pages))
                .addQueryParameter("prop", type.getType())
                .addQueryParameter("format", "json")
                .addQueryParameter("action", "query")
                .addQueryParameter(type.getPrefix() + "namespace", NAMESPACE)
                .addQueryParameter(type.getPrefix() + "limit", "max");
        con.ifPresent(value -> builder.addQueryParameter(type.getPrefix() + "continue", value));

        Request request = new Request.Builder()
                .url(builder.build())
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                if (verbose) {
                    logger.error("Impossible to load resources {}", pages);
                }
                return LinksResponse.empty();
            }

            return reader.read(response.body().string(), type);
        } catch (IOException e) {
            if (verbose) {
                logger.error("Impossible to load resources {}", pages);
            }
        }
        return LinksResponse.empty();
    }

}

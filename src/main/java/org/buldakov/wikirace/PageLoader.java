package org.buldakov.wikirace;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PageLoader {

    private static final Logger logger = LoggerFactory.getLogger(PageLoader.class);

    private final String baseUri;
    private final List<String> excludePrefixes;
    private final OkHttpClient client;
    private final HttpUrl endpoint;
    private final LinkExtractor extractor;
    private final boolean verbose;

    public PageLoader(String baseUri, List<String> excludePrefixes, boolean verbose) {
        this.baseUri = baseUri;
        this.excludePrefixes = excludePrefixes;
        this.verbose = verbose;
        this.client = new OkHttpClient();
        this.endpoint = HttpUrl.parse(baseUri);
        this.extractor = new LinkExtractor();
    }

    public List<String> getPaths(String path) {
        Request request = new Request.Builder()
                .url(endpoint.newBuilder().addPathSegments(StringUtils.removeFirst(path, "/")).build())
                .build();

        try {
            if (verbose) {
                logger.info("Request resource {}", path);
            }
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                logger.error("Impossible to load resource {} {}", path, response);
                response.close();
                return Collections.emptyList();
            }
            String body = response.body().string();

            return extractor.getLinks(body, baseUri)
                    .stream()
                    .map(HttpUrl::parse)
                    .filter(Objects::nonNull)
                    .filter(url -> url.pathSize() > 1)
                    .filter(url -> url.host().equals(endpoint.host()))
                    .filter(url -> url.scheme().equals(endpoint.scheme()))
                    .filter(url -> url.port() == endpoint.port())
                    .map(HttpUrl::encodedPath)
                    .filter(url -> excludePrefixes.stream().noneMatch(prefix -> StringUtils.startsWith(url, prefix)))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Impossible to load resource {}", path);
            return Collections.emptyList();
        }
    }
}

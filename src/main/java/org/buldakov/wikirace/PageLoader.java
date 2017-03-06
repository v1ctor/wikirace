package org.buldakov.wikirace;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PageLoader {

    private static final Logger logger = LoggerFactory.getLogger(PageLoader.class);

    private final String baseUri;
    private final List<String> excludePrefixes;
    private final OkHttpClient client;
    private final HttpUrl endpoint;
    private final LinkExtractor extractor;
    private final boolean verbose;
    private final LoadingCache<String, String> pages;

    public PageLoader(String baseUri, List<String> excludePrefixes, boolean verbose) {
        this.baseUri = baseUri;
        this.excludePrefixes = excludePrefixes;
        this.verbose = verbose;
        this.client = new OkHttpClient();
        this.endpoint = HttpUrl.parse(baseUri);
        this.extractor = new LinkExtractor();
        this.pages = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        return loadPage(key);
                    }
                });
    }

    public Set<String> getPaths(String path) {
        try {
            String body = pages.get(path);

            return extractor.getLinks(body, baseUri)
                    .stream()
                    .map(HttpUrl::parse)
                    .filter(this::filterLink)
                    .map(url -> String.join("/", url.pathSegments()))
                    .collect(Collectors.toSet());
        } catch (ExecutionException e) {
            logger.error("Impossible to load resource {}", path);
            return Collections.emptySet();
        }
    }

    private boolean filterLink(HttpUrl url) {
        return url != null
                && url.pathSize() > 1
                && url.scheme().equals(endpoint.scheme())
                && url.host().equals(endpoint.host())
                && url.port() == endpoint.port()
                && excludePrefixes.stream().noneMatch(prefix -> StringUtils.startsWith(url.encodedPath(), prefix));
    }

    private String loadPage(String path) throws IOException {
        Request request = new Request.Builder()
                .url(endpoint.newBuilder().addPathSegments(StringUtils.removeStart(path, "/")).build())
                .build();

        StopWatch watch = new StopWatch();
        watch.start();

        Response response = client.newCall(request).execute();

        watch.stop();
        if (verbose) {
            logger.info("Download the resource {} {}ms", path, watch.getTime());
        }
        if (!response.isSuccessful()) {
            logger.error("Impossible to load resource {} {}", path, response);
            response.close();
            throw new IllegalStateException("Impossible to load resource");
        }
        return response.body().string();
    }
}

package org.buldakov.wikirace.traversor;

import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;
import org.buldakov.wikirace.error.ValidationException;
import org.buldakov.wikirace.traversor.common.CommonTraversor;
import org.buldakov.wikirace.traversor.wiki.WikiTraversor;

import java.util.List;

public class TraversorFactory {

    private static final String WIKI_SUFFIX = ".wikipedia.org";

    public WebsiteTraversor traversor(String endpoint, List<String> excludes, boolean common, boolean verbose) {
        HttpUrl url = HttpUrl.parse(endpoint);
        if (url == null) {
            throw new ValidationException("Invalid website url");
        }
        if (!common && StringUtils.endsWith(url.host(), WIKI_SUFFIX)) {
            return new WikiTraversor(url, verbose);
        }
        return new CommonTraversor(endpoint, excludes, verbose);
    }


}

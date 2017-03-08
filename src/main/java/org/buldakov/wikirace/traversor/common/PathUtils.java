package org.buldakov.wikirace.traversor.common;

import okhttp3.HttpUrl;
import org.apache.commons.lang3.StringUtils;

public class PathUtils {

    public static String normalize(String path) {
        return StringUtils.removeStart(path, "/");
    }

    public static String urlDecoded(HttpUrl url) {
        return String.join("/", url.pathSegments());
    }
}

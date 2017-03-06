package org.buldakov.wikirace;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class WebsiteTraversor {

    private final PageLoader pageLoader;

    public WebsiteTraversor(String endpoint, List<String> excludePrefixes, boolean verbose) {
        this.pageLoader = new PageLoader(endpoint, excludePrefixes, verbose);
    }

    public List<String> traverse(String from, String to) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(from);
        visited.add(from);
        while (!queue.isEmpty()) {
            String value = queue.remove();
            if (value.equals(to)) {
                return Collections.emptyList();
            }
            List<String> paths = pageLoader.getPaths(value);
            for (String path : paths) {
                if (!visited.contains(path)) {
                    queue.offer(path);
                    visited.add(path);
                }
            }
        }
        return Collections.emptyList();
    }
}

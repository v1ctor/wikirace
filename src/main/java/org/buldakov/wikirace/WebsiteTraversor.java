package org.buldakov.wikirace;

import java.util.*;

public class WebsiteTraversor {

    private final PageLoader pageLoader;

    public WebsiteTraversor(String endpoint, List<String> excludePrefixes, boolean verbose) {
        this.pageLoader = new PageLoader(endpoint, excludePrefixes, verbose);
    }

    public List<String> traverse(String from, String to) {
        Map<String, String> visitedFrom = new HashMap<>();
        Map<String, String> visitedTo = new HashMap<>();
        Queue<String> queueFrom = new LinkedList<>();
        Queue<String> queueTo = new LinkedList<>();
        queueFrom.offer(from);
        queueTo.offer(to);
        visitedFrom.put(from, "");
        visitedTo.put(to, "");
        while (!visitedFrom.isEmpty()) {

            Optional<String> middle = visitFrom(queueFrom, visitedFrom, visitedTo);
            if (middle.isPresent()) {
                return buildPath(middle.get(), visitedFrom, visitedTo);
            }
            middle = visitTo(queueTo, visitedFrom, visitedTo);
            if (middle.isPresent()) {
                return buildPath(middle.get(), visitedFrom, visitedTo);
            }
        }
        return Collections.emptyList();
    }

    private List<String> buildPath(String middle, Map<String, String> visitedFrom, Map<String, String> visitedTo) {
        LinkedList<String> result = new LinkedList<>();
        result.add(middle);

        String current = middle;
        while (visitedFrom.get(current) != null && !visitedFrom.get(current).equals("")) {
            current = visitedFrom.get(current);
            result.addFirst(current);
        }
        current = middle;
        while (visitedTo.get(current) != null && !visitedTo.get(current).equals("")) {
            current = visitedTo.get(current);
            result.add(current);
        }
        return result;
    }

    private Optional<String> visitFrom(Queue<String> queueFrom, Map<String, String> visitedFrom,
                                       Map<String, String> visitedTo) {

        String value = queueFrom.remove();
        if (visitedTo.containsKey(value)) {
            return Optional.of(value);
        }
        Set<String> paths = pageLoader.getPaths(value);
        for (String path : paths) {
            if (!visitedFrom.containsKey(path)) {
                queueFrom.offer(path);
                visitedFrom.put(path, value);
            }
        }

        return Optional.empty();
    }

    private Optional<String> visitTo(Queue<String> queueTo, Map<String, String> visitedFrom,
                                     Map<String, String> visitedTo) {
        String value = queueTo.remove();
        if (visitedFrom.containsKey(value)) {
            return Optional.of(value);
        }
        Set<String> paths = pageLoader.getPaths(value);
        for (String path : paths) {
            //we need to check that this page actually have links to the destination page, because
            //links graph unidirectional.
            if (!visitedTo.containsKey(path) && pageLoader.getPaths(path).contains(value)) {
                queueTo.offer(path);
                visitedTo.put(path, value);
            }
        }

        return Optional.empty();
    }
}

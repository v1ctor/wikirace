package org.buldakov.wikirace.traversor.common;

import org.buldakov.wikirace.traversor.AbstractTraversor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class CommonTraversor extends AbstractTraversor {

    private final PathLoader pathLoader;

    public CommonTraversor(String endpoint, List<String> excludePrefixes, boolean verbose) {
        this.pathLoader = new PathLoader(endpoint, excludePrefixes, verbose);
    }

    @Override
    protected Optional<String> visitFrom(Queue<String> queueFrom, Map<String, String> visitedFrom,
                                       Map<String, String> visitedTo) {
        while (!queueFrom.isEmpty()) {
            String value = queueFrom.remove();
            Set<String> paths = pathLoader.getPaths(value);
            for (String path : paths) {
                if (!visitedFrom.containsKey(path)) {
                    queueFrom.offer(path);
                    visitedFrom.put(path, value);
                    if (visitedTo.containsKey(path)) {
                        return Optional.of(path);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    protected Optional<String> visitTo(Queue<String> queueTo, Map<String, String> visitedFrom,
                                     Map<String, String> visitedTo) {
        while (!queueTo.isEmpty()) {
            String value = queueTo.remove();
            Set<String> paths = pathLoader.getPaths(value);
            for (String path : paths) {
                if (!visitedTo.containsKey(path)) {
                    //we need to check that this page actually have back link to the destination page, because
                    //links graph unidirectional.
                    if (!pathLoader.getPaths(path).contains(value)) {
                        continue;
                    }
                    visitedTo.put(path, value);
                    queueTo.offer(path);
                    if (visitedFrom.containsKey(path)) {
                        return Optional.of(path);
                    }
                }
            }
        }

        return Optional.empty();
    }
}

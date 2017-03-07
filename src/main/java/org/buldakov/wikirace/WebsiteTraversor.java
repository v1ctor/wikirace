package org.buldakov.wikirace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebsiteTraversor {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteTraversor.class);

    private final PageLoader pageLoader;

    public WebsiteTraversor(String endpoint, List<String> excludePrefixes, boolean verbose) {
        this.pageLoader = new PageLoader(endpoint, excludePrefixes, verbose);
    }

    public List<String> traverse(String from, String to) {
        Map<String, String> visitedFrom = new ConcurrentHashMap<>();
        Map<String, String> visitedTo = new ConcurrentHashMap<>();
        Queue<String> queueFrom = new LinkedList<>();
        Queue<String> queueTo = new LinkedList<>();
        queueFrom.offer(from);
        queueTo.offer(to);
        visitedFrom.put(from, "");
        visitedTo.put(to, "");

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            AtomicBoolean finished = new AtomicBoolean(false);
            Optional<String> middle = executor.invokeAny(Arrays.asList(
                    () -> visitFrom(queueFrom, visitedFrom, visitedTo, finished),
                    () -> visitTo(queueTo, visitedFrom, visitedTo, finished)));
            finished.compareAndSet(false, true);
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
            if (middle.isPresent()) {
                return buildPath(middle.get(), visitedFrom, visitedTo);
            }
        } catch (Exception e) {
            logger.error("Invocation failed");
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
                                       Map<String, String> visitedTo, AtomicBoolean finished) {
        while (!queueFrom.isEmpty() && !finished.get()) {
            String value = queueFrom.remove();
            if (visitedTo.containsKey(value)) {
                return Optional.of(value);
            }
            Set<String> paths = pageLoader.getPaths(value);
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

    private Optional<String> visitTo(Queue<String> queueTo, Map<String, String> visitedFrom,
                                     Map<String, String> visitedTo, AtomicBoolean finished) {
        while (!queueTo.isEmpty() && !finished.get()) {
            String value = queueTo.remove();
            if (visitedFrom.containsKey(value)) {
                return Optional.of(value);
            }
            Set<String> paths = pageLoader.getPaths(value);
            for (String path : paths) {
                if (!visitedTo.containsKey(path) && !finished.get()) {
                    //we need to check that this page actually have back link to the destination page, because
                    //links graph unidirectional.
                    if (!pageLoader.getPaths(path).contains(value)) {
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

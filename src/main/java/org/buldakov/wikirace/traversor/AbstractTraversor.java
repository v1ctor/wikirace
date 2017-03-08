package org.buldakov.wikirace.traversor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractTraversor implements WebsiteTraversor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTraversor.class);

    @Override
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
            //Run back link search in a separate thread
            List<Future<Optional<String>>> futures = executor.invokeAll(Arrays.asList(
                    () -> visitTo(queueTo, visitedFrom, visitedTo),
                    () -> visitFrom(queueFrom, visitedFrom, visitedTo)));

            Optional<String> middleFrom = futures.get(0).get();
            Optional<String> middleTo = futures.get(1).get();
            executor.shutdown();
            if (middleFrom.isPresent()) {
                return buildPath(middleFrom.get(), visitedFrom, visitedTo);
            } else if (middleTo.isPresent()) {
                return buildPath(middleTo.get(), visitedFrom, visitedTo);
            }
        } catch (Exception e) {
            logger.error("Invocation failed", e);
        }
        return Collections.emptyList();
    }

    protected abstract Optional<String> visitTo(Queue<String> queueTo, Map<String, String> visitedFrom,
                                                Map<String, String> visitedTo);

    protected abstract Optional<String> visitFrom(Queue<String> queueFrom, Map<String, String> visitedFrom,
                                                  Map<String, String> visitedTo);

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
}

package org.buldakov.wikirace.traversor.wiki;

import okhttp3.HttpUrl;
import org.buldakov.wikirace.traversor.AbstractTraversor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;

public class WikiTraversor extends AbstractTraversor {

    private static final int BATCH_SIZE = 50;

    private final WikiClient client;

    public WikiTraversor(HttpUrl hostname, boolean verbose) {
        this.client = new WikiClient(hostname, verbose);
    }

    @Override
    protected Optional<String> visitFrom(Queue<String> queue, Map<String, String> visitedFrom,
                                         Map<String, String> visitedTo) {
        return visit(queue, visitedFrom, visitedTo, client::getFromLinks);
    }

    @Override
    protected Optional<String> visitTo(Queue<String> queue, Map<String, String> visitedFrom,
                                       Map<String, String> visitedTo) {
        return visit(queue, visitedTo, visitedFrom, client::getToLinks);
    }

    private Optional<String> visit(Queue<String> queue, Map<String, String> front, Map<String, String> back,
                                   Function<List<String>, Map<String, String>> linkLoader) {

        while (!queue.isEmpty()) {
            List<String> pages = new ArrayList<>(queue.size());
            while (!queue.isEmpty() && pages.size() <= BATCH_SIZE) {
                pages.add(queue.remove());
            }
            Map<String, String> paths = linkLoader.apply(pages);
            for (Map.Entry<String, String> path : paths.entrySet()) {
                if (!front.containsKey(path.getKey())) {
                    front.put(path.getKey(), path.getValue());
                    queue.offer(path.getKey());
                    if (back.containsKey(path.getKey())) {
                        return Optional.of(path.getKey());
                    }
                }
            }
        }
        return Optional.empty();
    }
}

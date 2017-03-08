package org.buldakov.wikirace;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.time.StopWatch;
import org.buldakov.wikirace.error.ValidationException;
import org.buldakov.wikirace.traversor.TraversorFactory;
import org.buldakov.wikirace.traversor.common.PathUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("wikirace")
                .defaultHelp(true)
                .description("Find a path from two resources on website if exists");
        parser.addArgument("-f", "--from")
                .required(true)
                .help("Specify the source resource");
        parser.addArgument("-t", "--to")
                .required(true)
                .help("Specify the destination resource");
        parser.addArgument("-w", "--website")
                .setDefault("https://en.wikipedia.org/")
                .help("Specify a website where we need to find a path");
        parser.addArgument("-e", "--exclude")
                .help("Link prefixes or titles to exclude")
                .action(Arguments.append());
        parser.addArgument("-v", "--verbose")
                .action(Arguments.storeTrue())
                .help("Verbosity level");
        parser.addArgument("-c", "--common")
                .action(Arguments.storeTrue())
                .help("Force common traversor usage");

        try {
            Namespace ns = parser.parseArgs(args);
            String website = ns.getString("website");
            String from = PathUtils.normalize(ns.getString("from"));
            String to = PathUtils.normalize(ns.getString("to"));
            List<String> excludes = ns.<String>getList("exclude").stream()
                    .map(PathUtils::normalize).collect(Collectors.toList());
            boolean verbose = ns.getBoolean("verbose");
            boolean common = ns.getBoolean("common");

            StopWatch watch = new StopWatch();
            watch.start();

            List<String> result = new TraversorFactory().traversor(website, excludes, common, verbose).traverse(from, to);

            watch.stop();

            System.out.println("Time elapsed: " + watch.getTime() + "ms ");
            if (result.isEmpty()) {
                System.out.println("There is no path");
            } else {
                System.out.println(result);
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

}

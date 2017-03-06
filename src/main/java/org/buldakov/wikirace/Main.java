package org.buldakov.wikirace;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("wikirace")
                .defaultHelp(true)
                .description("Find the path from two resources on website if exists");
        parser.addArgument("-f", "--from")
                .required(true)
                .help("Specify the source resource");
        parser.addArgument("-t", "--to")
                .required(true)
                .help("Specify the destination resource");
        parser.addArgument("-w", "--website")
                .setDefault("https://en.wikipedia.org/")
                .help("Specify website to find the path");
        parser.addArgument("-e", "--exclude")
                .help("Prefixes to exclude")
                .action(Arguments.append());
        parser.addArgument("-v", "--verbose")
                .setDefault(false)
                .choices(true, false)
                .type(Boolean.class)
                .help("Verbosity level");
        try {
            Namespace ns = parser.parseArgs(args);
            String from = ns.getString("from");
            String to = ns.getString("to");
            List<String> prefixes = ns.getList("exclude");
            boolean verbose = ns.getBoolean("verbose");


            StopWatch watch = new StopWatch();
            watch.start();

            List<String> result = findPath(ns, from, to, prefixes, verbose);

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
        }
    }

    private static List<String> findPath(Namespace ns, String from, String to, List<String> prefixes, boolean verbose) {
        WebsiteTraversor travesrsor = new WebsiteTraversor(ns.getString("website"), prefixes, verbose);
        return travesrsor.traverse(from, to);
    }

}

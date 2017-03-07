# wikirace


##Problem description
There’s an online form of racing called “Wikiracing”.  The goal is to traverse your way from one wikipedia page to another, using only links. 
For example the race of from “National_Basketball_Association” page → to the "Kobe_Bryant" page might be completed by traversing 3 hops:
  National_Basketball_Association -> NBA_Final
    NBA_Finals -> Los_Angeles_Lakers
    Los_Angeles_Lakers → Kobe_Bryant

We want to try and complete a wikirace as quickly as possible without requiring a complete graph (since wiki pages are changing all the time). For bonus points, we also want to be able to run this tool on any domain, not just Wikipedia. But starting against Wikipedia.com is the best place to start.

Your Wikiracer should take a starting point and an end page, and successfully figure out how to traverse from one to the other, or tell the user if there isn’t a path. 

Your Wikiracer gets points for finishing quickly, not for the lowest number of hops. You’ll want to optimize the speed of reaching one to the other. The output should be a list of paths, as well as the total elapsed time to run. You're free to use any techniques you'd like, third-party libraries, APIs, etc. Just tell us about the trade-offs of whatever implementation you choose.

Key requirements
We'd like to see the following things:
    - The code you write. We'll be looking both at cleanliness and the strategy you implement
    - A quick guide to get the wikiracer running. if it doesn't fully run, we'd like to see an explanation of whatever you're using to test
    - A README file explaining what the code does, what strategies you tried, and how long you spent on each part of the projects

## Solution

### Components

#### Extracting links
1. First approach is to use Wikipedia API. It could improve request speed but this solution isn't portable.  
2. Parse links by my own. This approach is slower but it fits better to our requirements. **(1 hour)**

#### Find a path
1. First I've written simple BFS solution, but it was working slowly. **(1 hour)**

2. Then I decided to implement bidirectional BFS, but tricky part in our case is that the wiki graph is unidirectional, so
 if the destination page have links to some page we could only assume that these pages may have link to our destination page.
 To check it we also need to download these pages, extract links from them, and check if our current page in these links.
 To avoid loading pages twice, first time for back-link, and second time in a regular workflow, I have added caching of already loaded
 pages. **(3 hours)**

## Usage
```bash
$ gradle distZip
$ unzip build/distributions/wikirace-1.0-SNAPSHOT.zip 
$ wikirace-1.0-SNAPSHOT/bin/wikirace -h
usage: wikirace [-h] -f FROM -t TO [-w WEBSITE] [-e EXCLUDE] [-v]

Find the path from two resources on website if exists

optional arguments:
  -h, --help             show this help message and exit
  -f FROM, --from FROM   Specify the source resource
  -t TO, --to TO         Specify the destination resource
  -w WEBSITE, --website WEBSITE
                         Specify website to find the path (default: https://en.wikipedia.org/)
  -e EXCLUDE, --exclude EXCLUDE
                         Prefixes to exclude
  -v, --verbose          Verbosity level (default: false)
$ wikirace-1.0-SNAPSHOT/bin/wikirace --vervose -f "/wiki/Hitler" -t "/wiki/Tinder" -e "/wiki/File:" -e "/wiki/Special:" -e "/wiki/Wikipedia:" -e "/w/index.php"  
```

## Examples
```bash
$ wikirace-1.0-SNAPSHOT/bin/wikirace -f "/wiki/Kitten" -t "/wiki/Hitler" -e "/wiki/File:" -e "/wiki/Special:" -e "/wiki/Wikipedia:" -e "/w/index.php"
Time elapsed: 14886ms 
[wiki/Kitten, wiki/JSTOR, wiki/Associated_Press, wiki/Hitler] 
```

## Limitations
1. Because this solution is appropriate for another websites except wikipedia, we need to specify the whole path instead 
of title.
2. In Wiki API it's possible to group requests in batches. This isn't possible with my solution, because I have to
download html page and then get links from it.
3. Right know we have only 2 simultaneous requests because otherwise we could be blocked by wikipedia admins.
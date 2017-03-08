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
1. First thought is to use Wikipedia API. It could improve request speed but this solution isn't portable to other sites.
2. Parse links by my own. This approach is slower but it fits better to our requirements. **(1 hour)**  
3. After complete common case I decided to optimize solution for the Wikipedia specially. 
I use Wikipedia API to retrieve links from and to web page. We could force usage of the common solution by passing option ```--common```. **(1 hour)**

#### Find a path
1. First I've written a simple BFS solution, but it was working slowly. **(1 hour)**  
2. Then I decided to implement bidirectional BFS, but the tricky part in our case is that the wiki graph is unidirectional, so if the destination page has links to some page we could only assume that these pages may have link to our destination page. To check it we also need to download these pages, extract links from them, and check if our current page is among these links. To avoid loading pages twice, first time for back-link, and second time in a regular workflow, I have added caching of already loaded pages. **(3 hours)**

## Usage
```bash
$ gradle distZip
$ unzip build/distributions/wikirace-1.0-SNAPSHOT.zip 
$ wikirace-1.0-SNAPSHOT/bin/wikirace -h
usage: wikirace [-h] -f FROM -t TO [-w WEBSITE] [-e EXCLUDE] [-v] [-c]

Find a path from two resources on website if exists

optional arguments:
  -h, --help             show this help message and exit
  -f FROM, --from FROM   Specify the source resource
  -t TO, --to TO         Specify the destination resource
  -w WEBSITE, --website WEBSITE
                         Specify a website where  we  need  to  find a path
                         (default: https://en.wikipedia.org/)
  -e EXCLUDE, --exclude EXCLUDE
                         Link prefixes or titles to exclude
  -v, --verbose          Verbosity level (default: false)
  -c, --common           Force usage of the common traversor (default: false)

$ wikirace-1.0-SNAPSHOT/bin/wikirace --vervose -f "/wiki/Hitler" -t "/wiki/Tinder" -e "/wiki/File:" -e "/wiki/Special:" -e "/wiki/Wikipedia:" -e "/w/index.php"  
```

## Examples
```bash
$ wikirace-1.0-SNAPSHOT/bin/wikirace --common -f "/wiki/Kitten" -t "/wiki/Hitler" -e "/wiki/File:" -e "/wiki/Special:" -e "/wiki/Wikipedia:" -e "/w/index.php"
Time elapsed: 23993ms 
[wiki/Kitten, wiki/Russian_Blue, wiki/World_War_II, wiki/Hitler]
$ wikirace-1.0-SNAPSHOT/bin/wikirace -f "Kitten" -t "Hitler"
Time elapsed: 7533ms 
[Kitten, Chartreux, Grenoble, Ardennes (department), Hitler]
```

## Limitations
1. By default en.wikipedia.org is used. For default case we have to pass page titles in arguments. This solution is optimized for Wikipedia but we could force to execute common solution for Wikipedia by using 
```--common``` flag. Because this solution is working not only for Wikipedia but also for other websites, we have to specify the whole path instead of a title. 
2. In Wiki API it's possible to group requests in batches. This isn't possible in my solution, because I have to download an html page first and then get links from it. 
3. Right now we can make only 2 simultaneous requests because otherwise we will be blocked by Wikipedia admins.
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
1. First approach is to use Wikipedia API but it doesn't scalable and doesn't fit to requirements as we have to work with different websites.
2. 

#### Find a path


#### Usage
```bash
java wikirace --vervose -f "/wiki/Hitler" -t "/wiki/Tinder" -e "/wiki/File:" -e "/wiki/Special:" -e "/wiki/Wikipedia:" -e "/w/index.php"  
```
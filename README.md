# BLOOMBERG NEWS INCIDENCE ANALYSIS

_Professor Terrence Hendershott with assistance from Mustafa M. Eisa_

### CRSP U.S. Stock Database

Standard daily prices data 2010-2015 including the following: Open, close, low, high; returns (with and without dividens) and excess returns; trading volume, market cap, shares outstanding; and other supplementary information. Covers all equities in NYSE, NASDAQ, and Arca along with composite indices, equal and value-weighted exchange returns.

Schema is as follows:

```
[PERMNO,date,NAMEENDT,SHRCD,EXCHCD,SICCD,NCUSIP,TICKER,COMNAM,SHRCLS,TSYMBOL,NAICS,PRIMEXCH,TRDSTAT,SECSTAT,PERMCO,ISSUNO,HEXCD,HSICCD,CUSIP,DCLRDT,DLAMT,DLPDT,DLSTCD,NEXTDT,PAYDT,RCRDDT,SHRFLG,HSICMG,HSICIG,DISTCD,DIVAMT,FACPR,FACSHR,ACPERM,ACCOMP,NWPERM,DLRETX,DLPRC,DLRET,TRTSCD,NMSIND,MMCNT,NSDINX,BIDLO,ASKHI,PRC,VOL,RET,BID,ASK,SHROUT,CFACPR,CFACSHR,OPENPRC,NUMTRD,RETX,vwretd,vwretx,ewretd,ewretx,sprtrn]
```
Data types and comments available at [CRSP's official site](http://www.crsp.com/products/documentation/stock-data-structure).
We produce a filtered dataframe of the form
```
[PERMNO: long, date: %Y%m%d, SHRCD: long, EXCHCD: long, SICCD: long, TICKER: str, SHROUT: long, OPENPRC: double, PRC: double, RET: double, BIDLO: double, ASKHI: double, VOL: long]
```
We disambiguate the column names as follows
- `PERMNO`: Unique security identifier.
- `SHRCD`: Share code.
- `EXCHCD`: Exchange code.
- `SICCD`: Standard Industrial Classification code (for mapping securities to industries).
- `SHROUT`: Shares outstanding (in 1000’s).
- `OPENPRC`: Daily opening price.
- `PRC`: Daily closing price.
- `RET`: Daily return.
- `BIDLO`: Daily lowest bid price.
- `ASKHI`: Daily highest ask price.
- `VOL`: Daily trading volume.

**For relational querying.** Using `TICKER` as key, the dataframe can be split, with with columns renamed for simplicity.
**For NoSQL solution.** The filtered dataframe, which in `CSV` format is under 3GB in size, may simply be loaded into an active spark session, compressed to `ORC` format, and stored on Amazon S3. All processing may then be carried out in Spark Shell or PySpark during the subsequent modeling sessions.

_If the closing price is not available for any given period, the number in the price field is replaced with a bid/ask average. Bid/ask averages have dashes placed in front of them. These do not incorrectly reflect negative prices; they serve to distinguish bid/ask averages from actual closing prices. If neither the price nor bid/ask average is available, the field is set to zero._

### TRNA NEWS ARCHIVE

Reuters NewsScope Archive is a historical database of Reuters’ and select third party news stories. Messages are timestamped to the millisecond and retain all sequencing and control data.

**Record structure.** Each release consists of a body together with its associated headline, also referred to as a *take*. Stories often begins with an alert—a short sentence that contains the facts and essential detail. It is common for several alerts to be filed in quick succession. A newsbreak is then released minutes later, containing body text, details, and context. An update may be filed 20-30 minutes after a newsbreak. Updates comprise a headline (sometimes different to the headline in the original newsbreak) and additional body text and information.

Headlines are of the form <headline tag>-<headline body>, while body text contains details such as author, time, location, and a set of keywords displayed at the tail of the article.

**Additional takes.** The story body together with its associated headline is called a take. Stories are usually filed in a single take. Occasionally, however, further takes are necessary to add text or codes to an existing story. These followup takes are filed with the same headline, but with the additional text and/or codes contained in their respective fields. Any takes with the `XREF` topic code are cross-reference tables for existing article, which suggests custom follow up articles at the end of the current story, in place of default suggestions.

**Updates.** If story take needs to be revised with additional information (e.g. fresh developments, reaction, added context or interpretation) an update is issued. The update may be refreshed as the story develops; these subsequent updates are filed either by replacing the previous update or by appending the latest information.
- Most subsequent updates supplant the previous update in the series, with the update number noted in the headline tag (e.g. “UPDATE2-Nemo enim…”)
- Some updates *append* to previous updates. In this case, the update number will be appended to the headline (e.g. “UPDATE-Nemo…aut odit =3”)

**Errors.** If there is a minor error in a story it is refiled in one of two ways: replacement or overwrite.
- In most refiles, the headline and body are both republished, with the headline tag `REFILE` (e.g. “REFILE-Nemo…”).
- If a correction must be made to a headline or body text, the story is reissued with the tag `CORRECTED` (e.g. “CORRECTED-Nemo enim…”).

**Data Structure.** The records in the archive table, provided as a compressed `TSV`, will be reduced to the following
```
[STOCK_RIC,…, RELEVANCE, SENTIMENT, SENT_POS, SENT_NEUT, SENT_NEG,…, ITEM_TYPE, ITEM_GENRE, BCAST_TEXT,…, PNAC,…, TAKE_TIME, STORY_DATE, STORY_TIME, TAKE_SEQNO, ATTRIBTN,…, TOPIC_CODE,…, LANG_IND, TIMESTAMP,…, SENT_WORDS, TOT_WORDS]
```
We proceed to disambiguate the field names above:
- `STOCK_RIC`: Reuters instrument code. All financial instruments including currencies, stocks, bonds and physical commodities are assigned a RIC. Typically of the form `<ticker>.<exchange identifier>` (e.g. AAPL.N for Apple in NYSE). Many unlisted companies and entities also have Instrument RICs; these are identified with square brackets and a UL (unlisted) identifier instead of an exchange identifier, e.g. [ARBU.UL] for Airbus. See [here](http://quant.stackexchange.com/questions/7568/mapping-symbols-between-tickers-reuters-rics-and-bloomberg-tickers) for more. 
- `RELEVANCE`: A real valued number (between 0.0 and 1.0) indicating the relevance of the news item to the asset.
- `SENTIMENT`: Tertiary {-1, 0, 1} indicating negative, neutral, or positive sentiment.
- `SENT_*`: The probability that the sentiment of the news item was * for the asset.
- `ITEM_TYPE`: Type of NewsScope event generated by the news item. One of the following: Alert, Article, Append, Overwrite.
- `ITEM_GENRE`: News release headline tag (e.g. `ADVISORY` or `NEWSMAKER`).
- `BCAST_TEXT`: Headline of the news release (255 characters or less).
- `PNAC`: Primary News Access Code. Creates a unique identification code when combined with `STORY_DATE` and `STORY_TIME`.
- `TAKE_TIME`: GMT time that take was issued.
- `STORY_DATE`: Date the very first take of a particular story was issued (therefore is the same for all takes associated with a given story).
- `STORY_TIME`: Same as `STORY_DATE`, but with time as opposed to date.
- `TAKE_SEQNO`: The take number for a given story. Starts at 1 for each story and increments with each release.
- `ATTRIBTN`: Source the news was collected from (e.g. `RTRS` for Reuters). We will exclusively use `RTRS` in this project.
- `TOPIC_CODE`: Categorical tags describing the story’s
subject matter (e.g. POL AR for politics and Argentina). Mapping TOPIC_CODE to human-readable string is provided in an auxiliary table.
- `LANG_IND`: Language ID, indicating which language the article is composed.
- `TIMESTAMP`: Date and time (up to millisecond) the news story was transmitted.
- `SENT_WORDS`: Number of sentiment words in the release text.
- `TOT_WORDS`: Total number of words in the release text.

**For relational querying.** Like in the CRSP daily prices case, the news may simply be partitioned by `RIC`, thereby generating a table of news events for each ticker.

**For NoSQL solution.** The archive data may be partitioned by quarter and compressed to `ORC`. In the modeling stages, the relevant periods may be extracted and merged.

### Bloomberg Event-Driven Feeds: News Wire

According to the [Event-Driven Feeds](https://www.bloomberg.com/enterprise/content-data/event-driven-feeds/) product overview, “Bloomberg News moves markets thousands of times a year, providing content on every asset class…the wire publishes more than 5,000 stories a day.“ Files are stored in `XML` format such as `EID34151_yyyymmdd.xml`, where the code `EID34151` marks all news wire data.

Each archive begins with a date field of the form 

```
<Archive StartTime="2016-05-01T00:00:00.000+00:00"

    EndTime="2016-05-02T00:00:00.000+00:00">
```

to indicate the news coverage period. Each news item then begins with a content tag of the form

```
<ContentT EID="34151" CaptureTime="2016-05-01T00:01:03.509+00:00"

        Origin="API" SchemaVersion="2016-01-09T00:00:00.000+00:00">
```

where `EID` reaffirms the subscription type (in this case News Wire) and other fields provide other metadata. For the following, the term **assigned** indicates assignment by editor while **derived** means inferred using an algorithm. The following attributes are embedded within each story item:

- `<Story ContentType=…>` A Story element with a content type other than "Current" occurs only on story update events, when there is old content in the message.
- `<Event>` can be one of the following:
- * `ADD_STORY` contains assigned and derived items
- * `ADD_1STPASS` the tag used when multiple, intermediate information is sent down the feed at multiple times under different “Actions.”
- * `UPDATE_ATTRIBUTE` used if an existing story is modified
- * `DELETE_STORY` used to indicate the deletion of a pre-existing story
- `<Body>` the body of the news story (typically article text)
- `<Metadata>` includes `<TimeOfArrival>` and `<Headline>`
- `<AssignedTickers>` lists assigned tickers with an accompanying relevancy score and additional identifiers
- `<AssignedTopics>` lists assigned topics with an accompanying relevancy score
- `<AssignedPeople>` lists assigned people (by ID, not name) and a relevancy score
- The remaining trailing fields include a boolean `<isMarketMover>` indicating whether the story item has “a high likelihood of causing a significant price move in an equity security,” and a set of unverified tickers (which are likely tickers that did not achieve a high relevance)

There are `Derived` versions of all the `Assigned` fields listed above. A score of 69 or less is considered low, 70-94 is medium, and 95+ is high. 

### Issues with Spark 1.6 on EC2

**AWS CREDENTIALS BUG**

Even when including `copy_aws_credentials`, permissions are not passed to the cluster. After `ssh` into `root@master`, export `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` for access to S3 etc.

**SPARK VERBOSITY**

Spark is by default verbose. To show warnings only, run the following

```
cd $SPARK_HOME/conf/
mv ./log4j.properties.template ./log4j.properties
vim log4j.properties
```

then simply change `log4j.rootCategory=INFO` to `log4j.rootCategory=WARN`.

**IMPORTING SPARK-CSV PACKAGE**

To import the `spark-csv` package, launch spark as follows

```
$SPARK_HOME/bin/<spark-script> --packages com.databricks:spark-csv_2.10:1.4.0
```

**BROKEN SQLCONTEXT**

For both `spark-shell` and `pyspark`, there are issues with the `sqlContext` instantiated upon launch of the script in `$SPARK_HOME/bin/`. Therefore, after launching `spark-shell`, run

```
val sqlContext = new org.apache.spark.sql.SQLContext(sc)
```

or, for `pyspark`,

```
from pyspark.sql import SQLContext
sqlContext = SQLContext(sc)
```

// for filtering columns on master csv and writing to disk as orc

val rootBucket = "s3n://bloombergprices/TRNA/"
val years = Array("2010", "2011", "2012", "2013", "2014", "2015")

years.map{ year =>
	val path = rootBucket + "archive" + year + ".txt"
	sqlContext.
		read.
		format("csv").
		option("header", "true").
		option("inferschema", "true").
		option("mode", "DROPMALFORMED").
		option("delimiter", "\t").
		load(path).
		select("PNAC", "STOCK_RIC", "RELEVANCE",
			"SENTIMENT", "SENT_POS", "SENT_NEUT", "SENT_NEG",
			"ITEM_TYPE", "ITEM_GENRE", "BCAST_TEXT",
			"TAKE_TIME", "STORY_DATE", "STORY_TIME",
			"TAKE_SEQNO", "ATTRIBTN", "TOPIC_CODE",
			"LANG_IND", "TIMESTAMP", "SENT_WORDS",
			"TOT_WORDS").
		write.format("parquet").
		save(rootBucket + "model" + year)
	"success"
	}.foreach(println)














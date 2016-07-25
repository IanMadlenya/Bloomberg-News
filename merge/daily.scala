// takes ticker merges news with prices

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
import sqlContext.implicits._
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions.udf
import java.sql.Date
var ticker = "ADS" // or sys.arg

// CRSP

val CRSPDateformat = new java.text.SimpleDateFormat("yyyyMMdd")

def CRSPDates(dateInt: Int): java.sql.Date = {
	val dateTime = CRSPDateformat.parse(dateInt.toString).getTime
	val sqlDate = new java.sql.Date(dateTime)
	return sqlDate
}

val CRSPDatesUDF = udf(CRSPDates(_:Int))

val CRSP = sqlContext.read.format("parquet").
	load("s3n://bloombergprices/CRSP/model").
	filter($"TICKER"===ticker).
	withColumn("date", CRSPDatesUDF($"date")).
	drop($"PERMNO").drop($"TICKER")

// TRNA

val TRNADateformat = new java.text.SimpleDateFormat("dd MMM yyyy")

def TRNADates(dateString: String): java.sql.Date = {
	val dateTime = TRNADateformat.parse(dateString).getTime
	val sqlDate = new java.sql.Date(dateTime)
	return sqlDate
}

val TRNADatesUDF = udf(TRNADates(_:String))

def getTicker(RIC: String): String = {
	return RIC.split("\\.")(0)
}

val getTickerUDF = udf(getTicker(_:String))

case class TRNARow(storyDate: java.sql.Date, relevance: Double,
			senti: Double, pos: Double, neut: Double, neg: Double,
			sentWords: Double, totWords: Double)

val TRNA = Array("2010","2011","2012","2014","2015").
	map{ year => 
		sqlContext.read.format("parquet").
		load("s3n://bloombergprices/TRNA/model" + year) }.
		reduce( (rdd1, rdd2) => rdd1.unionAll(rdd2) ).
		withColumn("STOCK_RIC", getTickerUDF($"STOCK_RIC")).
		withColumnRenamed("STOCK_RIC", "TICKER").
		filter($"TICKER"===ticker).
		withColumn("STORY_DATE", TRNADatesUDF($"STORY_DATE")).
		select("STORY_DATE","RELEVANCE","SENTIMENT",
			"SENT_POS","SENT_NEUT","SENT_NEG",
			"SENT_WORDS","TOT_WORDS").
		map{ case Row(storyDate: java.sql.Date, relevance: Double,
			senti: Int, pos: Double, neut: Double, neg: Double,
			sentWords: Int, totWords: Int) =>
			(storyDate, List(relevance, relevance*senti.toDouble, relevance*pos,
			relevance*neut, relevance*neg,
			relevance*sentWords, relevance*totWords, 1)) }.
			reduceByKey((_, _).zipped.map{_+_}).
			map{ case (storyDate, news) =>
				val count = news.last
				(storyDate, news.map{ metric => metric/count }) }.
			map{ case (storyDate, news) =>
				TRNARow(storyDate, news(0), news(1), news(2), news(3),
					news(4), news(5), news(6))}.toDF

// merge

val export = CRSP.join(TRNA, CRSP("date")===TRNA("storyDate"), "left_outer").
	drop($"storyDate").
	sort(desc("date"))

export.cache()

export.coalesce(1).write.format("csv").
save("s3n://bloombergprices/dailyMerge/"+ticker)

export.unpersist()

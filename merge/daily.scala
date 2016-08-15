// takes ticker merges news with prices

var ticker = "LNKD" // or sys.arg

// import statements

val sqlContext = new org.apache.spark.sql.SQLContext(sc)
import sqlContext.implicits._
import org.apache.spark.sql.Row
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.{StructField, StructType, LongType}
import org.apache.spark.sql.functions.udf
import java.text.SimpleDateFormat
import java.util.{TimeZone, Calendar}
import java.sql.Date

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

//// TIMESTAMP PROCESSORS

val TRNATimeStamp = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX") // broadcast this
val bcTRNATimeStamp = sc.broadcast(TRNATimeStamp)

val cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
val bcCal = sc.broadcast(cal)

//// ISO8601 TO TRADING DAY

def tradingDay(iso8601String: String,
               dateParser: java.text.SimpleDateFormat = bcTRNATimeStamp.value,
               cal: java.util.Calendar = bcCal.value): java.sql.Date = {
    
    val dateTime = dateParser.parse(iso8601String) // parse the dateString
    cal.setTime(dateTime) // initialize the calendar
    
    val hour: Int = cal.get(Calendar.HOUR_OF_DAY) // hour of day in NY
    val dayOfWeek: Int = cal.get(Calendar.DAY_OF_WEEK) // dayOfWeek in NY
    
    if (dayOfWeek >= 2 && dayOfWeek <= 5 && hour >= 16) { // afterhours M-Th
        cal.add(Calendar.DAY_OF_WEEK, 1)
    }
    else if (dayOfWeek == 6 && hour >= 16) { // afterhours F to M
        cal.add(Calendar.DAY_OF_WEEK, 3)
    }
    else if (dayOfWeek == 7 || dayOfWeek == 1) { // weekend to M
        val daysToMonday = 2 - (dayOfWeek%7)
        cal.add(Calendar.DAY_OF_WEEK, daysToMonday)
    }
    
    val sqlDate = new java.sql.Date(cal.getTimeInMillis) // get day for SQL
    return sqlDate
}

// val bcTradingDay = sc.broadcast(tradingDay(_:String))

//// UDF OF ISO8601 TO TRADING DAY

// val tradingDayUDF = udf(tradingDay(_:String))

//// PARSE TICKER

def getTicker(RIC: String): String = {
	return RIC.split("\\.")(0)
}

val getTickerUDF = udf(getTicker(_:String))

//// SCHEMA OF PROCESSED DF

case class TRNARow(storyDate: java.sql.Date, relevance: Double,
			senti: Double, pos: Double, neut: Double, neg: Double,
			sentWords: Double, totWords: Double)

case class tsRow(TAKE_TRADING_DAY: java.sql.Date)

//// MERGE PROGRAM

val TRNA = Array("2010","2011","2012","2013","2014","2015").
	map{ year => 
		sqlContext.read.format("parquet").
		load("s3n://bloombergprices/TRNA/model" + year) }.
	reduce( (rdd1, rdd2) => rdd1.unionAll(rdd2) ).
	withColumn("TICKER", getTickerUDF($"STOCK_RIC")).
	filter($"TICKER"===ticker).
	select("PNAC","TIMESTAMP","RELEVANCE",
		"SENTIMENT","SENT_POS","SENT_NEUT",
		"SENT_NEG","SENT_WORDS","TOT_WORDS")

val timeStamps = sc.parallelize(TRNA.select("PNAC","TIMESTAMP").
	collect.
	map{ row => ( row.getAs[String]("PNAC"),
	tradingDay(row.getAs[String]("TIMESTAMP")) ) }).
	toDF("PNAC_2","TAKE_TRADING_DAY")

val newDF = timeStamps.
join(TRNA, timeStamps("PNAC_2")===TRNA("PNAC"), "inner").
	drop("TIMESTAMP").
	drop("PNAC").
	drop("PNAC_2").
	map{ case Row(takeTradingDay: java.sql.Date, relevance: Double,
		senti: Int, pos: Double, neut: Double, neg: Double,
		sentWords: Int, totWords: Int) =>
		(takeTradingDay, List(relevance, relevance*senti.toDouble,
			relevance*pos, relevance*neut, relevance*neg,
			relevance*sentWords, relevance*totWords, 1)) }.
	reduceByKey((_, _).zipped.map{_+_}).
	map{ case (takeTradingDay, news) =>
			val count = news.last
			(takeTradingDay, news.map{ metric => metric/count }) }.
	map{ case (takeTradingDay, news) =>
			TRNARow(takeTradingDay, news(0), news(1), news(2), news(3),
				news(4), news(5), news(6))}.toDF

val TRNA = 0
val timeStamps = 0

// merge

val export = CRSP.join(newDF, CRSP("date")===newDF("storyDate"), "left_outer").
	drop($"storyDate").
	sort(desc("date"))

// export.cache

export.coalesce(1).write.format("csv").
save("s3n://bloombergprices/dailyMerge/"+ticker)

// export.unpersist()

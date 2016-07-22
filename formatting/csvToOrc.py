# For converting all CSV's in S3 bucket to ORC

from glob import glob
from os.path import join
from pyspark.sql import SQLContext
sqlContext = SQLContext(sc)

rootBucket = "s3n://bloombergprices/" # our S3 bucket for this project
CRSPBucket = join(rootBucket, "CRSP")
TRNABucket = join(rootBucket, "TRNA")
BLMBRGBucket = None
allBuckets = [CRSPBucket, TRNABucket, BLMBRGBucket]

for bucket in allBuckets:
	for  in glob(bucket):
		dataFrame = sqlContext.read.format("com.databricks.spcsvFileNameark.csv").option("header", "true").option("inferschema", "true").option("mode", "DROPMALFORMED").load(csvFileName)
		dataFrame.write.format("orc").save(join(bucket, csvFileName+".orc"))
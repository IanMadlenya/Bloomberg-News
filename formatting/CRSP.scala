// for filtering columns on master csv and writing to disk as orc

val CRSPData = sqlContext.
	read.
	format("csv").
	option("header", "true").
	option("inferschema", "true").
	option("mode", "DROPMALFORMED").
	load("s3n://bloombergprices/CRSP/master.csv")

// CRSPData.printSchema
	// |-- PERMNO: integer (nullable = true)
	// |-- date: integer (nullable = true)
	// |-- NAMEENDT: integer (nullable = true)
	// |-- SHRCD: integer (nullable = true)
	// |-- EXCHCD: integer (nullable = true)
	// |-- SICCD: string (nullable = true)
	// |-- NCUSIP: string (nullable = true)
	// |-- TICKER: string (nullable = true)
	// |-- COMNAM: string (nullable = true)
	// |-- SHRCLS: string (nullable = true)
	// |-- TSYMBOL: string (nullable = true)
	// |-- NAICS: integer (nullable = true)
	// |-- PRIMEXCH: string (nullable = true)
	// |-- TRDSTAT: string (nullable = true)
	// |-- SECSTAT: string (nullable = true)
	// |-- PERMCO: integer (nullable = true)
	// |-- ISSUNO: integer (nullable = true)
	// |-- HEXCD: integer (nullable = true)
	// |-- HSICCD: string (nullable = true)
	// |-- CUSIP: string (nullable = true)
	// |-- DCLRDT: integer (nullable = true)
	// |-- DLAMT: double (nullable = true)
	// |-- DLPDT: integer (nullable = true)
	// |-- DLSTCD: integer (nullable = true)
	// |-- NEXTDT: integer (nullable = true)
	// |-- PAYDT: integer (nullable = true)
	// |-- RCRDDT: integer (nullable = true)
	// |-- SHRFLG: integer (nullable = true)
	// |-- HSICMG: integer (nullable = true)
	// |-- HSICIG: integer (nullable = true)
	// |-- DISTCD: integer (nullable = true)
	// |-- DIVAMT: double (nullable = true)
	// |-- FACPR: double (nullable = true)
	// |-- FACSHR: double (nullable = true)
	// |-- ACPERM: integer (nullable = true)
	// |-- ACCOMP: integer (nullable = true)
	// |-- NWPERM: integer (nullable = true)
	// |-- DLRETX: string (nullable = true)
	// |-- DLPRC: double (nullable = true)
	// |-- DLRET: string (nullable = true)
	// |-- TRTSCD: integer (nullable = true)
	// |-- NMSIND: integer (nullable = true)
	// |-- MMCNT: integer (nullable = true)
	// |-- NSDINX: integer (nullable = true)
	// |-- BIDLO: double (nullable = true)
	// |-- ASKHI: double (nullable = true)
	// |-- PRC: double (nullable = true)
	// |-- VOL: integer (nullable = true)
	// |-- RET: string (nullable = true)
	// |-- BID: double (nullable = true)
	// |-- ASK: double (nullable = true)
	// |-- SHROUT: integer (nullable = true)
	// |-- CFACPR: double (nullable = true)
	// |-- CFACSHR: double (nullable = true)
	// |-- OPENPRC: double (nullable = true)
	// |-- NUMTRD: double (nullable = true)
	// |-- RETX: string (nullable = true)
	// |-- vwretd: double (nullable = true)
	// |-- vwretx: double (nullable = true)
	// |-- ewretd: double (nullable = true)
	// |-- ewretx: double (nullable = true)
	// |-- sprtrn: double (nullable = true)

CRSPData.select("PERMNO","SHRCD","EXCHCD","SICCD","SHROUT","OPENPRC",
	"PRC","RET","BIDLO","ASKHI","VOL").write.format("parquet").
	save("s3n://bloombergprices/CRSP/model")














# BLOOMBERG NEWS INCIDENCE ANALYSIS

_Professor Terrence Hendershott with assistance from Mustafa M. Eisa_

### CRSP U.S. Stock Database

Standard daily prices data 2010-2015 including the following: Open, close, low, high; returns (with and without dividens) and excess returns; trading volume, market cap, shares outstanding; and other supplementary information. Covers all equities in NYSE, NASDAQ, and Arca along with composite indices, equal and value-weighted exchange returns.

Schema is as follows:

```python
[PERMNO,date,NAMEENDT,SHRCD,EXCHCD,SICCD,NCUSIP,TICKER,COMNAM,SHRCLS,TSYMBOL,NAICS,PRIMEXCH,TRDSTAT,SECSTAT,PERMCO,ISSUNO,HEXCD,HSICCD,CUSIP,DCLRDT,DLAMT,DLPDT,DLSTCD,NEXTDT,PAYDT,RCRDDT,SHRFLG,HSICMG,HSICIG,DISTCD,DIVAMT,FACPR,FACSHR,ACPERM,ACCOMP,NWPERM,DLRETX,DLPRC,DLRET,TRTSCD,NMSIND,MMCNT,NSDINX,BIDLO,ASKHI,PRC,VOL,RET,BID,ASK,SHROUT,CFACPR,CFACSHR,OPENPRC,NUMTRD,RETX,vwretd,vwretx,ewretd,ewretx,sprtrn]
```

of which we produce a filtered dataframe of the form
```
[date: %Y%m%d, TICKER: str, OPNPRC: double, PRC: double, RET: double, BIDLO: double, ASKHI: double, VOL: long]
```
Using `TICKER` as key, the dataframe will be split, with with columns renamed for simplicity. The resulting tables, one per ticker, will be of the form
```
[date, open, close, return, low, high, volume]
```
 
_If the closing price is not available for any given period, the number in the price field is replaced with a bid/ask average. Bid/ask averages have dashes placed in front of them. These do not incorrectly reflect negative prices; they serve to distinguish bid/ask averages from actual closing prices. If neither the price nor bid/ask average is available, the field is set to zero._

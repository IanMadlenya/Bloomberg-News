# DAILY PRICES + NEWS: ADIDAS

### Summary Statistics

**Table head:**
```
        date SHRCD EXCHCD SICCD SHROUT   OPEN  CLOSE       RET       LO      HI    VOL REL NEG NEUT POS SENTI SENTWRDS TOTWRDS
1 2015-12-31    11      1  7322  61138 275.42 276.57 -0.000723 275.0001 279.890 267048  NA  NA   NA  NA    NA       NA      NA
2 2015-12-30    11      1  7322  61138 279.73 276.77 -0.012594 276.1800 279.730 196042  NA  NA   NA  NA    NA       NA      NA
3 2015-12-29    11      1  7322  61138 280.00 280.30  0.007766 277.3950 280.945 163196  NA  NA   NA  NA    NA       NA      NA
4 2015-12-28    11      1  7322  61138 276.98 278.14 -0.000216 275.4100 278.530 133990  NA  NA   NA  NA    NA       NA      NA
5 2015-12-24    11      1  7322  61138 278.22 278.20 -0.000539 276.5000 279.980  98452  NA  NA   NA  NA    NA       NA      NA
6 2015-12-23    11      1  7322  61138 275.91 278.35  0.016989 274.4000 279.060 350966  NA  NA   NA  NA    NA       NA      NA
```

**Variable Summary**
```
      date                SHRCD        EXCHCD      SICCD          SHROUT           OPEN            CLOSE       
 Min.   :2010-01-04   Min.   :11   Min.   :1   Min.   :7322   Min.   :48713   Min.   : 53.42   Min.   : 53.05  
 1st Qu.:2011-07-02   1st Qu.:11   1st Qu.:1   1st Qu.:7322   1st Qu.:49968   1st Qu.: 91.06   1st Qu.: 91.40  
 Median :2013-01-02   Median :11   Median :1   Median :7389   Median :51996   Median :148.00   Median :149.64  
 Mean   :2013-01-01   Mean   :11   Mean   :1   Mean   :7367   Mean   :53367   Mean   :171.06   Mean   :171.15  
 3rd Qu.:2014-07-02   3rd Qu.:11   3rd Qu.:1   3rd Qu.:7389   3rd Qu.:54588   3rd Qu.:260.84   3rd Qu.:261.69  
 Max.   :2015-12-31   Max.   :11   Max.   :1   Max.   :7389   Max.   :64020   Max.   :309.93   Max.   :309.91  
                                                                                                               
      RET                  LO               HI              VOL               REL              NEG         
 Min.   :-0.109990   Min.   : 52.70   Min.   : 54.18   Min.   :  94859   Min.   :0.0064   Min.   :-1.0000  
 1st Qu.:-0.006829   1st Qu.: 90.01   1st Qu.: 92.12   1st Qu.: 405746   1st Qu.:0.6703   1st Qu.: 0.0123  
 Median : 0.001050   Median :147.91   Median :150.66   Median : 586784   Median :1.0000   Median : 0.5012  
 Mean   : 0.001096   Mean   :169.36   Mean   :172.80   Mean   : 729691   Mean   :0.7992   Mean   : 0.4839  
 3rd Qu.: 0.009314   3rd Qu.:258.10   3rd Qu.:263.67   3rd Qu.: 879975   3rd Qu.:1.0000   3rd Qu.: 1.0000  
 Max.   : 0.103971   Max.   :305.94   Max.   :312.00   Max.   :6909400   Max.   :1.0000   Max.   : 1.0000  
                                                                         NA's   :934      NA's   :934      
      NEUT             POS             SENTI           SENTWRDS            TOTWRDS        
 Min.   :0.0004   Min.   :0.0008   Min.   :0.0001   Min.   :   0.2335   Min.   :   5.878  
 1st Qu.:0.1940   1st Qu.:0.1271   1st Qu.:0.0408   1st Qu.:  38.3125   1st Qu.:  92.028  
 Median :0.4293   Median :0.1643   Median :0.0689   Median : 162.7738   Median : 287.173  
 Mean   :0.4527   Mean   :0.2285   Mean   :0.1180   Mean   : 255.5720   Mean   : 464.443  
 3rd Qu.:0.7647   3rd Qu.:0.3207   3rd Qu.:0.1373   3rd Qu.: 356.2500   3rd Qu.: 589.125  
 Max.   :0.8558   Max.   :0.8942   Max.   :0.8191   Max.   :1873.0000   Max.   :5731.000  
 NA's   :934      NA's   :934      NA's   :934      NA's   :934         NA's   :934       
```

**Sentiment Time Series**

![](https://github.com/mustafameisa/Bloomberg-News/tree/master/daily/logInt.pdf)
![](https://github.com/mustafameisa/Bloomberg-News/tree/master/daily/Int.pdf)

### Regression Analysis

**Phillips-Perron Test.**
```
	Phillips-Perron Unit Root Test

data:  RET
Dickey-Fuller = -36.653, Truncation lag parameter = 7, p-value = 0.01

data:  SENTI
Dickey-Fuller = -38.881, Truncation lag parameter = 7, p-value = 0.01
```

**ARIMA Modeling.** Regress returns on AR(2,0,2) and one-period lag sentiment.

```
Coefficients:
          ar1      ar2      ar3     ma1     ma2  intercept  lagSenti
      -0.5275  -0.1392  -0.1159  0.6239  0.1152          0     0e+00
s.e.   0.1981   0.2766   0.0414  0.1992  0.2815          0     1e-04

sigma^2 estimated as 3.109e-08:  log likelihood = 10901.35,  aic = -21786.7
```

**Model Fit.**

![](https://github.com/mustafameisa/Bloomberg-News/tree/master/daily/ar302.pdf)
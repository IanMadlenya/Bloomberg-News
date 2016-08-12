# DAILY NEWS AND PRICES

# Import and Formatting

data = read.csv(file.choose(),header=T)
data$date = as.Date(data$date)
data[is.na(data)] = 0
attach(data)

# Graphics

norma = function(x) {
	(x - mean(x))/sd(x)
}

### LOG INTEGRATED

plot(date, cumprod(1+RET/OPEN), type="l", col="blue", lwd=2, xlab="",
	ylab="NORMALIZED RETURNS + SENTIMENT", main="LOG-INTEGRATED SENTIMENT AND PRICE")
par(new=T)
plot(date, cumprod(1+POS), type="l", col="green", lwd=2, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, cumprod(1+NEG), type="l", col="red", lwd=2, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, cumprod(1+NEUT), type="l", col="gray", lwd=2, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, cumprod(1+SENTI), type="l", col="purple", lwd=2, xlab="",
	ylab="", main="", axes=F)
legend("top",col=c("blue","green","red","gray","purple"),
	c("RETURNS", "POSITIVE SENTI", "NEUTRAL SENTI", "NEGATIVE SENTI", "AVG SENTI"),lwd=rep(2,5))

### INTEGRATED

plot(date, cumsum(RET/OPEN), type="l", col="blue", lwd=2, xlab="",
	ylab="NORMALIZED RETURNS + SENTIMENT", main="INTEGRATED SENTIMENT AND PRICE")
par(new=T)
plot(date, cumsum(POS), type="l", col="green", lwd=2, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, cumsum(NEG), type="l", col="red", lwd=2, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, cumsum(NEUT), type="l", col="gray", lwd=2, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, cumsum(SENTI), type="l", col="purple", lwd=2, xlab="",
	ylab="", main="", axes=F)
legend("top",col=c("blue","green","red","gray","purple"),
	c("RETURNS", "POSITIVE SENTI", "NEUTRAL SENTI", "NEGATIVE SENTI", "AVG SENTI"),lwd=rep(2,5))

# BASIC PANEL REGRESSION

### PHILLIPS-PERRON TEST

PP.test(SENTI)
PP.test(RET)

### ARIMAX

pRET = RET/OPEN
n = length(RET)
returns = rev(pRET[1:(n-1)])
lagReturns = rev(pRET[2:n])

senti = rev(SENTI[1:(n-1)])
lagSenti = rev(SENTI[2:n])

model = arima(x = returns, xreg = lagSenti, order = c(3,0,2))

plot(date[1:(n-1)], cumsum(rev(returns)),
	type="l", ylim=c(0,.02), xlab="", ylab="PERCENT RETURNS", main="INTEGRATED RETURNS")
par(new=T)
plot(date[1:(n-1)], cumsum(rev(returns)) + rev(model$residuals),
	type="l", ylim=c(0,.02), col="red", axes=F, xlab="", ylab="", main="", lwd=.5)
legend("topright",col=c("black","red"),
	c("Integrated Returns", "Fitted ARx(3,0,2)"),lwd=c(1,.5))

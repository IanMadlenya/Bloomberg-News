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

rCP = function(x) {
	rev(cumprod(rev(1 + x)))
}

plot(date, rCP(RET), type="l", col="blue", lwd=2, xlab="",
	ylab="NORMALIZED RETURNS + SENTIMENT", main="LOG-INTEGRATED SENTIMENT AND PRICE")
par(new=T)
plot(date, POS, type="l", col="green", lwd=.5, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, NEG, type="l", col="red", lwd=.5, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, NEUT, type="l", col="gray", lwd=.5, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, rCP(SENTI-mean(SENTI)), type="l", col="purple", lwd=2, xlab="",
	ylab="", main="", axes=F)
legend("top",col=c("blue","green","red","gray","purple"),
	c("RETURNS", "POSITIVE SENTI", "NEUTRAL SENTI", "NEGATIVE SENTI", "AVG SENTI"),lwd=rep(2,5))

### INTEGRATED

rCS = function(x) {
	rev(cumsum(rev(x)))
}

limits = range(rCS(RET))

plot(date, rCS(RET), type="l", col="blue", lwd=2, xlab="", ylim=limits,
	ylab="NORMALIZED RETURNS + SENTIMENT", main="INTEGRATED SENTIMENT AND PRICE")
par(new=T)
plot(date, POS, type="l", col="green", lwd=.5, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, NEG, type="l", col="red", lwd=.5, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, NEUT, type="l", col="gray", lwd=.5, xlab="",
	ylab="", main="", axes=F)
par(new=T)
plot(date, SENTI, type="l", col="purple", lwd=2, xlab="", ylim=limits,
	ylab="", main="", axes=F)
legend("top",col=c("blue","green","red","gray","purple"),
	c("RETURNS", "POSITIVE SENTI", "NEUTRAL SENTI", "NEGATIVE SENTI", "AVG SENTI"),lwd=rep(2,5))

# BASIC PANEL REGRESSION

### PHILLIPS-PERRON TEST

PP.test(SENTI)
PP.test(RET)

### ARIMAX

pRET = RET
n = length(RET)
returns = rev(pRET[1:(n-1)])
lagReturns = rev(pRET[2:n])

senti = rev(SENTI[1:(n-1)])
lagSenti = rev(SENTI[2:n])

model = arima(x = returns, xreg = lagSenti, order = c(3,0,2))

plot(date[1:(n-1)], rCS(returns),
	type="l", ylim=range(rCS(returns)), xlab="", ylab="PERCENT RETURNS", main="INTEGRATED RETURNS")
par(new=T)
plot(date[1:(n-1)], rCS(returns) + rev(model$residuals),
	type="l", ylim=range(rCS(returns)), col="red", axes=F, xlab="", ylab="", main="", lwd=.5)
legend("topleft",col=c("black","red"),
	c("Integrated Returns", "Fitted ARx(3,0,2)"),lwd=c(1,.5))

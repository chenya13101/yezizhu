package com.vincent.bean.sub;

import java.math.BigDecimal;

import com.vincent.bean.UseLimit;

public class CashLimit extends UseLimit {
	private BigDecimal minRequire;

	private BigDecimal maxSale;

	public CashLimit(BigDecimal minRequire, BigDecimal maxSale) {
		super();
		this.minRequire = minRequire;
		this.maxSale = maxSale;
	}

	public BigDecimal getMinRequire() {
		return minRequire;
	}

	public void setMinRequire(BigDecimal minRequire) {
		this.minRequire = minRequire;
	}

	public BigDecimal getMaxSale() {
		return maxSale;
	}

	public void setMaxSale(BigDecimal maxSale) {
		this.maxSale = maxSale;
	}

}

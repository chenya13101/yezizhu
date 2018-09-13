package com.vincent.bean.sub;

import java.math.BigDecimal;

import com.vincent.bean.UseLimit;

public class CashLimit extends UseLimit {
	private BigDecimal minRequire;

	private BigDecimal amount;

	public BigDecimal getMinRequire() {
		return minRequire;
	}

	public void setMinRequire(BigDecimal minRequire) {
		this.minRequire = minRequire;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
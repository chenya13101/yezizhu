package com.vincent.bean.sub;

import java.math.BigDecimal;

import com.vincent.bean.UseLimit;

public class CashLimit extends UseLimit {
	private BigDecimal minRequire;

	public CashLimit(BigDecimal minRequire, BigDecimal maxSale) {
		super();
		this.minRequire = minRequire;
		this.setMaxSale(maxSale);
	}

	public BigDecimal getMinRequire() {
		return minRequire;
	}

	public void setMinRequire(BigDecimal minRequire) {
		this.minRequire = minRequire;
	}

	@Override
	public boolean checkUseCondition(BigDecimal totalPromPrice) {
		return totalPromPrice.compareTo(minRequire) >= 0;
	}

}

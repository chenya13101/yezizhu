package com.vincent.bean.sub;

import java.math.BigDecimal;

public class SubDiscountLimit {

	private BigDecimal minRequire;

	private BigDecimal discount;

	public SubDiscountLimit(BigDecimal minRequire, BigDecimal discount) {
		super();
		this.minRequire = minRequire;
		this.discount = discount;
	}

	public BigDecimal getMinRequire() {
		return minRequire;
	}

	public void setMinRequire(BigDecimal minRequire) {
		this.minRequire = minRequire;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

}

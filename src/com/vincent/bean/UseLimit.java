package com.vincent.bean;

import java.math.BigDecimal;

public abstract class UseLimit {

	/**
	 * 最多优惠的金额
	 */
	private BigDecimal maxSale;

	public BigDecimal getMaxSale() {
		return maxSale;
	}

	public void setMaxSale(BigDecimal maxSale) {
		this.maxSale = maxSale;
	}

	/**
	 * 
	 * @param totalPromPrice
	 *            促销总价
	 * @return true 满足使用条件;false 不满足
	 */
	public abstract boolean checkUseCondition(BigDecimal totalPromPrice);
}

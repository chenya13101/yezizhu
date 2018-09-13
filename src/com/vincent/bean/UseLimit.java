package com.vincent.bean;

import java.math.BigDecimal;

public class UseLimit {

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

	// TODO 如果有需要可以改为接口或者抽象方法
	// TODO 是不是可以定义一些公用属性方法
}

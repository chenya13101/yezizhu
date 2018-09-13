package com.vincent.bean.sub;

import java.math.BigDecimal;

import com.vincent.bean.UseLimit;

public class RedPacketLimit extends UseLimit {
	private BigDecimal maxSale;

	public RedPacketLimit(BigDecimal maxSale) {
		super();
		this.maxSale = maxSale;
	}

	public BigDecimal getMaxSale() {
		return maxSale;
	}

	public void setMaxSale(BigDecimal maxSale) {
		this.maxSale = maxSale;
	}

}

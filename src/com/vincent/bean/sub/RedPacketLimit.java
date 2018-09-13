package com.vincent.bean.sub;

import java.math.BigDecimal;

import com.vincent.bean.UseLimit;

public class RedPacketLimit extends UseLimit {

	public RedPacketLimit(BigDecimal maxSale) {
		super();
		this.setMaxSale(maxSale);
	}

}

package com.vincent.bean.sub;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.vincent.bean.UseLimit;

public class DiscountLimit extends UseLimit {

	private BigDecimal maxSale;

	private List<SubDiscountLimit> limitList;

	public DiscountLimit(BigDecimal maxSale, List<SubDiscountLimit> limitList) {
		super();
		this.maxSale = maxSale;
		this.limitList = limitList;
	}

	public DiscountLimit(BigDecimal maxSale, BigDecimal minRequire, BigDecimal discount) {
		super();
		this.maxSale = maxSale;
		this.limitList = Collections.singletonList(new SubDiscountLimit(minRequire, discount));
	}

	public BigDecimal getMaxSale() {
		return maxSale;
	}

	public void setMaxSale(BigDecimal maxSale) {
		this.maxSale = maxSale;
	}

	public List<SubDiscountLimit> getLimitList() {
		return limitList;
	}

	public void setLimitList(List<SubDiscountLimit> limitList) {
		this.limitList = limitList;
	}

}

package com.vincent.bean.sub;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import com.vincent.bean.UseLimit;

public class DiscountLimit extends UseLimit {

	private List<SubDiscountLimit> limitList;

	public DiscountLimit(BigDecimal maxSale, List<SubDiscountLimit> limitList) {
		super();
		this.setMaxSale(maxSale);
		this.limitList = limitList;
	}

	public DiscountLimit(BigDecimal maxSale, BigDecimal minRequire, BigDecimal discount) {
		super();
		this.setMaxSale(maxSale);
		this.limitList = Collections.singletonList(new SubDiscountLimit(minRequire, discount));
	}

	public List<SubDiscountLimit> getLimitList() {
		return limitList;
	}

	public void setLimitList(List<SubDiscountLimit> limitList) {
		this.limitList = limitList;
	}

	@Override
	public boolean checkUseCondition(BigDecimal totalPromPrice) {
		return this.limitList.stream().filter(subLimit -> totalPromPrice.compareTo(subLimit.getMinRequire()) >= 0)
				.count() > 0;
	}

}

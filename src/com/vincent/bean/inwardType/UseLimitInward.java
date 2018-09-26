package com.vincent.bean.inwardType;

import java.math.BigDecimal;
import java.util.List;

import com.vincent.bean.sub.SubDiscountLimit;

/**
 * 封装使用限制参数，可能是外部想要传入的 红包 折扣券 代金券的任何一种的限制条件. 作为一个中间层,分割[外部的传入]与[系统内实际使用的对象]
 * 
 * @author WenSen
 * @date 2018年9月13日 上午11:19:45
 *
 */
public class UseLimitInward {

	// minRequire 在前端传入时做验证一定要 >= maxSale
	private BigDecimal minRequire;

	private BigDecimal maxSale;

	private List<SubDiscountLimit> discountLimitList;

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

	public List<SubDiscountLimit> getDiscountLimitList() {
		return discountLimitList;
	}

	public void setDiscountLimitList(List<SubDiscountLimit> discountLimitList) {
		this.discountLimitList = discountLimitList;
	}

}

package com.vincent.bean;

import java.math.BigDecimal;
import java.util.List;

public class CouponGroup {

	private List<CouponCode> couponCodeList;

	/**
	 * 优惠后商品总金额
	 */
	private BigDecimal total;

	// TODO 有没有必要添加其它属性
	public CouponGroup(List<CouponCode> couponCodeList, BigDecimal total) {
		super();
		this.couponCodeList = couponCodeList;
		this.total = total;
	}

	public List<CouponCode> getCouponCodeList() {
		return couponCodeList;
	}

	public BigDecimal getTotal() {
		return total;
	}

}

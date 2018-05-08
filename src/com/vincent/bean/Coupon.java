package com.vincent.bean;

import java.math.BigDecimal;

public class Coupon {

	private String code;

	private CouponTypeEnum couponTypeEnum;

	private BigDecimal discount;

	private BigDecimal amount;

	private BigDecimal fullElement;

	private FilterRule filterRule;

	public Coupon(String code, CouponTypeEnum couponTypeEnum, BigDecimal discount, BigDecimal amount,
			BigDecimal fullElement, FilterRule filterRule) {
		super();
		this.code = code;
		this.couponTypeEnum = couponTypeEnum;
		this.discount = discount;
		this.amount = amount;
		this.fullElement = fullElement;
		this.filterRule = filterRule;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public CouponTypeEnum getCouponTypeEnum() {
		return couponTypeEnum;
	}

	public void setCouponTypeEnum(CouponTypeEnum couponTypeEnum) {
		this.couponTypeEnum = couponTypeEnum;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getFullElement() {
		return fullElement;
	}

	public void setFullElement(BigDecimal fullElement) {
		this.fullElement = fullElement;
	}

	public FilterRule getFilterRule() {
		return filterRule;
	}

	public void setFilterRule(FilterRule filterRule) {
		this.filterRule = filterRule;
	}

}

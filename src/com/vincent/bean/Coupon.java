package com.vincent.bean;

import java.math.BigDecimal;

import com.vincent.common.CouponTypeEnum;

public class Coupon {

	private String code;

	private String name;

	private CouponTypeEnum couponTypeEnum; // TODO 优惠券类型，新版本会出现许多不一致的属性，难度增加

	private BigDecimal discount; // 折扣

	private BigDecimal amount;// 抵扣现金额

	private BigDecimal fullElement; // 需要满足的满减金额

	private PromotionRange promotionRange;

	public Coupon(String code, String name, CouponTypeEnum couponTypeEnum, BigDecimal discount, BigDecimal amount,
			BigDecimal fullElement, PromotionRange promotionRange) {
		super();
		this.code = code;
		this.name = name;
		this.couponTypeEnum = couponTypeEnum;
		this.discount = discount;
		this.amount = amount;
		this.fullElement = fullElement;
		this.promotionRange = promotionRange;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PromotionRange getPromotionRange() {
		return promotionRange;
	}

	public void setPromotionRange(PromotionRange promotionRange) {
		this.promotionRange = promotionRange;
	}

}

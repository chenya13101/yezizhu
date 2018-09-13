package com.vincent.bean;

import com.vincent.common.CouponTypeEnum;

public class Coupon {

	private String code;

	private String name;

	private PromotionRange promotionRange;

	private CouponTypeEnum typeEnum; // TODO 优惠券类型，新版本会出现许多不一致的属性，难度增加

	// TODO 这里可以把优惠券类型与具体的使用限制关联起来

	private UseLimit useLimit; // 使用限制 包括了 优惠金额，最低金额要求

	public Coupon(String code, String name, PromotionRange promotionRange, CouponTypeEnum typeEnum, UseLimit useLimit) {
		super();
		this.code = code;
		this.name = name;
		this.promotionRange = promotionRange;
		this.typeEnum = typeEnum;
		this.useLimit = useLimit;
	}

	public UseLimit getUseLimit() {
		return useLimit;
	}

	public void setUseLimit(UseLimit useLimit) {
		this.useLimit = useLimit;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public CouponTypeEnum getTypeEnum() {
		return typeEnum;
	}

	public void setTypeEnum(CouponTypeEnum typeEnum) {
		this.typeEnum = typeEnum;
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

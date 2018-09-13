package com.vincent.bean;

import com.vincent.bean.inwardType.UseLimitInward;

public class Coupon {

	private String code;

	private String name;

	private PromotionRange promotionRange;

	private int type; // CouponTypeEnum index

	// TODO 这里可以把优惠券类型与具体的使用限制关联起来

	private UseLimit useLimit; // 使用限制 包括了 优惠金额，最低金额要求

	// TODO 需要考虑前端html如何将数据传入，并且以何种形式展示给前端.同时不能产生混淆
	public Coupon(String code, String name, PromotionRange promotionRange, int type, UseLimit useLimit) {
		super();
		this.code = code;
		this.name = name;
		this.promotionRange = promotionRange;
		this.type = type;
		this.useLimit = useLimit;// TODO 这里传入的可以是一个复杂对象，包含三个的通用属性，但是在这里被转化为具体的Limit
	}

	public Coupon(String code, String name, PromotionRange promotionRange, int type, UseLimitInward useLimitInward) {
		super();
		this.code = code;
		this.name = name;
		this.promotionRange = promotionRange;
		this.type = type;
		this.useLimit = changeLimitInwardToLimit(type, useLimitInward);
	}

	private UseLimit changeLimitInwardToLimit(int type, UseLimitInward useLimitInward) {
		// TODO 这里传入的可以是一个复杂对象，包含三个的通用属性，但是在这里被转化为具体的Limit
		return null;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

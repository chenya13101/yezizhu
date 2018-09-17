package com.vincent.bean;

import com.vincent.bean.enums.CouponTypeEnum;
import com.vincent.bean.inwardType.UseLimitInward;
import com.vincent.bean.sub.CashLimit;
import com.vincent.bean.sub.DiscountLimit;
import com.vincent.bean.sub.RedPacketLimit;
import com.vincent.common.Constant;
import com.vincent.util.EnumUtil;

public class Coupon {

	private String code;

	private String name;

	private PromotionRange promotionRange;

	private int type; // CouponTypeEnum index

	private UseLimit useLimit; // 使用限制 包括了 优惠金额，最低金额要求
	// 是不是需要把优惠金额和使用条件作为两个对象。

	public Coupon(String code, String name, PromotionRange promotionRange, int type, UseLimitInward useLimitInward) {
		super();
		this.code = code;
		this.name = name;
		this.promotionRange = promotionRange;
		this.type = type;
		this.useLimit = changeLimitInwardToLimit(type, useLimitInward);
	}

	/**
	 * 将传入得参数转化为具体得limit类型, 以把优惠券类型与具体的使用限制关联起来
	 * 
	 * @param type
	 *            优惠券类型 CouponTypeEnum
	 * @param useLimitInward
	 *            这里传入的可以是一个复杂对象，包含三个的通用属性，但是在这里被转化为具体的Limit
	 * @return 具体的使用限制
	 */
	private UseLimit changeLimitInwardToLimit(int type, UseLimitInward useLimitInward) {
		CouponTypeEnum typeEnum = EnumUtil.getEnumObject(CouponTypeEnum.class, element -> element.getIndex() == type);
		switch (typeEnum) {
		case CASH:
			return new CashLimit(useLimitInward.getMinRequire(), useLimitInward.getMaxSale());
		case DISCOUNT:
			return new DiscountLimit(useLimitInward.getMaxSale(), useLimitInward.getDiscountLimitList());
		case RED_PACKET:
			return new RedPacketLimit(useLimitInward.getMaxSale());
		default:
			throw new IllegalArgumentException(Constant.INVALID_INDEX);
		}
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

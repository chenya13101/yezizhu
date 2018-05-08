package com.vincent.bean;

/**
 * 优惠券类型 1.折扣券 2.现金券
 * 
 * @author vincent
 *
 */
public enum CouponTypeEnum {
	DISCOUNT("折扣券", 1), CASH("代金券", 2);

	private String name;
	private int index;

	private CouponTypeEnum(String name, int index) {
		this.name = name;
		this.index = index;
	}

	// 普通方法
	public static String getName(int index) {
		for (CouponTypeEnum c : CouponTypeEnum.values()) {
			if (c.getIndex() == index) {
				return c.name;
			}
		}
		return null;
	}

	public static CouponTypeEnum getEnumByIndex(int index) {
		for (CouponTypeEnum c : CouponTypeEnum.values()) {
			if (c.getIndex() == index) {
				return c;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}

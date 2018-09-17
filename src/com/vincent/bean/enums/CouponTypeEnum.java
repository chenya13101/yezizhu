package com.vincent.bean.enums;

/**
 * 优惠券类型 1.折扣券 2.现金券
 * 
 * @author vincent
 *
 */
public enum CouponTypeEnum {
	DISCOUNT("折扣券", 1, "ZK"),
	CASH("代金券", 2, "DJ"),
	RED_PACKET("红包", 3, "HB");

	private String name;
	private int index;
	private String prefix;

	private CouponTypeEnum(String name, int index, String prefix) {
		this.name = name;
		this.index = index;
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getIndex() {
		return index;
	}
}

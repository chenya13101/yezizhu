package com.vincent.bean.enums;

public enum PromotionRangeTypeEnum {
	COMMODITY("商品券", 1),
	ALL("全场券", 4);

	private String name;
	private int index;

	private PromotionRangeTypeEnum(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}
}

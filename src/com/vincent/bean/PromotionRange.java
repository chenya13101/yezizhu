package com.vincent.bean;

import java.util.List;

import com.vincent.bean.sub.PromotionCommodity;

public class PromotionRange {

	private int type; // PromotionRangeTypeEnum getIndex

	private List<PromotionCommodity> commodityList;

	public PromotionRange(int type) {
		super();
		this.type = type;
	}

	public PromotionRange(int type, List<PromotionCommodity> commodityList) {
		super();
		this.type = type;
		this.commodityList = commodityList;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<PromotionCommodity> getCommodityList() {
		return commodityList;
	}

	public void setCommodityList(List<PromotionCommodity> commodityList) {
		this.commodityList = commodityList;
	}

}

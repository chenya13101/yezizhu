package com.vincent.bean;

import java.util.List;

import com.vincent.bean.sub.PromotionCommodity;
import com.vincent.common.PromotionRangeTypeEnum;

public class PromotionRange {

	PromotionRangeTypeEnum type;

	List<PromotionCommodity> commodityList;

	public PromotionRange(PromotionRangeTypeEnum type) {
		super();
		this.type = type;
	}

	public PromotionRange(PromotionRangeTypeEnum type, List<PromotionCommodity> commodityList) {
		super();
		this.type = type;
		this.commodityList = commodityList;
	}

	public PromotionRangeTypeEnum getType() {
		return type;
	}

	public void setType(PromotionRangeTypeEnum type) {
		this.type = type;
	}

	public List<PromotionCommodity> getCommodityList() {
		return commodityList;
	}

	public void setCommodityList(List<PromotionCommodity> commodityList) {
		this.commodityList = commodityList;
	}

}

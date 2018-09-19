package com.vincent.bean;

import java.util.List;

/**
 * 用于执行券码的计算逻辑
 * 
 * @author WenSen
 * @date 2018年9月12日 下午6:53:01
 *
 */
public class WorkStep {

	// private String name;

	// private final static BigDecimal TEN = new BigDecimal(10);

	// private final static int NUMS_AFTER_POINT = 4;

	// private BigDecimal totalSale = BigDecimal.ZERO;

	// private Map<String, BigDecimal> goodsCodePriceMap = new HashMap<>();

	private CouponCode couponCode;
	// 整个过程中 couponCode不能做任何的变更

	private List<Commodity> commodityList; // 只保存本优惠券范围内的商品,可以修改值，但是不能影响其它flow

	// private WorkStep nextStep;

	// public WorkStep getNextStep() {
	// return nextStep;
	// }

	// public void setNextStep(WorkStep nextStep) {
	// this.nextStep = nextStep;
	// }

	public List<Commodity> getCommodityList() {
		return commodityList;
	}

	public WorkStep(CouponCode couponCode, List<Commodity> commodityList) {
		super();
		this.couponCode = couponCode;
		this.commodityList = commodityList;
	}

	public void setCommodityList(List<Commodity> commodityList) {
		this.commodityList = commodityList;
	}

	public void setCouponCode(CouponCode couponCode) {
		this.couponCode = couponCode;
	}

	public CouponCode getCouponCode() {
		return couponCode;
	}

	// public void setCouponCode(CouponCode couponCode) { this.couponCode =
	// couponCode; }

	// private boolean hasNext() {
	// return nextStep != null;
	//
	// }
	//
	// public WorkStep getLast() {
	// WorkStep current = this;
	// while (current.hasNext()) {
	// current = this.getNextStep();
	// }
	// return current;
	//
	// }

	public void run() {
		// TODO 计算
	}

}

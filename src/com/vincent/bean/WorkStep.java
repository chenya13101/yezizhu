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

	// private final static BigDecimal TEN = new BigDecimal(10);

	// private final static int NUMS_AFTER_POINT = 4;

	// private BigDecimal totalSale = BigDecimal.ZERO;

	// private Map<String, BigDecimal> goodsCodePriceMap = new HashMap<>();

	private CouponCode couponCode;
	// 整个过程中 couponCode不能做任何的变更

	private List<Commodity> commodityList; // 只保存本优惠券范围内的商品,可以修改值，但是不能影响其它flow

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

	/**
	 * 计算
	 * 
	 * @return true: 代表有改变; false: 代表并未改变范围内商品的优惠价格
	 */
	public boolean run() {
		// 是否有必要为折扣券和红包券单独做逻辑，甚至封装成类。或者 函数式接口

		// TODO 计算，记得考虑优惠券的使用条件是否满足
		return true;
	}

}

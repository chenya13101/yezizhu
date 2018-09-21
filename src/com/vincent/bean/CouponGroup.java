package com.vincent.bean;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CouponGroup {

	private List<CouponCode> couponCodeList;

	List<Commodity> commodityList;

	/**
	 * 优惠后商品总金额
	 */
	private BigDecimal total;

	public CouponGroup(List<Commodity> commodityList, List<CouponCode> couponCodeList, BigDecimal total) {
		super();
		this.couponCodeList = couponCodeList;
		this.total = total;
		this.commodityList = commodityList;
	}

	public List<CouponCode> getCouponCodeList() {
		return couponCodeList;
	}

	public BigDecimal getTotal() {
		return total;
	}

	@Override
	public String toString() {
		return total + " <== "
				+ this.couponCodeList.stream().map(code -> code.getCoupon().getName() + " - " + code.getCode())
						.collect(Collectors.joining(";"))
				+ "\n" + this.commodityList.stream().map(Commodity::toString).collect(Collectors.joining(";\n"));
	}

	public List<Commodity> getCommodityList() {
		return commodityList;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

}

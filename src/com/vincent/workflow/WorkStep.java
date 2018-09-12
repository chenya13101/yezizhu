package com.vincent.workflow;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.vincent.bean.Coupon;

public class WorkStep implements Comparable<WorkStep> {

	private String name;

	private final static BigDecimal TEN = new BigDecimal(10);

	private final static int NUMS_AFTER_POINT = 4;

	private BigDecimal totalSale = BigDecimal.ZERO;

	private Map<String, BigDecimal> goodsCodePriceMap = new HashMap<>();

	private Coupon coupon;

	private WorkStep nextStep;

	private WorkStep previousStep;

	AtomicInteger useCount = new AtomicInteger(0);// 为了避免死循环，同时又不循环求中值而采取的折中方法
	final int maxUseCount = 2;

	public WorkStep getNextStep() {
		return nextStep;
	}

	public void setNextStep(WorkStep nextStep) {
		this.nextStep = nextStep;
	}

	public WorkStep getPreviousStep() {
		return previousStep;
	}

	public void setPreviousStep(WorkStep previousStep) {
		this.previousStep = previousStep;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	public static BigDecimal getTen() {
		return TEN;
	}

	public static int getNumsAfterPoint() {
		return NUMS_AFTER_POINT;
	}

	public void run() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(WorkStep o) {
		return this.getName().compareTo(o.getName());
	}

	public BigDecimal getTotalSale() {
		return totalSale;
	}

	public void setTotalSale(BigDecimal totalSale) {
		this.totalSale = totalSale;
	}

	public Map<String, BigDecimal> getGoodsCodePriceMap() {
		return goodsCodePriceMap;
	}

	public void setGoodsCodePriceMap(Map<String, BigDecimal> goodsCodePriceMap) {
		this.goodsCodePriceMap = goodsCodePriceMap;
	}

}

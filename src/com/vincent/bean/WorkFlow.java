package com.vincent.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

import java.math.BigDecimal;

public class WorkFlow {

	// 每个WorkFlow都是一个优惠券组合,每一个workStep都是用来计算这个步骤的优惠券的优惠
	private List<WorkStep> workSteps = new ArrayList<>();

	private List<Commodity> commodityList; // TODO 本flow内所有的step共享commodityList，操作价格会影响下一步计算 List<Commodity>
											// commodityList

	private List<CouponCode> couponCodeList = new ArrayList<>();

	public WorkFlow(List<Commodity> commodityList) {
		// 本flow内所有的step共享commodityList，操作价格会影响下一步计算 List<Commodity> commodityList
		this.commodityList = commodityList.stream().map(t -> {
			try {
				return (Commodity) t.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return new Commodity(t.getCode(), t.getPrice());
			}
		}).collect(Collectors.toList());
	}

	/**
	 * 尝试往flow中添加step
	 */
	public void addWorkStep(CouponCode codeParam, List<Commodity> coommodityParam) {
		couponCodeList.add(codeParam);
		// 特别需要注意处理 commodityParam,与本flown内commodityList关联，切断与外界传入值得关联
		List<String> commCodeList = coommodityParam.stream().map(Commodity::getCode).collect(toList());
		workSteps.add(new WorkStep(codeParam,
				commodityList.stream().filter(comm -> commCodeList.contains(comm.getCode())).collect(toList())));
	}

	public void start() {
		workSteps.forEach(WorkStep::run);// TODO 需要确保是按照顺序来运行的
	}

	public CouponGroup getResult() {
		List<CouponCode> couponCodeList = workSteps.stream().map(WorkStep::getCouponCode).collect(toList());
		BigDecimal total = commodityList.stream().map(Commodity::getPromotePrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		return new CouponGroup(couponCodeList, total);
	}

	public List<WorkStep> getWorkSteps() {
		return workSteps;
	}

}

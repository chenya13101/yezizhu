package com.vincent.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

import java.math.BigDecimal;

public class WorkFlow {

	// 每个WorkFlow都是一个优惠券组合,每一个workStep都是用来计算这个步骤的优惠券的优惠
	private List<WorkStep> workSteps = new ArrayList<>();

	private List<Commodity> commodityList; // 本flow内所有的step共享commodityList，操作价格会影响下一步计算 List<Commodity>

	public WorkFlow(List<Commodity> commodityList) {
		// 本flow内所有的step共享commodityList，操作价格会影响下一步计算 List<Commodity> commodityList
		this.commodityList = commodityList.stream().map(t -> {
			try {
				Commodity newComm = (Commodity) t.clone();
				if (newComm.getPromotePrice() == null) {
					newComm.setPromotePrice(t.getPrice());
				}

				return newComm;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				Commodity newComm = new Commodity(t.getCode(), t.getPrice());
				newComm.setPromotePrice(t.getPrice());
				return newComm;
			}
		}).collect(Collectors.toList());
	}

	/**
	 * 尝试往flow中添加step
	 */
	public void addWorkStep(CouponCode codeParam, List<Commodity> coommodityParam) {
		// 特别需要注意处理 commodityParam,与本flown内commodityList关联，切断与外界传入值得关联
		List<String> commCodeList = coommodityParam.stream().map(Commodity::getCode).collect(toList());
		workSteps.add(new WorkStep(codeParam,
				commodityList.stream().filter(comm -> commCodeList.contains(comm.getCode())).collect(toList())));
	}

	public void start() {
		int i = 0;
		int size = workSteps.size();
		List<WorkStep> unavailableSteps = new ArrayList<>();
		for (; i < size; i++) {
			WorkStep step = workSteps.get(i);
			if (!step.run()) { // true: 代表有改变; false: 代表并未改变范围内商品的优惠价格
				unavailableSteps.add(step);
				continue;
			}

			BigDecimal totalPrice = commodityList.stream().map(Commodity::getPromotePrice).reduce(BigDecimal.ZERO,
					BigDecimal::add);
			if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		if (i < size) {
			workSteps = workSteps.subList(0, i);
			System.out.println("当总金额降低到0时，可以结束,把后续步骤删除掉");
		}
		workSteps.removeAll(unavailableSteps);

	}

	public CouponGroup getResult() {
		List<CouponCode> couponCodeList = workSteps.stream().map(WorkStep::getCouponCode).collect(toList());
		BigDecimal total = commodityList.stream().map(Commodity::getPromotePrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		return new CouponGroup(this.commodityList, couponCodeList, total);
	}

	public List<WorkStep> getWorkSteps() {
		return workSteps;
	}

}

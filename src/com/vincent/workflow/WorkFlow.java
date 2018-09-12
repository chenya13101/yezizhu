package com.vincent.workflow;

import java.util.List;
import static java.util.stream.Collectors.*;

import java.math.BigDecimal;

import com.vincent.bean.Commodity;
import com.vincent.bean.CouponGroup;
import com.vincent.bean.CouponCode;

public class WorkFlow {

	private List<WorkStep> workSteps;

	private List<Commodity> commodityList; // TODO 本flow内所有的step共享commodityList，操作价格会影响下一步计算 List<Commodity>
											// commodityList

	// TODO 要保证不受其它flow干扰
	public WorkFlow(List<WorkStep> workSteps, List<Commodity> commodityList) {
		// TODO 本flow内所有的step共享commodityList，操作价格会影响下一步计算 List<Commodity> commodityList
		this.workSteps = workSteps;
		this.commodityList = commodityList;
	}

	public CouponGroup getResult() {
		List<CouponCode> couponCodeList = workSteps.stream().map(WorkStep::getCouponCode).collect(toList());
		BigDecimal total = commodityList.stream().map(Commodity::getPromotePrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		return new CouponGroup(couponCodeList, total);
	}

	public void start() {
		workSteps.forEach(WorkStep::run);// TODO 需要确保是按照顺序来运行的
	}

}

package com.vincent.workflow;

import java.util.List;
import java.util.stream.Collectors;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Condition;
import com.vincent.bean.Coupon;
import com.vincent.bean.Product;

public class WorkFlow {

	private List<CalculateUnit> calculateUnits;

	public static void main(String[] args) {
		List<Coupon> couponList = DataFactory.getCoupons();
		if (couponList == null)
			return;

		List<Product> productList = DataFactory.getProducts();
		WorkFlow workFlow = new WorkFlow();
		workFlow.createCalculateUnits(productList);

		List<WorkStep> steps = workFlow.createWorkSteps(couponList, workFlow.getCalculateUnits());
		workFlow.start(steps);
		workFlow.showResult();
	}

	public List<CalculateUnit> returnResult() {
		return calculateUnits;
	}

	public void showResult() {
		System.out.println("最终结果:");
		this.calculateUnits.forEach(System.out::println);
	}

	public void start(List<WorkStep> steps) {
		steps.get(0).run();
	}

	public List<WorkStep> createWorkSteps(List<Coupon> couponList, List<CalculateUnit> calculateUnits) {
		List<WorkStep> steps = couponList.stream().map(coupon -> {
			WorkStep step = new WorkStep();

			// 要根据规则过滤
			step.setCalculateUnits(calculateUnits.stream().filter(coupon.getFilterRule()).collect(Collectors.toList()));

			if (coupon.getFullElement() != null) {
				Condition condition = new Condition();
				condition.setFullElement(coupon.getFullElement());
				condition.setQrCode(coupon.getCode());

				List<CalculateUnit> calculateUnitList = calculateUnits.stream().filter(coupon.getFilterRule())
						.collect(Collectors.toList());
				CalculateUnit newUnit = new CalculateUnit();
				newUnit.setCalculateUnits(calculateUnitList);
				newUnit.setMin(coupon.getFullElement());

				// 这里整合成一个 AB而不是A+B
				condition.setCalculateUnit(newUnit);
				step.setCondition(condition);
			}
			step.setCoupon(coupon);

			return step;
		}).collect(Collectors.toList());

		WorkStep previous = null;
		int i = 1;
		for (WorkStep step : steps) {
			step.setName("step" + i++);
			if (previous == null) {
				previous = step;
				continue;
			}
			step.setPreviousStep(previous);
			previous.setNextStep(step);
			previous = step;
		}
		return steps;
	}

	public List<CalculateUnit> createCalculateUnits(List<Product> productList) {
		return productList.stream().map(product -> {
			CalculateUnit unit = new CalculateUnit();
			unit.setMax(product.getPrice());
			unit.setProductCode(product.getCode());
			return unit;
		}).collect(Collectors.toList());
	}

	public List<CalculateUnit> getCalculateUnits() {
		return calculateUnits;
	}

	public void setCalculateUnits(List<CalculateUnit> calculateUnits) {
		this.calculateUnits = calculateUnits;
	}

}

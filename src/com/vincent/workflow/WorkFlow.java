package com.vincent.workflow;

import java.util.List;
import java.util.stream.Collectors;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Condition;
import com.vincent.bean.Coupon;
import com.vincent.bean.Product;

public class WorkFlow {

	private List<CalculateUnit> calculateUnits;

	List<WorkStep> workSteps;

	public static void main(String[] args) {
		List<Coupon> couponList = DataFactory.getCoupons();
		if (couponList == null)
			return;

		List<Product> productList = DataFactory.getProducts();
		WorkFlow workFlow = new WorkFlow();
		workFlow.createCalculateUnits(productList);

		workFlow.createWorkSteps(couponList, workFlow.getCalculateUnits());
		workFlow.start();
		workFlow.showResult();
	}

	public void showResult() {
		System.out.println("最终结果:");
		this.calculateUnits.forEach(System.out::println);
	}

	public void start() {
		this.getWorkSteps().get(0).run();
	}

	public void createWorkSteps(List<Coupon> couponList, List<CalculateUnit> calculateUnits) {
		List<WorkStep> steps = couponList.stream().map(coupon -> {
			WorkStep step = new WorkStep();

			if (calculateUnits == null || coupon == null || coupon.getFilterRule() == null) {
				System.out.println();
			}
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
		this.setWorkSteps(steps);
	}

	public List<CalculateUnit> createCalculateUnits(List<Product> productList) {
		List<CalculateUnit> list = productList.stream().map(product -> {
			CalculateUnit unit = new CalculateUnit();
			unit.setMax(product.getPrice());
			unit.setProductCode(product.getCode());
			return unit;
		}).collect(Collectors.toList());
		this.setCalculateUnits(list);

		return list;
	}

	public List<CalculateUnit> getCalculateUnits() {
		return calculateUnits;
	}

	public void setCalculateUnits(List<CalculateUnit> calculateUnits) {
		this.calculateUnits = calculateUnits;
	}

	public List<WorkStep> getWorkSteps() {
		return workSteps;
	}

	public void setWorkSteps(List<WorkStep> workSteps) {
		this.workSteps = workSteps;
	}

}

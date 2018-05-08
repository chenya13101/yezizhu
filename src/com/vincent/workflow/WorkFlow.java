package com.vincent.workflow;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Condition;
import com.vincent.bean.Coupon;
import com.vincent.bean.Product;

public class WorkFlow {

	public static void main(String[] args) {
		List<Coupon> couponList = DataFactory.getCoupons();
		if (couponList == null)
			return;

		List<Product> productList = DataFactory.getProducts();
		List<CalculateUnit> calculateUnits = productList.stream().map(product -> {
			CalculateUnit unit = new CalculateUnit();
			unit.setMax(product.getPrice());
			unit.setProductCode(product.getCode());
			return unit;
		}).collect(Collectors.toList());

		List<WorkStep> steps = couponList.stream().map(coupon -> {
			WorkStep step = new WorkStep();
			step.setCalculateUnits(calculateUnits);
			if (coupon.getFullElement() != null) {
				Condition condition = new Condition();
				condition.setFullElement(coupon.getFullElement());
				condition.setQrCode(coupon.getCode());

				Set<CalculateUnit> calculateUnitSet = calculateUnits.stream()
						.filter(unit -> coupon.getFilterRule().checkInRange(unit.getProductCode()))
						.collect(Collectors.toSet());
				condition.setCalculateUnitSet(calculateUnitSet);
				step.setCondition(condition);
			}

			return step;
		}).collect(Collectors.toList());

		WorkStep previous = null;
		for (WorkStep step : steps) {
			if (previous == null) {
				previous = step;
				continue;
			}
			step.setPreviousStep(previous);
			previous.setNextStep(step);
		}

		steps.get(0).run();
	}

}
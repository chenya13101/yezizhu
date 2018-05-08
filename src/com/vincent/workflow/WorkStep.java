package com.vincent.workflow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Condition;
import com.vincent.bean.Coupon;
import com.vincent.bean.CouponTypeEnum;
import com.vincent.common.ResultMessage;

public class WorkStep {

	private final BigDecimal ten = new BigDecimal(10);

	private final int numsAfterPoint = 4;

	private Condition condition;

	private List<CalculateUnit> calculateUnits;

	private Coupon coupon;

	private WorkStep nextStep;

	private WorkStep previousStep;

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

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public List<CalculateUnit> getCalculateUnits() {
		return calculateUnits;
	}

	public void setCalculateUnits(List<CalculateUnit> calculateUnits) {
		this.calculateUnits = calculateUnits;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	private ResultMessage check() {
		return condition.isAvailable();
	}

	private void work() {
		CouponTypeEnum typeEnum = coupon.getCouponTypeEnum();
		switch (typeEnum) {
		case DISCOUNT:
			discount(coupon.getDiscount(), calculateUnits);
			break;
		case CASH:
			distribute(coupon.getAmount(), calculateUnits);
			break;
		default:
			coupon.getDiscount();
			break;
		}
	}

	private void discount(BigDecimal discount, List<CalculateUnit> calculateUnits2) {
		calculateUnits2.forEach(unit -> {
			unit.setCurrentValue(
					unit.getCurrentValue().multiply(discount).divide(ten, numsAfterPoint, RoundingMode.HALF_UP));
		});
	}

	private void distribute(BigDecimal amount, List<CalculateUnit> calculateUnitsParam) {
		BigDecimal total = calculateUnitsParam.stream().map(unit -> {
			return unit.getCurrentValue() == null ? unit.getMax() : unit.getCurrentValue();
		}).reduce(BigDecimal.ZERO, BigDecimal::add);

		CalculateUnit unit;
		BigDecimal previousAmountTotal = BigDecimal.ZERO;
		BigDecimal tmpReduce;
		for (int i = 0; i < calculateUnitsParam.size(); i++) {
			unit = calculateUnitsParam.get(i);
			if (i != calculateUnitsParam.size() - 1) {
				tmpReduce = unit.getCurrentValue().multiply(amount).divide(total, numsAfterPoint, RoundingMode.HALF_UP);
				unit.setCurrentValue(unit.getCurrentValue().subtract(tmpReduce));
				previousAmountTotal = previousAmountTotal.add(tmpReduce);
			} else {
				unit.setCurrentValue(unit.getCurrentValue().subtract(amount.subtract(previousAmountTotal)));
			}
		}
	}

	private void dealFailMessageFromNextStep(ResultMessage result) {
		if (this.calculateUnits.size() == 0 && this.previousStep == null) {
			System.out.println("无法进一步处理,结束");
			return;
		}

		System.out.println("处理next传递来的修改请求");

		Set<CalculateUnit> nextUnits = result.getUnitSet();
		Set<CalculateUnit> sameUnitSet = nextUnits.stream().filter(unit -> this.getCalculateUnits().contains(unit))
				.collect(Collectors.toSet());
		if (sameUnitSet != null && sameUnitSet.size() > 0) {
			// 如果这一步就已经包含了next的某个unit，那么尝试通过只改本步骤，而不继续往上来修改unit
			CouponTypeEnum typeEnum = coupon.getCouponTypeEnum();
			switch (typeEnum) {
			case DISCOUNT:
				// FIXME 似乎不应该出现这种对象，而应该用unit =A+B来处理
				specialDiscount(coupon.getDiscount(), calculateUnits, sameUnitSet, result.getMin());
				break;
			case CASH:
				specialDistribute(coupon.getAmount(), calculateUnits, sameUnitSet, result.getMin());
				break;
			default:
				break;
			}
		}
	}

	private void specialDistribute(BigDecimal amount, List<CalculateUnit> calculateUnits2,
			Set<CalculateUnit> sameUnitSet, BigDecimal min) {
		// TODO Auto-generated method stub

	}

	private void specialDiscount(BigDecimal discount, List<CalculateUnit> calculateUnits2,
			Set<CalculateUnit> sameUnitSet, BigDecimal min) {
		// TODO Auto-generated method stub

	}

	public void run() {
		ResultMessage result = this.check();
		if (!result.isSuccess()) {
			printFailMessage(result);
			// 平摊失败
			if (previousStep == null) {
				System.out.println("結束");
				return;
			}

			previousStep.dealFailMessageFromNextStep(result); // 通知之前的步骤调整
			return;
		}

		this.work();
		printUnits();
		if (nextStep != null) {
			nextStep.run();
			return;
		}
	}

	private void printFailMessage(ResultMessage result) {
		System.out.println("平摊失败: 需要[" + result.getMethod() + "] min=" + result.getMin());
		result.getUnitSet().forEach(System.out::println);
	}

	private void printUnits() {
		System.out.println("success:");
		calculateUnits.forEach(unit -> System.out.println(unit));
	}
}

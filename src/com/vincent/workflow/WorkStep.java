package com.vincent.workflow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Condition;
import com.vincent.bean.Coupon;
import com.vincent.bean.CouponTypeEnum;
import com.vincent.common.ResultCode;
import com.vincent.common.ResultMessage;

public class WorkStep implements Comparable<WorkStep> {

	private String name;

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
			unit.saveStepChangeValue(this, unit.getCurrentValue());
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
			unit.saveStepChangeValue(this, unit.getCurrentValue());
		}
	}

	private List<CalculateUnit> getAllCalculateUnitsFromOne(CalculateUnit calculateUnit) {
		if (calculateUnit.getCalculateUnits() == null || calculateUnit.getCalculateUnits().size() == 0) {
			return Arrays.asList(calculateUnit);
		}
		List<CalculateUnit> result = new ArrayList<>();
		result.add(calculateUnit);
		result.addAll(calculateUnit.getCalculateUnits());
		return result;

	}

	private ResultMessage dealFailMessageFromNextStep(ResultMessage result) {
		if (this.calculateUnits.size() == 0 && this.previousStep == null) {
			System.out.println("无法进一步处理,结束");
			return new ResultMessage(ResultCode.FAIL_END);
		}

		System.out.println(this.getName() + ":处理next传递来的修改请求");

		List<CalculateUnit> nextUnits = getAllCalculateUnitsFromOne(result.getCalculateUnit());
		Set<CalculateUnit> sameUnitSet = nextUnits.stream().filter(unit -> this.getCalculateUnits().contains(unit))
				.collect(Collectors.toSet());
		if (sameUnitSet != null && sameUnitSet.size() > 0) {
			if (this.calculateUnits.size() == 1) {
				if (previousStep != null) {
					previousStep.dealFailMessageFromNextStep(result);
				} else {
					return new ResultMessage(ResultCode.FAIL_END);
				}
			}

			// 如果这一步就已经包含了next的某个unit，那么尝试通过只改本步骤，而不继续往上来修改unit
			CouponTypeEnum typeEnum = coupon.getCouponTypeEnum();
			switch (typeEnum) {
			case DISCOUNT:
				// FIXME 似乎不应该出现这种对象，而应该用unit =A+B来处理
				return specialDiscount(coupon.getDiscount(), calculateUnits, sameUnitSet, result.getMin());
			case CASH:
				return specialDistribute(coupon.getAmount(), calculateUnits, sameUnitSet, result.getMin());
			default:
				break;
			}
		}
		return new ResultMessage(ResultCode.SUCCESS);
	}

	private ResultMessage specialDistribute(BigDecimal amount, List<CalculateUnit> calculateUnitsParam,
			Set<CalculateUnit> sameUnitSet, BigDecimal min) {
		ResultMessage result = new ResultMessage();
		result.setResultCode(ResultCode.FAIL);
		return result;
	}

	/**
	 * 根据折扣再平摊
	 * 
	 * @param discount
	 * @param calculateUnitsParam
	 *            全部的单元
	 * @param sameUnitSet
	 *            相同的单元
	 * @param min
	 *            最小值
	 * @return
	 */
	private ResultMessage specialDiscount(BigDecimal discount, List<CalculateUnit> calculateUnitsParam,
			Set<CalculateUnit> sameUnitSet, BigDecimal min) {
		ResultMessage result = new ResultMessage();
		result.setResultCode(ResultCode.FAIL);
		return result;
	}

	public void run() {
		ResultMessage result = this.check();
		switch (result.getResultCode()) {
		case SUCCESS:
			this.work();
			printUnits();
			if (nextStep != null) {
				nextStep.run();
			}
			break;
		case FAIL:
			printFailMessage(result);
			// 平摊失败
			if (previousStep == null) {
				System.out.println("結束");
				return;
			}

			ResultMessage previousResult = previousStep.dealFailMessageFromNextStep(result); // 通知之前的步骤调整
			switch (previousResult.getResultCode()) {
			case SUCCESS:
				System.out.println("上一步骤处理成功，请开始这一步的业务逻辑");
				break;
			case FAIL_END:
			case FAIL:
				System.out.println(this.getName() + ":上一步骤[" + previousStep.getName() + "]处理失败,结束流程");
				break;
			default:
				break;
			}
			break;
		case END:
			System.out.println("正常结束");
			break;
		case FAIL_END:
			System.out.println("失败结束");
			break;
		default:
			break;
		}

	}

	private void printFailMessage(ResultMessage result) {
		System.out.println("平摊失败: 需要[" + result.getMethod() + "] min=" + result.getMin());
		System.out.println(result.getCalculateUnit());
	}

	private void printUnits() {
		System.out.println("success:");
		calculateUnits.forEach(unit -> System.out.println(unit));
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
}

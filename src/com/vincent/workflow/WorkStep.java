package com.vincent.workflow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Condition;
import com.vincent.bean.Coupon;
import com.vincent.bean.CouponTypeEnum;
import com.vincent.common.ResultMessage;

public class WorkStep {

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
		if (condition != null) {
			System.out.println(condition.getQrCode());
		} else {
			System.out.println(System.nanoTime() / 1000000);
		}

		ResultMessage message = condition.isAvailable();
		return message;
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

		// TODO 除了condition还有正常的业务运算,操作价格的方法呢
		// calculateUnits
	}

	private void discount(BigDecimal discount, List<CalculateUnit> calculateUnits2) {
		// TODO Auto-generated method stub

	}

	private void distribute(BigDecimal amount, List<CalculateUnit> calculateUnits2) {
		BigDecimal total = calculateUnits2.stream().map(unit -> {
			return unit.getCurrentValue() == null ? unit.getMax() : unit.getCurrentValue();
		}).reduce(BigDecimal.ZERO, BigDecimal::add);

		CalculateUnit unit;
		BigDecimal previousTotal = BigDecimal.ZERO;
		for (int i = 0; i < calculateUnits2.size(); i++) {
			unit = calculateUnits2.get(i);
			if (i != calculateUnits2.size() - 1) {
				unit.setCurrentValue(unit.getMax().multiply(amount).divide(total, 4, RoundingMode.HALF_UP));
				previousTotal = previousTotal.add(unit.getCurrentValue());
			} else {
				unit.setCurrentValue(amount.subtract(previousTotal));
			}
		}
	}

	public void run() {
		this.work();
		ResultMessage result = this.check();
		if (result.isSuccess()) {
			if (nextStep != null) {
				nextStep.run();
			}
		} else {
			System.out.println(result.getMethod());
			result.getUnitSet().forEach(System.out::println);
		}

	}
}

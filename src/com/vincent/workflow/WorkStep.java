package com.vincent.workflow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Condition;
import com.vincent.bean.Coupon;
import com.vincent.common.CouponTypeEnum;
import com.vincent.common.ResultCode;
import com.vincent.common.ResultMessage;

public class WorkStep implements Comparable<WorkStep> {

	private String name;

	private final static BigDecimal TEN = new BigDecimal(10);

	private final static int NUMS_AFTER_POINT = 4;

	private Condition condition;

	private List<CalculateUnit> calculateUnits;

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

	private ResultMessage check(List<CalculateUnit> otherUnits) {
		return this.condition.isAvailable(otherUnits);
	}

	private ResultMessage check() {
		return condition.isAvailable();
	}

	public static BigDecimal getTen() {
		return TEN;
	}

	public static int getNumsAfterPoint() {
		return NUMS_AFTER_POINT;
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
			break;
		}
	}

	private void discount(BigDecimal discount, List<CalculateUnit> calculateUnits2) {
		calculateUnits2.forEach(unit -> {
			unit.setCurrentValue(
					unit.getCurrentValue().multiply(discount).divide(TEN, NUMS_AFTER_POINT, RoundingMode.HALF_UP),
					this);
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
				tmpReduce = unit.getCurrentValue().multiply(amount).divide(total, NUMS_AFTER_POINT,
						RoundingMode.HALF_UP);
				unit.setCurrentValue(unit.getCurrentValue().subtract(tmpReduce), this);
				previousAmountTotal = previousAmountTotal.add(tmpReduce);
			} else {
				unit.setCurrentValue(unit.getCurrentValue().subtract(amount.subtract(previousAmountTotal)), this);
			}
			unit.saveStepChangeValue(this, unit.getCurrentValue());
		}
		printCurrentStepUnits();
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
		if (useCount.incrementAndGet() > maxUseCount) {
			return new ResultMessage(ResultCode.FAIL_END);
		}

		recoverCalculateUnits();

		if (this.calculateUnits.size() == 0 && this.previousStep == null) {
			System.out.println("无法进一步处理,结束");
			return new ResultMessage(ResultCode.FAIL_END);
		}

		System.out.println(this.getName() + ":处理next传递来的修改请求");

		ResultMessage dealResult = null;
		List<CalculateUnit> nextUnits = getAllCalculateUnitsFromOne(result.getCalculateUnit());
		List<CalculateUnit> sameUnitSet = nextUnits.stream().filter(unit -> this.getCalculateUnits().contains(unit))
				.collect(Collectors.toList());
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
				dealResult = reDiscount(coupon.getDiscount(), sameUnitSet, result.getCalculateUnit());
				break;
			case CASH:
				dealResult = reDistribute(coupon.getAmount(), calculateUnits, sameUnitSet, result.getCalculateUnit());
				break;
			default:
				break;
			}
		}

		// 如果当前步骤没有平摊成功，那么寻求更早之前的step改变平摊范围
		ResultMessage previousMessage;
		if (dealResult == null && previousStep != null) {
			previousMessage = previousStep.dealFailMessageFromNextStep(result);
			if (previousMessage.getResultCode() == ResultCode.SUCCESS) {
				this.work();
			}
			return previousMessage;
		}
		if (dealResult.getResultCode() == ResultCode.FAIL && previousStep != null) {
			previousMessage = previousStep.dealFailMessageFromNextStep(dealResult);
			if (previousMessage.getResultCode() == ResultCode.SUCCESS) {
				this.work();
			}
			return previousMessage;
		}
		return dealResult;
	}

	/**
	 * 
	 * @param amount
	 * @param calculateUnitsParam
	 * @param sameUnitSet
	 * @param checkCalculateUnit
	 *            要求满足的计算单元
	 * @return
	 */
	private ResultMessage reDistribute(BigDecimal amount, List<CalculateUnit> calculateUnitsParam,
			List<CalculateUnit> containedUnitList, CalculateUnit checkCalculateUnit) {
		ResultMessage result = new ResultMessage();
		// recoverCalculateUnits(calculateUnitsParam);
		BigDecimal sameUnitCurrentValueSum = containedUnitList.stream().map(unit -> unit.getCurrentValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (checkCalculateUnit.getMin() == null) {
			throw new RuntimeException("怎么会出现空?");
		}
		int compareValue = sameUnitCurrentValueSum.compareTo(checkCalculateUnit.getMin());
		if (compareValue == 0) {
			// 计算其他的计算单元的current之和
			List<CalculateUnit> otherUnits = new ArrayList<>(calculateUnitsParam);
			otherUnits.removeAll(containedUnitList);// 减去非本步骤包含的当前值
			BigDecimal othersCurrentTotal = otherUnits.stream().map(unit -> unit.getCurrentValue())
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			if (othersCurrentTotal.compareTo(checkCalculateUnit.getMin()) < 0) {
				result.setResultCode(ResultCode.FAIL);
				result.setCalculateUnit(checkCalculateUnit);
				return result;
			}
			return reDistributeToOtherUnits(amount, otherUnits, checkCalculateUnit);
		} else if (compareValue > 0) {
			// 是不是相同的商品可以部分参与代金券的使用
			List<CalculateUnit> otherUnits = new ArrayList<>(calculateUnitsParam);
			otherUnits.removeAll(containedUnitList);// 减去非本步骤包含的当前值
			BigDecimal othersCurrentTotal = otherUnits.stream().map(unit -> unit.getCurrentValue())
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			int compareValue2 = othersCurrentTotal.compareTo(checkCalculateUnit.getMin());// 其它商品平摊前总价最低要求要大于min
			if (compareValue2 < 0) {
				// TODO 那么必须部分参与这次的平摊,如果不能参与那么可以直接判定失败
				System.out.println("那么必须部分参与这次的平摊,如果不能参与那么可以直接判定失败");
			} else {
				// 先不管其它步骤的处理，以及如何循环更改，直接让其它的计算单元平摊
				ResultMessage reDistributeResult = reDistributeToOtherUnits(amount, otherUnits, checkCalculateUnit);
				if (reDistributeResult.getResultCode() == ResultCode.FAIL) {
					return reDistributeToOtherUnitsWithPartContainedUnits(amount, otherUnits, checkCalculateUnit,
							containedUnitList);
				}
				return reDistributeResult;
			}
		} else {
			result.setResultCode(ResultCode.FAIL);
			result.setCalculateUnit(checkCalculateUnit);
			return result;
		}

		result.setResultCode(ResultCode.FAIL);
		return result;
	}

	private ResultMessage reDistributeToOtherUnitsWithPartContainedUnits(BigDecimal amount,
			List<CalculateUnit> otherUnits, CalculateUnit checkCalculateUnit, List<CalculateUnit> containedUnitList) {
		// 这里不需要再次check
		// 给containedUnitList排序一下，从低到高开始参与本次的平摊
		List<CalculateUnit> sortedUnitList = containedUnitList.stream()
				.sorted((unit1, unit2) -> unit1.getCurrentValue().compareTo(unit2.getCurrentValue()))
				.collect(Collectors.toList());

		BigDecimal otherUnitCurrentSum = otherUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		BigDecimal fullElement = this.getCondition().getFullElement();
		if (otherUnitCurrentSum.compareTo(fullElement) >= 0) {
			throw new RuntimeException("这种情况不应该出现，上一步就可以处理的");
		}

		List<CalculateUnit> partContainedUnits = getPartContainedUnits(sortedUnitList,
				fullElement.subtract(otherUnitCurrentSum));
		partContainedUnits.addAll(otherUnits);

		ResultMessage tmpResult = reDistributeToOtherUnits(amount, partContainedUnits, checkCalculateUnit);

		// TODO 之后可以考虑返回多个对象，而不是单个
		if (checkCalculateUnit.getCurrentValue().compareTo(checkCalculateUnit.getMin()) < 0) {
			// 再次计算平摊完是否满足 checkCalculateUnit的要求
			// recoverCalculateUnits(partContainedUnits);
			tmpResult.setResultCode(ResultCode.FAIL);
			tmpResult.setCalculateUnit(checkCalculateUnit);
		}
		return tmpResult;

	}

	List<CalculateUnit> getPartContainedUnits(List<CalculateUnit> sortedUnitList, BigDecimal min) {
		List<CalculateUnit> result = new ArrayList<>();
		for (CalculateUnit tmpUnit : sortedUnitList) {
			if (tmpUnit.getCurrentValue().compareTo(min) >= 0) {
				result.add(tmpUnit);
				return result;
			}
		}

		if (result.size() == sortedUnitList.size()) {
			result.clear(); // 可以一直加，但是如果碰到需要全部加入才能满足的情况就直接放弃
		}
		return result;
	}

	private ResultMessage reDistributeToOtherUnits(BigDecimal amount, List<CalculateUnit> otherUnits,
			CalculateUnit checkCalculateUnit) {
		// 需要检查是否满足本次平摊的条件
		ResultMessage result = this.check(otherUnits);
		switch (result.getResultCode()) {
		case SUCCESS:
			break;
		case FAIL:
			printFailMessage(result);
			// 平摊失败
			result.setCalculateUnit(checkCalculateUnit);
			result.setResultCode(ResultCode.FAIL);
			return result;
		default:
			break;
		}

		distribute(amount, otherUnits);
		checkCalculateUnit.setCurrentValue(checkCalculateUnit.getCurrentValue(), this);
		// printUnits(otherUnits);
		return result;
	}

	/**
	 * 还原成上一步骤处理过之后的数据
	 * 
	 * @param containedUnitList
	 * @return
	 */
	private void recoverCalculateUnits() {
		// containedUnitList.forEach(unit -> unit.recover(this.previousStep));
		this.calculateUnits.forEach(unit -> unit.recover(this.previousStep));
	}

	/**
	 * 根据折扣再平摊
	 * 
	 * @param discount
	 *            折扣率
	 * @param containedUnitSet
	 *            本步骤内包含的单元，一定是单数据单元
	 * @param checkCalculateUnits
	 *            可能是单数据单元或复合单元
	 * @return 计算结果
	 */
	private ResultMessage reDiscount(BigDecimal discount, List<CalculateUnit> containedUnitList,
			CalculateUnit checkCalculateUnit) {
		ResultMessage result = new ResultMessage();
		if (checkCalculateUnit.getCalculateUnits() == null || checkCalculateUnit.getCalculateUnits().size() == 0) {
			result.setResultCode(ResultCode.FAIL_END);
			return result;
		}

		List<CalculateUnit> sortedUnitList = containedUnitList.stream()
				.sorted((unit1, unit2) -> unit1.getCurrentValue().compareTo(unit2.getCurrentValue()))
				.collect(Collectors.toList());
		// 本步骤内有的计算单元，折扣一下，试试看怎么样才能满足组合

		BigDecimal totalMin = checkCalculateUnit.getMin(); // 该符合计算单元最低要求
		List<CalculateUnit> checkUnitAllUnits = new ArrayList<>(checkCalculateUnit.getCalculateUnits());
		checkUnitAllUnits.removeAll(containedUnitList);// 减去非本步骤包含的当前值
		BigDecimal othersCurrentTotal = checkUnitAllUnits.stream().map(unit -> unit.getCurrentValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal minAfterDiscount = totalMin.subtract(othersCurrentTotal);

		// BigDecimal minSum = minAfterDiscount.multiply(TEN).divide(discount,
		// NUMS_AFTER_POINT, RoundingMode.HALF_UP);// 最低要求

		BigDecimal allCurrentValueSum = containedUnitList.stream().map(unit -> unit.getCurrentValue())
				.reduce(BigDecimal.ZERO, BigDecimal::add);// currentValue之和的最大值
		if (allCurrentValueSum.compareTo(minAfterDiscount) < 0) {
			result.setResultCode(ResultCode.FAIL);
			return result;
		}
		// 只要某个值不折扣时
		List<CalculateUnit> excludeList = reDiscountByRemoveOneUnit(discount, sortedUnitList, minAfterDiscount);
		if (excludeList != null && excludeList.size() > 0) {
			reDiscountExcludeIndexArray(discount, sortedUnitList, excludeList);
			result.setResultCode(ResultCode.SUCCESS);
			return result;
		}

		result.setResultCode(ResultCode.FAIL);
		return result;
	}

	private void reDiscountExcludeIndexArray(BigDecimal discount, List<CalculateUnit> sortedUnitList,
			List<CalculateUnit> excludeList) {
		sortedUnitList.forEach(unit -> {
			if (!excludeList.contains(unit)) {
				unit.setCurrentValue(
						unit.getCurrentValue().multiply(discount).divide(TEN, NUMS_AFTER_POINT, RoundingMode.HALF_UP),
						this);
			}
		});
	}

	// TODO 未来可以引申为排除多个元素
	private List<CalculateUnit> reDiscountByRemoveOneUnit(BigDecimal discount, List<CalculateUnit> containedUnitList,
			BigDecimal minAfterDiscount) {
		BigDecimal afterDiscount;
		List<CalculateUnit> result = new ArrayList<>();
		for (int i = 0; i < containedUnitList.size(); i++) {
			final int index = i;
			afterDiscount = containedUnitList.stream().map(unit -> {
				if (containedUnitList.indexOf(unit) == index) {
					return unit.getCurrentValue();
				}
				return unit.getCurrentValue().multiply(discount).divide(TEN, NUMS_AFTER_POINT, RoundingMode.HALF_UP);
			}).reduce(BigDecimal.ZERO, BigDecimal::add);
			if (afterDiscount.compareTo(minAfterDiscount) >= 0) {
				result.add(containedUnitList.get(index));
				return result;
			}
		}
		return null;
	}

	public void run() {
		ResultMessage result = this.check();
		switch (result.getResultCode()) {
		case SUCCESS:
			this.work();
			// printCurrentStepUnits();
			if (nextStep != null) {
				nextStep.run();
			}
			break;
		case FAIL:
			printFailMessage(result);
			// 平摊失败
			if (previousStep == null) {
				System.out.println("失败结束");
				return;
			}

			ResultMessage previousResult = previousStep.dealFailMessageFromNextStep(result); // 通知之前的步骤调整
			switch (previousResult.getResultCode()) {
			case SUCCESS:
				// 所有步骤，都应该检验一下是否有计算单元的值是由之后步骤产生的，避免引起混淆
				run();// 再次执行本步骤，因为之前的步骤已经把平摊值改了
				break;
			case FAIL:
			case FAIL_END:
				System.out.println(this.getName() + ":上一步骤[" + previousStep.getName() + "]处理失败,结束流程");
				break;
			default:
				break;
			}
			break;
		default:
			System.out.println("未知 check 结果");
			break;
		}

	}

	private void printFailMessage(ResultMessage result) {
		System.out.println(this.getName() + "平摊失败: ");
		System.out.println(result.getCalculateUnit());
	}

	private void printCurrentStepUnits() {
		System.out.println(this.getName() + " success:");
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

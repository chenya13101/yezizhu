package com.vincent.bean;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

import com.vincent.workflow.WorkStep;

public class CalculateUnit {

	// TODO 有没有必要保存修改之前的值
	private BigDecimal max;

	private BigDecimal min;

	private BigDecimal currentValue;

	/**
	 * 上一个步骤保留的值 TODO 有可能需要一个map记录每一个步骤的更改
	 */
	private TreeMap<WorkStep, BigDecimal> previousStepVauleMap = new TreeMap<>();

	private String productCode;

	private List<CalculateUnit> calculateUnits;

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

	public BigDecimal getCurrentValue() {
		if (calculateUnits == null || calculateUnits.size() == 0) {
			return this.currentValue;
		}
		return calculateUnits.stream().map(unit -> unit.getCurrentValue()).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	public List<CalculateUnit> getCalculateUnits() {
		return calculateUnits;
	}

	public void setCalculateUnits(List<CalculateUnit> calculateUnits) {
		this.calculateUnits = calculateUnits;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public TreeMap<WorkStep, BigDecimal> getPreviousStepVauleMap() {
		return previousStepVauleMap;
	}

	public void setPreviousStepVauleMap(TreeMap<WorkStep, BigDecimal> previousStepVauleMap) {
		this.previousStepVauleMap = previousStepVauleMap;
	}

	// TODO 期待这个方法能够发挥作用
	public void saveStepChangeValue(WorkStep step, BigDecimal value) {
		previousStepVauleMap.put(step, value);
		if (previousStepVauleMap.size() > 1 && previousStepVauleMap.lastKey() != step) {
			previousStepVauleMap = new TreeMap<>(
					previousStepVauleMap.subMap(previousStepVauleMap.firstKey(), true, step, true));
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("max =").append(max).append("; min=").append(this.min).append("; current = ")
				.append(this.getCurrentValue()).append("; productCode = ").append(this.productCode);
		if (this.calculateUnits != null) {
			builder.append("\n");
			this.calculateUnits.forEach(unit -> builder.append(unit.toString()).append("\n"));
		}

		return builder.toString();
	}

}

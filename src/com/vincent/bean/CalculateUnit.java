package com.vincent.bean;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.vincent.workflow.WorkStep;


public class CalculateUnit {

	// 有没有必要保存修改之前的值 beforeValue
	private BigDecimal max;

	private BigDecimal min;

	private BigDecimal currentValue;

	/**
	 * 上一个步骤保留的值 ,map记录每一个步骤的更改
	 */
	private TreeMap<WorkStep, BigDecimal> previousStepVauleMap = new TreeMap<>();

	private String productCode;

	private List<CalculateUnit> calculateUnits;

	public BigDecimal getMax() {
		return max;
	}

	public void setMax(BigDecimal max) {
		this.max = max;
		this.currentValue = max.add(BigDecimal.ZERO);
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

	public void setCurrentValue(BigDecimal currentValue, WorkStep step) {
		this.currentValue = currentValue;
		this.getPreviousStepVauleMap().put(step, currentValue);
	}

	private void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	public List<CalculateUnit> getCalculateUnits() {
		return calculateUnits;
	}

	public void setCalculateUnits(List<CalculateUnit> calculateUnits) {
		this.calculateUnits = calculateUnits;
		this.setCurrentValue(this.calculateUnits.stream().map(unit -> unit.getCurrentValue()).reduce(BigDecimal.ZERO,
				BigDecimal::add));
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

	public void recover(WorkStep previous) {
		if (previous == null) {
			this.setCurrentValue(this.getMax());
			return;
		}

		// 有可能上一步并没有更改，那么应该一直往前走，直到找到或者到第一步替换为max
		BigDecimal previousValue = this.getPreviousStepVauleMap().get(previous);
		if (previousValue != null) {
			this.setCurrentValue(previousValue);
		} else {
			// 找到上一个步骤
			Entry<WorkStep, BigDecimal> entry = this.getPreviousStepVauleMap().lowerEntry(previous);
			if (entry == null) {
				this.setCurrentValue(this.getMax());
			} else {
				this.setCurrentValue(entry.getValue());
			}
		}

		if (previousStepVauleMap.size() > 1 && previousStepVauleMap.lastKey() != previous) {
			previousStepVauleMap = new TreeMap<>(
					previousStepVauleMap.subMap(previousStepVauleMap.firstKey(), true, previous, true));
		}
	}

}

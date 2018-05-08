package com.vincent.bean;

import java.math.BigDecimal;
import java.util.List;

public class CalculateUnit {

	// TODO 有没有必要保存修改之前的值
	private BigDecimal max;

	private BigDecimal min;

	private BigDecimal currentValue;

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
		return currentValue;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("max =").append(max).append("; min=").append(this.min).append("; current = ")
				.append(this.currentValue).append("; productCode = ").append(this.productCode);
		if (this.calculateUnits != null) {
			builder.append("\n");
			this.calculateUnits.forEach(unit -> builder.append(unit.toString()).append("\n"));
		}

		return builder.toString();
	}
}

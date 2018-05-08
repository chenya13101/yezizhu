package com.vincent.bean;

import java.math.BigDecimal;
import java.util.Set;

import com.vincent.common.MathMethod;
import com.vincent.common.ResultMessage;

public class Condition {

	private String qrCode;

	private BigDecimal fullElement;

	private Set<CalculateUnit> calculateUnitSet;

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public BigDecimal getFullElement() {
		return fullElement;
	}

	public void setFullElement(BigDecimal fullElement) {
		this.fullElement = fullElement;
	}

	public Set<CalculateUnit> getCalculateUnitSet() {
		return calculateUnitSet;
	}

	public void setCalculateUnitSet(Set<CalculateUnit> calculateUnitSet) {
		this.calculateUnitSet = calculateUnitSet;
	}

	public ResultMessage isAvailable() {
		ResultMessage result = new ResultMessage();
		BigDecimal total = calculateUnitSet.stream().map(unit -> unit.getCurrentValue()).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		if (total.compareTo(fullElement) > 0) {
			result.setSuccess(true);
			return result;
		}
		result.setSuccess(false);
		result.setMethod(MathMethod.ADD);
		result.setUnitSet(this.calculateUnitSet);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("qrCode =").append(qrCode).append("; fullElement=").append(this.fullElement).append(";");

		if (this.calculateUnitSet != null) {
			builder.append("\n");
			this.calculateUnitSet.forEach(unit -> builder.append(unit.toString()).append("\n"));
		}

		return builder.toString();
	}
}

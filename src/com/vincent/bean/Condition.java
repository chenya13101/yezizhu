package com.vincent.bean;

import java.math.BigDecimal;

import com.vincent.common.MathMethod;
import com.vincent.common.ResultCode;
import com.vincent.common.ResultMessage;

public class Condition {

	private String qrCode;

	private BigDecimal fullElement;

	private CalculateUnit calculateUnit;

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

	public CalculateUnit getCalculateUnit() {
		return calculateUnit;
	}

	public void setCalculateUnit(CalculateUnit calculateUnit) {
		this.calculateUnit = calculateUnit;
	}

	public ResultMessage isAvailable() {
		ResultMessage result = new ResultMessage();
		if (this.calculateUnit.getCurrentValue().compareTo(fullElement) >= 0) {
			result.setResultCode(ResultCode.SUCCESS);
			return result;
		}
		result.setResultCode(ResultCode.FAIL);
		result.setMethod(MathMethod.ADD);
		result.setCalculateUnit(this.calculateUnit);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("qrCode =").append(qrCode).append("; fullElement=").append(this.fullElement).append(";");
		builder.append(this.calculateUnit.toString());

		if (this.calculateUnit.getCalculateUnits() != null) {
			builder.append("\n");
			this.calculateUnit.getCalculateUnits().forEach(unit -> builder.append(unit.toString()).append("\n"));
		}

		return builder.toString();
	}
}

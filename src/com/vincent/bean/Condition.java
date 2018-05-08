package com.vincent.bean;

import java.math.BigDecimal;
import java.util.Set;

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

}

package com.vincent.common;

import java.util.Set;

import com.vincent.bean.CalculateUnit;

public class ResultMessage {

	private boolean success;

	private MathMethod method;

	private Set<CalculateUnit> unitSet;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public MathMethod getMethod() {
		return method;
	}

	public void setMethod(MathMethod method) {
		this.method = method;
	}

	public Set<CalculateUnit> getUnitSet() {
		return unitSet;
	}

	public void setUnitSet(Set<CalculateUnit> unitSet) {
		this.unitSet = unitSet;
	}

}

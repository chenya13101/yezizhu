package com.vincent.common;

import java.math.BigDecimal;
import java.util.Set;

import com.vincent.bean.CalculateUnit;

public class ResultMessage {

	private ResultCode resultCode;

	private MathMethod method;

	// TODO method 和 min是不是只要有一个就够了
	private BigDecimal min;

	private Set<CalculateUnit> unitSet;

	public ResultMessage() {

	}

	public ResultMessage(ResultCode result) {
		this.resultCode = result;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
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

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

}

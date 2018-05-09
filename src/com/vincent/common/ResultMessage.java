package com.vincent.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vincent.bean.CalculateUnit;

public class ResultMessage {

	private ResultCode resultCode;

	private MathMethod method;

	// TODO method 和 min是不是只要有一个就够了
	private BigDecimal min;

	private CalculateUnit calculateUnit;

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

	public List<CalculateUnit> getCalculateUnits() {
		if (this.calculateUnit.getCalculateUnits() == null || this.calculateUnit.getCalculateUnits().size() == 0) {
			return Arrays.asList(this.calculateUnit);
		}
		List<CalculateUnit> result = new ArrayList<>();
		result.add(this.calculateUnit);
		result.addAll(this.calculateUnit.getCalculateUnits());
		return result;
	}

	public void setCalculateUnit(CalculateUnit calculateUnit) {
		this.calculateUnit = calculateUnit;
	}

	public BigDecimal getMin() {
		return min;
	}

	public void setMin(BigDecimal min) {
		this.min = min;
	}

}

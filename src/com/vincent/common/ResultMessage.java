package com.vincent.common;

import com.vincent.bean.CalculateUnit;

public class ResultMessage {

	private ResultCode resultCode;

	// private MathMethod method;

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

	public CalculateUnit getCalculateUnit() {
		return this.calculateUnit;
	}

	public void setCalculateUnit(CalculateUnit calculateUnit) {
		this.calculateUnit = calculateUnit;
	}

	public static ResultMessage createSuccess() {
		return new ResultMessage(ResultCode.SUCCESS);
	}
}

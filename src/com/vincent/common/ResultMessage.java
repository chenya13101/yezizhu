package com.vincent.common;

import java.util.List;

public class ResultMessage {

	private boolean success;

	private List<String> addPriceProductCodes;

	private List<String> subPriceProductCodes;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<String> getAddPriceProductCodes() {
		return addPriceProductCodes;
	}

	public void setAddPriceProductCodes(List<String> addPriceProductCodes) {
		this.addPriceProductCodes = addPriceProductCodes;
	}

	public List<String> getSubPriceProductCodes() {
		return subPriceProductCodes;
	}

	public void setSubPriceProductCodes(List<String> subPriceProductCodes) {
		this.subPriceProductCodes = subPriceProductCodes;
	}

}

package com.vincent.bean;

import java.math.BigDecimal;

/**
 * 作为参数，被外部调用者传入
 * 
 * @author WenSen
 * @date 2018年9月12日 下午6:02:26
 *
 */
public class Commodity {

	private String code;
	private BigDecimal price;

	public Commodity(String code, BigDecimal price) {
		super();
		this.price = price;
		this.code = code;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}

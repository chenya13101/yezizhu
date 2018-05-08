package com.vincent.bean;

import java.math.BigDecimal;

public class Product {

	private String code;
	private BigDecimal price;

	public Product(String code, BigDecimal price) {
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

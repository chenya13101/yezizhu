package com.vincent.bean;

import java.math.BigDecimal;

/**
 * 作为参数，被外部调用者传入
 * 
 * @author WenSen
 * @date 2018年9月12日 下午6:02:26
 *
 */
public class Commodity implements Cloneable {

	private String code;
	private BigDecimal price;
	private BigDecimal promotePrice;

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

	public BigDecimal getPromotePrice() {
		return promotePrice;
	}

	public void setPromotePrice(BigDecimal promotePrice) {
		this.promotePrice = promotePrice;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (obj instanceof Commodity) {
			Commodity input = (Commodity) obj;
			return input.getCode() == this.getCode();
		}
		return false;
	}
}

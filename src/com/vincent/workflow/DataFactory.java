package com.vincent.workflow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.vincent.bean.Coupon;
import com.vincent.bean.CouponTypeEnum;
import com.vincent.bean.Product;

public class DataFactory {

	public static List<Coupon> getCoupons() {
		List<Coupon> list = new ArrayList<>();
		Coupon c1 = new Coupon("A001", CouponTypeEnum.CASH, null, new BigDecimal("10"), new BigDecimal(50),
				(input) -> input != null && (input.indexOf("a") > -1 || input.indexOf("c") > -1));
		list.add(c1);
		return list;
	}

	public static List<Product> getProducts() {
		List<Product> list = new ArrayList<>();
		Product p1 = new Product("apple", new BigDecimal(40));
		list.add(p1);
		return list;
	}
}

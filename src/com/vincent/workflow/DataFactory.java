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
		Coupon c1 = new Coupon("A001", CouponTypeEnum.DISCOUNT, new BigDecimal(6), null, new BigDecimal(40),
				(input) -> input != null && (input.indexOf("a") > -1 || input.indexOf("c") > -1));
		list.add(c1);

		Coupon c2 = new Coupon("A002", CouponTypeEnum.CASH, null, new BigDecimal(8.5), new BigDecimal(40),
				(input) -> input != null && (input.indexOf("coffee") > -1 || input.indexOf("p") > -1));
		list.add(c2);
		return list;
	}

	public static List<Product> getProducts() {
		List<Product> list = new ArrayList<>();
		Product p1 = new Product("apple", new BigDecimal(30));
		list.add(p1);

		Product p2 = new Product("apple", new BigDecimal(20));
		list.add(p2);
		return list;
	}
}

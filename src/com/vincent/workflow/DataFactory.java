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
		Coupon c1 = new Coupon("A001", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> unit.getProductCode() != null
						&& (unit.getProductCode().indexOf("a") > -1 || unit.getProductCode().indexOf("c") > -1));
		list.add(c1);

		Coupon c2 = new Coupon("A002", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(30),
				(unit) -> unit.getProductCode() != null && (unit.getProductCode().indexOf("apple") > -1));
		// ||input.indexOf("p") > -1
		list.add(c2);
		return list;
	}

	public static List<Product> getProducts() {
		List<Product> list = new ArrayList<>();
		Product p1 = new Product("apple", new BigDecimal(30));
		list.add(p1);

		Product p2 = new Product("coffee", new BigDecimal(20));
		list.add(p2);

		Product p3 = new Product("baozi", new BigDecimal(20));
		list.add(p3);
		return list;
	}
}

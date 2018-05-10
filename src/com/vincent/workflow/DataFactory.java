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
				(unit) -> unit.getProductCode() != null && (unit.getProductCode().indexOf("ap") > -1));
		// ||input.indexOf("p") > -1
		list.add(c2);
		return list;
	}

	public static List<Product> getProducts() {
		List<Product> list = new ArrayList<>();
		Product p1 = new Product("apple", new BigDecimal(23));
		list.add(p1);

		for (int i = 0; i < 4; i++) {
			Product p5 = new Product("apple", new BigDecimal(2));
			list.add(p5);
		}

		Product p2 = new Product("coffee", new BigDecimal(47));
		list.add(p2);

		// Product p3 = new Product("yapu", new BigDecimal(8));
		// list.add(p3);

		Product p4 = new Product("yapu", new BigDecimal(2));
		list.add(p4);

		// TODO 为什么在这种情况下没有回滚数据呢
		return list;
	}
}

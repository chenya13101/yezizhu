package com.vincent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.vincent.bean.Coupon;
import com.vincent.bean.Commodity;

public class DataFactory {

	public static List<Coupon> getCoupons() {
		List<Coupon> list = new ArrayList<>();
//		Coupon c1 = new Coupon("A001", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
//				(unit) -> unit.getProductCode() != null
//						&& (unit.getProductCode().indexOf("a") > -1 || unit.getProductCode().indexOf("c") > -1));
//		list.add(c1);
//
//		Coupon c2 = new Coupon("A002", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(30),
//				(unit) -> unit.getProductCode() != null && (unit.getProductCode().indexOf("ap") > -1));
		// ||input.indexOf("p") > -1
		//list.add(c2);
		return list;
	}

	public static List<Commodity> getProducts() {
		List<Commodity> list = new ArrayList<>();
		Commodity p1 = new Commodity("apple", new BigDecimal(23));
		list.add(p1);

		for (int i = 0; i < 4; i++) {
			Commodity p5 = new Commodity("apple", new BigDecimal(2));
			list.add(p5);
		}

		Commodity p2 = new Commodity("coffee", new BigDecimal(47));
		list.add(p2);

		Commodity p4 = new Commodity("yapu", new BigDecimal(2));
		list.add(p4);

		return list;
	}
}

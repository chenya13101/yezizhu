package com.vincent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.vincent.bean.CouponCode;
import com.vincent.bean.Commodity;

public class DataFactory {

	public static List<CouponCode> getCoupons() {
		List<CouponCode> list = new ArrayList<>();

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

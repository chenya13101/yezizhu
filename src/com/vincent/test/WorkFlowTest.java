package com.vincent.test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Coupon;
import com.vincent.bean.CouponTypeEnum;
import com.vincent.bean.Product;
import com.vincent.workflow.WorkFlow;
import com.vincent.workflow.WorkStep;

class WorkFlowTest {

	@Test
	void test1() {
		// 模拟创建优惠券
		List<Coupon> couponList = new ArrayList<>();
		Coupon c1 = new Coupon("A001", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> unit.getProductCode() != null
						&& (unit.getProductCode().indexOf("a") > -1 || unit.getProductCode().indexOf("c") > -1));
		couponList.add(c1);
		Coupon c2 = new Coupon("A002", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(30),
				(unit) -> unit.getProductCode() != null && (unit.getProductCode().indexOf("ap") > -1));
		couponList.add(c2);

		// 模拟创建产品
		List<Product> productList = new ArrayList<>();
		Product p1 = new Product("apple", new BigDecimal(23));
		productList.add(p1);
		for (int i = 0; i < 4; i++) {
			Product p5 = new Product("apple", new BigDecimal(2));
			productList.add(p5);
		}
		Product p2 = new Product("coffee", new BigDecimal(47));
		productList.add(p2);
		Product p4 = new Product("yapu", new BigDecimal(2));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		List<WorkStep> steps = workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start(steps);

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(new BigDecimal(80), resultSum);
	}

}

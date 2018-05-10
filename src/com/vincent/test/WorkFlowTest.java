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

	/**
	 * 场景描述： 两张折扣券，第一张如果全平摊第二张就无法使用了； 但是也无法平摊到第二张范围内商品中的单独任意一个上(即使加上了范围外的商品)
	 */
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

	/**
	 * 场景描述： 两张折扣券，第一张如果全平摊第二张就无法使用了； 但是 可以平摊到第二张范围内商品中的某一个上(可能需要加上了范围外的商品)
	 */
	@Test
	void test2() {
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
		Product p2 = new Product("coffee", new BigDecimal(47));
		productList.add(p2);
		Product p4 = new Product("yapu", new BigDecimal(10));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		List<WorkStep> steps = workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start(steps);
		workFlow.showResult(calculateUnits);

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertNotEquals(new BigDecimal(80), resultSum);
	}

	@Test
	void test3() {
		// 模拟创建优惠券
		List<Coupon> couponList = new ArrayList<>();
		Coupon c3 = new Coupon("全场券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> true);
		couponList.add(c3);

		Coupon c1 = new Coupon("a和c券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> unit.getProductCode() != null
						&& (unit.getProductCode().indexOf("a") > -1 || unit.getProductCode().indexOf("c") > -1));
		couponList.add(c1);
		Coupon c2 = new Coupon("ap券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(30),
				(unit) -> unit.getProductCode() != null && (unit.getProductCode().indexOf("ap") > -1));
		couponList.add(c2);

		// 模拟创建产品
		List<Product> productList = new ArrayList<>();
		Product p1 = new Product("apple", new BigDecimal(23));
		productList.add(p1);
		Product p2 = new Product("coffee", new BigDecimal(47));
		productList.add(p2);
		Product p4 = new Product("yapu", new BigDecimal(10));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		List<WorkStep> steps = workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start(steps);
		workFlow.showResult(calculateUnits);

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		// TODO 需要做更多验证
		assertNotEquals(new BigDecimal(80), resultSum);
	}
}

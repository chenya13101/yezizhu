package com.vincent.test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Coupon;
import com.vincent.bean.Product;
import com.vincent.common.CouponTypeEnum;
import com.vincent.workflow.WorkFlow;

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
		Product p2 = new Product("coffee", new BigDecimal(47));
		productList.add(p2);
		
		Product p1 = new Product("apple", new BigDecimal(19));
		productList.add(p1);
		for (int i = 0; i < 6; i++) {
			Product p5 = new Product("apple", new BigDecimal(2));
			productList.add(p5);
		}
		Product p4 = new Product("yapu", new BigDecimal(2));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start();
		workFlow.showResult();

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(new BigDecimal(60).doubleValue(), resultSum.doubleValue());
	}

	/**
	 * 场景描述： 两张折扣券，第一张如果全平摊第二张就无法使用了； 但是 可以平摊到第二张范围内商品中的某一个上(可能需要加上了范围外的商品)
	 */
	@Test
	void test2() {
		// 模拟创建优惠券
		List<Coupon> couponList = new ArrayList<>();
		Coupon c1 = new Coupon("a或c券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> unit.getProductCode() != null
						&& (unit.getProductCode().indexOf("a") > -1 || unit.getProductCode().indexOf("c") > -1));
		couponList.add(c1);
		Coupon c2 = new Coupon("ap券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(30),
				(unit) -> unit.getProductCode() != null && (unit.getProductCode().indexOf("ap") > -1));
		couponList.add(c2);

		// 模拟创建产品
		List<Product> productList = new ArrayList<>();
		
		Product p2 = new Product("coffee", new BigDecimal(47));
		productList.add(p2);
		
		Product p1 = new Product("apple", new BigDecimal(23));
		productList.add(p1);
		Product p4 = new Product("yapu", new BigDecimal(10));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start();
		workFlow.showResult();

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(new BigDecimal(60).doubleValue(), resultSum.doubleValue());
	}

	/**
	 * 前两个满足后在现有场景和优化力度下，step3不会成功
	 */
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
		workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start();
		workFlow.showResult();

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(60, resultSum.doubleValue());
	}

	@Test
	void test4() {
		// 模拟创建优惠券
		List<Coupon> couponList = new ArrayList<>();
		Coupon c3 = new Coupon("全场券", CouponTypeEnum.DISCOUNT, new BigDecimal(8), null, new BigDecimal(50),
				(unit) -> true);
		couponList.add(c3);

		Coupon c1 = new Coupon("a和c券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> unit.getProductCode() != null
						&& (unit.getProductCode().indexOf("a") > -1 || unit.getProductCode().indexOf("c") > -1));
		couponList.add(c1);

		// 模拟创建产品
		List<Product> productList = new ArrayList<>();
		Product p2 = new Product("coffee", new BigDecimal(50));
		productList.add(p2);
		Product p4 = new Product("yapu", new BigDecimal(10));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start();
		workFlow.showResult();

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(40, resultSum.doubleValue());
	}

	/**
	 * 场景：step1的使用条件无法满足
	 */
	@Test
	void test5() {
		// 模拟创建优惠券
		List<Coupon> couponList = new ArrayList<>();
		Coupon c3 = new Coupon("全场券", CouponTypeEnum.DISCOUNT, new BigDecimal(8), null, new BigDecimal(100),
				(unit) -> true);
		couponList.add(c3);

		// 模拟创建产品
		List<Product> productList = new ArrayList<>();
		Product p2 = new Product("coffee", new BigDecimal(50));
		productList.add(p2);
		Product p4 = new Product("yapu", new BigDecimal(10));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start();
		workFlow.showResult();

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(60, resultSum.doubleValue());
	}

	/**
	 * 场景：step1 折扣券的使用条件满足
	 */
	@Test
	void test6() {
		// 模拟创建优惠券
		List<Coupon> couponList = new ArrayList<>();
		Coupon c3 = new Coupon("全场券", CouponTypeEnum.DISCOUNT, new BigDecimal(8), null, new BigDecimal(50),
				(unit) -> true);
		couponList.add(c3);

		// 模拟创建产品
		List<Product> productList = new ArrayList<>();
		Product p2 = new Product("coffee", new BigDecimal(50));
		productList.add(p2);
		Product p4 = new Product("yapu", new BigDecimal(10));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start();
		workFlow.showResult();

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(48, resultSum.doubleValue());
	}

	/**
	 * 场景：step1 代金券的使用条件满足
	 */
	@Test
	void test7() {
		// 模拟创建优惠券
		List<Coupon> couponList = new ArrayList<>();
		Coupon c1 = new Coupon("a和c券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> unit.getProductCode() != null
						&& (unit.getProductCode().indexOf("a") > -1 || unit.getProductCode().indexOf("c") > -1));
		couponList.add(c1);

		// 模拟创建产品
		List<Product> productList = new ArrayList<>();
		Product p2 = new Product("coffee", new BigDecimal(50));
		productList.add(p2);
		Product p4 = new Product("yapu", new BigDecimal(10));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start();
		workFlow.showResult();

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(50, resultSum.doubleValue());
	}

	/**
	 * 场景：step1的使用条件满足， step2 折扣券需要修改step1才能满足
	 */
	@Test
	void test8() {
		// 模拟创建优惠券
		List<Coupon> couponList = new ArrayList<>();
		Coupon c3 = new Coupon("全场券", CouponTypeEnum.DISCOUNT, new BigDecimal(8), null, new BigDecimal(50),
				(unit) -> true);
		couponList.add(c3);

		Coupon c2 = new Coupon("全场券", CouponTypeEnum.DISCOUNT, new BigDecimal(5), null, new BigDecimal(50),
				(unit) -> true);
		couponList.add(c2);

		// 模拟创建产品
		List<Product> productList = new ArrayList<>();
		Product p2 = new Product("coffee", new BigDecimal(50));
		productList.add(p2);
		Product p4 = new Product("yapu", new BigDecimal(10));
		productList.add(p4);

		// 实际的工作流
		WorkFlow workFlow = new WorkFlow();
		List<CalculateUnit> calculateUnits = workFlow.createCalculateUnits(productList);
		workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start();
		workFlow.showResult();

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(25, resultSum.doubleValue());
	}

	@Test
	void test9() {
		// 模拟创建优惠券
		List<Coupon> couponList = new ArrayList<>();
		Coupon c4 = new Coupon("p券", CouponTypeEnum.CASH, null, new BigDecimal(12), new BigDecimal(31),
				(unit) -> unit.getProductCode() != null && (unit.getProductCode().indexOf("p") > -1));
		couponList.add(c4);

		Coupon c3 = new Coupon("全场券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> true);
		couponList.add(c3);

		Coupon c1 = new Coupon("a和c券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> unit.getProductCode() != null
						&& (unit.getProductCode().indexOf("a") > -1 || unit.getProductCode().indexOf("c") > -1));
		couponList.add(c1);

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
		workFlow.createWorkSteps(couponList, calculateUnits);
		workFlow.start();
		workFlow.showResult();

		// 验证结果
		BigDecimal resultSum = calculateUnits.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		assertEquals(48, resultSum.doubleValue());
	}

}

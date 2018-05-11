package com.vincent.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Coupon;
import com.vincent.bean.Product;
import com.vincent.common.CouponTypeEnum;
import com.vincent.util.CouponSequenceGenerator;
import com.vincent.util.SequenceGenerator;
import com.vincent.workflow.WorkFlow;
import com.vincent.workflow.WorkStep;

public class FullWorkFlowDemo {

	private SequenceGenerator sequenceGenerator = new CouponSequenceGenerator();

	private List<Coupon> getCouponList() {
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
		return couponList;
	}

	public List<Product> getProductList() {
		List<Product> productList = new ArrayList<>();
		Product p1 = new Product("apple", new BigDecimal(23));
		productList.add(p1);
		Product p2 = new Product("coffee", new BigDecimal(47));
		productList.add(p2);
		Product p4 = new Product("yapu", new BigDecimal(10));
		productList.add(p4);
		return productList;
	}

	public void test() {
		FullWorkFlowDemo demo = new FullWorkFlowDemo();
		// 模拟创建优惠券
		List<Coupon> couponList = demo.getCouponList();
		// 模拟创建产品
		List<Product> productList = getProductList();

		List<int[]> list = sequenceGenerator.getSequences(3, 3);

		BigDecimal min = null;
		int[] minArray = null;
		for (int[] tmpArray : list) {
			min = getCalculateResult(tmpArray, couponList, productList);
			minArray = tmpArray;
			// TODO 异步 completableTask来处理
		}
		System.out.println(min);
		System.out.println(minArray);

	}

	private BigDecimal getCalculateResult(int[] tmpArray, List<Coupon> couponList, List<Product> productList) {
		WorkFlow workFlow = new WorkFlow();
		workFlow.createCalculateUnits(productList);
		List<WorkStep> steps = workFlow.createWorkSteps(couponList, workFlow.getCalculateUnits());
		workFlow.start(steps);
		List<CalculateUnit> result = workFlow.returnResult();
		return result.stream().map(CalculateUnit::getCurrentValue).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}

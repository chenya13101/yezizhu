package com.vincent.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.vincent.bean.CalculateUnit;
import com.vincent.bean.Coupon;
import com.vincent.bean.Product;
import com.vincent.common.CouponTypeEnum;
import com.vincent.util.CouponSequenceGenerator;
import com.vincent.util.SequenceGenerator;
import com.vincent.workflow.WorkFlow;

public class FullWorkFlowDemo {
	private final static int MAX_COUPON_NUM = 3;

	public List<Coupon> getCouponList() {
		List<Coupon> couponList = new ArrayList<>();
		Coupon c1 = new Coupon("全场券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> true);
		couponList.add(c1);

		Coupon c2 = new Coupon("a和c券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(50),
				(unit) -> unit.getProductCode() != null
						&& (unit.getProductCode().indexOf("a") > -1 || unit.getProductCode().indexOf("c") > -1));
		couponList.add(c2);

		Coupon c3 = new Coupon("ap券", CouponTypeEnum.CASH, null, new BigDecimal(10), new BigDecimal(30),
				(unit) -> unit.getProductCode() != null && (unit.getProductCode().indexOf("ap") > -1));
		couponList.add(c3);

		Coupon c4 = new Coupon("p券", CouponTypeEnum.CASH, null, new BigDecimal(12), new BigDecimal(35),
				(unit) -> unit.getProductCode() != null && (unit.getProductCode().indexOf("p") > -1));
		couponList.add(c4);

		Coupon c5 = new Coupon("全场券", CouponTypeEnum.DISCOUNT, new BigDecimal(5), null, new BigDecimal(50),
				(unit) -> true);
		couponList.add(c5);

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

	public Result getCalculateResult(int[] tmpArray, List<Coupon> couponList, List<Product> productList) {
		WorkFlow workFlow = new WorkFlow();
		workFlow.createCalculateUnits(productList);
		List<Coupon> selectedCouponList = new ArrayList<>();
		for (int index : tmpArray) {
			selectedCouponList.add(couponList.get(index - 1));
		}
		workFlow.createWorkSteps(selectedCouponList, workFlow.getCalculateUnits());
		workFlow.start();
		BigDecimal totalCurrentValue = workFlow.getCalculateUnits().stream().map(CalculateUnit::getCurrentValue)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		return new Result(tmpArray, totalCurrentValue, workFlow.getCalculateUnits());
	}

	private class Result {
		private int[] couponArray;

		private BigDecimal total;

		private List<CalculateUnit> calculateUnits;

		public Result(int[] couponArray, BigDecimal total, List<CalculateUnit> calculateUnits) {
			// super();
			this.couponArray = couponArray;
			this.total = total;
			this.calculateUnits = calculateUnits;
		}

		public BigDecimal getTotal() {
			return total;
		}

		public int[] getCouponArray() {
			return couponArray;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(Arrays.toString(couponArray)).append("; \n");
			builder.append(total.toString()).append("; \n");
			this.calculateUnits.forEach(unit -> builder.append(unit.toString()).append("; \n"));
			return builder.toString();
		}
	}

	public static void main(String[] args) {
		SequenceGenerator sequenceGenerator = new CouponSequenceGenerator();
		FullWorkFlowDemo demo = new FullWorkFlowDemo();
		List<Coupon> couponList = demo.getCouponList();// 模拟创建优惠券
		List<Product> productList = demo.getProductList();// 模拟创建产品

		List<int[]> list = new ArrayList<>();
		int couponSize = couponList.size();
		for (int i = 1; i <= FullWorkFlowDemo.MAX_COUPON_NUM; i++) {
			list.addAll(sequenceGenerator.getSequences(couponSize, i));
		}

		List<CompletableFuture<Result>> calculateFutures = list.stream()
				.map(tmpArray -> CompletableFuture
						.supplyAsync(() -> demo.getCalculateResult(tmpArray, couponList, productList)))
				.collect(Collectors.toList());
		Optional<Result> leastResult = calculateFutures.stream().map(CompletableFuture::join).reduce((c1, c2) -> {
			int compare = c1.getTotal().compareTo(c2.getTotal());
			if (compare != 0)
				return compare < 0 ? c1 : c2;
			return c1.getCouponArray().length <= c2.getCouponArray().length ? c1 : c2;
		});

		leastResult.ifPresent(System.out::println);
	}
}

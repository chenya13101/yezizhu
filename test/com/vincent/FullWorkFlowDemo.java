package com.vincent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.vincent.bean.Coupon;
import com.vincent.bean.Commodity;

public class FullWorkFlowDemo {

	public List<Coupon> getCouponList() {
		List<Coupon> couponList = new ArrayList<>();
		return couponList;
	}

	public List<Commodity> getProductList() {
		List<Commodity> productList = new ArrayList<>();
		Commodity p2 = new Commodity("coffee", new BigDecimal(47));
		productList.add(p2);

		Commodity p1 = new Commodity("apple", new BigDecimal(23));
		productList.add(p1);
		Commodity p4 = new Commodity("yapu", new BigDecimal(10));
		productList.add(p4);

		return productList;
	}

	public Result getCalculateResult(int[] tmpArray, List<Coupon> couponList, List<Commodity> productList) {
		List<Coupon> selectedCouponList = new ArrayList<>();
		for (int index : tmpArray) {
			selectedCouponList.add(couponList.get(index - 1));
		}
		return new Result(tmpArray, null);
	}

	private class Result {
		private int[] couponArray;

		private BigDecimal total;

		public Result(int[] couponArray, BigDecimal total) {
			// super();
			this.couponArray = couponArray;
			this.total = total;

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
			return builder.toString();
		}
	}

	public static void main(String[] args) {
		FullWorkFlowDemo demo = new FullWorkFlowDemo();
		List<Coupon> couponList = demo.getCouponList();// 模拟创建优惠券
		List<Commodity> productList = demo.getProductList();// 模拟创建产品

		List<int[]> list = new ArrayList<>();

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

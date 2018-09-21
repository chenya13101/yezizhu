package com.vincent.redPacket;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.Assert;
import org.junit.Test;

import com.vincent.bean.Commodity;
import com.vincent.bean.Coupon;
import com.vincent.bean.CouponCode;
import com.vincent.bean.CouponGroup;
import com.vincent.bean.WorkFlow;
import com.vincent.util.CouponTemplateUtil;
import com.vincent.workflow.WorkFlowFactory;

public class RedPacketTest {

	@Test
	public void oneRedAll() {
		Coupon coupon = CouponTemplateUtil.getRedPacketAllCoupon(10);
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("HBQC001");
		couponCode.setCoupon(coupon);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(1000));
		Commodity comm2 = new Commodity("Book", new BigDecimal(15));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(1005)), 0);
	}

	@Test
	public void oneRedCommodity() {
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon();
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("HBSP002");
		couponCode.setCoupon(coupon);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(15));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());

		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(107)), 0);
	}

	@Test
	public void twoRedAll() {
		Coupon coupon = CouponTemplateUtil.getRedPacketAllCoupon(10);
		CouponCode couponCode1 = new CouponCode();
		couponCode1.setCode("HBQC001");
		couponCode1.setCoupon(coupon);

		Coupon coupon2 = CouponTemplateUtil.getRedPacketAllCoupon(15);
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("HBQC002");
		couponCode2.setCoupon(coupon2);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(15));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode1, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(90)), 0);
	}

	@Test
	public void oneCommodityOver() {

	}

	public void oneCommodityLess() {

	}

}

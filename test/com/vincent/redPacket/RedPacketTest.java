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
	public void oneRedAllOver() {
		Coupon coupon = CouponTemplateUtil.getRedPacketAllCoupon(150);
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("HBQC001");
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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(0)), 0);
	}

	@Test
	public void oneRedCommodity() {
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(8);
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
	public void oneRedCommodityOver() {
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(125);
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("HBSP002");
		couponCode.setCoupon(coupon);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));
		Commodity comm3 = new Commodity("BeiZi", new BigDecimal(40));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());

		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(60)), 0);
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

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(80));
		Commodity comm2 = new Commodity("Book", new BigDecimal(50));
		Commodity comm3 = new Commodity("XiGua", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode1, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(125)), 0);
	}

	@Test
	public void twoRedAllOver() {
		Coupon coupon = CouponTemplateUtil.getRedPacketAllCoupon(15);
		CouponCode couponCode1 = new CouponCode();
		couponCode1.setCode("HBQC001");
		couponCode1.setCoupon(coupon);

		Coupon coupon2 = CouponTemplateUtil.getRedPacketAllCoupon(150);
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("HBQC002");
		couponCode2.setCoupon(coupon2);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(80));
		Commodity comm2 = new Commodity("Book", new BigDecimal(50));
		Commodity comm3 = new Commodity("XiGua", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode1, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());
		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(0)), 0);
	}

	@Test
	public void manyRedAll() {
		Coupon coupon = CouponTemplateUtil.getRedPacketAllCoupon(10);
		CouponCode couponCode1 = new CouponCode();
		couponCode1.setCode("HBQC001");
		couponCode1.setCoupon(coupon);

		Coupon coupon2 = CouponTemplateUtil.getRedPacketAllCoupon(15);
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("HBQC002");
		couponCode2.setCoupon(coupon2);

		Coupon coupon3 = CouponTemplateUtil.getRedPacketAllCoupon(15);
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("HBQC003");
		couponCode3.setCoupon(coupon3);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(80));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));
		Commodity comm3 = new Commodity("XiGua", new BigDecimal(50));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode1, couponCode2, couponCode3);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());
		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(110)), 0);
	}

	@Test
	public void manyRedAllOver() {
		Coupon coupon = CouponTemplateUtil.getRedPacketAllCoupon(180);
		CouponCode couponCode1 = new CouponCode();
		couponCode1.setCode("HBQC001");
		couponCode1.setCoupon(coupon);

		Coupon coupon2 = CouponTemplateUtil.getRedPacketAllCoupon(50);
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("HBQC002");
		couponCode2.setCoupon(coupon2);

		Coupon coupon3 = CouponTemplateUtil.getRedPacketAllCoupon(110);
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("HBQC003");
		couponCode3.setCoupon(coupon3);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(80));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));
		Commodity comm3 = new Commodity("XiGua", new BigDecimal(50));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode1, couponCode2, couponCode3);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());
		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(0)), 0);
	}

	@Test
	public void oneCommodityOver() {

	}

	public void oneCommodityLess() {

	}

}

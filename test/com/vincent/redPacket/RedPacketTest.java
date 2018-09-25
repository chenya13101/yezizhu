package com.vincent.redPacket;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.Assert;
import org.junit.Test;

import com.vincent.bean.Commodity;
import com.vincent.bean.Coupon;
import com.vincent.bean.CouponCode;
import com.vincent.bean.CouponGroup;
import com.vincent.bean.WorkFlow;
import com.vincent.bean.sub.PromotionCommodity;
import com.vincent.util.CouponTemplateUtil;
import com.vincent.workflow.WorkFlowFactory;

public class RedPacketTest {

	// TODO 如果最后的金额一致，同时一个组内包含另一个组全部券码。取size小的，淘汰掉大的

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
	public void oneRedCommodity() {
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(8, "SPHB001");
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
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(125, "SPHB001");
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
	public void twoRedCommodity() {
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(8, "SPHB001");
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("HBSP002");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketCommodityCoupon(12, "SPHB002");
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("HBSP002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(15));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(95)), 0);
	}

	@Test
	public void twoRedCommodityOver() {
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(102, "SPHB001");
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("HBSP002");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketCommodityCoupon(8, "SPHB002");
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("HBSP002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(15));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(15)), 0);
	}

	@Test
	public void manyRedCommodity() {
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");
		PromotionCommodity pcomm4 = new PromotionCommodity("被子", "BeiZi");
		PromotionCommodity pcomm5 = new PromotionCommodity("苹果", "PingGuo");
		PromotionCommodity pcomm6 = new PromotionCommodity("枕头", "ZhenTou");
		PromotionCommodity pcomm7 = new PromotionCommodity("书", "Book");

		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(8, "SPHB001",
				Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("HBSP002");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketCommodityCoupon(12, "SPHB002",
				Arrays.asList(pcomm2, pcomm3, pcomm6));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("HBSP002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Coupon coupon3 = CouponTemplateUtil.getRedPacketCommodityCoupon(12, "SPHB003",
				Arrays.asList(pcomm4, pcomm5, pcomm7));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("HBSP003");
		couponCode3.setCoupon(coupon3);
		couponCode3.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(15));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2, couponCode3);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			flow.start();
			return CompletableFuture.supplyAsync(() -> flow.getResult());
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(95)), 0);
		// FIXME 两个问题
		// 100 <== 12.0元被子-苹果-书券 - HBSP003;8.0元韶音耳机-西瓜-电脑券 - HBSP002
		// ShaoYin - 100 - 100 ;
		// Book - 15 - 0
		//
		// 115 <==
		// ShaoYin - 100 - 100 ;
		// Book - 15 - 15

	}

}

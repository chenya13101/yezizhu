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
import com.vincent.bean.enums.CouponTypeEnum;
import com.vincent.bean.sub.PromotionCommodity;
import com.vincent.bean.sub.SubDiscountLimit;
import com.vincent.util.CouponTemplateUtil;
import com.vincent.workflow.WorkFlowFactory;

public class DiscountTest {

	@Test
	public void oneAllMaxSaleOverDiscount() {
		SubDiscountLimit subLimit1 = new SubDiscountLimit(new BigDecimal(20), new BigDecimal(9.0));
		SubDiscountLimit subLimit2 = new SubDiscountLimit(new BigDecimal(50), new BigDecimal(8.0));
		SubDiscountLimit subLimit3 = new SubDiscountLimit(new BigDecimal(100), new BigDecimal(5.0));
		List<SubDiscountLimit> limitList = Arrays.asList(subLimit1, subLimit2, subLimit3);

		Coupon coupon = CouponTemplateUtil.getDiscountRangeAllCoupon(10, "QCMZ001", limitList);
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMZ001");
		couponCode.setCoupon(coupon);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(80));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(90)), 0);
	}

	@Test
	public void oneAllDiscountOverMaxSale() {
		SubDiscountLimit subLimit1 = new SubDiscountLimit(new BigDecimal(20), new BigDecimal(9.0));
		SubDiscountLimit subLimit2 = new SubDiscountLimit(new BigDecimal(50), new BigDecimal(8.0));
		SubDiscountLimit subLimit3 = new SubDiscountLimit(new BigDecimal(100), new BigDecimal(5.0));
		List<SubDiscountLimit> limitList = Arrays.asList(subLimit1, subLimit2, subLimit3);

		Coupon coupon = CouponTemplateUtil.getDiscountRangeAllCoupon(60, "QCMZ001", limitList);
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMZ001");
		couponCode.setCoupon(coupon);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(80));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(50)), 0);
	}

	@Test
	public void oneAllMinRequire() {
		SubDiscountLimit subLimit1 = new SubDiscountLimit(new BigDecimal(50), new BigDecimal(9.0));
		SubDiscountLimit subLimit2 = new SubDiscountLimit(new BigDecimal(120), new BigDecimal(8.0));
		SubDiscountLimit subLimit3 = new SubDiscountLimit(new BigDecimal(800), new BigDecimal(5.0));
		List<SubDiscountLimit> limitList = Arrays.asList(subLimit1, subLimit2, subLimit3);

		Coupon coupon = CouponTemplateUtil.getDiscountRangeAllCoupon(10, "QCMZ001", limitList);
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMZ001");
		couponCode.setCoupon(coupon);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(80));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(90)), 0);
	}

	@Test
	public void oneAllOver() {
		SubDiscountLimit subLimit1 = new SubDiscountLimit(new BigDecimal(50), new BigDecimal(9.0));
		SubDiscountLimit subLimit2 = new SubDiscountLimit(new BigDecimal(120), new BigDecimal(8.0));
		SubDiscountLimit subLimit3 = new SubDiscountLimit(new BigDecimal(800), new BigDecimal(5.0));
		List<SubDiscountLimit> limitList = Arrays.asList(subLimit1, subLimit2, subLimit3);

		Coupon coupon = CouponTemplateUtil.getDiscountRangeAllCoupon(400, "QCMZ001", limitList);
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMZ001");
		couponCode.setCoupon(coupon);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(800));
		Commodity comm2 = new Commodity("Book", new BigDecimal(200));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(600)), 0);
	}

	@Test
	public void twoAll() {
		SubDiscountLimit subLimit1 = new SubDiscountLimit(new BigDecimal(50), new BigDecimal(9.0));
		SubDiscountLimit subLimit2 = new SubDiscountLimit(new BigDecimal(120), new BigDecimal(8.0));
		SubDiscountLimit subLimit3 = new SubDiscountLimit(new BigDecimal(800), new BigDecimal(5.0));
		List<SubDiscountLimit> limitList = Arrays.asList(subLimit1, subLimit2, subLimit3);

		Coupon coupon = CouponTemplateUtil.getDiscountRangeAllCoupon(250, "QCMZ001", limitList);
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMZ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getDiscountRangeAllCoupon(200, "QCMZ002", limitList);
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("QQCMZ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(800));
		Commodity comm2 = new Commodity("Book", new BigDecimal(200));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(750)), 0);
	}

	@Test
	public void oneCommodity() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 10, "QCMJ001",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(110)), 0);
	}

	@Test
	public void oneCommodityOver() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 120, "QCMJ001",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(20)), 0);
	}

	@Test
	public void twoCommodity() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 25, "QCMJ001",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 20, "QCMJ002",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("SPDJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(95)), 0);
	}

	@Test
	public void twoCommodityJoin() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 25, "QCMJ001",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 300, "QCMJ002",
				new BigDecimal(1000), Arrays.asList(pcomm1, pcomm3));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("SPDJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("DianNao", new BigDecimal(1200));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(1000)), 0);
	}

	@Test
	public void twoCommodityDisJoin() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 50, "QCMJ001",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 300, "QCMJ002",
				new BigDecimal(1000), Arrays.asList(pcomm2, pcomm3));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("SPDJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("DianNao", new BigDecimal(1200));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(950)), 0);
	}

	@Test
	public void twoCommodityOver() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 150, "QCMJ001",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 120, "QCMJ002",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("SPDJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("XiGua", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(0)), 0);
	}

	@Test
	public void manyCommodityDisJoin1() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm4 = new PromotionCommodity("桃子", "TaoZi");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 50, "SPMJ001",
				new BigDecimal(200), Arrays.asList(pcomm1, pcomm4));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 10, "SPMJ002",
				new BigDecimal(50), Arrays.asList(pcomm2, pcomm4));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("SPDJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(300));
		Commodity comm2 = new Commodity("XiGua", new BigDecimal(20));// 不满足 coupon2 使用条件
		Commodity comm3 = new Commodity("DianNao", new BigDecimal(1200));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(1470)), 0);
	}

	@Test
	public void manyCommodityDisJoin2() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");
		PromotionCommodity pcomm4 = new PromotionCommodity("桃子", "TaoZi");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 50, "SPMJ001",
				new BigDecimal(200), Arrays.asList(pcomm1, pcomm4));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 10, "SPMJ002",
				new BigDecimal(50), Arrays.asList(pcomm2, pcomm4));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("SPDJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Coupon coupon3 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 100, "SPMJ003",
				new BigDecimal(1000), Arrays.asList(pcomm3, pcomm4));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("SPDJ003");
		couponCode3.setCoupon(coupon3);
		couponCode3.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(300));
		Commodity comm2 = new Commodity("XiGua", new BigDecimal(50));
		Commodity comm3 = new Commodity("DianNao", new BigDecimal(1200));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2, couponCode3);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(1390)), 0);
	}

	@Test
	public void manyCommodityPartJoin() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");
		PromotionCommodity pcomm4 = new PromotionCommodity("桃子", "TaoZi");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 50, "SPMJ001",
				new BigDecimal(200), Arrays.asList(pcomm1, pcomm4));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 10, "SPMJ002",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm4));// 与coupon1有相同商品pcomm1,所以不能够叠加使用了
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("SPDJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Coupon coupon3 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 100, "SPMJ003",
				new BigDecimal(1000), Arrays.asList(pcomm3, pcomm4));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("SPDJ003");
		couponCode3.setCoupon(coupon3);
		couponCode3.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(300));
		Commodity comm2 = new Commodity("XiGua", new BigDecimal(50));
		Commodity comm3 = new Commodity("DianNao", new BigDecimal(1200));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2, couponCode3);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(1400)), 0);
	}

	@Test
	public void oneAllAndCommodity() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 10, "SPMJ001",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("SPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 10, "QCMJ001", new BigDecimal(50));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("QQCMJ001");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(120));
		Commodity comm2 = new Commodity("Book", new BigDecimal(30));
		Commodity comm3 = new Commodity("BeiZi", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(150)), 0);
	}

	@Test
	public void oneAllAndTwoCommodityJoin() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 25, "SPMJ001",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QSPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 20, "SPMJ002",
				new BigDecimal(50), Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("QSPDJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Coupon coupon3 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 10, "QCMJ001", new BigDecimal(15));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("QQCDJ003");
		couponCode3.setCoupon(coupon3);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(120));
		Commodity comm2 = new Commodity("XiGua", new BigDecimal(30));
		Commodity comm3 = new Commodity("BeiZi", new BigDecimal(10));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2, couponCode3);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(125)), 0);
	}

	@Test
	public void oneAllAndTwoCommodityDisJoin() { // TODO
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 20, "SPMJ001",
				new BigDecimal(100), Arrays.asList(pcomm1, pcomm2));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QSPDJ001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeCommodityCoupon(CouponTypeEnum.CASH, 100, "SPMJ002",
				new BigDecimal(1000), Arrays.asList(pcomm2, pcomm3));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("QSPDJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Coupon coupon3 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 80, "QCMJ001", new BigDecimal(500));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("QQCDJ003");
		couponCode3.setCoupon(coupon3);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(120));
		Commodity comm2 = new Commodity("DianNao", new BigDecimal(1000));
		Commodity comm3 = new Commodity("BeiZi", new BigDecimal(10));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode, couponCode2, couponCode3);
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream().map(flow -> {
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());

		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(930)), 0);
	}

}

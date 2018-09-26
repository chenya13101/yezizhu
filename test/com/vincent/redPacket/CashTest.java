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
import com.vincent.util.CouponTemplateUtil;
import com.vincent.workflow.WorkFlowFactory;

public class CashTest {

	@Test
	public void oneAll() {
		Coupon coupon = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 10, "QCMJ001", new BigDecimal(15));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMJ001");
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
	public void oneAllMinRequire() {
		Coupon coupon = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 10, "QCMJ001", new BigDecimal(101));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMJ001");
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
		Assert.assertEquals(groups.size(), 0);
	}

	@Test
	public void oneAllOver() {
		Coupon coupon = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 50, "QCMJ001", new BigDecimal(100));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMJ001");
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
	public void twoAll() {
		Coupon coupon = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 20, "QCMJ002", new BigDecimal(100));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMJ001");
		couponCode.setCoupon(coupon);

		Coupon coupon2 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 25, "QCMJ001", new BigDecimal(100));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("QQCMJ002");
		couponCode2.setCoupon(coupon2);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(90));
		Commodity comm2 = new Commodity("Book", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2);
		List<CouponCode> couponCodeList = Collections.singletonList(couponCode2);
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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(85)), 0);
	}

	@Test
	public void twoAll2() {
		Coupon coupon = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 20, "QCMJ002", new BigDecimal(90));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("QQCMJ001");
		couponCode.setCoupon(coupon);

		Coupon coupon2 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 25, "QCMJ001", new BigDecimal(120));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("QQCMJ002");
		couponCode2.setCoupon(coupon2);

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(90));
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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(90)), 0);
	}

	@Test
	public void twoAllOver() {
		Coupon coupon = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 150, "QCMJ001", new BigDecimal(90));
		CouponCode couponCode1 = new CouponCode();
		couponCode1.setCode("QQCMJ001");
		couponCode1.setCoupon(coupon);
		couponCode1.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 150, "QCMJ002", new BigDecimal(60));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("QQCMJ002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(80));
		Commodity comm2 = new Commodity("Book", new BigDecimal(50));
		Commodity comm3 = new Commodity("XiGua", new BigDecimal(20));

		List<Commodity> commodityList = Arrays.asList(comm1, comm2, comm3);
		List<CouponCode> couponCodeList = Arrays.asList(couponCode1, couponCode2);
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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(0)), 0);
	}

	@Test
	public void manyAll() {
		Coupon coupon = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 20, "QCMJ001", new BigDecimal(50));
		CouponCode couponCode1 = new CouponCode();
		couponCode1.setCode("HBQC001");
		couponCode1.setCoupon(coupon);

		Coupon coupon2 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 30, "QCMJ002", new BigDecimal(60));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("HBQC002");
		couponCode2.setCoupon(coupon2);

		Coupon coupon3 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 25, "QCMJ003", new BigDecimal(70));
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
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());
		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(120)), 0);
	}

	@Test
	public void manyAllOver() {
		Coupon coupon = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 25, "QCMJ001", new BigDecimal(50));
		CouponCode couponCode1 = new CouponCode();
		couponCode1.setCode("HBQC001");
		couponCode1.setCoupon(coupon);

		Coupon coupon2 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 160, "QCMJ002", new BigDecimal(60));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("HBQC002");
		couponCode2.setCoupon(coupon2);

		Coupon coupon3 = CouponTemplateUtil.getRangeAllCoupon(CouponTypeEnum.CASH, 140, "QCMJ003", new BigDecimal(70));
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
			return CompletableFuture.supplyAsync(() -> {
				flow.start();
				return flow.getResult();
			});
		}).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join)
				.filter(group -> group.getCouponCodeList().size() > 0).collect(toList());
		groups.sort((tmpGroup1, tmpGroup2) -> tmpGroup1.getTotal().compareTo(tmpGroup2.getTotal()));
		groups.forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(0)), 0);
	}

	@Test
	public void oneCommodity() {
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
	public void oneCommodityOver() {
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
	public void twoCommodity() {
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
	public void twoCommodityJoin() {
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
	public void twoCommodityDisJoin() {
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
	public void twoCommodityOver() {
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
	public void manyCommodityDisJoin1() {
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
	public void manyCommodityDisJoin2() {
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
	public void manyCommodity2() {
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");
		PromotionCommodity pcomm4 = new PromotionCommodity("被子", "BeiZi");
		PromotionCommodity pcomm5 = new PromotionCommodity("苹果", "PingGuo");
		PromotionCommodity pcomm6 = new PromotionCommodity("枕头", "ZhenTou");
		PromotionCommodity pcomm7 = new PromotionCommodity("书", "Book");
		// TODO test
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(8, "CPSPHB001",
				Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("code001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketCommodityCoupon(12, "CPSPHB002",
				Arrays.asList(pcomm2, pcomm3, pcomm6));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("code002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Coupon coupon3 = CouponTemplateUtil.getRedPacketCommodityCoupon(15, "CPSPHB003",
				Arrays.asList(pcomm1, pcomm4, pcomm5, pcomm7));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("code003");
		couponCode3.setCoupon(coupon3);
		couponCode3.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(15));
		Commodity comm3 = new Commodity("BeiZi", new BigDecimal(20));

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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(120)), 0);
	}

	@Test
	public void manyCommodity3() {
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");
		PromotionCommodity pcomm4 = new PromotionCommodity("被子", "BeiZi");
		PromotionCommodity pcomm5 = new PromotionCommodity("苹果", "PingGuo");
		PromotionCommodity pcomm6 = new PromotionCommodity("枕头", "ZhenTou");
		PromotionCommodity pcomm7 = new PromotionCommodity("书", "Book");
		// TODO test
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(8, "CPSPHB001",
				Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("code001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketCommodityCoupon(12, "CPSPHB002",
				Arrays.asList(pcomm1, pcomm2, pcomm3, pcomm6));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("code002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Coupon coupon3 = CouponTemplateUtil.getRedPacketCommodityCoupon(15, "CPSPHB003",
				Arrays.asList(pcomm1, pcomm4, pcomm5, pcomm7));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("code003");
		couponCode3.setCoupon(coupon3);
		couponCode3.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(15));
		Commodity comm3 = new Commodity("BeiZi", new BigDecimal(20));

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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(115)), 0);
	}

	@Test
	public void manyCommodity4() {
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");
		PromotionCommodity pcomm4 = new PromotionCommodity("被子", "BeiZi");
		PromotionCommodity pcomm5 = new PromotionCommodity("苹果", "PingGuo");
		PromotionCommodity pcomm6 = new PromotionCommodity("枕头", "ZhenTou");
		PromotionCommodity pcomm7 = new PromotionCommodity("书", "Book");
		// TODO test
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(8, "CPSPHB001",
				Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("code001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketCommodityCoupon(12, "CPSPHB002",
				Arrays.asList(pcomm1, pcomm2, pcomm3, pcomm6));
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("code002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Coupon coupon3 = CouponTemplateUtil.getRedPacketCommodityCoupon(15, "CPSPHB003",
				Arrays.asList(pcomm4, pcomm5, pcomm6, pcomm7));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("code003");
		couponCode3.setCoupon(coupon3);
		couponCode3.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(100));
		Commodity comm2 = new Commodity("Book", new BigDecimal(15));
		Commodity comm3 = new Commodity("BeiZi", new BigDecimal(20));

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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(100)), 0);
	}

	@Test
	public void oneAllAndCommodity() {
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");
		// TODO test
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(20, "CPSPHB001",
				Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("code001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketAllCoupon(15);
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("code002");
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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(135)), 0);
	}

	@Test
	public void oneAllAndTwoCommodity() {
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");
		// TODO test
		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(20, "CPSPHB001",
				Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("code001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());

		Coupon coupon3 = CouponTemplateUtil.getRedPacketCommodityCoupon(30, "CPSPHB003", Arrays.asList(pcomm1, pcomm2));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("code003");
		couponCode3.setCoupon(coupon3);
		couponCode3.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketAllCoupon(10);
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("code002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(100)), 0);
	}

	@Test
	public void oneAllAndTwoCommodity2() {
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(20, "CPSPHB001",
				Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("code001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());
		// TODO test
		Coupon coupon3 = CouponTemplateUtil.getRedPacketCommodityCoupon(30, "CPSPHB003", Arrays.asList(pcomm1, pcomm2));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("code003");
		couponCode3.setCoupon(coupon3);
		couponCode3.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketAllCoupon(10);
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("code002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(40));// 降低价格到 两个coupon comm之间
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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(20)), 0);
	}

	@Test
	public void oneAllAndTwoCommodity3() {
		PromotionCommodity pcomm1 = new PromotionCommodity("韶音耳机", "ShaoYin");
		PromotionCommodity pcomm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity pcomm3 = new PromotionCommodity("电脑", "DianNao");

		Coupon coupon = CouponTemplateUtil.getRedPacketCommodityCoupon(20, "CPSPHB001",
				Arrays.asList(pcomm1, pcomm2, pcomm3));
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("code001");
		couponCode.setCoupon(coupon);
		couponCode.setReceiveTime(new Date());
		// TODO test
		Coupon coupon3 = CouponTemplateUtil.getRedPacketCommodityCoupon(30, "CPSPHB003", Arrays.asList(pcomm1, pcomm2));
		CouponCode couponCode3 = new CouponCode();
		couponCode3.setCode("code003");
		couponCode3.setCoupon(coupon3);
		couponCode3.setReceiveTime(new Date());

		Coupon coupon2 = CouponTemplateUtil.getRedPacketAllCoupon(10);
		CouponCode couponCode2 = new CouponCode();
		couponCode2.setCode("code002");
		couponCode2.setCoupon(coupon2);
		couponCode2.setReceiveTime(new Date());

		Commodity comm1 = new Commodity("ShaoYin", new BigDecimal(10));
		Commodity comm2 = new Commodity("XiGua", new BigDecimal(5));
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
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(0)), 0);
	}
}

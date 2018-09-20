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
	public void oneAll() {
		Coupon coupon = CouponTemplateUtil.getRedPacketAllCoupon();
		CouponCode couponCode = new CouponCode();
		couponCode.setCode("RED001");
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

		// System.out.println(groups.size());
		// groups.stream().map(CouponGroup::getTotal).forEach(System.out::println);
		Assert.assertEquals(groups.get(0).getTotal().compareTo(new BigDecimal(1005)), 0);

	}

	public void oneCommodityOver() {

	}

	public void oneCommodityLess() {

	}

}

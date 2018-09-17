package com.vincent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import static java.util.stream.Collectors.toList;

import com.vincent.bean.Commodity;
import com.vincent.bean.CouponCode;
import com.vincent.bean.CouponGroup;
import com.vincent.bean.WorkFlow;
import com.vincent.workflow.WorkFlowFactory;

public class Main {
	public static void main(String[] args) {
		List<CouponCode> couponCodeList = null;
		List<Commodity> commodityList = null;
		List<WorkFlow> workFlowList = WorkFlowFactory.buildWorkFlow(commodityList, couponCodeList);

		List<CompletableFuture<CouponGroup>> calculateFutures = workFlowList.stream()
				.map(flow -> CompletableFuture.supplyAsync(() -> flow.getResult())).collect(toList());
		List<CouponGroup> groups = calculateFutures.stream().map(CompletableFuture::join).collect(toList());
		groups.stream().map(CouponGroup::getTotal).forEach(System.out::println);
	}

}

package com.vincent.workflow;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import com.vincent.bean.Commodity;
import com.vincent.bean.CouponCode;
import com.vincent.bean.WorkFlow;
import com.vincent.bean.enums.CouponTypeEnum;
import com.vincent.bean.enums.PromotionRangeTypeEnum;
import com.vincent.bean.sub.PromotionCommodity;
import com.vincent.common.Constant;
import com.vincent.util.EnumUtil;

public class WorkFlowFactory {

	private static Function<CouponCode, Integer> functionForType = code -> code.getCoupon().getType();
	private static Function<CouponCode, Integer> functionForRange = code -> code.getCoupon().getPromotionRange()
			.getType();

	private static final int SIZE_TWO = 2;

	/**
	 * 
	 * @param commodityList
	 *            外部传入的商品列表
	 * @param couponCodeList
	 *            用户已拥有的券码列表
	 * @return 可用的券码组合，需要通过计算得出优惠金额
	 */
	public static List<WorkFlow> buildWorkFlow(List<Commodity> commodityList, List<CouponCode> couponCodeList) {
		if (couponCodeList == null || couponCodeList.size() == 0) {
			return null;
		}
		// TODO
		if (couponCodeList.size() == 1) {
			// TODO 只有一张券的时候可以简单处理,甚至可以在调用这个方法的地方单独写if
			return buildFlowForSingleCode(couponCodeList.get(0), commodityList);
		}

		// TODO couponCodeList是否有必要去除重复的优惠券，或者是在某些情况下需要去除重复种类的券码
		// 如果coupon.code 相同，而不是 红包+全场券，那么只留下一张

		// Map<类型,Map<全场or商品,List<code>>>分组
		Map<Integer, Map<Integer, List<CouponCode>>> groupingMap = couponCodeList.stream()
				.collect(groupingBy(functionForType, groupingBy(functionForRange)));

		List<WorkFlow> resultWorkFlows = new ArrayList<>();
		groupingMap.forEach((key, rangeCodeMap) -> {
			CouponTypeEnum typeEnum = EnumUtil.getEnumObject(CouponTypeEnum.class, type -> type.getIndex() == key);
			switch (typeEnum) {
			case CASH:
				resultWorkFlows.addAll(buildCashWorkFlows(rangeCodeMap, commodityList));
				break;
			case DISCOUNT:
				resultWorkFlows.addAll(buildDiscountWorkFlows(rangeCodeMap, commodityList));
				break;
			case RED_PACKET:
				resultWorkFlows.addAll(buildRedPacketWorkFlows(rangeCodeMap, commodityList));
				break;
			default:
				throw new IllegalArgumentException(Constant.INVALID_INDEX);
			}

		});

		return resultWorkFlows;
	}

	/**
	 * 如果只有一张优惠券，可以简单处理
	 */
	private static List<WorkFlow> buildFlowForSingleCode(CouponCode codeParam, List<Commodity> commodityList) {
		WorkFlow flow = new WorkFlow(commodityList);
		flow.addWorkStep(codeParam, filterCommodityForCode(codeParam, commodityList));
		return Collections.singletonList(flow);
	}

	private static List<WorkFlow> buildRedPacketWorkFlows(Map<Integer, List<CouponCode>> rangeCodeMap,
			List<Commodity> commodityList) {
		List<CouponCode> commodityCodeList = rangeCodeMap.get(PromotionRangeTypeEnum.COMMODITY.getIndex());
		List<CouponCode> allCodeList = rangeCodeMap.get(PromotionRangeTypeEnum.ALL.getIndex());

		List<WorkFlow> commodityFlows = buildCommodityFlows(commodityList, commodityCodeList);
		List<WorkFlow> allFlows = buildFlowForRedPacketAll(allCodeList, commodityList);
		if (commodityCodeList == null || commodityCodeList.size() == 0)
			return allFlows;
		// 因为红包规则：全场与全场叠加不限制，而且全场与商品池券叠加也不限制，那么可以直接操作上一步骤生成的list
		if (allCodeList == null || allCodeList.size() == 0) {
			return commodityFlows;
		}

		List<WorkFlow> resultFlowList = new ArrayList<>();
		commodityFlows.forEach(tmpCommodityFlow -> {
			allFlows.forEach(tmpAllFlow -> {
				WorkFlow tmpFlow = new WorkFlow(commodityList);
				tmpCommodityFlow.getWorkSteps().forEach(step -> {
					tmpFlow.addWorkStep(step.getCouponCode(), step.getCommodityList());
				});
				tmpAllFlow.getWorkSteps().forEach(step -> {
					tmpFlow.addWorkStep(step.getCouponCode(), step.getCommodityList());
				});
				resultFlowList.add(tmpFlow);
			});
		});
		return resultFlowList;
	}

	/**
	 * 将红包类型得全部全场券组装为一个flow,因为他们可以无条件叠加. TODO 除非凭借部分券就是减扣到0
	 */
	private static List<WorkFlow> buildFlowForRedPacketAll(List<CouponCode> allCodeList,
			List<Commodity> commodityList) {
		if (allCodeList == null)
			return null;

		if (allCodeList.size() == 1) {
			WorkFlow workFlow = new WorkFlow(commodityList);
			workFlow.addWorkStep(allCodeList.get(0), commodityList);
			return Collections.singletonList(workFlow);
		}
		if (allCodeList.size() == SIZE_TWO) { // 简易版本，为两个券码设计
			return buildFlowFor2RedPacketAll(allCodeList, commodityList);
		}
		// TODO 看样子还是需要双层for循环，找出所有可能的组
		return buildFlowForManyRedPacketAll(allCodeList, commodityList);
	}

	private static List<WorkFlow> buildFlowForManyRedPacketAll(List<CouponCode> allCodeList,
			List<Commodity> commodityList) {
		int size = allCodeList.size();
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				// TODO 记得及时中断
			}
		}

		return null;
	}

	private static List<WorkFlow> buildFlowFor2RedPacketAll(List<CouponCode> allCodeList,
			List<Commodity> commodityList) {
		BigDecimal totalPrice = commodityList.stream().map(Commodity::getPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		List<WorkFlow> resultFlowList = new ArrayList<>();
		BigDecimal tmpMaxSale = BigDecimal.ZERO;
		WorkFlow workFlow = new WorkFlow(commodityList);
		int size = allCodeList.size();
		for (CouponCode code : allCodeList) {
			workFlow.addWorkStep(code, commodityList);
			tmpMaxSale = tmpMaxSale.add(code.getCoupon().getUseLimit().getMaxSale());
			if (tmpMaxSale.compareTo(totalPrice) >= 0) {
				resultFlowList.add(workFlow);
				workFlow = new WorkFlow(commodityList);
				tmpMaxSale = BigDecimal.ZERO;
			}
			// 加上最后一个 flow
			if (code == allCodeList.get(size - 1) && tmpMaxSale.compareTo(BigDecimal.ZERO) > 0) {
				resultFlowList.add(workFlow);
			}
		}

		return resultFlowList;
	}

	private static List<WorkFlow> buildDiscountWorkFlows(Map<Integer, List<CouponCode>> rangeCodeMap,
			List<Commodity> commodityList) {
		// TODO Auto-generated method stub
		return null;
	}

	private static List<WorkFlow> buildCashWorkFlows(Map<Integer, List<CouponCode>> rangeCodeMap,
			List<Commodity> commodityList) {
		// TODO Auto-generated method stub
		return null;
	}

	// 规则：先算商品池券，再算全场券。所以分为两个方法. workFlow中先添加的step会先行计算
	private static List<WorkFlow> buildCommodityFlows(List<Commodity> commodityList,
			List<CouponCode> promoteCommodityList) {
		if (promoteCommodityList == null)
			return null;
		List<WorkFlow> workFlowList = new ArrayList<>();
		// 1.使用双层for循环的方式，找出所有的可能workFlow代表的券码组合
		int size = promoteCommodityList.size();
		for (int i = 0; i < size; i++) {
			WorkFlow workFlow = new WorkFlow(commodityList);
			CouponCode out = promoteCommodityList.get(i);
			List<Commodity> outCommodities = filterCommodityForCode(out, commodityList);

			for (int j = i + 1; j < size; j++) {
				CouponCode inner = promoteCommodityList.get(j);
				List<Commodity> innerCommodities = filterCommodityForCode(inner, commodityList);
				// 判断:优惠券商品范围没有交叉才能叠加使用
				if (Collections.disjoint(outCommodities, innerCommodities)) {
					workFlow.addWorkStep(inner, outCommodities);
				}
				// FIXME 记得及时中断,可能遇到 maxSale之和已经大于 price之和的情况
			}
			workFlowList.add(workFlow);
		}
		// 根据产品的设计，实现组装code为 step,然后组装成flow
		return workFlowList;
	}

	private static List<Commodity> filterCommodityForCode(CouponCode codeParam, List<Commodity> commodityList) {
		// 如果是全场券,返回全部商品
		int codeRangeType = codeParam.getCoupon().getPromotionRange().getType();
		if (codeRangeType == PromotionRangeTypeEnum.ALL.getIndex()) {
			return commodityList;
		}

		Set<String> promotionCommodityCodeSet = codeParam.getCoupon().getPromotionRange().getCommodityList().stream()
				.map(PromotionCommodity::getCode).collect(Collectors.toSet());
		return commodityList.stream().filter(co -> {
			return promotionCommodityCodeSet.contains(co.getCode());
		}).collect(Collectors.toList());
	}
}

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
import java.util.Comparator;

import com.vincent.bean.Commodity;
import com.vincent.bean.Coupon;
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
	public static List<WorkFlow> buildWorkFlow(List<Commodity> commodityList, List<CouponCode> codeParamList) {
		if (codeParamList == null || codeParamList.size() == 0) {
			return null;
		}
		if (codeParamList.size() == 1) {// 只有一张券的时候可以简单处理
			return Collections.singletonList(buildFlowForSingleCode(codeParamList.get(0), commodityList));
		}

		// 如果coupon.code 相同，而不是 红包+全场券，那么只留下一张
		List<CouponCode> couponCodeList = distinct(codeParamList);

		// Map<类型,Map<全场or商品,List<code>>>分组
		Map<Integer, Map<Integer, List<CouponCode>>> groupingMap = couponCodeList.stream()
				.collect(groupingBy(functionForType, groupingBy(functionForRange)));

		List<WorkFlow> resultWorkFlows = new ArrayList<>();
		groupingMap.forEach((key, rangeCodeMap) -> {
			CouponTypeEnum typeEnum = EnumUtil.getEnumObject(CouponTypeEnum.class, type -> type.getIndex() == key);
			switch (typeEnum) {
			case CASH:
				resultWorkFlows.addAll(buildNonRedPacketWorkFlows(rangeCodeMap, commodityList));
				break;
			case DISCOUNT:
				resultWorkFlows.addAll(buildNonRedPacketWorkFlows(rangeCodeMap, commodityList));
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
	 * 去除非[红包+全场券]券码中的重复对象，因为无法叠加
	 */
	private static List<CouponCode> distinct(List<CouponCode> couponCodeParamList) {
		Map<String, List<CouponCode>> map = couponCodeParamList.stream()
				.collect(groupingBy(tmpCode -> tmpCode.getCoupon().getCode()));
		List<CouponCode> couponCodeList = new ArrayList<>();
		map.forEach((key, tmpCodeList) -> {
			if (tmpCodeList.size() == 1) {
				couponCodeList.add(tmpCodeList.get(0));
			} else {
				Coupon tmpCoupon = tmpCodeList.get(0).getCoupon();
				boolean isRedPacket = CouponTypeEnum.RED_PACKET.getIndex() == tmpCoupon.getType();
				boolean isForAll = tmpCoupon.getPromotionRange().getType() == PromotionRangeTypeEnum.ALL.getIndex();
				if (isRedPacket && isForAll) {// 是不是 红包+全场券
					couponCodeList.addAll(tmpCodeList);
				} else {// 其它类型的券码，因为无法叠加使用，所以只取领取时间最早的一张
					couponCodeList.add(tmpCodeList.stream().sorted(Comparator.comparing(CouponCode::getReceiveTime))
							.findFirst().get());
				}
			}
		});
		return couponCodeList;
	}

	/**
	 * 如果只有一张优惠券，可以简单处理
	 */
	private static WorkFlow buildFlowForSingleCode(CouponCode codeParam, List<Commodity> commodityList) {
		WorkFlow flow = new WorkFlow(commodityList);
		flow.addWorkStep(codeParam, filterCommodityForCode(codeParam, commodityList));
		return flow;
	}

	/**
	 * 为[红包]计算出可用的workFlow,其它类型的优惠券走其它相应逻辑
	 */
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

		// 尝试组装商品池券workFlow与全场券workflow
		BigDecimal totalPrice = commodityList.stream().map(Commodity::getPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		List<WorkFlow> resultFlowList = new ArrayList<>();
		commodityFlows.forEach(tmpCommodityFlow -> {
			allFlows.forEach(tmpAllFlow -> {
				BigDecimal tmpAllFlowTotalMaxSale = tmpAllFlow.getWorkSteps().stream()
						.map(tmpStep -> tmpStep.getCouponCode().getCoupon().getUseLimit().getMaxSale())
						.reduce(BigDecimal.ZERO, BigDecimal::add);
				// 判断全场红包flow是否已经可以将金额减扣到0
				if (tmpAllFlowTotalMaxSale.compareTo(totalPrice) < 0) {// 如果不能则需要与商品池红包组合
					WorkFlow tmpFlow = new WorkFlow(commodityList);
					tmpCommodityFlow.getWorkSteps().forEach(step -> {
						tmpFlow.addWorkStep(step.getCouponCode(), step.getCommodityList());
					});
					tmpAllFlow.getWorkSteps().forEach(step -> {
						tmpFlow.addWorkStep(step.getCouponCode(), step.getCommodityList());
					});
					resultFlowList.add(tmpFlow);
				} else {
					WorkFlow tmpFlow = new WorkFlow(commodityList);
					tmpAllFlow.getWorkSteps().forEach(step -> {
						tmpFlow.addWorkStep(step.getCouponCode(), step.getCommodityList());
					});
					resultFlowList.add(tmpFlow);
				}
			});
		});
		return resultFlowList;
	}

	/**
	 * 将红包类型得全部全场券组装为一个flow,因为他们可以无条件叠加. 除非凭借部分券就是减扣到0
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
		// 还是需要双层for循环，找出所有可能的组
		return buildFlowForManyRedPacketAll(allCodeList, commodityList);
	}

	private static List<WorkFlow> buildFlowForManyRedPacketAll(List<CouponCode> allCodeList,
			List<Commodity> commodityList) {
		BigDecimal totalPrice = commodityList.stream().map(Commodity::getPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		List<WorkFlow> resultFlowList = new ArrayList<>();
		int size = allCodeList.size();
		BigDecimal tmpMaxSale = BigDecimal.ZERO;
		for (int i = 0; i < size; i++) {
			WorkFlow workFlow = new WorkFlow(commodityList);
			CouponCode out = allCodeList.get(i);
			workFlow.addWorkStep(out, commodityList);
			tmpMaxSale = tmpMaxSale.add(out.getCoupon().getUseLimit().getMaxSale());

			for (int j = i + 1; j < size; j++) {
				CouponCode inner = allCodeList.get(i);
				workFlow.addWorkStep(inner, commodityList);
				tmpMaxSale = tmpMaxSale.add(inner.getCoupon().getUseLimit().getMaxSale());
				if (tmpMaxSale.compareTo(totalPrice) >= 0) {
					break;// 这个workFlow已经组装完毕了。
					// 本来应该更复杂的，但是实际上用户拥有三个以上全场红包的情况很少，同时红包又能全部抵扣的情况更少.
				}
			}
			resultFlowList.add(workFlow);
		}

		return resultFlowList;
	}

	private static List<WorkFlow> buildFlowFor2RedPacketAll(List<CouponCode> allCodeList,
			List<Commodity> commodityList) {
		BigDecimal totalPrice = commodityList.stream().map(Commodity::getPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		List<WorkFlow> resultFlowList = new ArrayList<>();
		CouponCode code1 = allCodeList.get(0);
		CouponCode code2 = allCodeList.get(1);
		boolean code1Over = code1.getCoupon().getUseLimit().getMaxSale().compareTo(totalPrice) >= 0;
		boolean code2Over = code2.getCoupon().getUseLimit().getMaxSale().compareTo(totalPrice) >= 0;
		if (code1Over && code2Over) {
			resultFlowList.add(buildFlowForSingleCode(code1, commodityList));
			resultFlowList.add(buildFlowForSingleCode(code2, commodityList));
		} else if (code1Over) {
			// 单独可以抵扣全部金额
			resultFlowList.add(buildFlowForSingleCode(code1, commodityList));
		} else if (code2Over) {
			resultFlowList.add(buildFlowForSingleCode(code2, commodityList));
		} else {
			WorkFlow flow = new WorkFlow(commodityList);
			flow.addWorkStep(code1, commodityList);
			flow.addWorkStep(code2, commodityList);
			resultFlowList.add(flow);
		}

		return resultFlowList;
	}

	/**
	 * 组装非红包券码为workFlowList
	 */
	private static List<WorkFlow> buildNonRedPacketWorkFlows(Map<Integer, List<CouponCode>> rangeCodeMap,
			List<Commodity> commodityList) {
		List<CouponCode> commodityCodeList = rangeCodeMap.get(PromotionRangeTypeEnum.COMMODITY.getIndex());
		List<CouponCode> allCodeList = rangeCodeMap.get(PromotionRangeTypeEnum.ALL.getIndex());
		if (commodityCodeList == null && allCodeList != null) {
			return allCodeList.stream().map(tmpCode -> buildFlowForSingleCode(tmpCode, commodityList))
					.collect(toList());
		}
		if (commodityCodeList != null && allCodeList == null) {
			return buildCommodityFlows(commodityList, commodityCodeList);
		}

		List<WorkFlow> commodityFlows = buildCommodityFlows(commodityList, commodityCodeList);
		List<WorkFlow> allFlows = allCodeList.stream().map(tmpCode -> buildFlowForSingleCode(tmpCode, commodityList))
				.collect(toList());

		// 尝试组装商品池券workFlow与全场券workFlow,组合规则与红包不同
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
	 * 为商品池券组建flow 规则：先算商品池券，再算全场券。所以分为两个方法. workFlow中先添加的step会先行计算
	 */
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
			workFlow.addWorkStep(out, outCommodities);

			for (int j = i + 1; j < size; j++) {
				CouponCode inner = promoteCommodityList.get(j);
				List<Commodity> innerCommodities = filterCommodityForCode(inner, commodityList);
				// 判断:优惠券商品范围没有交叉才能叠加使用
				if (Collections.disjoint(outCommodities, innerCommodities)) {
					workFlow.addWorkStep(inner, outCommodities);
				}
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

package com.vincent.workflow;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

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

		// FXIME 1.把优惠券按照类型区分为三类：红包 折扣 代金券。作为三个list
		// Map<类型,Map<全场or商品,List<code>>>
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

		// TODO 2.上面生成的workFlowList可以作为一个参数传入下面的 for循环。
		// TODO 3.比较明显的是下面的for不会出现双层，除非红包全场
		// WorkFlow workFlow = new WorkFlow(commodityList); // TODO 计算出多种多样的券组合
		// promoteAllList.forEach(tmpCode -> {
		// if (!workFlow.isConflict(tmpCode)) {
		// workFlow.addCouponCode(tmpCode);
		// }
		// });
		// List<WorkFlow> flowList = buildCommodityFlows(commodityList, couponCodeList);
		// return flowList;

	}

	/**
	 * 如果只有一张优惠券，可以简单处理
	 */
	private static List<WorkFlow> buildFlowForSingleCode(CouponCode codeParam, List<Commodity> commodityList) {
		WorkFlow flow = new WorkFlow(commodityList);
		flow.addCouponCode(codeParam, filterCommodityForCode(codeParam, commodityList));
		return Collections.singletonList(flow);
	}

	private static List<WorkFlow> buildRedPacketWorkFlows(Map<Integer, List<CouponCode>> rangeCodeMap,
			List<Commodity> commodityList) {
		List<CouponCode> commodityCodeList = rangeCodeMap.get(PromotionRangeTypeEnum.COMMODITY.getIndex());
		List<CouponCode> AllCodeList = rangeCodeMap.get(PromotionRangeTypeEnum.ALL.getIndex());

		// TODO 111
		List<WorkFlow> flows = buildCommodityFlows(commodityList, commodityCodeList);
		return flows;
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
					workFlow.addCouponCode(inner, outCommodities);
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

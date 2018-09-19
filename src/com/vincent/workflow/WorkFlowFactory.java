package com.vincent.workflow;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collection;

import com.vincent.bean.Commodity;
import com.vincent.bean.CouponCode;
import com.vincent.bean.WorkFlow;
import com.vincent.bean.enums.CouponTypeEnum;
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
			return null;
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
				resultWorkFlows.addAll(buildCashWorkFlows(rangeCodeMap));
				break;
			case DISCOUNT:
				resultWorkFlows.addAll(buildDiscountWorkFlows(rangeCodeMap));
				break;
			case RED_PACKET:
				resultWorkFlows.addAll(buildRedPacketWorkFlows(rangeCodeMap));
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

	private static List<WorkFlow> buildRedPacketWorkFlows(Map<Integer, List<CouponCode>> rangeCodeMap) {
		// TODO Auto-generated method stub
		return null;
	}

	private static List<WorkFlow> buildDiscountWorkFlows(Map<Integer, List<CouponCode>> rangeCodeMap) {
		// TODO Auto-generated method stub
		return null;
	}

	private static List<WorkFlow> buildCashWorkFlows(Map<Integer, List<CouponCode>> rangeCodeMap) {
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
			if (!workFlow.addCouponCode(out))
				continue;

			for (int j = i + 1; j < size; j++) {
				CouponCode inner = promoteCommodityList.get(j);
				if (!workFlow.isConflict(inner)) {
					workFlow.addCouponCode(inner);
				}
			}
			workFlowList.add(workFlow);
		}
		// TODO 根据产品的设计，实现组装code为 step,然后是flow
		// TODO 做一个代理类来管理 couponCode与优惠范围内的具体商品是不是更好一点.
		// TODO 需要处理好在哪儿组装成 workStep，在哪儿把commodityList分开
		return workFlowList;
	}

}

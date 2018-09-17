package com.vincent.workflow;

import java.util.List;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;

import com.vincent.bean.Commodity;
import com.vincent.bean.CouponCode;
import com.vincent.bean.WorkFlow;
import com.vincent.bean.enums.PromotionRangeTypeEnum;

public class WorkFlowFactory {

	/**
	 * 
	 * @param commodityList
	 *            外部传入的商品列表
	 * @param couponCodeList
	 *            用户已拥有的券码列表
	 * @return 可用的券码组合，需要通过计算得出优惠金额
	 */
	public static List<WorkFlow> buildWorkFlow(List<Commodity> commodityList, List<CouponCode> couponCodeList) {
		// TODO
		if (couponCodeList.size() == 1) {
			// TODO 只有一张券的时候可以简单处理,甚至可以在调用这个方法的地方单独写if
			return null;
		}

		List<CouponCode> promoteCommodityList = filterCodeListByPromotionRange(couponCodeList,
				PromotionRangeTypeEnum.COMMODITY);
		// TODO 需要为这些商品券找到对应的优惠范围内商品列表

		List<CouponCode> promoteAllList = filterCodeListByPromotionRange(couponCodeList, PromotionRangeTypeEnum.ALL);

		List<WorkFlow> workFlowList = new ArrayList<>();
		WorkFlow workFlow = new WorkFlow(commodityList); // TODO 计算出多种多样的券组合
		promoteCommodityList.forEach(tmpCode -> {
			if (!workFlow.isConflict(tmpCode)) {
				workFlow.addCouponCode(tmpCode);
			}
		});
		// TODO 1.是否有必要使用双层for循环的方式，找出所有的可能workFlow代表的券码组合
		// TODO 2.上面生成的workFlowList可以作为一个参数传入下面的 for循环。
		// TODO 3.比较明显的是下面的for不会出现双层，除非红包全场

		promoteAllList.forEach(tmpCode -> {
			if (!workFlow.isConflict(tmpCode)) {
				workFlow.addCouponCode(tmpCode);
			}
		});

		workFlowList.add(workFlow);
		// TODO 每个WorkFlow都是一个优惠券组合,每一个workStep都是用来计算这个步骤的优惠券的优惠
		// TODO 根据产品的设计，实现组装code为 step,然后是flow

		// TODO 做一个代理类来管理 couponCode与优惠范围内的具体商品是不是更好一点.

		// TODO 需要处理好在哪儿组装成 workStep，在哪儿把commodityList分开
		return workFlowList;
	}

	private static List<CouponCode> filterCodeListByPromotionRange(List<CouponCode> couponCodeList,
			PromotionRangeTypeEnum enumParam) {
		return couponCodeList.stream()
				.filter(code -> enumParam.getIndex() == code.getCoupon().getPromotionRange().getType())
				.collect(toList());
	}

}

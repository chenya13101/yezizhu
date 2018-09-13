package com.vincent.workflow;

import java.util.List;
import static java.util.stream.Collectors.toList;

import com.vincent.bean.Commodity;
import com.vincent.bean.CouponCode;
import com.vincent.common.PromotionRangeTypeEnum;

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
		List<CouponCode> promoteAllList = filterCodeListByPromotionRange(couponCodeList, PromotionRangeTypeEnum.ALL);

		// TODO 每个WorkFlow都是一个优惠券组合,每一个workStep都是用来计算这个步骤的优惠券的优惠
		// TODO 根据产品的设计，实现组装code为 step,然后是flow

		return null;
	}

	private static List<CouponCode> filterCodeListByPromotionRange(List<CouponCode> couponCodeList,
			PromotionRangeTypeEnum enumParam) {
		return couponCodeList.stream()
				.filter(code -> enumParam.getIndex() == code.getCoupon().getPromotionRange().getType())
				.collect(toList());
	}

}

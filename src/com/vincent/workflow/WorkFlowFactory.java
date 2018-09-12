package com.vincent.workflow;

import java.util.List;

import com.vincent.bean.Commodity;
import com.vincent.bean.CouponCode;

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

		List<CouponCode> promoteCommodityCodeList = null;
		List<CouponCode> promoteAllList = null;

		return null;
	}

}

package com.vincent.util;

import java.math.BigDecimal;
import java.util.Arrays;

import com.vincent.bean.Coupon;
import com.vincent.bean.PromotionRange;
import com.vincent.bean.enums.CouponTypeEnum;
import com.vincent.bean.enums.PromotionRangeTypeEnum;
import com.vincent.bean.inwardType.UseLimitInward;
import com.vincent.bean.sub.PromotionCommodity;

public class CouponTemplateUtil {

	/**
	 * 得到红包全场优惠券的模板
	 * 
	 * @return
	 */
	public static Coupon getRedPacketAllCoupon(double maxSaleParam) {
		BigDecimal maxSale = new BigDecimal(maxSaleParam);
		String code = "CPHB001";
		String name = maxSaleParam + "元全场红包";

		UseLimitInward useLimitInward = new UseLimitInward();
		useLimitInward.setMaxSale(maxSale);
		int couponType = CouponTypeEnum.RED_PACKET.getIndex();
		int promotionType = PromotionRangeTypeEnum.ALL.getIndex();
		PromotionRange promotionRange = new PromotionRange(promotionType, null);
		return new Coupon(code, name, promotionRange, couponType, useLimitInward);
	}

	/**
	 * 得到红包全场优惠券的模板
	 * 
	 * @return
	 */
	public static Coupon getRedPacketCommodityCoupon() {
		BigDecimal maxSale = new BigDecimal(8);
		String code = "CPHB001";
		String name = "8元全场商品券";

		UseLimitInward useLimitInward = new UseLimitInward();
		useLimitInward.setMaxSale(maxSale);
		int couponType = CouponTypeEnum.RED_PACKET.getIndex();
		int promotionType = PromotionRangeTypeEnum.COMMODITY.getIndex();

		PromotionCommodity comm1 = new PromotionCommodity("韶音骨传导耳机", "ShaoYin");
		PromotionCommodity comm2 = new PromotionCommodity("西瓜", "XiGua");
		PromotionCommodity comm3 = new PromotionCommodity("电脑", "DianNao");

		PromotionRange promotionRange = new PromotionRange(promotionType, Arrays.asList(comm1, comm2, comm3));
		return new Coupon(code, name, promotionRange, couponType, useLimitInward);
	}

	public static Coupon getDiscountCoupon() {
		return null;
	}

	public static Coupon getCashCoupon() {
		return null;
	}
}

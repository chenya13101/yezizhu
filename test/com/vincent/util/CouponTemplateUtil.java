package com.vincent.util;

import java.math.BigDecimal;

import com.vincent.bean.Coupon;
import com.vincent.bean.PromotionRange;
import com.vincent.bean.enums.CouponTypeEnum;
import com.vincent.bean.enums.PromotionRangeTypeEnum;
import com.vincent.bean.inwardType.UseLimitInward;

public class CouponTemplateUtil {

	/**
	 * 得到红包全场优惠券的模板
	 * 
	 * @return
	 */
	public static Coupon getRedPacketAllCoupon() {
		BigDecimal maxSale = new BigDecimal(10);
		String code = "CPHB001";
		String name = "10元全场红包";

		UseLimitInward useLimitInward = new UseLimitInward();
		useLimitInward.setMaxSale(maxSale);
		int couponType = CouponTypeEnum.RED_PACKET.getIndex();
		int promotionType = PromotionRangeTypeEnum.ALL.getIndex();
		PromotionRange promotionRange = new PromotionRange(promotionType, null);
		return new Coupon(code, name, promotionRange, couponType, useLimitInward);
	}

	public static Coupon getDiscountCoupon() {
		return null;
	}

	public static Coupon getCashCoupon() {
		return null;
	}
}

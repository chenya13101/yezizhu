package com.vincent.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vincent.bean.Coupon;
import com.vincent.bean.PromotionRange;
import com.vincent.bean.enums.CouponTypeEnum;
import com.vincent.bean.enums.PromotionRangeTypeEnum;
import com.vincent.bean.inwardType.UseLimitInward;
import com.vincent.bean.sub.PromotionCommodity;
import com.vincent.bean.sub.SubDiscountLimit;

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
	public static Coupon getRedPacketCommodityCoupon(double maxSaleParam, String code,
			List<PromotionCommodity> commodityList) {
		BigDecimal maxSale = new BigDecimal(maxSaleParam);
		String name = maxSaleParam + "元"
				+ commodityList.stream().map(PromotionCommodity::getName).collect(Collectors.joining("-")) + "券";

		UseLimitInward useLimitInward = new UseLimitInward();
		useLimitInward.setMaxSale(maxSale);
		int couponType = CouponTypeEnum.RED_PACKET.getIndex();
		int promotionType = PromotionRangeTypeEnum.COMMODITY.getIndex();

		PromotionRange promotionRange = new PromotionRange(promotionType, commodityList);
		return new Coupon(code, name, promotionRange, couponType, useLimitInward);
	}

	/**
	 * 得到红包全场优惠券的模板
	 * 
	 * @return
	 */
	public static Coupon getRedPacketCommodityCoupon(double maxSaleParam, String code) {
		BigDecimal maxSale = new BigDecimal(maxSaleParam);
		String name = maxSaleParam + "元 韶音 西瓜 电脑商品券";

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

	/**
	 * 得到CouponTypeEnum全场优惠券的模板
	 * 
	 * @return
	 */
	public static Coupon getRangeAllCoupon(CouponTypeEnum typeEnum, double maxSaleParam, String code,
			BigDecimal minRequire) {
		switch (typeEnum) {
		case RED_PACKET:
			return getRedPacketAllCoupon(maxSaleParam);
		case CASH:
			return getCashRangeAllCoupon(maxSaleParam, code, minRequire);
		// case DISCOUNT:
		// return getDiscountRangeAllCoupon(maxSaleParam, code, minRequire);
		default:
			break;
		}
		return null;
	}

	public static Coupon getDiscountRangeAllCoupon(double maxSaleParam, String code, List<SubDiscountLimit> limitList) {
		BigDecimal maxSale = new BigDecimal(maxSaleParam);
		String name = "最高" + maxSaleParam + "元全场满折券";

		UseLimitInward useLimitInward = new UseLimitInward();
		useLimitInward.setMaxSale(maxSale);
		useLimitInward.setDiscountLimitList(limitList);

		int couponType = CouponTypeEnum.DISCOUNT.getIndex();
		int promotionType = PromotionRangeTypeEnum.ALL.getIndex();
		PromotionRange promotionRange = new PromotionRange(promotionType, null);
		return new Coupon(code, name, promotionRange, couponType, useLimitInward);
	}

	private static Coupon getCashRangeAllCoupon(double maxSaleParam, String code, BigDecimal minRequire) {
		BigDecimal maxSale = new BigDecimal(maxSaleParam);
		String name = maxSaleParam + "元全场满减券";

		UseLimitInward useLimitInward = new UseLimitInward();
		useLimitInward.setMaxSale(maxSale);
		useLimitInward.setMinRequire(minRequire);

		int couponType = CouponTypeEnum.CASH.getIndex();
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

	public static Coupon getRangeCommodityCoupon(CouponTypeEnum typeEnum, int maxSaleParam, String code,
			BigDecimal minRequire, List<PromotionCommodity> commodityList) {
		switch (typeEnum) {
		case RED_PACKET:
			return getRedPacketCommodityCoupon(maxSaleParam, code);
		case CASH:
			return getCashRangeCommodityCoupon(maxSaleParam, code, minRequire, commodityList);
		case DISCOUNT:
			throw new IllegalArgumentException("暂不支持");
		default:
			throw new IllegalArgumentException("异常");
		}
	}

	private static Coupon getCashRangeCommodityCoupon(int maxSaleParam, String code, BigDecimal minRequire,
			List<PromotionCommodity> commodityList) {
		BigDecimal maxSale = new BigDecimal(maxSaleParam);
		String name = maxSaleParam + "元"
				+ commodityList.stream().map(PromotionCommodity::getName).collect(Collectors.joining("-")) + "代金券";

		UseLimitInward useLimitInward = new UseLimitInward();
		useLimitInward.setMinRequire(minRequire);
		useLimitInward.setMaxSale(maxSale);
		int couponType = CouponTypeEnum.CASH.getIndex();
		int promotionType = PromotionRangeTypeEnum.COMMODITY.getIndex();

		PromotionRange promotionRange = new PromotionRange(promotionType, commodityList);
		return new Coupon(code, name, promotionRange, couponType, useLimitInward);

	}

	public static Coupon getDiscountRangeCommodityCoupon(double maxSaleParam, String code,
			List<SubDiscountLimit> limitList, List<PromotionCommodity> commodityList) {
		BigDecimal maxSale = new BigDecimal(maxSaleParam);
		String name = "最高" + maxSaleParam
				+ commodityList.stream().map(PromotionCommodity::getName).collect(Collectors.joining("-")) + "满折券";

		UseLimitInward useLimitInward = new UseLimitInward();
		useLimitInward.setMaxSale(maxSale);
		useLimitInward.setDiscountLimitList(limitList);

		int couponType = CouponTypeEnum.DISCOUNT.getIndex();
		int promotionType = PromotionRangeTypeEnum.COMMODITY.getIndex();
		PromotionRange promotionRange = new PromotionRange(promotionType, commodityList);
		return new Coupon(code, name, promotionRange, couponType, useLimitInward);
	}
}

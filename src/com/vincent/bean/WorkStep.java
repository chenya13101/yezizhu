package com.vincent.bean;

import java.math.BigDecimal;
import java.util.List;

import com.vincent.bean.enums.CouponTypeEnum;
import com.vincent.bean.sub.DiscountLimit;
import com.vincent.common.Constant;
import com.vincent.util.EnumUtil;

/**
 * 用于执行券码的计算逻辑
 * 
 * @author WenSen
 * @date 2018年9月12日 下午6:53:01
 *
 */
public class WorkStep {

	private BigDecimal sale = BigDecimal.ZERO;

	private CouponCode couponCode;
	// 整个过程中 couponCode不能做任何的变更

	private List<Commodity> commodityList; // 只保存本优惠券范围内的商品,可以修改值，但是不能影响其它flow

	public List<Commodity> getCommodityList() {
		return commodityList;
	}

	public WorkStep(CouponCode couponCode, List<Commodity> commodityList) {
		super();
		this.couponCode = couponCode;
		this.commodityList = commodityList;
	}

	public void setCommodityList(List<Commodity> commodityList) {
		this.commodityList = commodityList;
	}

	public void setCouponCode(CouponCode couponCode) {
		this.couponCode = couponCode;
	}

	public CouponCode getCouponCode() {
		return couponCode;
	}

	public BigDecimal getSale() {
		return sale;
	}

	/**
	 * 计算
	 * 
	 * @return true: 代表有改变; false: 代表并未改变范围内商品的优惠价格
	 */
	public boolean run() {
		BigDecimal beforeChangeTotalPromPrice = getTotalPromPrice();
		if (beforeChangeTotalPromPrice.compareTo(BigDecimal.ZERO) == 0) {
			return false;
		}

		Coupon currentCoupon = couponCode.getCoupon();
		UseLimit limit = currentCoupon.getUseLimit();
		CouponTypeEnum typeEnum = EnumUtil.getEnumObject(CouponTypeEnum.class,
				type -> type.getIndex() == currentCoupon.getType());
		switch (typeEnum) {
		case CASH:
			// 判断优惠券的使用条件是否满足
			if (!limit.checkUseCondition(beforeChangeTotalPromPrice)) {
				return false;
			}
			shareSaleForNonDiscount(limit.getMaxSale());
			break;
		case DISCOUNT:
			// 判断优惠券的使用条件是否满足
			if (!limit.checkUseCondition(beforeChangeTotalPromPrice)) {
				return false;
			}

			BigDecimal bestDiscount = ((DiscountLimit) limit).getBestDiscount(beforeChangeTotalPromPrice);
			if (bestDiscount == null || bestDiscount.compareTo(BigDecimal.ZERO) <= 0)
				return false;

			calculateForDiscount(beforeChangeTotalPromPrice, bestDiscount, limit.getMaxSale());
			break;
		case RED_PACKET:
			shareSaleForNonDiscount(currentCoupon.getUseLimit().getMaxSale());
			break;
		default:
			throw new IllegalArgumentException(Constant.INVALID_INDEX);
		}
		// 是否有必要为折扣券和红包券单独做逻辑，甚至封装成类。或者 函数式接口

		this.sale = beforeChangeTotalPromPrice.subtract(getTotalPromPrice());
		return this.sale.compareTo(BigDecimal.ZERO) > 0;
	}

	/**
	 * 计算折扣券
	 */
	private void calculateForDiscount(BigDecimal beforeChangeTotalPromPrice, BigDecimal discount, BigDecimal maxSale) {
		BigDecimal discountSale = beforeChangeTotalPromPrice.subtract(beforeChangeTotalPromPrice.multiply(discount));
		if (discountSale.compareTo(maxSale) >= 0) {
			shareSaleForNonDiscount(maxSale);
		} else {
			shareSaleForNonDiscount(discountSale);
		}
	}

	private BigDecimal getTotalPromPrice() {
		return commodityList.stream().map(Commodity::getPromotePrice).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private void shareSaleForNonDiscount(BigDecimal maxSaleParam) {
		BigDecimal beforeChangeTotalPromPrice = getTotalPromPrice();
		boolean moreThanTotalPromPrice = maxSaleParam.compareTo(beforeChangeTotalPromPrice) >= 0;
		if (moreThanTotalPromPrice) {
			this.commodityList.forEach(commodity -> commodity.setPromotePrice(BigDecimal.ZERO));
		} else {
			BigDecimal currentReduceMoney;
			Commodity current;
			BigDecimal totalReducedMoney = BigDecimal.ZERO;// 累计已经优惠了的金额
			int size = this.commodityList.size();
			for (int i = 0; i < size; i++) {
				current = commodityList.get(i);
				currentReduceMoney = current.getPromotePrice().multiply(maxSaleParam).divide(beforeChangeTotalPromPrice,
						Constant.COUPON_CALCULATE_PRECISION, BigDecimal.ROUND_HALF_UP);
				totalReducedMoney = totalReducedMoney.add(currentReduceMoney);

				if (i == size - 1 && totalReducedMoney.compareTo(maxSaleParam) != 0) {
					currentReduceMoney = maxSaleParam.subtract(totalReducedMoney).add(currentReduceMoney);
				}

				current.setPromotePrice(current.getPromotePrice().subtract(currentReduceMoney));
			}
		}
	}

}

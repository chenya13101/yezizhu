package com.vincent.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vincent.bean.enums.TypeRangeEnum;
import com.vincent.bean.enums.PromotionRangeTypeEnum;
import com.vincent.bean.sub.PromotionCommodity;
import com.vincent.common.Constant;

import static java.util.stream.Collectors.*;

import java.math.BigDecimal;

public class WorkFlow {

	private static final Map<TypeRangeEnum, List<TypeRangeEnum>> conflictMap;
	static {
		// 根据现有规则初始化得到的一个冲突map,用于 isConflict()方法，规则由产品决定
		conflictMap = new HashMap<>();
		conflictMap.put(TypeRangeEnum.CASH_ALL, Arrays.asList(TypeRangeEnum.RED_ALL, TypeRangeEnum.RED_COMMODITY,
				TypeRangeEnum.DISCOUNT_ALL, TypeRangeEnum.DISCOUNT_COMMODITY, TypeRangeEnum.CASH_ALL));
		conflictMap.put(TypeRangeEnum.CASH_COMMODITY, Arrays.asList(TypeRangeEnum.RED_ALL, TypeRangeEnum.RED_COMMODITY,
				TypeRangeEnum.DISCOUNT_ALL, TypeRangeEnum.DISCOUNT_COMMODITY));

		conflictMap.put(TypeRangeEnum.RED_ALL, Arrays.asList(TypeRangeEnum.CASH_ALL, TypeRangeEnum.CASH_COMMODITY,
				TypeRangeEnum.DISCOUNT_ALL, TypeRangeEnum.DISCOUNT_COMMODITY));
		conflictMap.put(TypeRangeEnum.RED_COMMODITY, Arrays.asList(TypeRangeEnum.CASH_ALL, TypeRangeEnum.CASH_COMMODITY,
				TypeRangeEnum.DISCOUNT_ALL, TypeRangeEnum.DISCOUNT_COMMODITY));

		conflictMap.put(TypeRangeEnum.DISCOUNT_ALL, Arrays.asList(TypeRangeEnum.RED_ALL, TypeRangeEnum.RED_COMMODITY,
				TypeRangeEnum.CASH_ALL, TypeRangeEnum.CASH_COMMODITY, TypeRangeEnum.DISCOUNT_ALL));
		conflictMap.put(TypeRangeEnum.DISCOUNT_COMMODITY, Arrays.asList(TypeRangeEnum.RED_ALL,
				TypeRangeEnum.RED_COMMODITY, TypeRangeEnum.CASH_ALL, TypeRangeEnum.CASH_COMMODITY));
	}

	// 每个WorkFlow都是一个优惠券组合,每一个workStep都是用来计算这个步骤的优惠券的优惠
	private List<WorkStep> workSteps = new ArrayList<>();

	private List<Commodity> commodityList; // TODO 本flow内所有的step共享commodityList，操作价格会影响下一步计算 List<Commodity>
											// commodityList

	private List<CouponCode> couponCodeList = new ArrayList<>();

	private Set<TypeRangeEnum> codeTypeSet = new HashSet<>(); // FIXME 似乎可以删掉，这个Enum似乎也可以删

	public WorkFlow(List<Commodity> commodityList) {
		// 本flow内所有的step共享commodityList，操作价格会影响下一步计算 List<Commodity> commodityList
		this.commodityList = commodityList.stream().map(t -> {
			try {
				return (Commodity) t.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return new Commodity(t.getCode(), t.getPrice());
			}
		}).collect(Collectors.toList());
	}

	/**
	 * 如果冲突了，不能加入当前workFlow
	 * 
	 * @param codeParam
	 * @return true：冲突; false：不冲突
	 */
	public boolean isConflict(CouponCode codeParam) {
		if (couponCodeList.size() == 0) {
			return false;
		}

		TypeRangeEnum inputEnum = getTypeRangeEnum(codeParam);
		switch (inputEnum) {
		case RED_ALL:
		case CASH_ALL:
		case DISCOUNT_ALL:
			List<TypeRangeEnum> conflictEnumList = conflictMap.get(inputEnum);
			return !Collections.disjoint(conflictEnumList, codeTypeSet);
		case CASH_COMMODITY:
			// TODO 是否冲突
			break;
		case DISCOUNT_COMMODITY:

			break;

		case RED_COMMODITY:
			break;
		default:
			throw new IllegalArgumentException(Constant.INVALID_INDEX);
		}

		// TODO 是否还有其它的要求
		return true;
	}

	/**
	 * 尝试往flow中添加券码
	 * 
	 * @param codeParam
	 *            券码
	 * @return false：没有添加成功
	 */
	public boolean addCouponCode(CouponCode codeParam) {
		// if (isConflict(codeParam)) {
		// return false;
		// } TODO 将是否冲突的方法对外暴露,需要防范危险.

		couponCodeList.add(codeParam);
		workSteps.add(new WorkStep(codeParam, filterCommodityForCode(codeParam)));
		codeTypeSet.add(getTypeRangeEnum(codeParam));
		return true;
	}

	private TypeRangeEnum getTypeRangeEnum(CouponCode codeParam) {
		Coupon coupon = codeParam.getCoupon();
		return TypeRangeEnum.getEnum(coupon.getType(), coupon.getPromotionRange().getType());
	}

	private List<Commodity> filterCommodityForCode(CouponCode codeParam) {
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

	public void start() {
		workSteps.forEach(WorkStep::run);// TODO 需要确保是按照顺序来运行的
	}

	public CouponGroup getResult() {
		List<CouponCode> couponCodeList = workSteps.stream().map(WorkStep::getCouponCode).collect(toList());
		BigDecimal total = commodityList.stream().map(Commodity::getPromotePrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		return new CouponGroup(couponCodeList, total);
	}

}

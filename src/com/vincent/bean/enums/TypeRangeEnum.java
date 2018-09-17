package com.vincent.bean.enums;

import java.util.Arrays;
import java.util.Optional;

import com.vincent.common.Constant;

public enum TypeRangeEnum {
	RED_COMMODITY(3, 1),
	RED_ALL(3, 4),

	DISCOUNT_COMMODITY(1, 1),
	DISCOUNT_ALL(1, 4),

	CASH_COMMODITY(2, 1),
	CASH_ALL(2, 4);

	private int couponType;

	private int rangeType;

	TypeRangeEnum(int couponType, int rangeType) {
		this.couponType = couponType;
		this.rangeType = rangeType;
	}

	public static TypeRangeEnum getEnum(int couponTypeParam, int rangeTypeParam) {
		TypeRangeEnum[] enumArray = TypeRangeEnum.values();
		Optional<TypeRangeEnum> optionalEnum = Arrays.stream(enumArray)
				.filter(tmp -> tmp.couponType == couponTypeParam && tmp.rangeType == rangeTypeParam).findAny();

		if (optionalEnum.isPresent()) {
			return optionalEnum.get();
		}
		throw new IllegalArgumentException(Constant.INVALID_INDEX);
	}
}

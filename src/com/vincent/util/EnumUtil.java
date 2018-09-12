package com.vincent.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import com.vincent.common.Constant;
import com.vincent.common.PromotionRangeTypeEnum;

/**
 * 枚举类型不用改变，甚至不需要枚举类实现任何接口
 * 
 * @author WenSen
 * @date 2018年8月3日 下午4:25:04
 *
 */
public class EnumUtil {

	private static Map<Class<?>, Object> enumMap = new ConcurrentHashMap<>();

	/**
	 * 改进后的方法，不需要初始化一大堆的map.
	 */
	public static <T> T getEnumObject(Class<T> clazz, Predicate<T> predicate) {
		if (!clazz.isEnum()) {
			throw new IllegalArgumentException("非枚举类型");
		}

		Object mapValue = enumMap.get(clazz);
		if (mapValue == null) {
			try {
				enumMap.put(clazz, clazz.getEnumConstants());
				mapValue = enumMap.get(clazz);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		T[] enumArray = (T[]) mapValue;
		Optional<T> optional = Arrays.stream(enumArray).filter(predicate).findAny();
		if (optional.isPresent()) {
			return optional.get();
		}
		throw new IllegalArgumentException(Constant.INVALID_INDEX);
	}

	public static void main(String[] args) {
		Predicate<PromotionRangeTypeEnum> predicate = enumKey -> 2 == enumKey.getIndex();
		PromotionRangeTypeEnum enum1 = EnumUtil.getEnumObject(PromotionRangeTypeEnum.class, predicate);
		System.out.println(enum1);
	}
}
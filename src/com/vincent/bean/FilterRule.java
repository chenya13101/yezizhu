package com.vincent.bean;

/**
 * 
 * 可以根据产品code来判断是否在本优惠券范围内
 * 
 * @author WenSen
 * @date 2018年5月7日 下午6:46:47
 *
 */
public interface FilterRule {
	boolean checkInRange(String productCode);
}

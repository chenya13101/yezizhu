package com.suneee.marketingcenter.marketingcenter.model.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.suneee.marketingcenter.marketingcenter.model.constant.CouponTypeEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.StatusConstants;
import com.suneee.marketingcenter.marketingcenter.model.vo.CouponCodeVO;

public class CouponGroupDTO implements Cloneable {

	/**
	 * 优先级，数字越小优先级越高
	 */
	private Integer priority;

	/**
	 * 当前可优惠金额(减少了多少)
	 */
	private BigDecimal currentSale = null;

	/**
	 * 该优惠券范围内的商品id
	 */
	private List<String> containGoodsCodeList;

	/**
	 * 是否可叠加
	 */
	private Integer overlying;

	/**
	 * 组合内所有券码的最小的使用结束时间
	 */
	private String minAvailableEndTime;
	/**
	 * 该组合内已有的优惠券券码
	 */
	private List<CouponCodeVO> couponCodeList = new ArrayList<>();

	private HashMap<String, BigDecimal> qrCodeSaleMap = new HashMap<>();
	// 用于保存每张券码具体减少了多少钱,折上折时需要考虑<券码,折扣金额>

	private HashMap<String, BigDecimal> qrCodeFullElementMap = new HashMap<>();
	/**
	 * <券码,<goodsCode,优惠金额>>
	 */
	private HashMap<String, HashMap<String, BigDecimal>> couponCodePerGoodsReduceMoneyMap = new HashMap<>();

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public List<CouponCodeVO> getCouponCodeList() {
		return couponCodeList;
	}

	private void setCouponCodeList(List<CouponCodeVO> couponCodeList) {
		this.couponCodeList = couponCodeList;
	}

	public BigDecimal getCurrentSale() {
		return currentSale;
	}

	public void setCurrentSale(BigDecimal currentSale) {
		this.currentSale = currentSale;
	}

	@Deprecated
	// FIXME 准备从这个类中移除
	private void addCouponCode(CouponCodeVO couponCodeVO, BigDecimal tmpReduceMoney,
			HashMap<String, BigDecimal> mallProdVoSaleMap) {
		if (couponCodeVO == null) {
			return;
		}
		qrCodeFullElementMap.put(couponCodeVO.getQrCode(), couponCodeVO.getFullElement());
		if (couponCodeList.size() == 0) {
			this.setCurrentSale(tmpReduceMoney);
			couponCodeVO.setDeductibleAmount(tmpReduceMoney);
			qrCodeSaleMap.put(couponCodeVO.getQrCode(), tmpReduceMoney);
			couponCodePerGoodsReduceMoneyMap.put(couponCodeVO.getQrCode(), mallProdVoSaleMap);
		} else if (couponCodeVO.getCouponType() == CouponTypeEnum.CASH.getIndex()) {
			// 代金券也简单处理
			this.setCurrentSale(getCurrentSale().add(tmpReduceMoney));
			couponCodeVO.setDeductibleAmount(tmpReduceMoney);
			qrCodeSaleMap.put(couponCodeVO.getQrCode(), tmpReduceMoney);
			couponCodePerGoodsReduceMoneyMap.put(couponCodeVO.getQrCode(), mallProdVoSaleMap);
		} else {
			// 不能直接保存这个优惠金额，折扣券需要考虑折上折
			BigDecimal reCalculateRecudeMoney = reCalculateDiscountRecudeMoney(couponCodeVO, tmpReduceMoney);
			qrCodeSaleMap.put(couponCodeVO.getQrCode(), reCalculateRecudeMoney);
			if (tmpReduceMoney.compareTo(reCalculateRecudeMoney) != 0) {
				// 需要修改针对具体商品的平摊后价格
			}
			mallProdVoSaleMap.entrySet().forEach(
					entry -> entry.setValue(entry.getValue().multiply(reCalculateRecudeMoney).divide(tmpReduceMoney,
							StatusConstants.COUPON_CALCULATE_PRECISION, BigDecimal.ROUND_HALF_UP)));
			couponCodePerGoodsReduceMoneyMap.put(couponCodeVO.getQrCode(), mallProdVoSaleMap);
			couponCodeVO.setDeductibleAmount(reCalculateRecudeMoney);
			// 总的优惠金额也需要改变
			this.setCurrentSale(getTotalReduceMoney());
		}
		couponCodeList.add(couponCodeVO);

		// 改变组合内的最小的结束时间
		boolean currentEndDateNull = minAvailableEndTime == null || minAvailableEndTime.length() == 0;
		if (currentEndDateNull) {
			minAvailableEndTime = couponCodeVO.getAvailableEndTime();
		} else {
			boolean inputEndDateNull = couponCodeVO.getAvailableEndTime() == null
					&& couponCodeVO.getAvailableEndTime().length() == 0;
			if (!inputEndDateNull && couponCodeVO.getAvailableEndTime().compareTo(minAvailableEndTime) < 0)
				minAvailableEndTime = couponCodeVO.getAvailableEndTime();
		}

	}

	private BigDecimal getTotalReduceMoney() {
		BigDecimal totalReduce = BigDecimal.ZERO;
		Collection<BigDecimal> collection = qrCodeSaleMap.values();
		for (BigDecimal tmpSale : collection) {
			totalReduce = totalReduce.add(tmpSale);
		}

		return totalReduce;
	}

	/**
	 * 为折扣券重新计算优惠的价格，因为可能遇到折上折的问题 FIXME 从bean类剔除
	 * 
	 * @param couponCodeVO
	 * @param tmpReduceMoney
	 * @return
	 */
	private BigDecimal reCalculateDiscountRecudeMoney(CouponCodeVO couponCodeVO, BigDecimal tmpReduceMoney) {
		int containSameCouponCount = 0;
		String canUseCode = couponCodeVO.getCouponCode();
		for (CouponCodeVO tmpCouponCode : couponCodeList) {
			if (canUseCode.equals(tmpCouponCode.getCouponCode())) {
				containSameCouponCount++;
			}
		}
		if (containSameCouponCount == 0) {
			return tmpReduceMoney;
		}

		BigDecimal reduceAfterDiscount = new BigDecimal(tmpReduceMoney.toString());
		BigDecimal couponDiscount = couponCodeVO.getCouponDiscount();
		for (int i = 0; i < containSameCouponCount; i++) {
			// 多张折扣券叠加，规则为折上折
			reduceAfterDiscount = reduceAfterDiscount.multiply(couponDiscount).multiply(StatusConstants.TEN_PERCENT);
		}

		return reduceAfterDiscount;
	}

	public Integer getOverlying() {
		return overlying;
	}

	public void setOverlying(Integer overlying) {
		this.overlying = overlying;
	}

	public String getMinAvailableEndTime() {
		return minAvailableEndTime;
	}

	public void setMinAvailableEndTime(String minAvailableEndTime) {
		this.minAvailableEndTime = minAvailableEndTime;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CouponGroupDTO clone() throws CloneNotSupportedException {
		CouponGroupDTO group = (CouponGroupDTO) super.clone();
		group.setCouponCodeList(new ArrayList<>(couponCodeList));

		if (qrCodeSaleMap != null) {
			HashMap<String, BigDecimal> cloneMap = (HashMap<String, BigDecimal>) qrCodeSaleMap.clone();
			group.setCouponCodeSaleMap(cloneMap);
		}
		if (couponCodePerGoodsReduceMoneyMap != null) {
			HashMap<String, HashMap<String, BigDecimal>> cloneMap = (HashMap<String, HashMap<String, BigDecimal>>) couponCodePerGoodsReduceMoneyMap
					.clone();
			group.setCouponCodePerGoodsReduceMoneyMap(cloneMap);
		}

		if (currentSale != null) {
			group.setCurrentSale(new BigDecimal(String.valueOf(currentSale.doubleValue())));
		}

		group.setOverlying(new Integer(overlying.intValue()));
		group.setQrCodeFullElementMap((HashMap<String, BigDecimal>) qrCodeFullElementMap.clone());
		group.setMinAvailableEndTime(new String(minAvailableEndTime));
		group.setContainGoodsCodeList(new ArrayList<>(containGoodsCodeList));// 需要clone containsGoodsCodeList
		return group;
	}

	public List<String> getContainGoodsCodeList() {
		return containGoodsCodeList;
	}

	public void setContainGoodsCodeList(List<String> containGoodsCodeList) {
		this.containGoodsCodeList = containGoodsCodeList;
	}

	public HashMap<String, BigDecimal> getCouponCodeSaleMap() {
		return qrCodeSaleMap;
	}

	public void setCouponCodeSaleMap(HashMap<String, BigDecimal> couponCodeSaleMap) {
		this.qrCodeSaleMap = couponCodeSaleMap;
	}

	public HashMap<String, BigDecimal> getQrCodeSaleMap() {
		return qrCodeSaleMap;
	}

	public void setQrCodeSaleMap(HashMap<String, BigDecimal> qrCodeSaleMap) {
		this.qrCodeSaleMap = qrCodeSaleMap;
	}

	public HashMap<String, BigDecimal> getQrCodeFullElementMap() {
		return qrCodeFullElementMap;
	}

	public void setQrCodeFullElementMap(HashMap<String, BigDecimal> qrCodeFullElementMap) {
		this.qrCodeFullElementMap = qrCodeFullElementMap;
	}
	
	public HashMap<String, HashMap<String, BigDecimal>> getCouponCodePerGoodsReduceMoneyMap() {
		return couponCodePerGoodsReduceMoneyMap;
	}

	public void setCouponCodePerGoodsReduceMoneyMap(
			HashMap<String, HashMap<String, BigDecimal>> couponCodePerGoodsReduceMoneyMap) {
		this.couponCodePerGoodsReduceMoneyMap = couponCodePerGoodsReduceMoneyMap;
	}

}

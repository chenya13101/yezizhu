package com.suneee.marketingcenter.marketingcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.suneee.marketingcenter.marketingcenter.common.util.Assert;
import com.suneee.marketingcenter.marketingcenter.model.constant.CouponTypeEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.PreferentialRangeTypeEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.StatusConstants;
import com.suneee.marketingcenter.marketingcenter.model.dto.CouponDTO;
import com.suneee.marketingcenter.marketingcenter.model.dto.CouponGroupDTO;
import com.suneee.marketingcenter.marketingcenter.model.vo.CouponCodeVO;
import com.suneee.marketingcenter.marketingcenter.model.vo.MallProdVo;
import com.suneee.marketingcenter.marketingcenter.service.CouponService.ReduceInfo;

@Service("couponGroupService")
public class CouponGroupService {

	private CouponService couponService;

	private final int MOST_COUPON_NUM = 3;

	/**
	 * 得到优惠券券编号和可适用的商品集合的映射map
	 * 
	 * @param canUseCouponDtoList
	 *            上一部判断出的可以适用于最少一个商品的优惠券集合
	 * @param goodsVoList
	 *            商城输入的全部商品的集合
	 * @return map<couponCode,可适用的商品集合>
	 */
	public Map<String, List<MallProdVo>> getCouponCodeProductMap(List<CouponDTO> canUseCouponDtoList,
			List<MallProdVo> goodsVoList) {
		List<MallProdVo> prodVoList;// 选择出所有适用于该优惠券的商品列表

		Map<String, List<MallProdVo>> couponCodeProductsMap = new HashMap<>(canUseCouponDtoList.size());
		PreferentialRangeTypeEnum rangeEnum;
		for (CouponDTO tmpCouponDto : canUseCouponDtoList) {
			rangeEnum = PreferentialRangeTypeEnum.getEnumByIndex(tmpCouponDto.getUseRange());
			Assert.notNull(rangeEnum, "优惠券使用范围");

			switch (rangeEnum) {
			case BRANDS:
				prodVoList = goodsVoList.stream().filter(goodsVo -> {
					return couponService.checkIsInBrands(goodsVo,
							tmpCouponDto.getPreferentialRange().getPromotionBrandList());// 过滤得到合适品牌
				}).collect(Collectors.toList());
				break;
			case CLASSES:
			case ZY_CLASSES:
				prodVoList = goodsVoList.stream().filter(goodsVo -> {
					return couponService.checkIsInClasses(goodsVo,
							tmpCouponDto.getPreferentialRange().getPromotionClassList(),
							tmpCouponDto.getEnterpriseId());// 过滤得到合适品类
				}).collect(Collectors.toList());
				break;
			case GOODS:
				prodVoList = goodsVoList.stream().filter(goodsVo -> {
					return couponService.checkIsInGoods(goodsVo,
							tmpCouponDto.getPreferentialRange().getPromotionGoodsList());// 过滤得到合适商品
				}).collect(Collectors.toList());
				break;
			default:
				prodVoList = goodsVoList.stream().collect(Collectors.toList());
			}
			couponCodeProductsMap.put(tmpCouponDto.getCouponCode(), prodVoList);
		}

		return couponCodeProductsMap;
	}

	/**
	 * 
	 * @param codeReduceMap
	 *            <券码vo,优惠信息>map
	 * @param couponCodeProductsMap
	 *            <优惠券编号,可以匹配的商品list> TODO 是不是可以删除掉
	 * @param allpromTotalPrice
	 *            调用接口传入的所有商品的总价
	 * @return
	 */
	public List<CouponGroupDTO> getCanOverlyingGroups(Map<CouponCodeVO, ReduceInfo> codeReduceMap,
			Map<String, List<MallProdVo>> couponCodeProductsMap, BigDecimal allPromTotalPrice) {
		List<CouponGroupDTO> singleCouponGroupList = new ArrayList<>(); // 初始的组集合，每个组内只有一张优惠券,等待与其它组合碰撞组合出新的更优惠组合
		List<CouponGroupDTO> fullDiscountGroupList = new ArrayList<>(); // 单张优惠券就已经可以抵扣全部金额的组合
		codeReduceMap.forEach((couponCodeVO, reduceInfo) -> {
			if (isNullOrZero(reduceInfo.getMostReduceMoney()))
				return;// 只处理已经满足了使用条件的优惠券
			final CouponGroupDTO newGroup = new CouponGroupDTO();
			newGroup.setCurrentSale(reduceInfo.getMostReduceMoney());
			// newGroup.addCouponCode(couponCodeVO, reduceInfo.getMostReduceMoney(),
			// reduceInfo.getMallProdVoSaleMap());
			newGroup.setOverlying(StatusConstants.YES);
			newGroup.setMinAvailableEndTime(couponCodeVO.getAvailableEndTime());
			newGroup.setContainGoodsCodeList(reduceInfo.getContainGoodsCodeList());

			if (reduceInfo.getMostReduceMoney().compareTo(allPromTotalPrice) >= 0) {
				fullDiscountGroupList.add(newGroup);// 如果该组合已经到达优惠券的促销后总应付金额，那么不需要再与其他的组合合并。
			} else {
				singleCouponGroupList.add(newGroup);// 可以尝试与其它组合继续碰撞的组合
			}
		});

		if (singleCouponGroupList.size() == 0)
			return fullDiscountGroupList;
		if (fullDiscountGroupList.size() > 0)
			return fullDiscountGroupList; // 如果已经有券可以抵扣全部金额，那么直接返回这一组合
		if (singleCouponGroupList.size() == 1)
			return singleCouponGroupList;// 如果只有一张可以选，直接返回

		List<CouponGroupDTO> afterBuildGroups = buildSingleGroups(singleCouponGroupList, allPromTotalPrice);
		afterBuildGroups.addAll(fullDiscountGroupList);
		return afterBuildGroups;
	}

	private List<CouponGroupDTO> buildSingleGroups(List<CouponGroupDTO> singleCouponGroupList,
			BigDecimal allPromTotalPrice) {
		List<CouponGroupDTO> afterBuildGroups = new ArrayList<>();

		List<CouponGroupDTO> sortedGroupList = singleCouponGroupList.stream()
				.sorted((g1, g2) -> g1.getCurrentSale().compareTo(g2.getCurrentSale())).collect(Collectors.toList());
		if (sortedGroupList.size() <= MOST_COUPON_NUM) {// 如果可以选的优惠券张数小于等于每单最多可以使用的优惠券数量

			// TODO 尝试组合全部的券

			// TODO 如果全部一起用有问题，那么从最小的开始舍弃，金额一直往上，一张到多张，知道组成相对最优解
		} else {

			// <挑选>组合，然后进行匹配,尝试找出最优解
		}

		// 采用猜想算法来找出有最大可能性是最优的解法，而不是迭代算法找出所有可能的结果集中的最优解,可以显著的提高在高数据量下的计算速度。
		// 迭代算法会随着最多可用券码量和持有的优惠券数量，商品数量的增加而呈指数快速增长

		// 组合后优惠券就已经可以抵扣全部金额的组合
		// for (CouponGroupDTO singleCouponGroup : singleCouponGroupList) {
		// afterBuildGroups.addAll(recursiveBuildGroup(Collections.singletonList(singleCouponGroup),
		// singleCouponGroupList, allPromTotalPrice));
		// }

		return afterBuildGroups;
	}

	/**
	 * 迭代配对，寻找能组合在一起的组合
	 * 
	 * @param outGroupList
	 *            已经组装好的集合
	 * @param innerGroupList,这里面的每个组合都只有一张券
	 *            待组合的集合
	 * @param allPromTotalPrice
	 *            商品总价
	 * @return 组合后的集合
	 */
	private List<CouponGroupDTO> recursiveBuildGroup(List<CouponGroupDTO> outGroupList,
			List<CouponGroupDTO> innerGroupList, BigDecimal allPromTotalPrice) {
		if (outGroupList.size() >= MOST_COUPON_NUM) { // 设置一个边际：每张订单最多使用的优惠券数量
			return outGroupList;
		}

		boolean packaged = false;
		List<CouponGroupDTO> packageGroupList = null;
		String[] qrCodeArray;
		for (CouponGroupDTO outGroup : outGroupList) {
			for (CouponGroupDTO innerGroup : innerGroupList) {
				if (checkGroupConflict(outGroup, innerGroup, allPromTotalPrice))
					continue;// 检查 outGroup与innerGroup是否冲突,有冲突则跳过
				if (!packaged) {
					packageGroupList = new ArrayList<>();
					packageGroupList.addAll(outGroupList);
				}
				qrCodeArray = getBestUseOrder(outGroup, innerGroup);// TODO 也许在这一步就可以得到最佳的组合了
				if (qrCodeArray == null)
					continue;
				// TODO 找出不冲突的组合的最佳使用顺序

				// 如果不冲突那么组装在一起
				packageGroupList.add(packageGroup(outGroup, innerGroup, qrCodeArray));
				packaged = true;
			}
		}
		if (!packaged) // 代表没有任何带组装的组合可以和outGroupList里的继续组合
			return outGroupList;

		return recursiveBuildGroup(packageGroupList, innerGroupList, allPromTotalPrice);
		// 如果上一步还有成功组装的的，那么继续尝试组装
	}

	/**
	 * 得到优惠券组合在一起的最佳使用顺序
	 * 
	 * @param outGroup
	 *            外部组合，可能有一到多张优惠券
	 * @param innerGroup
	 *            内部组合，只有一张优惠券
	 * @return 使用顺序
	 */
	private String[] getBestUseOrder(CouponGroupDTO outGroup, CouponGroupDTO innerGroup) {
		// TODO 根据券类型可以做第一步的判断，折折折和减减减是最好算的
		List<CouponCodeVO> outCodes = outGroup.getCouponCodeList();
		List<CouponCodeVO> innerCodes = innerGroup.getCouponCodeList();
		boolean isAllDiscount = true;
		boolean isAllCash = true;

		for (CouponCodeVO tmpOutCode : outCodes) {
			if (tmpOutCode.getCouponType() == CouponTypeEnum.CASH.getIndex()) {
				isAllDiscount = false;
			} else {
				isAllCash = false;
			}
		}
		if (isAllDiscount || isAllCash) {
			// TODO 采用简单的处理方式就好了，只用判断互不冲突
		} else {
			// 需要找出所有的可能组合顺序，并找出最优
		}

		// TODO 其实，前面只用找出所有的组合，只有最后才要计算出唯一的一次顺序就好了，如果后面还可以和其它券组合就找出顺序，也许不是最佳的

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param outGroup
	 *            一或者多长券码的组合
	 * @param innerGroup
	 *            里面都只有一张券码
	 * @param qrCodeArray
	 *            最佳的组合顺序
	 * @return 组装后的组合
	 */
	private CouponGroupDTO packageGroup(CouponGroupDTO outGroup, CouponGroupDTO innerGroup, String[] qrCodeArray) {
		CouponGroupDTO newGroup = null;
		try {
			// TODO 找出该组合最优的顺序

			// TODO 该组合不一定可以用，之前检查是否冲突的判定有点理想化,需要进一步验证

			// newGroup = outGroup.clone();// 复制一个原有的group

			// CouponCodeVO couponCodeVO = innerGroup.getCouponCodeList().get(0);
			// TODO 将逻辑代码从group类中迁移出，只保存基本的数据
			// outGroup.getQrCodeFullElementMap().put(couponCodeVO.getQrCode(),
			// couponCodeVO.getFullElement());

			// 接下来就是最难的，打乱顺序求出一个最佳解

			// 或者先只把可能的组合列出来，但是顺序并不处理。组装完毕后再找出一个顺序
		} catch (Exception e) {
			// TODO: handle exception
		}
		return newGroup;
	}

	/**
	 * 检查两个组合是否冲突
	 * 
	 * @param group1
	 *            组合1
	 * @param group2
	 *            组合2
	 * @param allPromTotalPrice
	 *            商品总价
	 * @return true:有冲突不能组合在一起
	 */
	private boolean checkGroupConflict(CouponGroupDTO group1, CouponGroupDTO group2, BigDecimal allPromTotalPrice) {
		if (checkHasSameQrCodes(group1, group2))
			return true;

		// TODO 这里的代码要灵活，可以兼容多次变化，而且需要可以做更深层的拓展
		if (checkMostSaleConflict(group1, group2, allPromTotalPrice))
			return true;

		// TODO (规则是统一先减后折),先废弃

		// TODO 检测是否冲突需要正着来一次，反着也来一次,如果是多个的话还得算出多种情形下最优解

		// TODO 肯定是不能有同一张券码
		return false;
	}

	/**
	 * 检查前一个组合中是否已经包含第二个组合中的qrCode
	 * 
	 * @param group1
	 *            组合1
	 * @param group2
	 *            组合2
	 * @return true:冲突
	 */
	private boolean checkHasSameQrCodes(CouponGroupDTO group1, CouponGroupDTO group2) {
		Set<String> set1 = group1.getQrCodeFullElementMap().keySet();
		Set<String> set2 = group2.getQrCodeFullElementMap().keySet();

		for (String str : set2) {
			if (set1.contains(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 暴力规则1： 检查是否满足 商品总价 - 最多优惠金额 > 最大满减要求
	 * 
	 * @param group1
	 *            组合1
	 * @param group2
	 *            组合2
	 * @param allPromTotalPrice
	 *            商品总价
	 * @return true:冲突
	 */
	private boolean checkMostSaleConflict(CouponGroupDTO group1, CouponGroupDTO group2, BigDecimal allPromTotalPrice) {
		BigDecimal mostSale = group1.getCurrentSale().add(group2.getCurrentSale());// 最理想的状况下，两个组合之间没有互相影响(商品集合没有交集)

		BigDecimal maxFullElement1 = group1.getQrCodeFullElementMap().values().stream()
				.filter(v -> v != null && v.compareTo(BigDecimal.ZERO) > 0).max(BigDecimal::compareTo)
				.orElse(BigDecimal.ZERO);
		BigDecimal maxFullElement2 = group2.getQrCodeFullElementMap().values().stream()
				.filter(v -> v != null && v.compareTo(BigDecimal.ZERO) > 0).max(BigDecimal::compareTo)
				.orElse(BigDecimal.ZERO);
		BigDecimal maxFullElement = maxFullElement1.compareTo(maxFullElement2) > 0 ? maxFullElement1 : maxFullElement2;

		return allPromTotalPrice.subtract(mostSale).compareTo(maxFullElement) < 0;
	}

	/**
	 * 判断是否为空或0
	 * 
	 * @param decimal
	 *            入参
	 * @return true:为空或0
	 */
	private boolean isNullOrZero(BigDecimal decimal) {
		return decimal == null || decimal.compareTo(BigDecimal.ZERO) <= 0;
	}

}

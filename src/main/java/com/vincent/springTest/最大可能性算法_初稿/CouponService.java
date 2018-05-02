package com.suneee.marketingcenter.marketingcenter.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.suneee.mall.common.util.ErpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.marketingcenter.marketingcenter.common.util.Assert;
import com.suneee.marketingcenter.marketingcenter.common.util.DateUtils;
import com.suneee.marketingcenter.marketingcenter.common.util.GoodsClassUtil;
import com.suneee.marketingcenter.marketingcenter.common.util.VipUtil;
import com.suneee.marketingcenter.marketingcenter.dao.CouponCodeJobDao;
import com.suneee.marketingcenter.marketingcenter.dao.CouponDao;
import com.suneee.marketingcenter.marketingcenter.model.constant.ActivityAuditorStatusEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.AvailableTimeTypeEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.CouponAuditorStatusEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.CouponCodeStatusEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.CouponModelNameEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.CouponTypeEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.GrantModeStatusEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.PreferentialRangeTypeEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.ReceiveFrequencyTypeEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.SaleTypeEnum;
import com.suneee.marketingcenter.marketingcenter.model.constant.StatusConstants;
import com.suneee.marketingcenter.marketingcenter.model.constant.UsePlatformEnum;
import com.suneee.marketingcenter.marketingcenter.model.dbo.Coupon;
import com.suneee.marketingcenter.marketingcenter.model.dbo.CouponCodeJob;
import com.suneee.marketingcenter.marketingcenter.model.dbo.PreferentialRange;
import com.suneee.marketingcenter.marketingcenter.model.dbo.Promotional;
import com.suneee.marketingcenter.marketingcenter.model.dbo.PromotionalBrand;
import com.suneee.marketingcenter.marketingcenter.model.dbo.PromotionalClass;
import com.suneee.marketingcenter.marketingcenter.model.dbo.PromotionalGoods;
import com.suneee.marketingcenter.marketingcenter.model.dto.CouponActivityDTO;
import com.suneee.marketingcenter.marketingcenter.model.dto.CouponDTO;
import com.suneee.marketingcenter.marketingcenter.model.dto.CouponGroupDTO;
import com.suneee.marketingcenter.marketingcenter.model.dto.CouponReceiveRecordDTO;
import com.suneee.marketingcenter.marketingcenter.model.exception.MarketingCenterException;
import com.suneee.marketingcenter.marketingcenter.model.vo.CouponActivityVIPVO;
import com.suneee.marketingcenter.marketingcenter.model.vo.CouponActivityVO;
import com.suneee.marketingcenter.marketingcenter.model.vo.CouponCodeVO;
import com.suneee.marketingcenter.marketingcenter.model.vo.CouponVO;
import com.suneee.marketingcenter.marketingcenter.model.vo.MallProdVo;
import com.suneee.marketingcenter.marketingcenter.model.vo.StoreCouponVO;
import com.suneee.marketingcenter.marketingcenter.model.vo.VipVO;

@Service("couponService")
public class CouponService {
	private static Logger logger = LoggerFactory.getLogger(CouponService.class);

	private static final BigDecimal FULL_DISCOUNT = new BigDecimal(10);

	/**
	 * 每笔订单中全场券的最多叠加数
	 */
	private static int COUPON_ALL_MOST_NUM = 3;

	@Autowired
	private CouponDao couponDao;

	@Autowired
	private CouponCodeJobDao couponCodeJobDao;

	@Autowired
	private PromotionRangeService promotionRangeService;

	@Autowired
	private CouponActivityService couponActivityService;

	@Autowired
	private CouponCodeService couponCodeService;

	@Autowired
	private CouponCodeJobService couponCodeJobService;

	@Autowired
	private CouponGroupService couponGroupService;

	@Autowired
	private PromotionalService promotionalService;

	/**
	 * 必填: enterpriseCodeList 所属企业codeList 选填: couponCode 优惠券编码; couponType 优惠券类型(1
	 * 折扣劵/ 2代金劵); couponName 优惠券名称; useRange 使用范围; pageSize 每页记录数 ; auditorStatus:
	 * 审核状态，对应CouponAuditorStatusEnum; startRow等; occupied 数字: 0查询没有被活动占用的,1查询被占用的，
	 * usePlatforms List<String> pc,wap和offline
	 * <p>
	 * 增加参数表示是否被占用
	 */
	public List<CouponVO> getCouponListByMap(Map<String, Object> map) throws Exception {
		// map.put("occupied", 1);
		return couponDao.getCouponListByMap(map);
	}

	/**
	 * 保存优惠券vo对象，会处理关联的优惠范围
	 */
	public int add(CouponDTO couponDTO) throws Exception {
		int count = couponDao.insertSelective(couponDTO);
		// 如果是全场券则不需要设定优惠范围
		if (couponDTO.getUseRange() == PreferentialRangeTypeEnum.ALL.getIndex()) {
			return count;
		}

		int couponId = couponDTO.getId();
		PreferentialRange preferentialRange = couponDTO.getPreferentialRange();
		// 还需要保存优惠券的商品使用范围，品类范围，品牌范围,相对应的在coupon类里映射出这几个对象
		savePreferentialRange(preferentialRange, couponId, couponDTO.getUseRange());
		return count;
	}

	/**
	 * 保存优惠券的商品使用范围，品类范围，品牌范围
	 */
	private void savePreferentialRange(PreferentialRange preferentialRange, int couponId, int useRange)
			throws Exception {
		// 还需要保存优惠券的商品使用范围，品类范围，品牌范围
		PreferentialRangeTypeEnum preferentialRangeTypeEnum = PreferentialRangeTypeEnum.getEnumByIndex(useRange);
		Assert.notNull(preferentialRangeTypeEnum, "优惠券使用范围");
		switch (preferentialRangeTypeEnum) {
		case GOODS:
			// 组装并保存promotionGoodsList
			promotionRangeService.addGoodsList(null, couponId, preferentialRange.getPromotionGoodsList());
			break;
		case CLASSES:
		case ZY_CLASSES:
			promotionRangeService.addClassesList(null, couponId, preferentialRange.getPromotionClassList());
			break;
		case BRANDS:
			promotionRangeService.addBrandsList(null, couponId, preferentialRange.getPromotionBrandList());
			break;
		case ALL:
			break;
		default:
			logger.warn(couponId + "无效的优惠券使用范围:" + useRange);
		}
	}

	public Coupon selectDBO(Integer couponId, List<String> enterpriseCodeList) {
		return couponDao.selectDBO(couponId, enterpriseCodeList);
	}

	Coupon selectByCouponCode(String couponCode, String enterpriseCode) {
		return couponDao.selectByCouponCode(couponCode, enterpriseCode);
	}

	public void deleteById(Integer id, List<String> enterpriseCodeList) {
		couponDao.deleteByPrimaryKey(id, enterpriseCodeList);
	}

	/**
	 * 根据id和couponCode的组合条件更改，两个都要. 会去除去除之前的审核信息，并保存优惠范围信息
	 */
	public int updateByPrimaryKey(CouponDTO couponDTO) throws Exception {
		int count = couponDao.updateByPrimaryKey(couponDTO);
		// 如果本次是全场券则不需要设定优惠范围
		if (couponDTO.getUseRange() == PreferentialRangeTypeEnum.ALL.getIndex()) {
			return count;
		}

		savePreferentialRange(couponDTO.getPreferentialRange(), couponDTO.getId(), couponDTO.getUseRange());
		return count;
	}

	/**
	 * 更新优惠券的指定属性的值，不包括auditor和touching有关的字段
	 */
	public int updateByPrimaryKeySelective(Coupon coupon) {
		return couponDao.updateByPrimaryKeySelective(coupon);
	}

	/**
	 * 为优惠券设置 关联的模块和在该模块内的id.只能占用未被其他模块占用的优惠券
	 *
	 * @param couponCode
	 *            优惠券的编码
	 * @param modelTargetId
	 *            对象在模块里的主键id
	 * @param modelCode
	 *            CouponModelNameEnum enum类里取模块code
	 */
	public int setCouponModelInfo(String couponCode, int modelTargetId, String modelCode, String enterpriseCode)
			throws MarketingCenterException {
		Coupon coupon = couponDao.selectByCouponCode(couponCode, enterpriseCode);
		if (coupon == null) {
			throw new MarketingCenterException("优惠券不存在:" + couponCode);
		}
		if (coupon.getAuditorStatus() != CouponAuditorStatusEnum.AUDIT_PASS_WAIT_GRANT.getIndex()) {
			throw new MarketingCenterException("只能发放[已生效未发劵]的优惠券");
		}
		if (coupon.getModelCode() != null) {
			throw new MarketingCenterException("该优惠券已被其他活动占用:" + couponCode);
		}

		int affectRow = couponDao.setCouponModelInfo(couponCode, modelTargetId, modelCode);
		if (affectRow == 0) {
			throw new MarketingCenterException("优惠活动设置发放指定的优惠券失败");
		}
		return affectRow;
	}

	/**
	 * 将优惠券状态改为领取结束
	 *
	 * @param modelTargetId
	 *            目标id
	 * @param modelCode
	 *            从CouponModelNameEnum获取code
	 */
	public void stopCouponByModelInfo(int modelTargetId, String modelCode) {
		couponDao.stopCouponByModelInfo(modelTargetId, modelCode, CouponAuditorStatusEnum.RECEIVE_END.getIndex());

		List<Integer> toEndActivityIdList = new ArrayList<>();
		toEndActivityIdList.add(modelTargetId);
		List<Integer> toEndCouponIdList = getToEndCouponIdList(toEndActivityIdList, modelCode);
		couponCodeService.stopCouponCodeByCouponIdList(toEndCouponIdList);
	}

	/**
	 * 将优惠券状态改为领取结束
	 *
	 * @param modelTargetIdList
	 *            目标id集合
	 * @param modelCode
	 *            从CouponModelNameEnum获取code
	 */
	public void stopCouponByModelInfoList(List<Integer> modelTargetIdList, String modelCode) {
		if (modelTargetIdList == null || modelTargetIdList.size() == 0) {
			return;
		}

		couponDao.stopCouponByModelInfoList(modelTargetIdList, modelCode,
				CouponAuditorStatusEnum.RECEIVE_END.getIndex());
		List<Integer> toEndCouponIdList = getToEndCouponIdList(modelTargetIdList, modelCode);
		couponCodeService.stopCouponCodeByCouponIdList(toEndCouponIdList);
	}

	public int updateAuditStatus(CouponVO coupon) throws Exception {
		return couponDao.updateAuditStatus(coupon);
	}

	public int getCouponCountByMap(Map<String, Object> map) {
		return couponDao.getCouponCountByMap(map);
	}

	/**
	 * id或者couponCode有其中一个就行
	 */
	public CouponVO getVO(Integer couponId, String couponCode, String enterpriseCode) throws MarketingCenterException {
		if (couponId == null && couponCode == null) {
			throw new MarketingCenterException("缺少必要的查询参数");
		}
		return couponDao.getVo(couponId, couponCode, enterpriseCode);
	}

	public CouponDTO getCouponDTO(Integer id, String couponCode, List<String> enterpriseCodeList)
			throws MarketingCenterException {
		if (id == null && couponCode == null) {
			throw new MarketingCenterException("缺少必要的查询参数");
		}

		return couponDao.getCouponDTO(id, couponCode, enterpriseCodeList);
	}

	public CouponDTO getSimpleCouponDTO(Integer couponId, String couponCode, List<String> enterpriseCodeList) {
		return couponDao.getSimpleCouponDTO(couponId, couponCode, enterpriseCodeList);
	}

	/**
	 * 解除指定优惠券与(商品买赠，优惠券活动，订单返利)模块的关联关系
	 *
	 * @param modelTargetId
	 *            对象在所属模块里的主键id
	 * @param modelCode
	 *            CouponModelNameEnum enum类里取模块code
	 */
	public void clearModelInfo(Integer modelTargetId, String modelCode) throws Exception {
		couponDao.clearCouponModelInfo(modelTargetId, modelCode);
	}

	/**
	 * 根据modelTargetId和modelCode查询 活动所关联的优惠券信息
	 *
	 * @param modelTargetId
	 *            必填
	 * @param modelCode
	 *            必填
	 * @param startRow
	 *            选填
	 * @param pageSize
	 *            选填
	 * @param enterpriseCodeList
	 *            必填
	 */
	public List<CouponVO> getVOListByModelInfo(Integer modelTargetId, String modelCode, Integer startRow,
			Integer pageSize, List<String> enterpriseCodeList) {
		return couponDao.getVOListByModelInfo(modelTargetId, modelCode, startRow, pageSize, enterpriseCodeList);
	}

	/**
	 * 根据modelTargetId和modelCode查询 活动所关联的优惠券dbo信息
	 *
	 * @param modelTargetId
	 *            必填
	 * @param modelCode
	 *            必填
	 * @param startRow
	 *            选填
	 * @param pageSize
	 *            选填
	 */
	List<Coupon> getDBOListByModelInfo(Integer modelTargetId, String modelCode, Integer startRow, Integer pageSize,
			List<String> enterpriseCodeList) {
		return couponDao.getDBOListByModelInfo(modelTargetId, modelCode, startRow, pageSize, enterpriseCodeList);
	}

	/**
	 * 当活动审核通过后主动调用本方法生成券码
	 */
	public void createQRCodeForActivity(Integer modelTargetId, String modelCode, List<String> enterpriseCodeList,
			Date activityStartTime) throws Exception {
		if (enterpriseCodeList == null || enterpriseCodeList.size() == 0) {
			throw new MarketingCenterException("企业code不能为空");
		}

		CouponCodeJob job = new CouponCodeJob(modelCode, modelTargetId, enterpriseCodeList.get(0), activityStartTime);
		couponCodeJobDao.insertSelective(job);
	}

	/**
	 * 之后查询优惠券详情的不再调用这个方法，因为返回的数据中不包含优惠范围
	 */
	public List<CouponVO> getEnterpriseReleasedCouponList(String enterpriseCode, Integer grantMode, Integer userId,
			Integer vipId, String userLevelId, List<Integer> auditorStatusList) {
		Map<String, Object> map = new HashMap<>();
		map.put("enterpriseCode", enterpriseCode);
		map.put("auditorStatusList", auditorStatusList);
		map.put("grantMode", grantMode);

		// 需要返回活动优惠券和vip list
		List<CouponActivityDTO> activityList = couponActivityService.getDtoList(map);
		if (activityList == null || activityList.size() == 0) {
			return null;
		}

		List<CouponReceiveRecordDTO> receivedCouponList = null;
		Map<Integer, List<Date>> receiveRecordMap = new HashMap<>();
		// 之后考虑改为map<couponId,List<Date>>
		if (userId != null || vipId != null) {
			receivedCouponList = couponDao.getReceivedRecordDTOListByUserInfo(enterpriseCode, userId, vipId);
			if (receivedCouponList != null) {
				for (CouponReceiveRecordDTO record : receivedCouponList) {
					receiveRecordMap.put(record.getCouponId(), record.getReceiveDateList());
				}
			}
		}

		List<CouponVO> result = new ArrayList<>();
		List<CouponVO> canReceiveAgainList;
		boolean check;
		List<CouponVO> couponList;
		List<String> enterpriseCodeList = new ArrayList<>(1);
		enterpriseCodeList.add(enterpriseCode);
		for (CouponActivityDTO activityDto : activityList) {
			check = (userId == null && vipId == null) || checkUserJoinActivity(activityDto, userId, userLevelId, vipId);

			if (check) {
				couponList = couponDao.getVOListByCouponActivityModelInfo(activityDto.getCouponActivityId(),
						CouponModelNameEnum.COUPON_ACTIVITY.getModelCode(), null, null, enterpriseCodeList);
				// 分别检验每张券用户是否还可以领取
				canReceiveAgainList = filterCanNotReceiveAgainCouponVo(couponList, receiveRecordMap,
						activityDto.getReceiveFrequencyType(), activityDto.getReceiveFrequency());

				result.addAll(canReceiveAgainList);
			}
		}

		return result;
	}

	/**
	 * 过滤掉不能再次领取的优惠券,判断条件为 领取频率是否超过活动限制
	 *
	 * @param couponList
	 *            优惠券集合
	 * @param receiveRecordMap
	 *            领取记录map
	 * @param receiveFrequencyType
	 *            活动领取频率类型
	 * @param receiveFrequency
	 *            活动领取频率
	 * @return 可以领取的优惠券list
	 */
	private List<CouponVO> filterCanNotReceiveAgainCouponVo(List<CouponVO> couponList,
			Map<Integer, List<Date>> receiveRecordMap, Integer receiveFrequencyType, Integer receiveFrequency) {
		if (couponList == null || couponList.size() == 0) {
			return couponList;
		}

		List<CouponVO> canReceiveAgainCouponList = new ArrayList<>();
		boolean notReceivedBefore = receiveRecordMap == null || receiveRecordMap.size() == 0;
		boolean couponReceiveUnlimited = receiveFrequencyType == ReceiveFrequencyTypeEnum.UNLIMITED.getIndex();

		String availableEndTime;
		Date availableEndDate;
		Date now = new Date();

		if (notReceivedBefore || couponReceiveUnlimited) {
			for (CouponVO coupon : couponList) {
				availableEndTime = coupon.getAvailableEndTime();
				boolean canReceive = coupon.getReceivedCouponNum() < coupon.getCouponNum()
						&& coupon.getAuditorStatus() != CouponAuditorStatusEnum.RECEIVE_END.getIndex();
				if (!StringUtils.isEmpty(availableEndTime)) {
					availableEndDate = DateUtils.parseDateTime(availableEndTime);
					canReceive = canReceive && availableEndDate.after(now);
				}

				if (canReceive) {
					canReceiveAgainCouponList.add(coupon);// 判断优惠券是否还有剩余
				}
			}
			return canReceiveAgainCouponList;
		}

		List<Date> receiveDateList;
		if (receiveFrequencyType == ReceiveFrequencyTypeEnum.PER_MAN.getIndex()) {
			for (CouponVO coupon : couponList) {
				boolean canNotReceive = coupon.getReceivedCouponNum() >= coupon.getCouponNum()
						|| coupon.getAuditorStatus() == CouponAuditorStatusEnum.RECEIVE_END.getIndex();

				availableEndTime = coupon.getAvailableEndTime();
				if (!StringUtils.isEmpty(availableEndTime)) {
					availableEndDate = DateUtils.parseDateTime(availableEndTime);
					canNotReceive = canNotReceive || availableEndDate.before(now);
				}
				if (canNotReceive) {
					// 判断优惠券是否还有剩余
					continue;
				}

				receiveDateList = receiveRecordMap.get(coupon.getId());// 判断累计领取次数是否大于receiveFrequency
				if (receiveDateList == null || receiveDateList.size() < receiveFrequency) {
					canReceiveAgainCouponList.add(coupon);
				}
			}
			return canReceiveAgainCouponList;
		}

		if (receiveFrequencyType == ReceiveFrequencyTypeEnum.PER_DAY_MAN.getIndex()) {
			int todayReceiveCount = 0;
			Date todayBegin = DateUtils.getTodayBegin();
			for (CouponVO coupon : couponList) {
				boolean canNotReceive = coupon.getReceivedCouponNum() >= coupon.getCouponNum()
						|| coupon.getAuditorStatus() == CouponAuditorStatusEnum.RECEIVE_END.getIndex();

				availableEndTime = coupon.getAvailableEndTime();
				if (!StringUtils.isEmpty(availableEndTime)) {
					availableEndDate = DateUtils.parseDateTime(availableEndTime);
					canNotReceive = canNotReceive || availableEndDate.before(now);
				}
				if (canNotReceive) {
					// 判断优惠券是否还有剩余
					continue;
				}

				todayReceiveCount = 0;
				receiveDateList = receiveRecordMap.get(coupon.getId());
				// 判断今天的领取数是否小于receiveFrequency
				if (receiveDateList == null || receiveDateList.size() == 0) {
					canReceiveAgainCouponList.add(coupon);
					continue;
				}

				for (Date receiveDate : receiveDateList) {
					if (receiveDate.after(todayBegin)) {
						todayReceiveCount++;
					}
				}
				if (todayReceiveCount < receiveFrequency) {
					canReceiveAgainCouponList.add(coupon);
				}
			}
			return canReceiveAgainCouponList;
		}

		return null;
	}

	/**
	 * 前端查询某一个商品詳情時展示可領取使用的优惠券: 如果用户已登录，查询出该会员可领取得的适用于该商品的优惠券; 如果未登录，查询出所有适用于该商品的优惠券
	 *
	 * @param enterpriseCode
	 *            必填
	 * @param grantMode
	 *            投放方式 GrantModeStatusEnum
	 * @param userId
	 *            可以为空
	 * @param userLevelId
	 *            可以为空
	 * @param goodsVO
	 *            商品详细信息
	 * @param platform
	 *            必填
	 */
	public List<CouponVO> getEnterpriseReleasedCouponListForOneGoods(String enterpriseCode, Integer grantMode,
			Integer userId, String userLevelId, MallProdVo goodsVO, String platform, Integer vipId)
			throws MarketingCenterException {
		if (platform == null) {
			throw new MarketingCenterException("必须要使用平台");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("传入的参数为:enterpriseCode = {}, platform = {}, vipId = {}, userId = {} ", enterpriseCode,
					platform, vipId, userId);
			logger.debug(
					"和brandCode:{}, classCode:{}, GoodsCode:{}, ProdNum:{}, PromPrice:{}, PromTotalPrice:{},  price:{}, totalPrice:{}",
					goodsVO.getBrandCode(), goodsVO.getClassCode(), goodsVO.getGoodsCode(), goodsVO.getProdNum(),
					goodsVO.getPromPrice(), goodsVO.getPromTotalPrice(), goodsVO.getPrice(), goodsVO.getTotalPrice());
		}

		List<Integer> auditorStatusList = new ArrayList<>(2);
		Integer releasedStatus = ActivityAuditorStatusEnum.RELEASE_WAIT_START.getIndex();
		Integer inActivityStatus = ActivityAuditorStatusEnum.IN_ACTIVITY.getIndex();
		auditorStatusList.add(releasedStatus); // 发布未开始
		auditorStatusList.add(inActivityStatus);// 活动中
		List<CouponVO> couponList = getEnterpriseReleasedCouponList(enterpriseCode, grantMode, userId, vipId,
				userLevelId, auditorStatusList);

		if (couponList == null || couponList.size() == 0) {
			return null;
		}

		List<Integer> couponIdList = new ArrayList<>();
		for (CouponVO couponVO : couponList) {
			couponIdList.add(couponVO.getId());
		}
		List<String> enterpriseCodeList = new ArrayList<>();
		enterpriseCodeList.add(enterpriseCode);
		List<CouponDTO> couponDtoList = couponDao.getDtoListByPrimaryKeys(couponIdList, enterpriseCodeList);
		List<CouponVO> couponVoList = couponDao.getVoListByCouponIds(couponIdList, enterpriseCodeList,
				CouponModelNameEnum.COUPON_ACTIVITY.getModelCode());

		List<CouponVO> resultList = new ArrayList<>();
		// String couponPlatform;
		int couponUseRange;
		boolean isInRange;
		Date now = new Date();
		Date availableEndTime;
		for (CouponDTO couponDto : couponDtoList) {
			/*
			 * 和产品商量过了，商品详情页展示可领取的优惠券，不根据优惠券的使用平台去过滤，即wap端可以看到只有在pc端才能使用的优惠券。
			 * 只有在购物车里只展示当前平台可用且可领取的优惠券。 couponPlatform = couponDto.getUsePlatform(); if
			 * (!StatusConstants.UNLIMITED.equals(couponPlatform) &&
			 * couponPlatform.indexOf(platform.toLowerCase()) < 0) { continue; }
			 */
			if (couponDto.getAvailableTimeType() == AvailableTimeTypeEnum.FIXED_TIME.getIndex()) {
				// 判断优惠券是否已过期
				availableEndTime = DateUtils.parseDateTime(couponDto.getAvailableEndTime());
				if (availableEndTime.before(now)) {
					continue;
				}
			}

			couponUseRange = couponDto.getUseRange();
			PreferentialRangeTypeEnum preferentialRangeTypeEnum = PreferentialRangeTypeEnum
					.getEnumByIndex(couponUseRange);
			Assert.notNull(preferentialRangeTypeEnum, "优惠券优惠范围");

			switch (preferentialRangeTypeEnum) {
			case BRANDS:
				isInRange = checkIsInBrands(goodsVO, couponDto.getPreferentialRange().getPromotionBrandList());
				break;
			case CLASSES:
			case ZY_CLASSES:
				isInRange = checkIsInClasses(goodsVO, couponDto.getPreferentialRange().getPromotionClassList(),
						couponDto.getEnterpriseId());
				break;
			case GOODS:
				isInRange = checkIsInGoods(goodsVO, couponDto.getPreferentialRange().getPromotionGoodsList());
				break;
			default:
				isInRange = true;
			}

			if (isInRange) {
				for (CouponVO tmpVo : couponVoList) {
					if (tmpVo.getId() == couponDto.getId().intValue()) {
						resultList.add(tmpVo);
						break;
					}
				}
			}
		}

		return resultList;
	}

	/**
	 * 购物车展示[可領取][可使用于这一批商品的]优惠券: 如果用户已登录，查询出该会员可领取得的适用于该商品的优惠券;
	 * 如果未登录，查询出所有适用于这一批商品的优惠券
	 *
	 * @param enterpriseCode
	 *            必填
	 * @param userId
	 *            可以为空
	 * @param userLevelId
	 *            可以为空
	 * @param goodsVoList
	 *            商品列表 必填
	 * @param vipId
	 *            会员id
	 * @return 优惠券列表
	 * @throws Exception
	 *             异常信息
	 */
	public List<CouponVO> getEnterpriseReleasedCouponListForManyGoods(String enterpriseCode, Integer userId,
			String userLevelId, List<MallProdVo> goodsVoList, String receivePlatform, Integer vipId, String usePlatform)
			throws Exception {
		if (usePlatform == null) {
			throw new Exception("必须要使用平台");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("传入的参数为:enterpriseCode = {}, receivePlatform = {}, vipId = {}, userId = {} ,usePlatform ={}",
					enterpriseCode, receivePlatform, vipId, userId, usePlatform);
			goodsVoList.forEach(goods -> logger.debug(
					"和 brandCode:{}, classCode:{}, GoodsCode:{}, ProdNum:{}, PromPrice:{}, PromTotalPrice:{},  price:{}, totalPrice:{}",
					goods.getBrandCode(), goods.getClassCode(), goods.getGoodsCode(), goods.getProdNum(),
					goods.getPromPrice(), goods.getPromTotalPrice(), goods.getPrice(), goods.getTotalPrice()));

		}

		List<Integer> auditorStatusList = new ArrayList<>(1);
		auditorStatusList.add(ActivityAuditorStatusEnum.IN_ACTIVITY.getIndex());// 活动中

		int grantMode = 0; //
		UsePlatformEnum receivePlatformEnum = UsePlatformEnum.getEnumByModelCode(receivePlatform);
		Assert.notNull(receivePlatformEnum, "领取平台枚举");
		switch (receivePlatformEnum) {
		case OFFLINE:
			grantMode = GrantModeStatusEnum.OFFLINE.getIndex();
			break;
		case ONLINE:
		case WAP:
		case PC:
			grantMode = GrantModeStatusEnum.ONLINE_ACCEPT.getIndex();
			break;
		default:
			break;
		}

		List<CouponVO> couponList = getEnterpriseReleasedCouponList(enterpriseCode, grantMode, userId, vipId,
				userLevelId, auditorStatusList);
		if (couponList == null || couponList.size() == 0) {
			return null;
		}

		List<Integer> couponIdList = new ArrayList<>();
		for (CouponVO couponVO : couponList) {
			couponIdList.add(couponVO.getId());
		}
		List<String> enterpriseCodeList = new ArrayList<>();
		enterpriseCodeList.add(enterpriseCode);
		List<CouponDTO> couponDtoList = couponDao.getDtoListByPrimaryKeys(couponIdList, enterpriseCodeList);
		List<CouponVO> couponVoList = couponDao.getVoListByCouponIds(couponIdList, enterpriseCodeList,
				CouponModelNameEnum.COUPON_ACTIVITY.getModelCode());

		List<CouponVO> resultList = new ArrayList<>();
		String couponPlatform;
		int couponUseRange;
		boolean isInRange;
		Date now = new Date();
		Date availableStartTime;
		Date availableEndTime;
		for (CouponDTO couponDto : couponDtoList) {
			isInRange = false;
			couponPlatform = couponDto.getUsePlatform();
			if (!StatusConstants.UNLIMITED.equals(couponPlatform)
					&& !couponPlatform.contains(usePlatform.toLowerCase())) {
				continue;
			}
			if (couponDto.getAvailableTimeType() == AvailableTimeTypeEnum.FIXED_TIME.getIndex()) {
				// 判断优惠券是否已过期
				availableStartTime = DateUtils.parseDateTime(couponDto.getAvailableStartTime());
				if (availableStartTime.after(now)) {
					continue;
				}
				availableEndTime = DateUtils.parseDateTime(couponDto.getAvailableEndTime());
				if (availableEndTime.before(now)) {
					continue;
				}
			}

			couponUseRange = couponDto.getUseRange();
			PreferentialRangeTypeEnum preferentialRangeTypeEnum = PreferentialRangeTypeEnum
					.getEnumByIndex(couponUseRange);
			Assert.notNull(preferentialRangeTypeEnum, "优惠券使用范围");
			switch (preferentialRangeTypeEnum) {
			case BRANDS:
				for (MallProdVo goodsVO : goodsVoList) {
					isInRange = checkIsInBrands(goodsVO, couponDto.getPreferentialRange().getPromotionBrandList());
					if (isInRange) {
						break;
					}
				}
				break;
			case CLASSES:
			case ZY_CLASSES:
				for (MallProdVo goodsVO : goodsVoList) {
					isInRange = checkIsInClasses(goodsVO, couponDto.getPreferentialRange().getPromotionClassList(),
							couponDto.getEnterpriseId());
					if (isInRange) {
						break;
					}
				}

				break;
			case GOODS:
				for (MallProdVo goodsVO : goodsVoList) {
					isInRange = checkIsInGoods(goodsVO, couponDto.getPreferentialRange().getPromotionGoodsList());
					if (isInRange) {
						break;
					}
				}

				break;
			default:
				isInRange = true;
			}

			if (isInRange) {
				for (CouponVO tmpVo : couponVoList) {
					if (tmpVo.getId() == couponDto.getId().intValue()) {
						resultList.add(tmpVo);
						break;
					}
				}
			}
		}

		return resultList;
	}

	/**
	 * 得到可用于指定商品的最好的一张优惠券,TODO 也许这里也可以返回一个优惠券组合
	 *
	 * @param enterpriseCode
	 *            企业code
	 * @param vipId
	 *            会员id
	 * @param userId
	 *            用户Id
	 * @param goodsVoList
	 *            商品列表
	 * @param platform
	 *            使用平台
	 * @return 优惠额度最大的优惠券
	 * @throws Exception
	 *             输出值检查出异常
	 */
	public CouponCodeVO getPerfectCouponCodeForManyGoods(String enterpriseCode, Integer userId,
			List<MallProdVo> goodsVoList, String platform, Integer vipId) throws Exception {
		if (StringUtils.isEmpty(platform)) {
			throw new Exception("必须要使用平台");
		}
		// 这里获取到的应该是 couponDTOList，但是需要额外获取到couponCodeList;
		List<CouponDTO> couponDtoList = getUnusedDtoListByReceiverInfo(userId, vipId,
				CouponCodeStatusEnum.RECEIVED.getIndex(), enterpriseCode);
		if (couponDtoList == null || couponDtoList.size() == 0) {
			return null;
		}

		CouponCodeVO bestCouponCode = null;
		CouponCodeVO tmpCouponCode;
		BigDecimal mostReduceMoney = BigDecimal.ZERO;
		BigDecimal tmpReduceMoney; // 现在这张券针对这批商品最多能省多少钱
		int compareValue;

		// 算出每张优惠券能减少的最大的价格
		for (CouponDTO couponDto : couponDtoList) {
			tmpReduceMoney = getMostReduceMoney(couponDto, goodsVoList, platform);
			compareValue = tmpReduceMoney.compareTo(mostReduceMoney);

			if (compareValue > 0) {
				mostReduceMoney = tmpReduceMoney;
				bestCouponCode = getEarliestReceivedCouponCode(couponDto);
			} else if (compareValue == 0) {
				if (tmpReduceMoney.compareTo(BigDecimal.ZERO) == 0) {// 第一次时best为空
					continue;
				}
				tmpCouponCode = getEarliestReceivedCouponCode(couponDto);
				// 多张金额相等就取最早领取的优惠券使用
				if (tmpCouponCode.getReceiveTime().compareTo(bestCouponCode.getReceiveTime()) < 0) {
					bestCouponCode = tmpCouponCode;
				}
			}
		}

		return bestCouponCode;
	}

	/**
	 * 之前的写法多表关联了，查询效率会因为券码表的数据量快速增加而降低，要拆分成两个方法了
	 */
	private List<CouponDTO> getUnusedDtoListByReceiverInfo(Integer userId, Integer vipId, int receiveStatus,
			String enterpriseCode) {
		List<CouponCodeVO> couponCodeList = couponCodeService.getVoListByReceiverInfo(userId, vipId, receiveStatus,
				enterpriseCode);
		if (couponCodeList == null || couponCodeList.size() == 0) {
			return null;
		}

		Set<Integer> couponIdSet = new HashSet<>(); // 用于记录所有的优惠券id
		Map<Integer, List<CouponCodeVO>> couponIdMap = new HashMap<>(); // couponId和券码的映射
		List<CouponCodeVO> tmpCodeList;
		Integer tmpCouponId;
		for (CouponCodeVO tmpCode : couponCodeList) {
			tmpCouponId = tmpCode.getCouponId();
			if (couponIdMap.get(tmpCouponId) == null) {
				tmpCodeList = new ArrayList<>();
			} else {
				tmpCodeList = couponIdMap.get(tmpCode.getCouponId());
			}
			tmpCodeList.add(tmpCode);
			couponIdMap.put(tmpCouponId, tmpCodeList);
			couponIdSet.add(tmpCode.getCouponId());
		}

		List<CouponDTO> dtoList = couponDao.getDtoListByPrimaryKeySet(couponIdSet, enterpriseCode, StatusConstants.NO);
		for (CouponDTO dto : dtoList) {
			tmpCodeList = couponIdMap.get(dto.getId());
			if (tmpCodeList != null) {
				for (CouponCodeVO tmpCode : tmpCodeList) {
					tmpCode.setUseRange(dto.getUseRange());
					tmpCode.setCouponName(dto.getCouponName());
					tmpCode.setCouponType(dto.getCouponType());
					tmpCode.setCouponAmount(dto.getCouponAmount());
					tmpCode.setCouponDiscount(dto.getCouponDiscount());
					tmpCode.setUseRestriction(dto.getUseRestriction());
					tmpCode.setFullElement(dto.getFullElement());
					tmpCode.setUseLimit(dto.getUseLimit());
					tmpCode.setOverlying(dto.getOverlying());
					tmpCode.setUsePlatform(dto.getUsePlatform());
				}
			}
			dto.setCouponCodeList(tmpCodeList);
		}

		return dtoList;
	}

	/**
	 * 得到最早领取的那张优惠券券码
	 *
	 * @param couponCodeList
	 *            优惠券列表集合
	 */
	private CouponCodeVO getEarliestReceivedCouponCode(List<CouponCodeVO> couponCodeList) {
		if (couponCodeList == null || couponCodeList.size() == 0) {
			return null;
		}

		CouponCodeVO bestCouponCode = null;
		for (CouponCodeVO tmpCode : couponCodeList) {
			if (bestCouponCode == null) {
				bestCouponCode = tmpCode;
			} else {
				if (tmpCode.getReceiveTime().compareTo(bestCouponCode.getReceiveTime()) < 0) {
					bestCouponCode = tmpCode;
				}
			}
		}
		return bestCouponCode;
	}

	private CouponCodeVO getEarliestReceivedCouponCode(CouponDTO couponDto) {
		List<CouponCodeVO> couponCodeList = couponDto.getCouponCodeList();
		if (couponCodeList == null || couponCodeList.size() == 0) {
			return null;
		}

		CouponCodeVO bestCouponCode = null;
		for (CouponCodeVO tmpCode : couponCodeList) {
			if (bestCouponCode == null) {
				bestCouponCode = tmpCode;
			} else {
				if (tmpCode.getReceiveTime().compareTo(bestCouponCode.getReceiveTime()) < 0) {
					bestCouponCode = tmpCode;
				}
			}
		}
		return bestCouponCode;
	}

	/**
	 * 如果对指定一批商品使用某一张优惠券，可以获得的最大优惠额
	 *
	 * @param couponDto
	 *            dto
	 * @param goodsVoList
	 *            商品列表
	 * @param platform
	 *            使用平台
	 * @return 最大优惠金额
	 */
	private BigDecimal getMostReduceMoney(CouponDTO couponDto, List<MallProdVo> goodsVoList, String platform) {
		if (goodsVoList == null || goodsVoList.size() == 0) {
			return BigDecimal.ZERO;
		}

		// 有一个特别需要注意的地方是，指定优惠券用于指定的商品，而不是整个订单
		String couponPlatform = couponDto.getUsePlatform();
		if (!StatusConstants.UNLIMITED.equals(couponPlatform) && !couponPlatform.contains(platform)) {
			return BigDecimal.ZERO;
		}

		List<MallProdVo> prodVoList = new ArrayList<>();// 选择出所有适用于该优惠券的商品列表
		PreferentialRangeTypeEnum preferentialRangeTypeEnum = PreferentialRangeTypeEnum
				.getEnumByIndex(couponDto.getUseRange());
		Assert.notNull(preferentialRangeTypeEnum, "优惠券使用范围");

		switch (preferentialRangeTypeEnum) {
		case BRANDS:
			for (MallProdVo goodsVO : goodsVoList) {
				if (checkIsInBrands(goodsVO, couponDto.getPreferentialRange().getPromotionBrandList())) {
					prodVoList.add(goodsVO);
				}
			}
			break;
		case CLASSES:
		case ZY_CLASSES:
			for (MallProdVo goodsVO : goodsVoList) {
				if (checkIsInClasses(goodsVO, couponDto.getPreferentialRange().getPromotionClassList(),
						couponDto.getEnterpriseId())) {
					prodVoList.add(goodsVO);
				}
			}
			break;
		case GOODS:
			for (MallProdVo goodsVO : goodsVoList) {
				if (checkIsInGoods(goodsVO, couponDto.getPreferentialRange().getPromotionGoodsList())) {
					prodVoList.add(goodsVO);
				}
			}
			break;
		default:
			prodVoList = goodsVoList;
			break;
		}
		if (prodVoList.size() == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal totalPrice = BigDecimal.ZERO;
		for (MallProdVo prodVo : prodVoList) {
			if (prodVo.getProdNum() == 0) {// 默认为一件商品
				if (prodVo.getPromPrice() == null) {
					totalPrice = totalPrice.add(prodVo.getPrice());
				} else {
					totalPrice = totalPrice.add(prodVo.getPromPrice());
				}
			} else {
				if (prodVo.getPromTotalPrice() == null) {
					totalPrice = totalPrice.add(prodVo.getTotalPrice());
				} else {
					totalPrice = totalPrice.add(prodVo.getPromTotalPrice());
				}
			}
		}

		int coupontype = couponDto.getCouponType();
		int useRestriction = couponDto.getUseRestriction();

		if (useRestriction == StatusConstants.NO && coupontype == CouponTypeEnum.CASH.getIndex()) {
			if (totalPrice.compareTo(couponDto.getCouponAmount()) >= 0) {// 需要比较totalPrice和CouponAmount，返回较小的那个
				return couponDto.getCouponAmount();
			} else {
				return totalPrice;
			}
		}

		if (useRestriction == StatusConstants.NO && coupontype == CouponTypeEnum.DISCOUNT.getIndex()) {
			return FULL_DISCOUNT.subtract(couponDto.getCouponDiscount()).multiply(totalPrice)
					.multiply(StatusConstants.TEN_PERCENT);// 折扣是一个小于10的数字
		}

		if (useRestriction == StatusConstants.YES && coupontype == CouponTypeEnum.CASH.getIndex()) {
			// 如果总金额超过 使用条件
			if (totalPrice.compareTo(couponDto.getFullElement()) >= 0) {
				return couponDto.getCouponAmount();
			}
			return BigDecimal.ZERO;
		}

		if (useRestriction == StatusConstants.YES && coupontype == CouponTypeEnum.DISCOUNT.getIndex()) {
			if (totalPrice.compareTo(couponDto.getFullElement()) >= 0) {// 如果总金额超过使用条件
				return FULL_DISCOUNT.subtract(couponDto.getCouponDiscount()).multiply(totalPrice)
						.multiply(StatusConstants.TEN_PERCENT);
			}
			return BigDecimal.ZERO;
		}

		return BigDecimal.ZERO;
	}

	/**
	 * 判断指定商品是否在优惠商品范围内
	 *
	 * @param goodsVO
	 *            待判断的商品
	 * @param promotionGoodsList
	 *            优惠商品列表
	 * @return true包含
	 */
	boolean checkIsInGoods(MallProdVo goodsVO, List<PromotionalGoods> promotionGoodsList) {
		if (promotionGoodsList == null || promotionGoodsList.size() == 0) {
			return false;
		}
		boolean isExlude = false;
		boolean equals;
		for (PromotionalGoods promotionGood : promotionGoodsList) {
			isExlude = promotionGood.getExclude() == StatusConstants.YES;
			if (isExlude) {
				// 排除这些商品
				equals = goodsVO.getGoodsCode() != null && goodsVO.getGoodsCode().equals(promotionGood.getGoodsCode());
				if (equals) {
					return false;
				}
			} else {
				// 包含这些商品
				equals = goodsVO.getGoodsCode() != null && goodsVO.getGoodsCode().equals(promotionGood.getGoodsCode());
				if (equals) {
					return true;
				}
			}
		}

		return isExlude; // 如果是要排除商品，但是到最后都不equals，那么返回true.包含这些商品也同理
	}

	boolean checkIsInClasses(MallProdVo goodsVO, List<PromotionalClass> promotionClassList, Integer enterpriseId) {
		String goodsClassCode = goodsVO.getClassCode();
		if (StringUtils.isEmpty(goodsClassCode))
			return false;

		String goodsClassCodeWithOutZY = goodsClassCode.replace(ErpUtils.ZY_FIRST_LEVEL_PRFIX, "");
		List<String> subClassCodeSet;
		String promotionClassCode;
		for (PromotionalClass promotionClass : promotionClassList) {
			promotionClassCode = promotionClass.getClassCode();
			Assert.notNull(promotionClassCode, "品类折扣券类别code");

			// 商城一级分类下所有商品 = 该分类下直接的商品 + 所有子分类下所有的商品
			// 从ERP导入到商城时，ERP一级分类(03)下的商品也随同拷贝到了商城拷贝后的分类(zy03)下
			if (promotionClassCode.equals(goodsClassCode) || goodsClassCodeWithOutZY.equals(promotionClassCode)) {
				return true;
			}

			subClassCodeSet = GoodsClassUtil.getAllSubClassCodeSet(enterpriseId, promotionClassCode);
			if (subClassCodeSet == null || subClassCodeSet.size() == 0) {
				continue;
			}

			if (subClassCodeSet.contains(goodsClassCode)) {
				return true;
			}
		}
		return false;
	}

	boolean checkIsInBrands(MallProdVo goodsVO, List<PromotionalBrand> promotionBrandList) {
		for (PromotionalBrand promotionBrand : promotionBrandList) {
			if (promotionBrand.getBrandCode().equals(goodsVO.getBrandCode())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查用户的vip等级是否满足活动的vip等级限制
	 */
	Boolean checkVipLevel(String activityVipLevel, String vipLevelId, String enterpriseCode, Integer enterpriseId,
			Integer vipId, Integer userId) {
		boolean containLevel = false;
		if (!StatusConstants.UNLIMITED.equals(activityVipLevel)) {
			// 选择的vipLevelId中是否包含当前用户的levelId
			String[] activityVipLevelArray = activityVipLevel.split(",");
			for (int i = 0; i < activityVipLevelArray.length; i++) {
				if (activityVipLevelArray[i].equals(vipLevelId)) {
					containLevel = true;
				}
			}
			if (!containLevel) {// 如果领取优惠券的会员等级没有包含在活动指定的等级内
				if (activityVipLevel.contains(StatusConstants.NEW)) {
					// 如果活动指定的会员等级中包含了[新会员]
					List<VipVO> vipList = null;
					try {
						vipList = VipUtil.noBuyHistoryUser(enterpriseCode, null, null, null, enterpriseId);
					} catch (MarketingCenterException e) {
						logger.error(e.getMsgDesc(), e);
					}
					if (vipList == null || vipList.size() == 0) {
						return false;
					}
					boolean vipIdEquals = false;
					boolean userIdEquals = false;
					for (VipVO activityVip : vipList) {
						if (activityVip.getVipId() != null && vipId != null) {
							vipIdEquals = activityVip.getVipId().intValue() == vipId;
							if (vipIdEquals) {
								break;
							}
						}
						if (activityVip.getUserId() != null && userId != null) {
							userIdEquals = userId.intValue() == activityVip.getUserId();
							if (userIdEquals) {
								break;
							}
						}
					}
					if (!vipIdEquals && !userIdEquals) {
						// 证明指定用户不是新用户
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return null;
	}

	Boolean checkVipInfo(Integer activityExcludeVip, List<CouponActivityVIPVO> vipList, Integer userId, Integer vipId) {
		boolean excludeVip = activityExcludeVip == StatusConstants.YES;
		if (excludeVip) {
			// 排除指定的一群会员
			boolean vipIdEquals;
			boolean userIdEquals;
			for (CouponActivityVIPVO vip : vipList) {
				if (vip.getVipId() != null && vipId != null) {
					vipIdEquals = vip.getVipId().intValue() == vipId;
					if (vipIdEquals) {
						return false;
					}
				}
				if (vip.getUserId() != null && userId != null) {
					userIdEquals = userId.intValue() == vip.getUserId();
					if (userIdEquals) {
						return false;
					}
				}
			}
			return true;
		} else {
			// 只包含指定的会员
			for (CouponActivityVIPVO vip : vipList) {
				if (vip.getVipId() != null && vipId != null) {
					if (vip.getVipId().intValue() == vipId) {
						return true;
					}
				}
				if (vip.getUserId() != null && userId != null) {
					if (userId.intValue() == vip.getUserId()) {
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * 根据userId userLevelId再过滤,检查消费者是否可以参加某个优惠券活动
	 *
	 * @param activityDto
	 *            活动对象dto
	 * @param userId
	 *            用户ID
	 * @param vipLevelId
	 *            vip等级ID
	 * @param vipId
	 *            vipId
	 * @return true 可以参加
	 */
	private boolean checkUserJoinActivity(CouponActivityDTO activityDto, Integer userId, String vipLevelId,
			Integer vipId) {
		// 是否包含指定的userLevelId-- >是否有指定的userId -- >是排除还是添加
		Boolean isFitVipLevel = checkVipLevel(activityDto.getVipLevel(), vipLevelId, activityDto.getEnterpriseCode(),
				activityDto.getEnterpriseId(), vipId, userId);
		if (isFitVipLevel != null)
			return isFitVipLevel;

		List<CouponActivityVIPVO> vipList = activityDto.getCouponActivityVIPList();
		if (vipList == null || vipList.size() == 0) {
			return true;
		}

		// 检查会员id信息是否满足活动
		return checkVipInfo(activityDto.getExcludeVIP(), vipList, userId, vipId);
	}

	/**
	 * 活动开始后将活动绑定的优惠券的状态改为[领取中]
	 *
	 * @param toStartActivityIdList
	 *            即将开始的活动的id
	 * @param modelCode
	 *            模块code
	 */
	public void startGrantCouponCodesForActivities(List<Integer> toStartActivityIdList, String modelCode) {
		if (toStartActivityIdList != null && toStartActivityIdList.size() > 0) {
			// 查詢是否已经制券,如果没有制券，则强制在活动开始前制券
			List<CouponCodeJob> jobList = couponCodeJobDao.selectCouponCodeJobsByModelInfos(toStartActivityIdList,
					modelCode, StatusConstants.NO);
			for (CouponCodeJob job : jobList) {
				try {
					couponCodeJobService.createQRCodeForActivity(job);
				} catch (MarketingCenterException e) {
					logger.error("为活动制造券码失败:" + e.getMsgDesc(), e);
				} catch (Exception e) {
					logger.error("为活动制造券码失败", e);
				}
			}

			couponDao.startGrantCouponCodesForActivities(toStartActivityIdList, modelCode,
					CouponAuditorStatusEnum.RECEIVING.getIndex());
		}
	}

	public void occupyOneCouponCode(Integer couponId, String enterpriseCode) throws MarketingCenterException {
		int rowsAffected = couponDao.occupyOneCouponCode(couponId, enterpriseCode);
		if (rowsAffected == 0) {
			logger.warn("该优惠券已经领取完了:" + couponId);
			throw new MarketingCenterException(CouponCodeService.USED_UP, MarketingCenterException.USED_UP_DESCRIPTION);
		}
	}

	public List<Integer> getToEndCouponIdList(List<Integer> modelTargetIdList, String modelCode) {
		if (modelTargetIdList != null && modelTargetIdList.size() > 0) {
			return couponDao.getToEndCouponIdList(modelTargetIdList, modelCode);
		}
		return null;
	}

	/**
	 * 用户下单时，返回已拥有的优惠券的各种可使用组合
	 *
	 * @param enterpriseCode
	 *            企业编码
	 * @param userId
	 *            用户id
	 * @param goodsVoList
	 *            商品集合
	 * @param platform
	 *            使用平台
	 * @param vipId
	 *            vip Id
	 * @return 可用的优惠券组合
	 * @throws MarketingCenterException
	 *             业务异常
	 */
	public CouponGroupDTO getUserCouponGroupListForManyGoods(String enterpriseCode, Integer userId,
			List<MallProdVo> goodsVoList, String platform, Integer vipId) throws MarketingCenterException {
		if (StringUtils.isEmpty(platform)) {
			throw new MarketingCenterException("必须要使用平台");
		}
		if (userId == null && vipId == null) {
			throw new MarketingCenterException("userId和vipId不能同时为空");
		}
		if (goodsVoList == null || goodsVoList.size() == 0) {
			throw new MarketingCenterException("商品列表不能为空");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("传入的参数为:enterpriseCode = {}, platform = {}, vipId = {}, userId = {} ", enterpriseCode,
					platform, vipId, userId);
			goodsVoList.forEach(goods -> logger.debug(
					"brandCode:{}, classCode:{}, GoodsCode:{}, ProdNum:{}, PromPrice:{}, PromTotalPrice:{},  price:{}, totalPrice:{}",
					goods.getBrandCode(), goods.getClassCode(), goods.getGoodsCode(), goods.getProdNum(),
					goods.getPromPrice(), goods.getPromTotalPrice(), goods.getPrice(), goods.getTotalPrice()));

		}

		List<CouponDTO> couponDtoList = getUnusedDtoListByReceiverInfo(userId, vipId,
				CouponCodeStatusEnum.RECEIVED.getIndex(), enterpriseCode);
		if (couponDtoList == null || couponDtoList.size() == 0) {
			return null;
		}

		List<CouponDTO> canUseCouponDtoList = filterCannotUseCouponDto(couponDtoList, goodsVoList, platform);
		List<CouponGroupDTO> groupList = new ArrayList<>();
		List<CouponGroupDTO> cannotOverlyingGroup = new ArrayList<>();

		/**
		 * 使用优惠券前最多应该支付的钱
		 */
		BigDecimal allPromTotalPrice = BigDecimal.ZERO;
		for (MallProdVo prodVo : goodsVoList) {
			if (prodVo.getPromTotalPrice() == null) {
				allPromTotalPrice = allPromTotalPrice.add(prodVo.getTotalPrice());
			} else {
				allPromTotalPrice = allPromTotalPrice.add(prodVo.getPromTotalPrice());
			}
		}

		Map<CouponCodeVO, ReduceInfo> codeReduceMap = new HashMap<>();// 券码和优惠信息map
		Map<String, List<MallProdVo>> couponCodeProductsMap = couponGroupService
				.getCouponCodeProductMap(canUseCouponDtoList, goodsVoList);// <couponCode,mallGoodsVo的list>

		for (CouponDTO canUseCoupon : canUseCouponDtoList) {
			if (canUseCoupon.getOverlying() == StatusConstants.YES) {
				final ReduceInfo tmpReduce = getMaxReduce(canUseCoupon, couponCodeProductsMap);
				canUseCoupon.getCouponCodeList().forEach(c -> codeReduceMap.put(c, tmpReduce));
			} else {
				// 总优惠金额越大,领取时间越早的优先级越高
				cannotOverlyingGroup.addAll(createGroupsByCanUseUnOvlyingCoupon(canUseCoupon, goodsVoList, platform));
			}
		}

		groupList.addAll(
				couponGroupService.getCanOverlyingGroups(codeReduceMap, couponCodeProductsMap, allPromTotalPrice));
		groupList.addAll(cannotOverlyingGroup);
		return sortGroupList(groupList);
	}

	private ReduceInfo getMaxReduce(CouponDTO couponDto, Map<String, List<MallProdVo>> couponCodeProductsMap) {
		ReduceInfo reduceInfo = new ReduceInfo();

		List<MallProdVo> prodVoList = couponCodeProductsMap.get(couponDto.getCouponCode());
		reduceInfo.setProdVoList(prodVoList);
		reduceInfo.setContainGoodsCodeList(
				prodVoList.stream().map(MallProdVo::getGoodsCode).collect(Collectors.toList()));

		BigDecimal totalPrice = BigDecimal.ZERO;
		for (MallProdVo prodVo : prodVoList) {
			if (prodVo.getProdNum() == 0) {// 默认为一件商品
				if (prodVo.getPromPrice() == null) {
					totalPrice = totalPrice.add(prodVo.getPrice());
				} else {
					totalPrice = totalPrice.add(prodVo.getPromPrice());
				}
			} else {
				if (prodVo.getPromTotalPrice() == null) {
					totalPrice = totalPrice.add(prodVo.getTotalPrice());
				} else {
					totalPrice = totalPrice.add(prodVo.getPromTotalPrice());
				}
			}
		}

		int coupontype = couponDto.getCouponType();
		int useRestriction = couponDto.getUseRestriction();

		if (useRestriction == StatusConstants.NO && coupontype == CouponTypeEnum.CASH.getIndex()) {
			if (totalPrice.compareTo(couponDto.getCouponAmount()) >= 0) {// 需要比较totalPrice和CouponAmount，返回较小的那个
				reduceInfo.setMostReduceMoney(couponDto.getCouponAmount());
			} else {
				reduceInfo.setMostReduceMoney(totalPrice);
			}
		} else if (useRestriction == StatusConstants.NO && coupontype == CouponTypeEnum.DISCOUNT.getIndex()) {
			// 折扣是一个小于10的数字. 这是折扣后的价格
			reduceInfo.setMostReduceMoney(FULL_DISCOUNT.subtract(couponDto.getCouponDiscount()).multiply(totalPrice)
					.multiply(StatusConstants.TEN_PERCENT));
		} else if (useRestriction == StatusConstants.YES && coupontype == CouponTypeEnum.CASH.getIndex()) {
			// 如果总金额超过使用条件
			if (totalPrice.compareTo(couponDto.getFullElement()) >= 0) {
				reduceInfo.setMostReduceMoney(couponDto.getCouponAmount());
			}
		} else if (useRestriction == StatusConstants.YES && coupontype == CouponTypeEnum.DISCOUNT.getIndex()) {
			if (totalPrice.compareTo(couponDto.getFullElement()) >= 0) {// 如果总金额超过使用条件
				reduceInfo.setMostReduceMoney(FULL_DISCOUNT.subtract(couponDto.getCouponDiscount()).multiply(totalPrice)
						.multiply(StatusConstants.TEN_PERCENT));
			}
		}

		// 需要一个平摊算法，单个商品的优惠额 = (单个商品的价格/总商品的价格 )*优惠的总额
		reduceInfo.setMallProdVoSaleMap(
				flatReduceMoneyToPerGoods(reduceInfo.getMostReduceMoney(), totalPrice, prodVoList));
		return reduceInfo;
	}

	private List<CouponGroupDTO> getCanOverlyingGroupsByCouponDto(CouponDTO canUseCoupon,
			List<CouponGroupDTO> canOverlyingGroup, List<MallProdVo> goodsVoList, String platform,
			BigDecimal allPromTotalPrice) {
		List<CouponGroupDTO> returnGroups = new ArrayList<>();

		List<CouponCodeVO> couponCodesList = canUseCoupon.getCouponCodeList();
		if (couponCodesList == null || couponCodesList.size() == 0) {
			return returnGroups;
		}

		ReduceInfo reduceInfo = getReduceInfo(canUseCoupon, goodsVoList, platform);
		// canOverlyingGroup在本方法内不许有增删操作
		BigDecimal mostReduceMoney = reduceInfo.getMostReduceMoney();
		// getMostReduceMoney(canUseCoupon, goodsVoList, platform);
		if (mostReduceMoney == null || mostReduceMoney.compareTo(BigDecimal.ZERO) <= 0) {
			return returnGroups;
		}

		// 不管有没有加入过其他的组合，都可以为这个优惠券单独做一个组
		CouponGroupDTO newGroup = new CouponGroupDTO();
		CouponCodeVO earliestCouponCode = getEarliestReceivedCouponCode(couponCodesList);
		// 这里就删除了，会导致接下来本类优惠券无法与其他的组合合并组成新的组合
		// couponCodesList.remove(earliestCouponCode);

		// newGroup.addCouponCode(earliestCouponCode, mostReduceMoney,
		// reduceInfo.getMallProdVoSaleMap());
		// newGroup.setCurrentSale();
		newGroup.setOverlying(StatusConstants.YES);
		newGroup.setMinAvailableEndTime(earliestCouponCode.getAvailableEndTime());
		newGroup.setContainGoodsCodeList(reduceInfo.getContainGoodsCodeList());
		returnGroups.add(newGroup);
		if (mostReduceMoney.compareTo(allPromTotalPrice) >= 0) {
			// 如果该组合已经到达优惠券的促销后总应付金额，那么不需要再与其他的组合合并。
			return returnGroups;
		}

		List<CouponGroupDTO> totalGroupList = new ArrayList<>(canOverlyingGroup);// 原来的groupList与新的groupList加起来
		totalGroupList.add(newGroup);

		List<CouponGroupDTO> newGroupList = new ArrayList<>();

		while (couponCodesList.size() > 0) {
			earliestCouponCode = getEarliestReceivedCouponCode(couponCodesList);// 在新的组合里,取出canUseCoupon中最快到达结束使用时间的那张券码,和这一个组成一个新的组合
			if (earliestCouponCode == null) {
				break;
			}
			for (CouponGroupDTO tmpGroup : totalGroupList) {
				if (tmpGroup.getCurrentSale() != null && tmpGroup.getCurrentSale().compareTo(allPromTotalPrice) >= 0) {
					// 该组合已经到达优惠券的促销后总应付金额，那么不需要再与其他的组合合并。
					continue;
				}

				if (isConflict(tmpGroup, canUseCoupon, reduceInfo)) {
					continue;
				}

				if (hasSameGroup(tmpGroup, earliestCouponCode, totalGroupList)) {
					continue;// 这个方法不应该排除多张券可能同时存在的可能性，要加强验证
				}

				// 如果组合之中之前就已经包含了这张券码，那么跳过
				if (tmpGroup.getCouponCodeSaleMap().keySet().contains(earliestCouponCode.getQrCode())) {
					continue;
				}

				// 因为全场券可以叠加，而其他的优惠券组合时不会有商品交叉的情况，所以只需要考虑全场券，叠加时总的优惠金额大于最多应付款额时的处理
				if (canUseCoupon.getUseRange() == PreferentialRangeTypeEnum.ALL.getIndex()) {
					if (tmpGroup.getCurrentSale().add(mostReduceMoney).compareTo(allPromTotalPrice) <= 0) {
						// 与canOverlyingGroup中原有的group进行组合形成新的组合
						// newGroup = createNewGroupByCloneOldGroup(tmpGroup, earliestCouponCode,
						// mostReduceMoney,
						// reduceInfo.getContainGoodsCodeList(), reduceInfo.getMallProdVoSaleMap());
					} else {
						// 如果合并后，优惠金额超过了最大的促销金额，也需要调整本优惠券对每件商品的优惠金额
						BigDecimal recalculateReduceMoney = allPromTotalPrice.subtract(tmpGroup.getCurrentSale());
						HashMap<String, BigDecimal> saleMap = reduceInfo.getMallProdVoSaleMap();
						saleMap.entrySet()
								.forEach(entry -> entry.setValue(
										entry.getValue().multiply(recalculateReduceMoney).divide(mostReduceMoney,
												StatusConstants.COUPON_CALCULATE_PRECISION, BigDecimal.ROUND_HALF_UP)));
						// newGroup = createNewGroupByCloneOldGroup(tmpGroup, earliestCouponCode,
						// recalculateReduceMoney,
						// reduceInfo.getContainGoodsCodeList(), reduceInfo.getMallProdVoSaleMap());
					} //
				} else {
					// 与canOverlyingGroup中原有的group进行组合形成新的组合
					// newGroup = createNewGroupByCloneOldGroup(tmpGroup, earliestCouponCode,
					// mostReduceMoney,
					// reduceInfo.getContainGoodsCodeList(), reduceInfo.getMallProdVoSaleMap());
				}

				// 如果有多张，那么尽可能的尝试添加
				newGroupList.add(newGroup);
			}
			couponCodesList.remove(earliestCouponCode);
			returnGroups.addAll(newGroupList);
			totalGroupList.addAll(newGroupList);
			newGroupList.clear();
		}

		return returnGroups;
	}

	/**
	 * 获取指定优惠券用于购物车中商品时的优惠信息详情
	 *
	 * @param couponDto
	 *            优惠券dto
	 * @param goodsVoList
	 *            商品列表
	 * @param platform
	 *            使用平台
	 * @return 折扣信息
	 */
	private ReduceInfo getReduceInfo(CouponDTO couponDto, List<MallProdVo> goodsVoList, String platform) {
		ReduceInfo reduceInfo = new ReduceInfo();

		if (goodsVoList == null || goodsVoList.size() == 0) {
			return reduceInfo;
		}

		// 有一个特别需要注意的地方是，指定优惠券用于指定的商品，而不是整个订单
		String couponPlatform = couponDto.getUsePlatform();
		if (!StatusConstants.UNLIMITED.equals(couponPlatform) && !couponPlatform.contains(platform.toLowerCase())) {
			return reduceInfo;
		}

		List<MallProdVo> prodVoList = new ArrayList<>();// 选择出所有适用于该优惠券的商品列表
		List<String> goodsCodeList = new ArrayList<>();// 选择出所有适用于该优惠券的商品列表
		int couponUseRange = couponDto.getUseRange();
		PreferentialRangeTypeEnum rangeEnum = PreferentialRangeTypeEnum.getEnumByIndex(couponUseRange);
		Assert.notNull(rangeEnum, "优惠券使用范围");
		switch (rangeEnum) {
		case BRANDS:
			for (MallProdVo goodsVO : goodsVoList) {
				if (checkIsInBrands(goodsVO, couponDto.getPreferentialRange().getPromotionBrandList())) {
					prodVoList.add(goodsVO);
					goodsCodeList.add(goodsVO.getGoodsCode());
				}
			}
			break;
		case CLASSES:
		case ZY_CLASSES:
			for (MallProdVo goodsVO : goodsVoList) {
				if (checkIsInClasses(goodsVO, couponDto.getPreferentialRange().getPromotionClassList(),
						couponDto.getEnterpriseId())) {
					prodVoList.add(goodsVO);
					goodsCodeList.add(goodsVO.getGoodsCode());
				}
			}
			break;
		case GOODS:
			for (MallProdVo goodsVO : goodsVoList) {
				if (checkIsInGoods(goodsVO, couponDto.getPreferentialRange().getPromotionGoodsList())) {
					prodVoList.add(goodsVO);
					goodsCodeList.add(goodsVO.getGoodsCode());
				}
			}
			break;
		default:
			for (MallProdVo goodsVO : goodsVoList) {
				goodsCodeList.add(goodsVO.getGoodsCode());
			}
			prodVoList = goodsVoList;
			break;
		}
		if (prodVoList.size() == 0) {
			return reduceInfo;
		}
		reduceInfo.setProdVoList(prodVoList);
		reduceInfo.setContainGoodsCodeList(goodsCodeList);

		BigDecimal totalPrice = BigDecimal.ZERO;
		for (MallProdVo prodVo : prodVoList) {
			if (prodVo.getProdNum() == 0) {// 默认为一件商品
				if (prodVo.getPromPrice() == null) {
					totalPrice = totalPrice.add(prodVo.getPrice());
				} else {
					totalPrice = totalPrice.add(prodVo.getPromPrice());
				}
			} else {
				if (prodVo.getPromTotalPrice() == null) {
					totalPrice = totalPrice.add(prodVo.getTotalPrice());
				} else {
					totalPrice = totalPrice.add(prodVo.getPromTotalPrice());
				}
			}
		}

		int coupontype = couponDto.getCouponType();
		int useRestriction = couponDto.getUseRestriction();

		if (useRestriction == StatusConstants.NO && coupontype == CouponTypeEnum.CASH.getIndex()) {
			if (totalPrice.compareTo(couponDto.getCouponAmount()) >= 0) {// 需要比较totalPrice和CouponAmount，返回较小的那个
				reduceInfo.setMostReduceMoney(couponDto.getCouponAmount());
			} else {
				reduceInfo.setMostReduceMoney(totalPrice);
			}
		} else if (useRestriction == StatusConstants.NO && coupontype == CouponTypeEnum.DISCOUNT.getIndex()) {
			// 折扣是一个小于10的数字. 这是折扣后的价格
			reduceInfo.setMostReduceMoney(FULL_DISCOUNT.subtract(couponDto.getCouponDiscount()).multiply(totalPrice)
					.multiply(StatusConstants.TEN_PERCENT));
		} else if (useRestriction == StatusConstants.YES && coupontype == CouponTypeEnum.CASH.getIndex()) {
			// 如果总金额超过使用条件
			if (totalPrice.compareTo(couponDto.getFullElement()) >= 0) {
				reduceInfo.setMostReduceMoney(couponDto.getCouponAmount());
			}
		} else if (useRestriction == StatusConstants.YES && coupontype == CouponTypeEnum.DISCOUNT.getIndex()) {
			if (totalPrice.compareTo(couponDto.getFullElement()) >= 0) {// 如果总金额超过使用条件
				reduceInfo.setMostReduceMoney(FULL_DISCOUNT.subtract(couponDto.getCouponDiscount()).multiply(totalPrice)
						.multiply(StatusConstants.TEN_PERCENT));
			}
		}

		// 需要一个平摊算法，单个商品的优惠额 = (单个商品的价格/总商品的价格 )*优惠的总额
		HashMap<String, BigDecimal> mallProdVoSaleMap = flatReduceMoneyToPerGoods(reduceInfo.getMostReduceMoney(),
				totalPrice, prodVoList);
		reduceInfo.setMallProdVoSaleMap(mallProdVoSaleMap);
		return reduceInfo;
	}

	/**
	 * 平摊算法，单个商品的优惠额 = (单个商品的价格/总商品的价格 )*优惠的总额
	 *
	 * @param mostReduceMoney
	 *            最多优惠金额
	 * @param totalPrice
	 *            总金额
	 * @param prodVoList
	 *            商品列表
	 * @return GoodsCode, 单件商品的优惠额度map集合
	 */
	private HashMap<String, BigDecimal> flatReduceMoneyToPerGoods(BigDecimal mostReduceMoney, BigDecimal totalPrice,
			List<MallProdVo> prodVoList) {
		if (mostReduceMoney == null || mostReduceMoney.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}
		if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		}

		HashMap<String, BigDecimal> map = new HashMap<>(prodVoList.size());

		BigDecimal perTotalPrice;
		BigDecimal perReduceMoney;
		MallProdVo prodVo;
		BigDecimal reducedMoney = BigDecimal.ZERO;// 累计已经优惠了的金额
		int prodLength = prodVoList.size();
		for (int i = 0; i < prodLength; i++) {
			prodVo = prodVoList.get(i);
			if (prodVo.getPromTotalPrice() == null) {
				perTotalPrice = prodVo.getTotalPrice();
			} else {
				perTotalPrice = prodVo.getPromTotalPrice();
			}
			perReduceMoney = perTotalPrice.multiply(mostReduceMoney).divide(totalPrice,
					StatusConstants.COUPON_CALCULATE_PRECISION, BigDecimal.ROUND_HALF_UP);
			reducedMoney = reducedMoney.add(perReduceMoney);

			if (i == prodLength - 1 && reducedMoney.compareTo(mostReduceMoney) != 0) {
				perReduceMoney = mostReduceMoney.subtract(reducedMoney).add(perReduceMoney);
			}

			map.put(prodVo.getGoodsCode(), perReduceMoney);// 这里得出的优惠金额，在加入组时可能需要再次折算，因为该券的实际总优惠额度降低了
		}

		return map;
	}

	/**
	 * 检测传入的优惠券是否与组合内的已有优惠券有冲突. (如果是多张全场券，是可以叠加的,全场券也只能与全场券叠加)
	 *
	 * @param currentGroup
	 *            当前的组合
	 * @param canUseCoupon
	 *            可用的优惠券，待加入组合
	 * @param reduceInfo
	 *            优惠信息
	 * @return 冲突true
	 */
	private boolean isConflict(CouponGroupDTO currentGroup, CouponDTO canUseCoupon, ReduceInfo reduceInfo) {
		List<CouponCodeVO> couponCodeList = currentGroup.getCouponCodeList();
		int containSameCouponCount = 0;
		String canUseCode = canUseCoupon.getCouponCode();
		int preferenceAllCoupons = 0;// 全场券的数量

		for (CouponCodeVO tmpCouponCode : couponCodeList) {
			// 需要保证这个couponCode的准确
			if (canUseCode.equals(tmpCouponCode.getCouponCode())) {
				containSameCouponCount++;
			}
			if (tmpCouponCode.getUseRange() == PreferentialRangeTypeEnum.ALL.getIndex()) {
				preferenceAllCoupons++; // 全场券叠加时，最多使用三张
			}
		}

		// useLimit, 如果有多张，那么尽可能的尝试添加
		if ((containSameCouponCount + 1) > canUseCoupon.getUseLimit()) { // 已经超过了该优惠券的每订单最多可使用数量
			return true;
		} else if (containSameCouponCount == 0) {
			// 如果都是全场券，那么可以不考虑商品交叉的情况,全场券也只能与全场券进行叠加; 只用计算是否冲突。并且在前端就已经限制了全场券只可能为代金券.
			if (canUseCoupon.getUseRange() == PreferentialRangeTypeEnum.ALL.getIndex()) {
				return (preferenceAllCoupons >= COUPON_ALL_MOST_NUM || preferenceAllCoupons <= 0)
						|| checkPreferenceAllAmountCouponConflict(couponCodeList, canUseCoupon, reduceInfo);
			}

			// 应该是与组合里的券码在没有交叉商品的情况下，可以有多个 折扣券
			List<String> groupGoodsCodeList = currentGroup.getContainGoodsCodeList();
			List<String> canUseGoodsCodeList = reduceInfo.getContainGoodsCodeList();
			for (String goodsCode : canUseGoodsCodeList) {
				if (groupGoodsCodeList.contains(goodsCode)) {
					return true; // 但是如果有交叉商品了，那么就算是有冲突。
				}
			}
			return false;
		} else { // 计算叠加后是否还能满足使用条件
			return checkOverlyingSameCoupon(canUseCoupon, reduceInfo, containSameCouponCount);
		}
	}

	/**
	 * 如果用户领取了同一种可叠加的优惠券，那么检测是否在这笔订单内可以多次叠加这一张
	 */
	private boolean checkOverlyingSameCoupon(CouponDTO canUseCoupon, ReduceInfo reduceInfo,
			int containSameCouponCount) {
		int useRestriction = canUseCoupon.getUseRestriction();
		if (useRestriction == StatusConstants.NO) {
			return false; // 如果没有满减限制，那么则不冲突
		}

		BigDecimal totalPrice = BigDecimal.ZERO;
		List<MallProdVo> prodVoList = reduceInfo.getProdVoList();
		for (MallProdVo prodVo : prodVoList) {
			if (prodVo.getProdNum() == 0) {// 默认为一件商品
				if (prodVo.getPromPrice() == null) {
					totalPrice = totalPrice.add(prodVo.getPrice());
				} else {
					totalPrice = totalPrice.add(prodVo.getPromPrice());
				}
			} else {
				if (prodVo.getPromTotalPrice() == null) {
					totalPrice = totalPrice.add(prodVo.getTotalPrice());
				} else {
					totalPrice = totalPrice.add(prodVo.getPromTotalPrice());
				}
			}
		}

		int coupontype = canUseCoupon.getCouponType();
		if (useRestriction == StatusConstants.YES && coupontype == CouponTypeEnum.CASH.getIndex()) {
			BigDecimal reduceCount = canUseCoupon.getCouponAmount()
					.multiply(new BigDecimal(containSameCouponCount + 1));
			BigDecimal reduced = totalPrice.subtract(reduceCount); // 总金额减去优惠后的价格得出剩余的金额

			return !(canUseCoupon.getFullElement().compareTo(reduced) >= 0);
		}

		if (useRestriction == StatusConstants.YES && coupontype == CouponTypeEnum.DISCOUNT.getIndex()) {
			BigDecimal priceAfterDiscount = totalPrice;
			BigDecimal tmpDiscount = canUseCoupon.getCouponDiscount();
			for (int i = 0; i < (containSameCouponCount + 1); i++) {
				priceAfterDiscount = priceAfterDiscount.multiply(tmpDiscount).multiply(StatusConstants.TEN_PERCENT); // 多张折扣券叠加，规则为折上折
			}

			return !(priceAfterDiscount.compareTo(canUseCoupon.getFullElement()) >= 0);
		}

		return true;
	}

	/**
	 * 检查全场可用的代金券，是否与现有组内的券码冲突
	 */
	private boolean checkPreferenceAllAmountCouponConflict(List<CouponCodeVO> couponCodeList, CouponDTO canUseCoupon,
			ReduceInfo reduceInfo) {
		List<MallProdVo> productList = reduceInfo.getProdVoList();
		BigDecimal allPromTotal = BigDecimal.ZERO; // 全部商品的促销总价格之和
		for (MallProdVo productVo : productList) {
			if (productVo.getPromTotalPrice() == null) {
				allPromTotal = allPromTotal.add(productVo.getTotalPrice());
			} else {
				allPromTotal = allPromTotal.add(productVo.getPromTotalPrice());
			}
		}

		BigDecimal groupCouponAmount = BigDecimal.ZERO; // 计算原有的每张全场券的代金券的额度之和
		for (CouponCodeVO couponCode : couponCodeList) {
			groupCouponAmount = groupCouponAmount.add(couponCode.getCouponAmount());
		}

		BigDecimal currentCouponAmount = canUseCoupon.getCouponAmount();
		BigDecimal totalReduced = groupCouponAmount.add(currentCouponAmount);
		BigDecimal shouldPay = allPromTotal.subtract(totalReduced);

		for (CouponCodeVO couponCode : couponCodeList) {
			if (couponCode.getUseRestriction() == StatusConstants.YES
					&& couponCode.getFullElement().compareTo(shouldPay) > 0) {
				// 如果优惠后的金额达不到之前的优惠券的满减要求，那么表示有冲突
				return true;
			}
		}
		// 如果优惠后的金额达不到之前的优惠券的满减要求，那么表示有冲突
		return canUseCoupon.getUseRestriction() == StatusConstants.YES
				&& canUseCoupon.getFullElement().compareTo(shouldPay) > 0;
	}

	/**
	 * 是否已经有相同的组
	 */
	private boolean hasSameGroup(CouponGroupDTO tmpGroup, CouponCodeVO earliestCouponCode,
			List<CouponGroupDTO> totalGroupList) {
		boolean hasSameGroup = false;
		// 如果这个想要新加的券码组合在之前已经存在过了，那么就不再考虑新建
		for (CouponGroupDTO innerGroup : totalGroupList) {
			List<CouponCodeVO> innerCodeList = innerGroup.getCouponCodeList();
			List<CouponCodeVO> tmpCodeList = tmpGroup.getCouponCodeList();

			if (innerCodeList.size() == tmpCodeList.size() + 1) {
				List<Integer> innerCouponIdList = new ArrayList<>(innerCodeList.size());
				List<Integer> tmpCouponIdList = new ArrayList<>(tmpCodeList.size());
				for (CouponCodeVO vo : innerCodeList) {
					innerCouponIdList.add(vo.getCouponId());
				}
				for (CouponCodeVO vo : tmpCodeList) {
					tmpCouponIdList.add(vo.getCouponId());
				}
				tmpCouponIdList.add(earliestCouponCode.getCouponId());

				if (innerCouponIdList.containsAll(tmpCouponIdList)) {
					hasSameGroup = true;
				}
			}
		}
		return hasSameGroup;
	}

	/*
	 * private CouponGroupDTO createNewGroupByCloneOldGroup(CouponGroupDTO
	 * existedGroup, CouponCodeVO earliestCouponCode, BigDecimal mostReduceMoney,
	 * List<String> containGoodsCodeList, HashMap<String, BigDecimal>
	 * mallProdVoSaleMap) { CouponGroupDTO newGroup = null; try { newGroup =
	 * existedGroup.clone();// 复制一个原有的group //
	 * 在新的组合里,取出canUseCoupon中最快到达结束使用时间的那张券码,和这一个组成一个新的组合
	 * newGroup.addCouponCode(earliestCouponCode, mostReduceMoney,
	 * mallProdVoSaleMap); // 没有体现出折上折，这里只是单纯的金额累加 //
	 * newGroup.setCurrentSale(mostReduceMoney.add(newGroup.getCurrentSale()));
	 * 
	 * List<String> oldGoodCodeList = newGroup.getContainGoodsCodeList();
	 * oldGoodCodeList.removeAll(containGoodsCodeList);
	 * oldGoodCodeList.addAll(containGoodsCodeList);
	 * 
	 * int timeCompare =
	 * earliestCouponCode.getAvailableEndTime().compareTo(newGroup.
	 * getMinAvailableEndTime()); if (timeCompare < 0) {
	 * newGroup.setMinAvailableEndTime(earliestCouponCode.getAvailableEndTime()); }
	 * } catch (CloneNotSupportedException e) { logger.error("克隆CouponGroupDTO异常",
	 * e); }
	 * 
	 * return newGroup; }
	 */

	/**
	 * 如果不能叠加，那么用单个券码作为一个组. 再成为组之前需要检测这张券的使用条件是否满足.
	 */
	private List<CouponGroupDTO> createGroupsByCanUseUnOvlyingCoupon(CouponDTO canUseCoupon,
			List<MallProdVo> goodsVoList, String platform) {
		List<CouponGroupDTO> groupList = new ArrayList<>();
		// 算出该优惠券能减少的最大的价格
		ReduceInfo reduceInfo = getReduceInfo(canUseCoupon, goodsVoList, platform);
		BigDecimal tmpReduceMoney = reduceInfo.getMostReduceMoney();
		if (tmpReduceMoney == null || tmpReduceMoney.compareTo(BigDecimal.ZERO) <= 0) {
			return groupList;
		}

		CouponCodeVO earlistCode = getEarliestReceivedCouponCode(canUseCoupon);
		if (earlistCode == null) {
			return groupList;
		}
		CouponGroupDTO tmpGroup = new CouponGroupDTO();
		// tmpGroup.addCouponCode(earlistCode, tmpReduceMoney,
		// reduceInfo.getMallProdVoSaleMap());
		// tmpGroup.setCurrentSale(tmpReduceMoney);
		tmpGroup.setOverlying(StatusConstants.NO);
		tmpGroup.setMinAvailableEndTime(earlistCode.getAvailableEndTime());
		tmpGroup.setContainGoodsCodeList(reduceInfo.getContainGoodsCodeList());
		groupList.add(tmpGroup);

		return groupList;
	}

	/**
	 * 最后根据优惠金额大小，最小的使用结束时间,确定各个组合的优先级 ; 优惠力度越大, 优先级越高,结束使用时间越小，优先级越高,priority数字越小;
	 */
	private CouponGroupDTO sortGroupList(List<CouponGroupDTO> groupList) {
		return groupList.stream().sorted((g1, g2) -> {
			// 结束使用时间越小，优先级越高
			int saleCompare = g2.getCurrentSale().compareTo(g1.getCurrentSale());
			if (saleCompare != 0)
				return saleCompare;

			return g1.getMinAvailableEndTime().compareTo(g2.getMinAvailableEndTime());
		}).findFirst().orElse(null);
	}

	/**
	 * 根据优惠券的使用范围,过滤掉不能使用在这一批商品上的优惠券
	 */
	private List<CouponDTO> filterCannotUseCouponDto(List<CouponDTO> couponDtoList, List<MallProdVo> goodsVoList,
			String platform) {
		List<CouponDTO> resultList = new ArrayList<>();
		String couponPlatform;
		boolean isInRange = false;
		for (CouponDTO couponDto : couponDtoList) {
			couponPlatform = couponDto.getUsePlatform();
			if (!StatusConstants.UNLIMITED.equals(couponPlatform) && !couponPlatform.contains(platform.toLowerCase())) {
				continue;
			}
			PreferentialRangeTypeEnum preferentialRangeTypeEnum = PreferentialRangeTypeEnum
					.getEnumByIndex(couponDto.getUseRange());
			Assert.notNull(preferentialRangeTypeEnum, "优惠券使用范围");

			switch (preferentialRangeTypeEnum) {
			case BRANDS:
				for (MallProdVo goodsVO : goodsVoList) {
					isInRange = checkIsInBrands(goodsVO, couponDto.getPreferentialRange().getPromotionBrandList());
					if (isInRange) {
						break;
					}
				}
				break;
			case CLASSES:
			case ZY_CLASSES:
				for (MallProdVo goodsVO : goodsVoList) {
					isInRange = checkIsInClasses(goodsVO, couponDto.getPreferentialRange().getPromotionClassList(),
							couponDto.getEnterpriseId());
					if (isInRange) {
						break;
					}
				}
				break;
			case GOODS:
				for (MallProdVo goodsVO : goodsVoList) {
					isInRange = checkIsInGoods(goodsVO, couponDto.getPreferentialRange().getPromotionGoodsList());
					if (isInRange) {
						break;
					}
				}
				break;
			default:
				isInRange = true;
			}

			if (isInRange) {
				resultList.add(couponDto);
			}
		}
		return resultList;
	}

	class ReduceInfo {
		private List<String> containGoodsCodeList;

		private BigDecimal mostReduceMoney = BigDecimal.ZERO;

		private List<MallProdVo> prodVoList = new ArrayList<>();

		// Key为goodcode,value为每个商品优惠的价格
		private HashMap<String, BigDecimal> mallProdVoSaleMap = new HashMap<>();

		List<String> getContainGoodsCodeList() {
			return containGoodsCodeList;
		}

		private void setContainGoodsCodeList(List<String> containGoodsCodeList) {
			this.containGoodsCodeList = containGoodsCodeList;
		}

		BigDecimal getMostReduceMoney() {
			return mostReduceMoney;
		}

		private void setMostReduceMoney(BigDecimal mostReduceMoney) {
			this.mostReduceMoney = mostReduceMoney;
		}

		private List<MallProdVo> getProdVoList() {
			return prodVoList;
		}

		private void setProdVoList(List<MallProdVo> prodVoList) {
			this.prodVoList = prodVoList;
		}

		HashMap<String, BigDecimal> getMallProdVoSaleMap() {
			return mallProdVoSaleMap;
		}

		private void setMallProdVoSaleMap(HashMap<String, BigDecimal> mallProdVoSaleMap) {
			this.mallProdVoSaleMap = mallProdVoSaleMap;
		}
	}

	void releaseOneCouponCode(Integer couponId, String enterpriseCode) {
		couponDao.releaseOneCouponCode(couponId, enterpriseCode);
	}

	public List<StoreCouponVO> getStoreCouponListByMap(Map<String, Object> map) {
		return couponDao.getStoreCouponListByMap(map);
	}

	public int getStoreCouponCountByMap(Map<String, Object> map) {
		return couponDao.getStoreCouponCountByMap(map);
	}

	/**
	 * @param enterpriseCode
	 *            企业code
	 * @param grantModel
	 *            优惠券发放方式
	 * @param otherModelCode
	 *            如果被其他模块，比如抽奖使用需要填写
	 * @return 可用的优惠券vo list
	 */
	public List<CouponVO> getCouponListByGrantMode(String enterpriseCode, Integer grantModel, String otherModelCode) {
		List<Integer> auditorStatusList = new ArrayList<>(1);
		Integer inActivityStatus = ActivityAuditorStatusEnum.IN_ACTIVITY.getIndex();
		auditorStatusList.add(inActivityStatus);// 活动中

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enterpriseCode", enterpriseCode);
		map.put("auditorStatusList", auditorStatusList);
		map.put("grantMode", grantModel);
		map.put("otherModelCode", otherModelCode);

		// 需要返回活动优惠券和vip list
		List<CouponActivityDTO> activityList = couponActivityService.getDtoList(map);
		if (activityList == null || activityList.size() == 0) {
			return null;
		}

		List<CouponVO> result = new ArrayList<>();
		List<CouponVO> couponList;
		List<String> enterpriseCodeList = new ArrayList<>(1);
		enterpriseCodeList.add(enterpriseCode);
		List<CouponVO> canReceiveAgainList;
		for (CouponActivityDTO activityDto : activityList) {
			couponList = couponDao.getVOListByCouponActivityModelInfo(activityDto.getCouponActivityId(),
					CouponModelNameEnum.COUPON_ACTIVITY.getModelCode(), null, null, enterpriseCodeList);
			// 分别检验每张券用户是否还可以领取
			canReceiveAgainList = filterCanNotReceiveAgainCouponVo(couponList, null,
					activityDto.getReceiveFrequencyType(), activityDto.getReceiveFrequency());
			result.addAll(canReceiveAgainList);
		}
		return result;
	}

	public List<CouponDTO> getEnterpriseReleasedCouponDtoList(String enterpriseCode, Integer grantMode, Integer userId,
			Integer vipId, String userLevelId, List<Integer> auditorStatusList) {
		Map<String, Object> map = new HashMap<>();
		map.put("enterpriseCode", enterpriseCode);
		map.put("auditorStatusList", auditorStatusList);
		map.put("grantMode", grantMode);

		// 需要返回活动优惠券和vip list
		List<CouponActivityDTO> activityList = couponActivityService.getDtoList(map);
		if (activityList == null || activityList.size() == 0) {
			return null;
		}

		List<CouponReceiveRecordDTO> receivedCouponList = null;
		Map<Integer, List<Date>> receiveRecordMap = new HashMap<>();
		// 之后考虑改为map<couponId,List<Date>>
		if (userId != null || vipId != null) {
			receivedCouponList = couponDao.getReceivedRecordDTOListByUserInfo(enterpriseCode, userId, vipId);
			if (receivedCouponList != null) {
				for (CouponReceiveRecordDTO record : receivedCouponList) {
					receiveRecordMap.put(record.getCouponId(), record.getReceiveDateList());
				}
			}
		}

		List<CouponDTO> result = new ArrayList<>();
		List<CouponDTO> canReceiveAgainList;
		boolean check;
		List<CouponDTO> couponDtoList;
		List<String> enterpriseCodeList = new ArrayList<>(1);
		enterpriseCodeList.add(enterpriseCode);
		CouponActivityVO couponActivityVO;
		for (CouponActivityDTO activityDto : activityList) {
			check = (userId == null && vipId == null) || checkUserJoinActivity(activityDto, userId, userLevelId, vipId);
			if (check) {
				couponDtoList = couponDao.getCouponDTOListWithPromotion(activityDto.getCouponActivityId(),
						CouponModelNameEnum.COUPON_ACTIVITY.getModelCode(), enterpriseCodeList);
				if (couponDtoList == null || couponDtoList.size() == 0) {
					continue;
				}

				// 分别检验每张券用户是否还可以领取
				canReceiveAgainList = filterCanNotReceiveAgainCouponDto(couponDtoList, receiveRecordMap,
						activityDto.getReceiveFrequencyType(), activityDto.getReceiveFrequency());
				if (canReceiveAgainList == null || canReceiveAgainList.size() == 0) {
					continue;
				}
				couponActivityVO = new CouponActivityVO();
				couponActivityVO.setStartTime(activityDto.getStartTime());
				couponActivityVO.setEndTime(activityDto.getEndTime());

				for (CouponDTO tmpDto : canReceiveAgainList) {
					tmpDto.setCouponActivityVO(couponActivityVO);
				}

				result.addAll(canReceiveAgainList);
			}
		}

		return result;
	}

	/**
	 * 完全拷贝自filterCanNotReceiveAgainCouponVo,但是因为 传入的对象继承关系被解除，无法共用一套代码
	 */
	private List<CouponDTO> filterCanNotReceiveAgainCouponDto(List<CouponDTO> couponList,
			Map<Integer, List<Date>> receiveRecordMap, Integer receiveFrequencyType, Integer receiveFrequency) {
		if (couponList == null || couponList.size() == 0) {
			return couponList;
		}

		List<CouponDTO> canReceiveAgainCouponList = new ArrayList<>();
		boolean notReceivedBefore = receiveRecordMap == null || receiveRecordMap.size() == 0;
		boolean couponReceiveUnlimited = receiveFrequencyType == ReceiveFrequencyTypeEnum.UNLIMITED.getIndex();

		String availableEndTime;
		Date availableEndDate;
		Date now = new Date();

		if (notReceivedBefore || couponReceiveUnlimited) {
			for (CouponDTO coupon : couponList) {
				availableEndTime = coupon.getAvailableEndTime();
				boolean canReceive = coupon.getReceivedCouponNum() < coupon.getCouponNum()
						&& coupon.getAuditorStatus() != CouponAuditorStatusEnum.RECEIVE_END.getIndex();
				if (!StringUtils.isEmpty(availableEndTime)) {
					availableEndDate = DateUtils.parseDateTime(availableEndTime);
					canReceive = canReceive && availableEndDate.after(now);
				}

				if (canReceive) {
					canReceiveAgainCouponList.add(coupon);// 判断优惠券是否还有剩余
				}
			}
			return canReceiveAgainCouponList;
		}

		List<Date> receiveDateList;
		if (receiveFrequencyType == ReceiveFrequencyTypeEnum.PER_MAN.getIndex()) {
			for (CouponDTO coupon : couponList) {
				boolean canNotReceive = coupon.getReceivedCouponNum() >= coupon.getCouponNum()
						|| coupon.getAuditorStatus() == CouponAuditorStatusEnum.RECEIVE_END.getIndex();

				availableEndTime = coupon.getAvailableEndTime();
				if (!StringUtils.isEmpty(availableEndTime)) {
					availableEndDate = DateUtils.parseDateTime(availableEndTime);
					canNotReceive = canNotReceive || availableEndDate.before(now);
				}
				if (canNotReceive) {
					// 判断优惠券是否还有剩余
					continue;
				}

				receiveDateList = receiveRecordMap.get(coupon.getId());// 判断累计领取次数是否大于receiveFrequency
				if (receiveDateList == null || receiveDateList.size() < receiveFrequency) {
					canReceiveAgainCouponList.add(coupon);
				}
			}
			return canReceiveAgainCouponList;
		}

		if (receiveFrequencyType == ReceiveFrequencyTypeEnum.PER_DAY_MAN.getIndex()) {
			int todayReceiveCount = 0;
			Date todayBegin = DateUtils.getTodayBegin();
			for (CouponDTO coupon : couponList) {
				boolean canNotReceive = coupon.getReceivedCouponNum() >= coupon.getCouponNum()
						|| coupon.getAuditorStatus() == CouponAuditorStatusEnum.RECEIVE_END.getIndex();

				availableEndTime = coupon.getAvailableEndTime();
				if (!StringUtils.isEmpty(availableEndTime)) {
					availableEndDate = DateUtils.parseDateTime(availableEndTime);
					canNotReceive = canNotReceive || availableEndDate.before(now);
				}
				if (canNotReceive) {
					// 判断优惠券是否还有剩余
					continue;
				}

				todayReceiveCount = 0;
				receiveDateList = receiveRecordMap.get(coupon.getId());
				// 判断今天的领取数是否小于receiveFrequency
				if (receiveDateList == null || receiveDateList.size() == 0) {
					canReceiveAgainCouponList.add(coupon);
					continue;
				}

				for (Date receiveDate : receiveDateList) {
					if (receiveDate.after(todayBegin)) {
						todayReceiveCount++;
					}
				}
				if (todayReceiveCount < receiveFrequency) {
					canReceiveAgainCouponList.add(coupon);
				}
			}
			return canReceiveAgainCouponList;
		}

		return null;
	}

	public List<CouponActivityDTO> getOtherModelAcceptCouponActivityList(String enterpriseCode, int grantModel,
			String otherModelCode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enterpriseCode", enterpriseCode);
		map.put("auditorStatusList", Collections.singletonList(ActivityAuditorStatusEnum.IN_ACTIVITY.getIndex()));
		map.put("grantMode", grantModel);
		map.put("otherModelCode", otherModelCode);
		map.put("couponModelCode", CouponModelNameEnum.COUPON_ACTIVITY.getModelCode());

		return couponActivityService.getDtoListWithoutPreferential(map);
	}

	/**
	 * 查询优惠范围包含了【指定的商品】并且是商品券的优惠券
	 */
	public List<CouponVO> getCouponListByBoundGoods(String goodsCode, String enterpriseCode,
			List<Integer> auditorStatus) {
		Assert.notNull(goodsCode, "商品编码");
		Assert.notNull(enterpriseCode, "企业编码");
		Assert.notEmpty(auditorStatus, "优惠券审核状态");

		// List<Integer> auditorStatus = new ArrayList<>(2);
		// auditorStatus.add(CouponAuditorStatusEnum.AUDIT_PASS_WAIT_GRANT.getIndex());
		// auditorStatus.add(CouponAuditorStatusEnum.ALREADY_MADE_COUPON_CODE.getIndex());
		// auditorStatus.add(CouponAuditorStatusEnum.RECEIVING.getIndex());

		return couponDao.getCouponListByBoundGoods(goodsCode, enterpriseCode, auditorStatus);
	}

	public List<CouponDTO> getDtoListByPrimaryKeySet(Set<Integer> couponIdSet, String enterpriseCode,
			Integer searchDeleted) {
		if (couponIdSet == null || couponIdSet.size() == 0) {
			return null;
		}
		return couponDao.getDtoListByPrimaryKeySet(couponIdSet, enterpriseCode, searchDeleted);
	}

	public List<CouponReceiveRecordDTO> getReceivedRecordDTOListByUserInfo(String enterpriseCode, Integer userId,
			Integer vipId) {
		return couponDao.getReceivedRecordDTOListByUserInfo(enterpriseCode, userId, vipId);
	}

	/**
	 * 设置发放方式
	 * 
	 * @param couponDTO
	 * @throws MarketingCenterException
	 */
	public void setGrantModelName(CouponDTO couponDTO) throws MarketingCenterException {
		// 判断如果是优惠券活动就二次查询获取活动发放方式
		switch (CouponModelNameEnum.getEnumByModelCode(couponDTO.getModelCode())) {
		case GIFT:
			// 赠品可能被拿去作为 订单返利的返利单位，所以需要查询赠品的最后去向
			Promotional promotional = promotionalService.getPromotionalByCouponOfGiftId(couponDTO.getModelTargetId(),
					couponDTO.getEnterpriseCode());
			switch (SaleTypeEnum.getEnumByIndex(promotional.getSaleType())) {
			case BUY_PRESENT:
				// 商品买赠
				couponDTO.setGrantModelName(CouponModelNameEnum.GIFT.getModelName());
				break;
			case REBATE:
				couponDTO.setGrantModelName(CouponModelNameEnum.REBATE.getModelName());
				break;
			default:
				logger.error("无效的促销活动销售类型:" + promotional.getSaleType());
				break;
			}
			break;
		case COUPON_ACTIVITY:
			// 所属优惠券活动---查询优惠券发放的方式 领取限制
			CouponActivityVO couponActivityVO = couponActivityService.getActivityGrantModelAndReceiveFrequency(
					couponDTO.getModelTargetId(), Arrays.asList(couponDTO.getEnterpriseCode()));
			if (couponActivityVO != null) {
				couponDTO.setGrantModelName(GrantModeStatusEnum.getModeName(couponActivityVO.getGrantMode()));
				couponDTO.setReceiveFrequencyType(couponActivityVO.getReceiveFrequencyType());
				couponDTO.setReceiveFrequencyTypeName(
						ReceiveFrequencyTypeEnum.getFrequencyTypeName(couponActivityVO.getReceiveFrequencyType()));
				couponDTO.setReceiveFrequency(couponActivityVO.getReceiveFrequency());
			}

			break;
		case REBATE:
			couponDTO.setGrantModelName(CouponModelNameEnum.getModelName(couponDTO.getModelCode()));
			break;
		default:
			logger.error("未知的绑定优惠券的模块编码:" + couponDTO.getModelCode());
			throw new MarketingCenterException("未知的绑定优惠券的模块编码:" + couponDTO.getModelCode());
		}

	}

}
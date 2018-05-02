package com.suneee.marketingcenter.marketingcenter.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CouponSequenceGenerator extends SequenceGenerator {

	private static Map<Integer, Integer> modMap = new HashMap<>();

	/**
	 * 从几开始有选择性的查询
	 */
	private static final int START_SKIP_INDEX = 2;
	/**
	 * 每次跳过的数量乘积因子,后一个跳过幅度=前一个跳过幅度 * START_SKIP_FREQUENCE
	 */
	private static final int START_SKIP_FREQUENCE = 2;

	/**
	 * 以这个数的跳跃幅度作为最大跳跃数
	 */
	private static final int MOST_SKIP_INDEX = 8;

	static {

		int startNum = 1;
		for (int i = 1; i <= MOST_SKIP_INDEX; i++) {
			if (i < START_SKIP_INDEX) {
				modMap.put(i, 1);
				continue;
			}
			startNum = startNum * START_SKIP_FREQUENCE;
			modMap.put(i, startNum);
		}
	}

	@Override
	protected List<int[]> filterRule(List<int[]> tmpList) {
		if (tmpList == null)
			return null;
		List<int[]> result = new ArrayList<>();
		int modelNum;
		for (int i = 0; i < tmpList.size(); i++) {
			if (modMap.get(tmpList.get(i)[0]) == null) {
				modelNum = modMap.get(MOST_SKIP_INDEX);
			} else {
				modelNum = modMap.get(tmpList.get(i)[0]);
			}

			if (i % modelNum == 0) {
				result.add(tmpList.get(i));
			}
		}
		return result;
	}

	/*
	public static void main(String[] args) {
		SequenceGenerator demo = new CouponSequenceGenerator();
		List<int[]> list = demo.getSequences(10, 4);
		demo.showResult(list);
		List<int[]> list2 = demo.getSequences(10, 4);
		demo.showResult(list2);
	}
	*/
}

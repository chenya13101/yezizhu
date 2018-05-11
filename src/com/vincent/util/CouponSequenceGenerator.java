package com.vincent.util;

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
	public List<int[]> filterRule(List<int[]> tmpList) {
		if (tmpList == null)
			return null;
		List<int[]> result = new ArrayList<>();
		int modelNum;
		int previousFirstNum = 0;
		int currentFirstNum;
		for (int i = 0; i < tmpList.size(); i++) {
			currentFirstNum = tmpList.get(i)[0];
			if (currentFirstNum != previousFirstNum) {
				result.add(tmpList.get(i));
				previousFirstNum = currentFirstNum;// 以不同数字为数组第一个元素的，第一个序列设置为默认可以添加
				continue;
			}

			if (modMap.get(currentFirstNum) == null) {
				modelNum = modMap.get(MOST_SKIP_INDEX);
			} else {
				modelNum = modMap.get(currentFirstNum);
			}

			if (i % modelNum == 0) {
				result.add(tmpList.get(i));
			}
			previousFirstNum = currentFirstNum;
		}
		return result;
	}

	public static void main(String[] args) {
		SequenceGenerator demo = new CouponSequenceGenerator();
		List<int[]> list = demo.getSequences(3, 3);
		demo.showResult(list);
		// List<int[]> list2 = demo.getSequences(10, 4);
		// demo.showResult(list2);
	}

}

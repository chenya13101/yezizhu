package com.vincent.util;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

public class SequenceGenerator {

	private static Map<Integer, Map<Integer, List<int[]>>> sequenceMap = new HashMap<>();

	/**
	 * 
	 * @param recordNums
	 *            总共拥有的记录条数
	 * @param arrayLength
	 *            预期的结果数组的长度
	 * @return
	 */
	public List<int[]> getSequences(int recordNums, int arrayLength) {
		if (arrayLength > recordNums)
			throw new RuntimeException("数组长度不能超过记录数");
		Map<Integer, List<int[]>> recordNumMap = sequenceMap.get(recordNums);
		if (recordNumMap != null && recordNumMap.get(arrayLength) != null) {
			return recordNumMap.get(arrayLength);
		}

		List<Integer> recordArray = IntStream.rangeClosed(1, recordNums).boxed().collect(toList());
		List<int[]> tmpList = init(recordNums);// [1],[2],[3],[4],[5]
		for (int currentLenth = 2; currentLenth <= arrayLength; currentLenth++) {
			tmpList = getMapList(tmpList, currentLenth, recordArray);
		}
		List<int[]> result = filterRule(tmpList);

		// 缓存结果集
		Map<Integer, List<int[]>> recordNumMap2 = new HashMap<>();
		recordNumMap2.put(arrayLength, result);
		sequenceMap.put(recordNums, recordNumMap2);

		return result;
	}

	/**
	 * 数组映射，加长一位
	 * 
	 * @param intArrayList
	 *            例如[1],[2],[3]
	 * @param targetLength
	 *            例如为2
	 * @param recordArray
	 *            <1,2,3>
	 * @return <[1,2],[1,3],[2,1],[2,3],[3,1],[3,2]>
	 */
	private List<int[]> getMapList(List<int[]> intArrayList, int targetLength, List<Integer> recordArray) {
		if (intArrayList == null) {
			return null;
		}

		List<List<int[]>> resultList = intArrayList.stream().map(tmpIntArray -> {
			List<Integer> remainNumList = recordArray.stream().filter((record) -> {
				for (int i = 0; i < tmpIntArray.length; i++) {
					if (tmpIntArray[i] == record) {
						return false;
					}
				}
				return true;
			}).collect(toList());

			int resultLength = recordArray.size() - tmpIntArray.length;
			List<int[]> returnList = new ArrayList<>(resultLength);
			int[] targetArray;
			for (Integer remainNum : remainNumList) {
				targetArray = new int[targetLength];
				System.arraycopy(tmpIntArray, 0, targetArray, 0, tmpIntArray.length);
				targetArray[targetLength - 1] = remainNum;
				returnList.add(targetArray);
			}

			return returnList;
		}).collect(toList());

		List<int[]> result = new ArrayList<>();
		resultList.forEach(l -> result.addAll(l));
		return result;
	}

	/**
	 * 可以自定义一些过滤规则
	 * 
	 * @param resultList
	 * @return
	 */
	protected List<int[]> filterRule(List<int[]> tmpList) {
		return tmpList;
	}

	/**
	 * 
	 * @param ints
	 * @return
	 */
	private List<int[]> init(int recordNums) {
		return IntStream.rangeClosed(1, recordNums).boxed().map(a -> new int[] { a }).collect(toList());
	}

	protected void showResult(List<int[]> result) {
		if (result == null)
			return;
		int nums = 0;
		for (int[] tmpIntArray : result) {
			System.out.print(++nums + " : ");//
			for (int s : tmpIntArray) {
				System.out.print(s);
			}
			System.out.println();
		}
	}

	// public static void main(String[] args) {
	// SequenceGenerator demo = new SequenceGenerator();
	// List<int[]> list = demo.getSequences(10, 4);
	// demo.showResult(list);
	// }

}

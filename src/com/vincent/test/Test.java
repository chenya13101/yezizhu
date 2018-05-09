package com.vincent.test;

import java.util.SortedMap;
import java.util.TreeMap;

public class Test {
	public static void main(String[] args) {
		TreeMap<String, Integer> stepVauleMap = new TreeMap<>();
		stepVauleMap.put("a", 1);
		stepVauleMap.put("b", 2);
		stepVauleMap.put("c", 3);
		stepVauleMap.put("d", 4);

		// 改变C的值，之后的也干掉
		stepVauleMap.put("c", 5);
		stepVauleMap.put("a", 6);
		SortedMap<String, Integer> sortedMap = stepVauleMap.subMap("a", true, "c", true);

		System.out.println("substract返回的对象");
		sortedMap.forEach((key, value) -> System.out.println(key + "," + value));
		System.out.println("原来的stepVauleMap");
		stepVauleMap.forEach((key, value) -> System.out.println(key + "," + value));

	}
}

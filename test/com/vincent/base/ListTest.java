package com.vincent.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vincent.bean.Commodity;

public class ListTest {

	@Test
	public void testContains() {
		List<Commodity> list1 = Arrays.asList(new Commodity("12", null), new Commodity("13", null));
		List<Commodity> list2 = Arrays.asList(new Commodity("12", null));
		Assert.assertEquals(Collections.disjoint(list1, list2), false);
	}
}

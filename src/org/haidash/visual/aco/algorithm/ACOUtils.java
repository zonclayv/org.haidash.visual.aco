package org.haidash.visual.aco.algorithm;

import java.util.List;

public class ACOUtils {

	public static int countNumberEqual(final List<Integer> itemList, final int item) {

		int count = 0;

		for (Integer integer : itemList) {
			if (integer == item) {
				count++;
			}
		}

		return count;
	}
}

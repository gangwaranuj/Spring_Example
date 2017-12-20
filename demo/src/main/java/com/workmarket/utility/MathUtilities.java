package com.workmarket.utility;

import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.sum;

/**
 * Author: rocio
 */
public class MathUtilities {

	private MathUtilities() {
	}

	public static double mean(List<BigDecimal> data) {
		data = CollectionUtilities.filterNull(data);
		if (CollectionUtils.isNotEmpty(data)) {
			double sumOfNumbers = sum(data).doubleValue();
			return sumOfNumbers / data.size();
		}
		return 0;
	}

	public static double median(List<BigDecimal> data) {
		data = CollectionUtilities.filterNull(data);
		if (CollectionUtils.isNotEmpty(data)) {
			Collections.sort(data);
			int middle = data.size() / 2;
			if (data.size() % 2 == 1) {
				return data.get(middle).doubleValue();
			} else {
				return (data.get(middle - 1).doubleValue() + data.get(middle).doubleValue()) / 2.0;
			}
		}
		return 0;
	}

	public static double variance(List<BigDecimal> data) {
		data = CollectionUtilities.filterNull(data);
		if (CollectionUtils.isNotEmpty(data)) {
			double mean = mean(data);
			double temp = 0;
			for (BigDecimal a : data)
				temp += (mean - a.doubleValue()) * (mean - a.doubleValue());
			return temp / data.size();
		}
		return 0;
	}

	public static double stdDeviation(List<BigDecimal> data) {
		data = CollectionUtilities.filterNull(data);
		if(CollectionUtils.isNotEmpty(data)) {
			return Math.sqrt(variance(data));
		}
		return 0;
	}

}

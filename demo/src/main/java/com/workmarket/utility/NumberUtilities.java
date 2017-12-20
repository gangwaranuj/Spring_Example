package com.workmarket.utility;

import com.google.common.base.MoreObjects;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberUtilities {

	private static final Log logger = LogFactory.getLog(NumberUtilities.class);

	private static final RoundingMode CURRENCY_ROUNDING_MODE = RoundingMode.HALF_UP;

	public static BigDecimal defaultValue(BigDecimal value) {
		return defaultValue(value, BigDecimal.ZERO);
	}

	public static Integer defaultValue(Integer value) {
		if (value == null) {
			return 0;
		}
		return value;
	}

	public static BigDecimal defaultValue(BigDecimal value, BigDecimal defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public static Long defaultValue(Long value) {
		return defaultValue(value, 0L);
	}

	public static Long defaultValue(Long value, Long defaultValue) {
		if (value == null) { return defaultValue; }
		return value;
	}

	public static Double defaultValue(Double value) {
		return defaultValue(value, 0D);
	}

	public static Double defaultValue(Double value, Double defaultValue) {
		if (value == null) { return defaultValue; }
		return value;
	}

	public static Integer min(Integer... numbers) {
		Assert.notNull(numbers);
		Assert.noNullElements(numbers);

		Integer min = numbers[0];
		for (int i = 1; i < numbers.length; i++)
			if (min > numbers[i])
				min = numbers[i];
		return min;
	}

	public static Double firstNonZero(Double... numbers) {
		Assert.notNull(numbers);

		for (int i = 0; i < numbers.length; i++)
			if (numbers[i] != null && numbers[i] > 0)
				return numbers[i];
		return 0d;
	}

	public static Double convertBigDecimalToDoubleNullSafe(BigDecimal number) {
		if (number == null)
			return null;

		return number.doubleValue();
	}

	public static BigDecimal convertDoubleToBigDecimalNullSafe(Double number) {
		if (number == null)
			return null;

		return BigDecimal.valueOf(number);
	}

	public static BigDecimal nullSafeAddDoubleToBigDecimal(Double d, BigDecimal bd) {
		if (d == null) d = 0D;
		return MoreObjects.firstNonNull(bd, new BigDecimal(0D)).add(BigDecimal.valueOf(d));
	}

	public static boolean isWithinRange(Number number, Number min, Number max) {
		if (number == null) return false;

		BigDecimal bdNumber = new BigDecimal(number.doubleValue());
		BigDecimal bdMin = (min != null) ? new BigDecimal(min.doubleValue()) : null;
		BigDecimal bdMax = (max != null) ? new BigDecimal(max.doubleValue()) : null;

		if (bdMin != null && bdMax != null)
			return bdMax.compareTo(bdNumber) >= 0 && bdMin.compareTo(bdNumber) <= 0;
		if (bdMin != null)
			return bdMin.compareTo(bdNumber) <= 0;
		if (bdMax != null)
			return bdMax.compareTo(bdNumber) >= 0;
		return true;
	}


	public static Integer getNullSafe(Integer i) {
		if (i == null)
			return Integer.valueOf(0);
		return i;
	}


	public static int intValueNullSafe(Long n) {
		if (n == null)
			return 0;
		return n.intValue();
	}


	public static BigDecimal max(BigDecimal... amounts) {
		Assert.notNull(amounts);
		Assert.notEmpty(amounts);
		Assert.noNullElements(amounts);

		BigDecimal max = amounts[0];
		for (int i = 1; i < amounts.length; i++)
			if (max.compareTo(amounts[i]) < 0)
				max = amounts[i];

		return max;
	}


	public static BigDecimal min(BigDecimal... amounts) {
		Assert.notNull(amounts);
		Assert.notEmpty(amounts);
		Assert.noNullElements(amounts);

		BigDecimal min = amounts[0];
		for (int i = 1; i < amounts.length; i++)
			if (min.compareTo(amounts[i]) > 0)
				min = amounts[i];

		return min;
	}


	public static BigDecimal roundMoney(BigDecimal value) {
		return round(value, 2);
	}

	public static BigDecimal roundHalfUp(BigDecimal value) {
		value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
		return value;
	}

	public static BigDecimal roundHalfDown(BigDecimal value) {
		value = value.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		return value;
	}

	public static BigDecimal round(BigDecimal value, int scale) {
		logger.debug("round: before => " + value);
		value = value.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
		logger.debug("round: after  => " + value);
		return value;
	}


	public static BigDecimal rate(BigDecimal xValue, BigDecimal multiplier, BigDecimal divisor) {
		BigDecimal yValue = BigDecimal.ZERO;
		if (divisor.compareTo(BigDecimal.ZERO) > 0) {
			yValue = xValue.multiply(multiplier, MathContext.DECIMAL32).divide(divisor, MathContext.DECIMAL32);
		}
		return yValue;
	}


	public static BigDecimal rate(BigDecimal xValue, Integer multiplier, Integer divisor) {
		return rate(xValue, BigDecimal.valueOf(multiplier), BigDecimal.valueOf(divisor));
	}


	public static float calculatePercent(float part, float total) {
		return (total > 0F) ? 100F * (part / total) : 0F;
	}


	public static String currency(BigDecimal value) {
		if (value == null) return StringUtils.EMPTY;
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(value);
	}

	public static String percent(BigDecimal value) {
		if (value == null) return StringUtils.EMPTY;
		NumberFormat formatter = NumberFormat.getPercentInstance();
		return formatter.format(value);
	}

	public static String currency(double value) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(value);
	}

	public static String currency(Double value) {
		if (value == null) {
			return StringUtils.EMPTY;
		}
		return currency((double) value);
	}

	/**
	 * Compares many values to one value for equality (null safe)
	 *
	 * @param number
	 * @param nums
	 * @return
	 */
	public static Boolean areAllEqual(Double number, Double... nums) {
		if (number == null || nums == null) return false;
		for (Double num : nums)
			if (!number.equals(num)) return false;
		return true;
	}

	public static <T extends Number & Comparable<? super T>> boolean anyGreaterThan(T limit, T... values) {
		for (T v : values)
			if (v != null && v.compareTo(limit) > 0)
				return true;
		return false;
	}

	public static BigDecimal nullSafeAbs(BigDecimal value) {
		BigDecimal result = BigDecimal.ZERO;
		if (value != null) {
			result = value.abs();
		}
		return result;
	}

	/**
	 * Exponentiation function for integers. Positive exponents only.
	 * @param base
	 * @param exp
	 * @return
	 */
	public static int ipow(int base, int exp) {
		if (exp < 0) return 0;

		int result = 1;
		while (exp != 0) {
			if ((exp & 1) == 1)
				result *= base;
			exp >>= 1;
			base *= base;
		}
		return result;
	}

	/**
	 * Returns true if number (BigDecimal) is positive (>0)
	 * @param number
	 */
	public static boolean isPositive(BigDecimal number) {
		return (number != null && number.compareTo(BigDecimal.ZERO) == 1);
	}

	public static boolean isZero(BigDecimal number) {
		return (number != null && number.compareTo(BigDecimal.ZERO) == 0);
	}

	/**
	 * Returns true if number (BigDecimal) is negative (<0)
	 * @param number
	 */
	public static boolean isNegative(BigDecimal number) {
		return (number != null && number.compareTo(BigDecimal.ZERO) == -1);
	}

	/**
	 * Returns true if number (Integer) is positive (>0)
	 * @param number
	 */
	public static boolean isPositive(Integer number) {
		return (number != null && number.compareTo(0) == 1);
	}

	/**
	 * Returns true if number (Integer) is positive (>0)
	 * @param number
	 */
	public static boolean isPositive(Long number) {
		return (number != null && number.compareTo(0l) == 1);
	}

	public static boolean isNegative(Long number) {
		return (number != null && number.compareTo(0l) == -1);
	}

	public static BigDecimal percentageComplement(BigDecimal total, BigDecimal part) {
		if (total == null || part == null) { return BigDecimal.ZERO; }
		return BigDecimal.valueOf(100).subtract(rate(part, BigDecimal.valueOf(100), total)).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal percentageComplement(int total, int part) {
		if (total == 0) { return BigDecimal.ZERO; }
		return percentageComplement(BigDecimal.valueOf(total), BigDecimal.valueOf(part));
	}

	public static BigDecimal percentage(BigDecimal total, BigDecimal part) {
		if (total == null || part == null) { return BigDecimal.ZERO; }
		return rate(part, BigDecimal.valueOf(100), total).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal percentage(int total, int part) {
		if (total == 0) { return BigDecimal.ZERO; }
		return percentage(BigDecimal.valueOf(total), BigDecimal.valueOf(part));
	}

	public static int safeLongToInt(long l) {
		if (l < 0L) {
			return 0;
		}
		if (l > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int) l;
	}

	public static Long safeLong(final String s) {
		try {
			return Long.valueOf(s);
		} catch (final Exception e) {
			return null;
		}
	}

	public static long milesToKilometers(int miles) {
		return (long) (miles * (float) 1.609344);
	}

	// Fastest way to calculate number length, used for number generation
	// http://stackoverflow.com/questions/1306727/way-to-get-number-of-digits-in-an-int
	public static int getLength(long n) throws NumberFormatException {
		if (n < 0 || n > 9999999999L) {
			throw new NumberFormatException("The number entered, " + n + " is less than 0 or greater than 10 digits.");
		}

		if (n < 100000) {
			// 5 or less
			if (n < 100) {
				// 1 or 2
				if (n < 10)
					return 1;
				else
					return 2;
			} else {
				// 3 or 4 or 5
				if (n < 1000)
					return 3;
				else {
					// 4 or 5
					if (n < 10000)
						return 4;
					else
						return 5;
				}
			}
		} else {
			// 6 or more
			if (n < 10000000) {
				// 6 or 7
				if (n < 1000000)
					return 6;
				else
					return 7;
			} else {
				// 8 to 10
				if (n < 100000000)
					return 8;
				else {
					// 9 or 10
					if (n < 1000000000)
						return 9;
					else
						return 10;
				}
			}
		}
	}


	public static BigDecimal currency(String strAmount) {
		if (StringUtils.isEmpty(strAmount)) {
			return null;
		}

		return new BigDecimal(strAmount).setScale(2, CURRENCY_ROUNDING_MODE);
	}

	public static BigDecimal currency(float amount) {
		return new BigDecimal(amount).setScale(2, CURRENCY_ROUNDING_MODE);
	}

	public static BigDecimal currency(int amount) {
		return new BigDecimal(amount).setScale(2, CURRENCY_ROUNDING_MODE);
	}

	public static Integer safeParseInteger(final String value) {
		try {
			return Integer.parseInt(value);
		} catch (final Exception e) {
			return null;
		}
	}

	public static Double safeParseDouble(final String value) {
		try {
			return Double.parseDouble(value);
		} catch (final Exception e) {
			return null;
		}
	}

	public static Long safeParseLong(final String value) {
		try {
			return Long.parseLong(value);
		} catch (final Exception e) {
			return null;
		}
	}

	public static Long safeIntToLong(final Integer id) {
		if (id != null) {
			return id.longValue();
		}
		return null;
	}
}
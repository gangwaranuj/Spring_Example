package com.workmarket.utility;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(BlockJUnit4ClassRunner.class)
public class NumberUtilitiesTest {

	@Test
	public void defaultValueBigDecimal_Null_Zero() throws Exception {
		Assert.assertEquals(BigDecimal.ZERO, NumberUtilities.defaultValue((BigDecimal) null));
	}

	@Test
	public void defaultValueBigDecimal_SetValue_Same() throws Exception {
		Assert.assertEquals(BigDecimal.TEN, NumberUtilities.defaultValue(BigDecimal.TEN));
	}

	@Test
	public void defaultValueLong_Null_Zero() throws Exception {
		Assert.assertEquals((Object) 0L, NumberUtilities.defaultValue((Long) null));
	}

	@Test
	public void defaultValueLong_SetValue_Same() throws Exception {
		Assert.assertEquals((Object) 10L, NumberUtilities.defaultValue(10L));
	}

	@Test
	public void defaultValueDouble_Null_Zero() throws Exception {
		Assert.assertEquals((Object) 0D, NumberUtilities.defaultValue((Double) null));
	}

	@Test
	public void defaultValueDouble_SetValue_Same() throws Exception {
		Assert.assertEquals((Object) 10D, NumberUtilities.defaultValue(10D));
	}

	@Test
	public void test_min() throws Exception {
		Assert.assertEquals(Integer.valueOf(1), NumberUtilities.min(new Integer[]{2, 4, 2, 3, 1}));
	}

	@Test
	public void test_firstNonZero() {
		Assert.assertEquals(Double.valueOf(5), NumberUtilities.firstNonZero(0d, 0d, 0d, 5D));
		Assert.assertEquals(Double.valueOf(0), NumberUtilities.firstNonZero(0d, 0d, 0d, 0d));
		Assert.assertEquals(Double.valueOf(0), NumberUtilities.firstNonZero(0d, null, 0d, 0d));
	}

	@Test
	public void test_withinRange() throws Exception {
		Assert.assertTrue(NumberUtilities.isWithinRange(4, 1, 10));
		Assert.assertTrue(NumberUtilities.isWithinRange(4.5, 1, 10));
		Assert.assertTrue(NumberUtilities.isWithinRange(4.5, 1.0, 10.0));

		Assert.assertTrue(NumberUtilities.isWithinRange(1, 1, 10));
		Assert.assertTrue(NumberUtilities.isWithinRange(1, 1.0, 10));
		Assert.assertTrue(NumberUtilities.isWithinRange(1.0, 1, 10));
		Assert.assertTrue(NumberUtilities.isWithinRange(1.0, 1.0, 10));

		Assert.assertTrue(NumberUtilities.isWithinRange(10, 1, 10));
		Assert.assertTrue(NumberUtilities.isWithinRange(10, 1, 10.0));
		Assert.assertTrue(NumberUtilities.isWithinRange(10.0, 1, 10));
		Assert.assertTrue(NumberUtilities.isWithinRange(10.0, 1, 10.0));
	}

	@Test
	public void test_withinRangeNoMax() throws Exception {
		Assert.assertTrue(NumberUtilities.isWithinRange(4, 1, null));
		Assert.assertTrue(NumberUtilities.isWithinRange(4.5, 1, null));
		Assert.assertTrue(NumberUtilities.isWithinRange(4.5, 1.0, null));
	}

	@Test
	public void test_withinRangeNoMin() throws Exception {
		Assert.assertTrue(NumberUtilities.isWithinRange(4, null, 10));
		Assert.assertTrue(NumberUtilities.isWithinRange(4.5, null, 10));
		Assert.assertTrue(NumberUtilities.isWithinRange(4.5, null, 10.0));
	}

	@Test
	public void anyGreaterThan() throws Exception {
		Assert.assertFalse(NumberUtilities.anyGreaterThan(0, 0));
		Assert.assertFalse(NumberUtilities.anyGreaterThan(0, -1));
		Assert.assertTrue(NumberUtilities.anyGreaterThan(0, 1));
		Assert.assertTrue(NumberUtilities.anyGreaterThan(0, 0, 1));
	}

	@Test
	public void ipowTest() {
		Assert.assertEquals(NumberUtilities.ipow(2, 0), 1);
		Assert.assertEquals(NumberUtilities.ipow(2, 1), 2);
		Assert.assertEquals(NumberUtilities.ipow(2, 5), 32);
		Assert.assertEquals(NumberUtilities.ipow(-2, 5), -32);
		Assert.assertEquals(NumberUtilities.ipow(-2, -5), 0);
	}

	@Test
	public void convertDoubleToBigDecimalNullSafeTest() {
		Assert.assertEquals(BigDecimal.valueOf(5D), NumberUtilities.convertDoubleToBigDecimalNullSafe(5D));
		Assert.assertEquals(null, NumberUtilities.convertDoubleToBigDecimalNullSafe(null));
	}

	@Test
	public void convertBigDecimalToDoubleNullSafeTest() {
		Assert.assertEquals((Object) 5D, NumberUtilities.convertBigDecimalToDoubleNullSafe(BigDecimal.valueOf(5D)));
		Assert.assertEquals(null, NumberUtilities.convertBigDecimalToDoubleNullSafe(null));
	}

	@Test
	public void nullSafeAddDoubleToBigDecimalTest() {
		Assert.assertEquals(BigDecimal.valueOf(5D), NumberUtilities.nullSafeAddDoubleToBigDecimal(null, new BigDecimal(5D)));
		Assert.assertEquals(BigDecimal.valueOf(5D), NumberUtilities.nullSafeAddDoubleToBigDecimal(5D, null));
		Assert.assertEquals(BigDecimal.valueOf(8D), NumberUtilities.nullSafeAddDoubleToBigDecimal(3D, new BigDecimal(5D)));
	}

	@Test
	public void isPositiveTest() {
		Assert.assertFalse(NumberUtilities.isPositive(BigDecimal.ZERO));
		Assert.assertFalse(NumberUtilities.isPositive(BigDecimal.ONE.negate()));
		Assert.assertTrue(NumberUtilities.isPositive(BigDecimal.ONE.divide(BigDecimal.TEN)));
		Assert.assertFalse(NumberUtilities.isPositive(BigDecimal.ONE.divide(BigDecimal.TEN).negate()));
	}

	@Test
	public void isNegativeTest() {
		Assert.assertFalse(NumberUtilities.isNegative(BigDecimal.ZERO));
		Assert.assertTrue(NumberUtilities.isNegative(BigDecimal.ONE.negate()));
		Assert.assertFalse(NumberUtilities.isNegative(BigDecimal.ONE.divide(BigDecimal.TEN)));
		Assert.assertTrue(NumberUtilities.isNegative(BigDecimal.ONE.divide(BigDecimal.TEN).negate()));
	}

	@Test
	public void percentageComplement_withZero_success() {
		Assert.assertTrue(100.0 == NumberUtilities.percentageComplement(100, 0).doubleValue());
	}

	@Test
	public void percentageComplement_success() {
		Assert.assertEquals(50, NumberUtilities.percentageComplement(100, 50).intValue());
		Assert.assertEquals(50, NumberUtilities.percentageComplement(80, 40).intValue());
		Assert.assertEquals(90, NumberUtilities.percentageComplement(100, 10).intValue());
	}

	@Test
	public void currency_WithNullParam_ReturnEmpty() {
		Assert.assertEquals(StringUtils.EMPTY, NumberUtilities.currency((Double)null));
		Assert.assertNotNull(NumberUtilities.currency(1234));
	}

	@Test
	public void isZero_success() {
		Assert.assertTrue(NumberUtilities.isZero(BigDecimal.ZERO));
	}

	@Test
	public void isZero_withPositiveNumber_success() {
		Assert.assertFalse(NumberUtilities.isZero(BigDecimal.TEN));
	}

	@Test
	public void isZero_withNegativeNumber_success() {
		Assert.assertFalse(NumberUtilities.isZero(BigDecimal.valueOf(-1)));
	}

	@Test
	public void isZero_withNull_success() {
		Assert.assertFalse(NumberUtilities.isZero(null));
	}

	@Test
	public void nullSafeAbs_withNull_success() {
		Assert.assertEquals(BigDecimal.ZERO, NumberUtilities.nullSafeAbs(null));
	}

	@Test
	public void nullSafeAbs_success() {
		Assert.assertEquals(BigDecimal.valueOf(100), NumberUtilities.nullSafeAbs(BigDecimal.valueOf(-100)));
		Assert.assertEquals(BigDecimal.valueOf(100), NumberUtilities.nullSafeAbs(BigDecimal.valueOf(100)));
	}


	@Test
	public void currency_convertStringToBigDecimal_null_returns_null_success() {
		Assert.assertNull(NumberUtilities.currency((String)null));
		Assert.assertNull(NumberUtilities.currency(StringUtils.EMPTY));
	}

	@Test
	public void currency_verify_roundingAndScale_sucess() {
		Assert.assertEquals(new BigDecimal("1.01"), NumberUtilities.currency("1.005"));
		Assert.assertEquals(new BigDecimal("1.05"), NumberUtilities.currency("1.045"));
		Assert.assertEquals(new BigDecimal("1.01"), NumberUtilities.currency(1.01f));
		Assert.assertEquals(new BigDecimal("1.05"), NumberUtilities.currency(1.05f));
		Assert.assertEquals(new BigDecimal("1.00"), NumberUtilities.currency(1));
		Assert.assertEquals(new BigDecimal("2.00"), NumberUtilities.currency(2));
	}
}

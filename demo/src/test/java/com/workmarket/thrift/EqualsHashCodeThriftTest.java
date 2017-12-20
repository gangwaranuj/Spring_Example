package com.workmarket.thrift;

import com.workmarket.testutils.TestReflectionUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;

/*
 * This tests verifies that all of the classes in the com.workmarket.thrift.* package
 * implement equals and hashcode correctly (HINT: they don't)
 */
@RunWith(Parameterized.class)
@Ignore  /// <----- REMOVE THIS TO RUN THIS TEST
public class EqualsHashCodeThriftTest {

	static final String PACKAGE_NAME = "com.workmarket.thrift";

	@Parameter public Class<?> clazz;

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> cases() {
		return TestReflectionUtils.getClasses(PACKAGE_NAME);
	}

	@Test
	public void equals_equalsContract() {
		EqualsVerifier
			.forClass(clazz)
			.suppress(Warning.NONFINAL_FIELDS)
			.verify();
	}
}
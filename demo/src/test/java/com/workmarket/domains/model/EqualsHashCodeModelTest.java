package com.workmarket.domains.model;

import com.workmarket.testutils.TestReflectionUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

/**
 * Created by alejandrosilva on 3/16/15.
 */
@RunWith(Parameterized.class)
@Ignore  /// <----- REMOVE THIS TO RUN THIS TEST
public class EqualsHashCodeModelTest {

	static final String PACKAGE_NAME = "com.workmarket.domains.model";

	@Parameterized.Parameter public Class<?> clazz;

	@Parameterized.Parameters(name = "{index}: {0}")
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

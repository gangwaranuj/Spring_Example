package com.workmarket.service.security;

import com.workmarket.domains.authentication.services.LoggerManager;
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class LoggerManagerTest {
	private LoggerManager loggerManager = new LoggerManager();

	@Before
	public void setup() {
		// since LogManager is static under the covers, need to set back for each run
		loggerManager.setRootLoggerLevel("warn");
		loggerManager.setLoggerLevel("blarg", "warn");
	}

	@Test
	public void setRootLoggerLevel_HappyPath() {
		String result = loggerManager.setRootLoggerLevel("debug");
		assertTrue(result.startsWith("Root logger set to: debug"));
		assertEquals("DEBUG", LogManager.getRootLogger().getLevel().toString());

		result = loggerManager.setRootLoggerLevel("warn");
		assertTrue(result.startsWith("Root logger set to: warn"));
		assertEquals("WARN", LogManager.getRootLogger().getLevel().toString());
	}

	@Test
	public void setRootLoggerLevel_BadLevel() {
		String result = loggerManager.setRootLoggerLevel("blarg");
		assertEquals("Level must be one of: [fatal, error, warn, info, debug, trace]", result);
	}

	@Test
	public void setRootLoggerLevel_NullLevel() {
		String result = loggerManager.setRootLoggerLevel(null);
		assertEquals("Level must be one of: [fatal, error, warn, info, debug, trace]", result);
	}

	@Test
	public void setLoggerLevel_HappyPath() {
		String result = loggerManager.setLoggerLevel("blarg", "debug");
		assertEquals("DEBUG", result);
	}

	@Test
	public void setLoggerLevel_BadLevel() {
		String result = loggerManager.setLoggerLevel("blarg", "flarn");
		assertEquals("Level must be one of: [fatal, error, warn, info, debug, trace]", result);
	}

	@Test
	public void setLoggerLevel_NullLevel() {
		String result = loggerManager.setLoggerLevel("blarg", null);
		assertEquals("Level must be one of: [fatal, error, warn, info, debug, trace]", result);
	}

	@Test
	public void setLoggerLevel_NullPackage() {
		String result = loggerManager.setLoggerLevel(null, null);
		assertEquals("An empty or null package name is not valid.", result);
	}

	@Test
	public void setLoggerLevel_EmptyPackage() {
		String result = loggerManager.setLoggerLevel("", null);
		assertEquals("An empty or null package name is not valid.", result);
	}

	@Test
	public void getLoggerLevel_EffectiveLevel() {
		loggerManager.setRootLoggerLevel("info");
		String result = loggerManager.getLoggerLevel("filth.flarn.filth");
		assertEquals("INFO (effective)", result);
	}

	@Test
	public void getLoggerLevel_Explicit() {
		loggerManager.setLoggerLevel("blarg", "info");
		String result = loggerManager.getLoggerLevel("blarg");
		assertEquals("INFO", result);
	}

	@Test
	public void getLoggerLevel_NullPackage() {
		String result = loggerManager.getLoggerLevel(null);
		assertEquals("An empty or null package name is not valid.", result);
	}

	@Test
	public void getLoggerLevel_EmptyPackage() {
		String result = loggerManager.getLoggerLevel("");
		assertEquals("An empty or null package name is not valid.", result);
	}

	@Test
	public void getAllCurrentLoggers() {
		loggerManager.setRootLoggerLevel("debug");
		String result = loggerManager.getAllCurrentLoggers();
		assertTrue(result.startsWith("Current global level: DEBUG"));
	}
}

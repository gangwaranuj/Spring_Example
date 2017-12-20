package com.workmarket.domains.authentication.services;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * User: micah
 * Date: 3/31/14
 * Time: 6:11 AM
 */
@Component
@ManagedResource(objectName="bean:name=loggers", description="change logger level on the fly")
public class LoggerManager {

	private static String[] validLevelsAry = {"fatal", "error", "warn", "info", "debug", "trace"};
	private static List<String> validLevels = Arrays.asList(validLevelsAry);

	private class ValidateResult {
		private boolean valid;
		private String msg;

		private boolean isValid() {
			return valid;
		}

		private void setValid(boolean valid) {
			this.valid = valid;
		}

		private String getMsg() {
			return msg;
		}

		private void setMsg(String msg) {
			this.msg = msg;
		}
	}

	private ValidateResult validatePackageName(String packageName) {
		ValidateResult result = new ValidateResult();
		if (StringUtils.isEmpty(packageName)) {
			result.setValid(false);
			result.setMsg("An empty or null package name is not valid.");
		} else {
			result.setValid(true);
		}
		return result;
	}

	private ValidateResult validateLevel(String level) {
		ValidateResult result = new ValidateResult();
		if (validLevels.contains(level)) {
			result.setValid(true);
		} else {
			result.setValid(false);
			result.setMsg("Level must be one of: " + Arrays.toString(validLevelsAry));
		}
		return result;
	}

	@ManagedOperation(description="Set root log level")
	@ManagedOperationParameters({
		@ManagedOperationParameter(name = "level", description = "log level"),
	})
	public String setRootLoggerLevel(String level) {
		ValidateResult result = validateLevel(level);
		if (!result.isValid()) { return result.getMsg(); }
		LogManager.getRootLogger().setLevel(Level.toLevel(level));
		return  "Root logger set to: " + level +"\n\nNOTE: This impacts the entire application server.";
	}

	@ManagedOperation(description="Set log level")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "packageName", description = "fully qualified package name for logger"),
			@ManagedOperationParameter(name = "level", description = "log level"),
	})
	public String setLoggerLevel(String packageName, String level) {
		ValidateResult packageResult = validatePackageName(packageName);
		if (!packageResult.isValid()) { return packageResult.getMsg(); }

		ValidateResult levelResult = validateLevel(level);
		if (!levelResult.isValid()) { return levelResult.getMsg(); }

		LogManager.getLogger(packageName).setLevel(Level.toLevel(level));
		return getLoggerLevel(packageName);
	}

	@ManagedOperation(description="Set log level")
	@ManagedOperationParameters({
		@ManagedOperationParameter(name = "packageName", description = "fully qualified package name for logger"),
	})
	public String getLoggerLevel(String packageName) {
		ValidateResult result = validatePackageName(packageName);
		if (!result.isValid()) { return result.getMsg(); }
		Logger l = LogManager.getLogger(packageName);
		return (l.getLevel() == null) ? l.getEffectiveLevel() + " (effective)" : l.getLevel().toString();
	}

	@ManagedOperation(description="Show all current loggers")
	public String getAllCurrentLoggers() {
		StringBuilder sb = new StringBuilder();

		sb.append("Current global level: " + LogManager.getRootLogger().getLevel() + "\n\n");

		Enumeration e = LogManager.getCurrentLoggers();
		while (e.hasMoreElements()) {
			Logger l = (Logger)e.nextElement();
			sb.append(l.getName() + ": " + l.getEffectiveLevel());
			sb.append("\n");
		}

		return sb.toString();
	}
}

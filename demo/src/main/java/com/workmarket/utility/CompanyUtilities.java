package com.workmarket.utility;

import com.workmarket.configuration.Constants;

import java.util.Calendar;

public class CompanyUtilities {
	public static boolean hasLockWarning(Calendar lockAccountWarningSentOn) {
		return lockAccountWarningSentOn != null &&
				DateUtilities.getHoursBetweenFromNow(lockAccountWarningSentOn) <= 24;
	}

	public static boolean hasOverdueWarning(int overdueWarningDaysBetweenFromNow) {
		return overdueWarningDaysBetweenFromNow >= 0;
	}

	public static int getOverdueWarningDaysBetweenFromNow(Calendar overdueAccountWarningSentOn) {
		int daysBetweenFromNow = -1;

		if (overdueAccountWarningSentOn != null) {
			daysBetweenFromNow = DateUtilities.getDaysBetweenFromNow(overdueAccountWarningSentOn);
		}

		return (daysBetweenFromNow < Constants.LOCKED_ACCOUNT_WINDOW_DAYS) ? daysBetweenFromNow : -1;
	}
}

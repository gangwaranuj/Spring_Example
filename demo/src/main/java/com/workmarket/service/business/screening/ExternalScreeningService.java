package com.workmarket.service.business.screening;

import com.workmarket.screening.model.ScreeningStatusCode;

public interface ExternalScreeningService {
	static final String WM_CLIENT_ID = "E8137506";

	/**
	 * Get the screening vendor request identifier from the response payload.
	 *
	 * @param results vendor response
	 * @return Screening vendor request identifier
	 */
	public String getVendorId(String results) throws Exception;

	/**
	 * Get the screening results from the response payload.
	 *
	 * @param results vendor response
	 * @return Success or failure of the screening
	 */
	public ScreeningStatusCode getScreeningStatus(String results) throws Exception;
}
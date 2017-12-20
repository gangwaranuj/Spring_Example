package com.workmarket.service.thrift;

import com.workmarket.thrift.work.Work;

/**
 * This helper service is to extract and expose the existing pricing normalization logic for WorkResponse to various touch points
 * within the application
 */
public interface WorkResponsePricingHelper {

	public void normalizePricing(Work work);
}


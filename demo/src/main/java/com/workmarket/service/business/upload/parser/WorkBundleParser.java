package com.workmarket.service.business.upload.parser;

/**
 * User: micah
 * Date: 11/7/13
 * Time: 11:04 AM
 */
public interface WorkBundleParser {
	void build(WorkUploaderBuildResponse response, WorkUploaderBuildData buildData);
}

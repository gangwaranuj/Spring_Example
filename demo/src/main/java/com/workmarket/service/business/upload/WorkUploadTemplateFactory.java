package com.workmarket.service.business.upload;

import com.workmarket.thrift.work.Work;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.thrift.work.uploader.WorkUploadException;
import com.workmarket.thrift.work.uploader.WorkUploadValue;

import java.util.List;

public interface WorkUploadTemplateFactory {
	/**
	 * Cached looked for a work template by template ID and user ID.
	 * @param templateId
	 * @param userId
	 * @return
	 * @throws WorkUploadException
	 */
	Work getTemplate(Long templateId, Long userId) throws WorkUploadException;

	/**
	 * Breaks down a given work object into a flattened structure of values
	 * matching how a CSV file is interpreted. Cached by the <code>work.id</code> key.
	 * @param buildResponse
	 * @return
	 */
	List<WorkUploadValue> extractValues(WorkUploaderBuildResponse buildResponse);
}

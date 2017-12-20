package com.workmarket.service.business.wrapper;

import com.workmarket.common.service.status.BaseStatus;
import com.workmarket.common.service.wrapper.response.BulkResponse;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.status.CloseWorkStatus;

/**
 * Created by nick on 4/23/13 4:40 PM
 */
public class CloseWorkBulkResponse extends BulkResponse<String, CloseWorkResponse> {

	public BaseStatus getStatus() {
		for (String key : responses.keySet()) {
			if (!responses.get(key).getStatus().isSuccessful())
				return CloseWorkStatus.FAILURE;
		}
		return CloseWorkStatus.SUCCESS;
	}

	@Override
	public Class<?> getKeyClass() {
		return Work.class;
	}

	@Override
	public String getKeyName() {
		return "workNumber";
	}
}

package com.workmarket.service.business.event.search;

import com.google.common.collect.Lists;
import com.workmarket.service.business.event.ScheduledEvent;

import java.util.List;

public class IndexerEvent extends ScheduledEvent {

	private static final long serialVersionUID = -8922414315606378685L;

	private Long fromId;
	private Long toId;
	private List<Long> ids;
	private boolean delete = false;

	public Long getFromId() {
		return fromId;
	}

	public IndexerEvent setFromId(Long fromId) {
		this.fromId = fromId;
		return this;
	}

	public Long getToId() {
		return toId;
	}

	public IndexerEvent setToId(Long toId) {
		this.toId = toId;
		return this;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public List<Long> getIds() {
		return ids;
	}

	public IndexerEvent setIds(List<Long> ids) {
		this.ids = Lists.newArrayList(ids);
		return this;
	}

	public IndexerEvent setId(Long id) {
		this.ids = Lists.newArrayList(id);
		return this;
	}
}

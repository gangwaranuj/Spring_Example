package com.workmarket.service.business.event.work;

import com.google.common.collect.Lists;
import com.workmarket.service.business.event.search.IndexerEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkUpdateSearchIndexEvent extends IndexerEvent {

	private List<Long> workIds;
	private List<String> workNumbers;

	private static final long serialVersionUID = -7945002793046337224L;
	
	public WorkUpdateSearchIndexEvent() {}

	public WorkUpdateSearchIndexEvent(Collection<Long> workIds) {
		this.workIds = Lists.newArrayList(workIds);
	}

	public WorkUpdateSearchIndexEvent(Long workId) {
		this.workIds = Lists.newArrayList(workId);
	}

	public WorkUpdateSearchIndexEvent(Long workId, boolean isDelete) {
		this.workIds = Lists.newArrayList(workId);
		setDelete(isDelete);
	}

	public WorkUpdateSearchIndexEvent(List<Long> workIds, boolean isDelete) {
		this.workIds = workIds;
		setDelete(isDelete);
	}

	public List<Long> getWorkIds() {
		return workIds;
	}

	public WorkUpdateSearchIndexEvent setWorkIds(List<Long> workIds) {
		this.workIds = workIds;
		return this;
	}

	public List<String> getWorkNumbers() {
		return workNumbers;
	}

	public WorkUpdateSearchIndexEvent setWorkNumbers(List<String> workNumbers) {
		this.workNumbers = workNumbers;
		return this;
	}

	@Override
	public String toString() {
		return "WorkUpdateSearchIndexEvent{" +
				"workIds=" + workIds +
				", workNumbers=" + workNumbers +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WorkUpdateSearchIndexEvent that = (WorkUpdateSearchIndexEvent) o;

		if (workIds != null) {
			if (that.workIds == null) {
				return false;
			} else if (workIds.size() != that.workIds.size()) {
				return false;
			}

			Set<Long> wIds = new HashSet<>();
			wIds.addAll(workIds);
			wIds.removeAll(that.workIds);

			if (wIds.size() > 0) {
				return false;
			}

		} else if (that.workIds != null) {
			return false;
		}

		if (workNumbers != null) {
			if (that.workNumbers == null) {
				return false;
			} else if (workNumbers.size() != that.workNumbers.size()) {
				return false;
			}

			Set<String> wIds = new HashSet<>();
			wIds.addAll(workNumbers);
			wIds.removeAll(that.workNumbers);
			new ArrayList<>().hashCode();

			if (wIds.size() > 0) {
				return false;
			}
		} else if (that.workNumbers != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = 0;

		if (workIds != null) {
			List<Long> wIds = new ArrayList<Long>() {{ addAll(workIds); }};
			Collections.sort(wIds);
			result = wIds.hashCode();
		}

		if (workNumbers != null) {
			List<String> wNumbers = new ArrayList<String>() {{ addAll(workNumbers); }};
			Collections.sort(wNumbers);
			return 31 * result + wNumbers.hashCode();
		} else {
			return 31 * result;
		}
	}

}

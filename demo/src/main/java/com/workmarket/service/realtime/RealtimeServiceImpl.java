package com.workmarket.service.realtime;

import com.google.common.collect.Lists;
import com.workmarket.dao.realtime.RealtimeWorkReportDAO;
import com.workmarket.domains.model.SortDirection;
import com.workmarket.domains.model.realtime.RealtimeReportType;
import com.workmarket.domains.model.realtime.RealtimeResourceComparator;
import com.workmarket.domains.model.realtime.RealtimeServicePagination;
import com.workmarket.domains.model.realtime.ResourceNoteComparator;
import com.workmarket.domains.work.dao.ResourceNoteDAO;
import com.workmarket.domains.work.model.WorkResourceAction;
import com.workmarket.domains.work.model.WorkResourceActionType;
import com.workmarket.domains.work.service.resource.action.WorkResourceActionService;
import com.workmarket.thrift.core.TimeRange;
import com.workmarket.thrift.services.realtime.RealtimeCSRStatusRequest;
import com.workmarket.thrift.services.realtime.RealtimePagination;
import com.workmarket.thrift.services.realtime.RealtimeResource;
import com.workmarket.thrift.services.realtime.RealtimeRow;
import com.workmarket.thrift.services.realtime.RealtimeRowFact;
import com.workmarket.thrift.services.realtime.RealtimeStatusException;
import com.workmarket.thrift.services.realtime.RealtimeStatusPage;
import com.workmarket.thrift.services.realtime.RealtimeStatusRequest;
import com.workmarket.thrift.services.realtime.ResourceIconType;
import com.workmarket.thrift.services.realtime.SortByType;
import com.workmarket.thrift.services.realtime.SortDirectionType;
import com.workmarket.thrift.services.realtime.TotalAssignmentCount;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.thrift.work.ResourceNoteType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class RealtimeServiceImpl implements RealtimeService {

	private static final Log logger = LogFactory.getLog(RealtimeServiceImpl.class);

	private static final RealtimeResourceComparator resourceComparator = RealtimeResourceComparator.instance;

	@Autowired private RealtimeWorkReportDAO realtimeWorkDAO;
	@Autowired private ResourceNoteDAO resourceNoteDAO;
	@Autowired private WorkResourceActionService workResourceActionService;

	@Override
	public RealtimeStatusPage getRealtime(RealtimeStatusRequest request) throws RealtimeStatusException {
		RealtimeStatusPage page = (RealtimeStatusPage) realtimeWorkDAO.generateRealtimeStatusPage(request.getCompanyId(),
				RealtimeReportType.REALTIME_COMPANY, request.getFilters(),
				createServicePagination(request.getPaginationRequest(), request.getInternalOwnerFilter()));
		hydratePageData(page);

		return page;
	}

	private RealtimeServicePagination createServicePagination(RealtimePagination realtimePagination, List<String> internalOwnerFilter) {
		RealtimeServicePagination pagination = new RealtimeServicePagination();
		pagination.setCursorPosition(realtimePagination.getCursorPosition());
		pagination.setPageSize(realtimePagination.getPageSize());
		if (!realtimePagination.isSetSortBy()) {
			pagination.setSortBy(SortByType.ORDER_AGE);
		} else {
			pagination.setSortBy(realtimePagination.getSortBy());
		}
		if (!realtimePagination.isSetSortDirection()) {
			pagination.setSortDirection(SortDirection.ASC);
		} else {
			pagination.setSortDirection(mapDirection(realtimePagination.getSortDirection()));
		}
		if (internalOwnerFilter != null && internalOwnerFilter.size() > 0) {
			pagination.setInternalOwnerFilter(internalOwnerFilter);
		}
		return pagination;
	}

	private SortDirection mapDirection(SortDirectionType direction) {
		switch (direction) {
		case ASC:
			return SortDirection.ASC;
		case DESC:
			return SortDirection.DESC;
		}
		return null;
	}

	@Override
	public RealtimeStatusPage getRealtimeCSR(RealtimeCSRStatusRequest request) throws RealtimeStatusException {
		RealtimeStatusPage page = (RealtimeStatusPage) realtimeWorkDAO.generateRealtimeStatusPage(null, RealtimeReportType.REALTIME_CSR,
				request.getFilters(), createServicePagination(request.getPaginationRequest(), request.getInternalOwnerFilter()));
		hydratePageData(page);
		return page;
	}

	private void hydratePageData(RealtimeStatusPage page) {
		hydrateHoverNotesForResources(page);
		hydrateRowFacts(page);
		hydratePercentOfResourcesStats(page.getRows());
		sortResources(page);
		sortResourceNotes(page);
	}

	private void sortResourceNotes(RealtimeStatusPage page) {
		if (!page.isSetRows()) {
			return;
		}
		ResourceNoteComparator comparator = new ResourceNoteComparator();
		List<RealtimeRow> rows = page.getRows();
		for (RealtimeRow row : rows) {
			if (row.isSetInvitedResources()) {
				for (RealtimeResource invitedResource : row.getInvitedResources()) {
					if (invitedResource.isSetHoverNotes()) {
						Collections.sort(invitedResource.getHoverNotes(), comparator);
					}
				}
			}
		}
	}

	private void sortResources(RealtimeStatusPage page) {
		if (!page.isSetRows()) {
			return;
		}
		List<RealtimeRow> rows = page.getRows();
		for (RealtimeRow row : rows) {
			sortResources(row);
		}
	}

	private void sortResources(RealtimeRow row) {
		List<RealtimeResource> resources = row.getInvitedResources();
		if (CollectionUtils.isEmpty(resources)) {
			return;
		}
		Collections.sort(resources, resourceComparator);
	}

	private void hydratePercentOfResourcesStats(List<RealtimeRow> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		for (RealtimeRow row : list) {
			double percentOfDecline = calcPercentOf(row.getInvitedResources(), ResourceIconType.OFFER_DECLINED);
			row.setPercentResourcesDeclined(percentOfDecline);
			long scheduleFrom = row.getAssignmentTimeRange().getFrom();
			double percentTimeElapsed = calcPercentOf(scheduleFrom, row.getOrderSentOn());
			row.setPercentTimeToWorkElapsed(percentTimeElapsed);
		}
	}

	private double calcPercentOf(long scheduleFrom, long orderSentOn) {
		double timeTotal = scheduleFrom - orderSentOn;
		double timeToNow = System.currentTimeMillis() - orderSentOn;
		return timeToNow / timeTotal;
	}

	private double calcPercentOf(List<RealtimeResource> invitedResources, ResourceIconType type) {
		if (CollectionUtils.isEmpty(invitedResources)) {
			return 0;
		}
		double totalResources = invitedResources.size();
		int resourcesHave = 0;
		for (RealtimeResource resource : invitedResources) {
			if (resource.isSetIcons() && resource.getIcons().contains(type)) {
				resourcesHave++;
			}
		}
		return ((double) resourcesHave) / totalResources;
	}

	/**
	 * Row facts are things that trigger off something with the assignment to the front end
	 * 
	 * @param page
	 */
	private void hydrateRowFacts(RealtimeStatusPage page) {
		List<RealtimeRow> rows = page.getRows();
		if (CollectionUtils.isEmpty(rows)) {
			return;
		}
		for (RealtimeRow row : rows) {
			// RealtimeRowFact.ALL_RESOURCES_DECLINED
			if (isAllResourcesDeclined(row)) {
				row.addToFacts(RealtimeRowFact.ALL_RESOURCES_DECLINED);
			}
			if (isAllResourcesOffered(row)) {
				row.addToFacts(RealtimeRowFact.ALL_RESOURCES_OFFERED);
			}
			if (isExpiresIn3Hours(row)) {
				row.addToFacts(RealtimeRowFact.EXPIRES_IN_3_HOURS);
			}
			if (isTimeElapsed(row)) {
				row.addToFacts(RealtimeRowFact.TIME_ELAPSED);
			}
		}

	}

	private boolean isTimeElapsed(RealtimeRow row) {
		if (!row.isSetAssignmentTimeRange()) {
			return false;
		}
		TimeRange range = row.getAssignmentTimeRange();
		if (range.isSetTo()) {
			return range.getTo() > System.currentTimeMillis();
		} else {
			return range.getFrom() > System.currentTimeMillis();
		}
	}

	private boolean isExpiresIn3Hours(RealtimeRow row) {
		if (row.isSetDueOn()) {
			if (row.getDueOn() < (System.currentTimeMillis() + (3 * 60 * 60 * 1000))) {
				return true;
			}
		}
		return false;
	}

	private boolean isAllResourcesOffered(RealtimeRow row) {
		return allResourcesHave(row, ResourceIconType.OFFER_OPEN);
	}

	private boolean isAllResourcesDeclined(RealtimeRow row) {
		return allResourcesHave(row, ResourceIconType.OFFER_DECLINED);
	}

	private boolean allResourcesHave(RealtimeRow row, ResourceIconType type) {
		if (!row.isSetInvitedResources()) {
			return false;
		}
		for (RealtimeResource resource : row.getInvitedResources()) {
			if (resource.isSetIcons() && !resource.getIcons().contains(type)) {
				return false;
			}
		}
		return true;
	}

	private void hydrateHoverNotesForResources(RealtimeStatusPage page) {
		Collection<Long> resourceIdsFromPage = getResourceIdsFromPage(page);
		Map<Long, List<ResourceNote>> hoverNotes = resourceNoteDAO.getResourceNotesByResourceIds(resourceIdsFromPage);
		if (CollectionUtils.isEmpty(hoverNotes)) {
			return;
		}
		for (RealtimeRow row : page.getRows()) {
			if (row.getInvitedResourcesSize() > 0) {
				for (RealtimeResource resource : row.getInvitedResources()) {
					List<ResourceNote> hoverNotesForResource = hoverNotes.get(resource.getResourceId());
					if (!isEmpty(hoverNotesForResource)) {
						for(ResourceNote note : hoverNotesForResource) {
							WorkResourceAction action = workResourceActionService.findById(note.getActionCodeId());
							note.setHoverType(mapResourceNoteType(action.actionType()));
							note.setActionCodeDescription(action.getActionDescription());
							resource.addToHoverNotes(note);
							if (note.getHoverType() == ResourceNoteType.NOTE) {
								if (!resource.isSetIcons() || !resource.getIcons().contains(ResourceIconType.NOTE)) {
									resource.addToIcons(ResourceIconType.NOTE);
								}
							}
						}
					}
				}
			}
		}
	}

	private ResourceNoteType mapResourceNoteType(WorkResourceActionType actionType) {
		switch (actionType) {
		case ACCEPT_WORK:
			return ResourceNoteType.ACCEPT;
		case COUNTER_OFFER:
			return ResourceNoteType.COUNTER;
		case DECLINE_WORK:
			return ResourceNoteType.DECLINE;
		case NOTE:
			return ResourceNoteType.NOTE;
		case QUESTION:
			return ResourceNoteType.QUESTION;
		case REROUTE_WORK:
			return ResourceNoteType.REROUTE;
		}
		return null;
	}

	private Collection<Long> getResourceIdsFromPage(RealtimeStatusPage page) {
		if (!page.isSetRows()) {
			return emptyList();
		}
		Collection<Long> resourceIds = Lists.newLinkedList();
		for (RealtimeRow row : page.getRows()) {
			if (row.isSetInvitedResources()) {
				for (RealtimeResource resource : row.getInvitedResources()) {
					resourceIds.add(resource.getResourceId());
				}
			}
		}
		return resourceIds;
	}

	@Override
	public TotalAssignmentCount calculateTotalOpenAssignments(long companyId, String timeZone) {
		return realtimeWorkDAO.calculateTotalOpenAssignments(companyId, timeZone);
	}

	@Override
	public TotalAssignmentCount calculateTotalOpenAssignments(String timeZone) {
		return realtimeWorkDAO.calculateTotalOpenAssignments(timeZone);
	}

}

package com.workmarket.domains.work.service.resource.action;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.workmarket.domains.work.dao.WorkResourceActionDAO;
import com.workmarket.domains.work.model.WorkResourceAction;
import com.workmarket.domains.work.model.WorkResourceActionType;
import com.workmarket.service.SpringInitializedService;
import com.workmarket.service.exception.InvalidParameterException;
import com.workmarket.thrift.work.DeclineWorkOfferRequest;
import com.workmarket.thrift.work.ResourceNoteRequest;
import com.workmarket.thrift.work.WorkQuestionRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class WorkResourceActionServiceImpl extends SpringInitializedService implements WorkResourceActionService {

	private static final Log logger = LogFactory.getLog(WorkResourceActionServiceImpl.class);
	private Map<WorkResourceActionType, List<WorkResourceAction>> tableMap;
	private final Map<Long, WorkResourceAction> actionMap = Maps.newHashMapWithExpectedSize(50);

	@Autowired
	private WorkResourceActionDAO workResourceActionDAO;
	
	private void sortTableMapEntries() {
		Collection<List<WorkResourceAction>> actionLists = tableMap.values();
		for (List<WorkResourceAction> actionList : actionLists) {
			Collections.sort(actionList);
			tableMap.put(actionList.get(0).actionType(), actionList);
		}
	}

	private void addToMap(WorkResourceAction action) {
		List<WorkResourceAction> actions = tableMap.get(action.actionType());
		if (actions == null) {
			actions = Lists.newArrayList();
		}
		actions.add(action);
		//logger.info(action);
		tableMap.put(action.actionType(), actions);
	}

	@Override
	public void initialize() {
		Preconditions.checkNotNull(workResourceActionDAO);
		List<WorkResourceAction> allResourceAction = workResourceActionDAO.getAll();
		this.tableMap = Maps.newEnumMap(WorkResourceActionType.class);
		for (WorkResourceAction action : allResourceAction) {
			addToMap(action);
			actionMap.put(action.getId(), action);
		}
		sortTableMapEntries();		
	}

	@Override
	public WorkResourceAction findAction(DeclineWorkOfferRequest request) throws InvalidParameterException {
		List<WorkResourceAction> declineWorkActions = this.tableMap.get(WorkResourceActionType.DECLINE_WORK);
		for(WorkResourceAction action : declineWorkActions) {
			if(action.getActionCode().equals(request.getActionCode().name())) {
				return action;
			}
		}
		throw new InvalidParameterException("There is no action found for the decline work offer request. " + request);
	}

	@Override
	public WorkResourceAction findAction(ResourceNoteRequest request) {
		List<WorkResourceAction> noteActions = this.tableMap.get(WorkResourceActionType.NOTE);
		for (WorkResourceAction action : noteActions) {
			if (action.getActionCode().equals(request.getActionType().name())) {
				return action;
			}
		}
		return null;
	}

	@Override
	public WorkResourceAction findAction(WorkQuestionRequest request) {
		List<WorkResourceAction> questionWorkActions = this.tableMap.get(WorkResourceActionType.QUESTION);
		//as of this time there is only one accept work action
		return questionWorkActions.get(0);
	}

	@Override
	public WorkResourceAction findById(Long actionCodeId) {
		return actionMap.get(actionCodeId);
	}

}

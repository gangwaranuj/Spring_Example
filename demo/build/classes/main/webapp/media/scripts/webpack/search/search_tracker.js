import _ from 'underscore';
import $ from 'jquery';
import SearchModesModel from './search_modes_model';
import WorkerListCollection from './worker_list_collection';

const searchModes = new SearchModesModel();

// current states
const states = {
	sessionUuid: null,
	searchUuid: null,
	query: null,
	selections: new WorkerListCollection()
};

const ActionContext = {
	pushToWorkerPool: 'pushToPool',
	removeWorkerFromWorkerPool: 'removeFromPool',
	pushToWork: 'pushToWork',
	pushToGroup: 'pushToGroup',
	pushToTest: 'pushToTest',
	pushComment: 'pushComment',
	showProfile: 'showProfile',
	dispatcherForwardWork: 'dispatcherWork',
	dispatcherSendApplication: 'dispatcherSend',
	goToSendAssignment: 'goToAssignment'
};

const nonTrackingFields = [
	'_tk',
	'search_type',
	'work_number',
	'description',
	'skills',
	'resource_mode'
];

const multiChecks = [
	'group',
	'assessment',
	'lane',
	'industry',
	'companytypes',
	'verification',
	'certification',
	'license'
];

const parseQuery = (query) => {
	var queryPairs = query.split('&').map(function (queryItem) { return queryItem.split('='); });
	var queryTerms = {};
	queryPairs.forEach(function (queryPair) {
		if (queryPair[1] && nonTrackingFields.indexOf(queryPair[0]) === -1) {
			if (multiChecks.indexOf(queryPair[0]) > -1) {
				if (!queryTerms.hasOwnProperty(queryPair[0])) {
					queryTerms[queryPair[0]] = [];
				}
				queryTerms[queryPair[0]].push(queryPair[1]);
			} else {
				queryTerms[queryPair[0]] = queryPair[1];
			}
		}
	});
	if (!queryTerms.sortby) {
		queryTerms.sortby = 'relevance';
	}
	if (!queryTerms.start) {
		queryTerms.start = 0;
	}
	if (!queryTerms.limit) {
		queryTerms.limit = 50;
	}
	return queryTerms;
};

const generateUUID = () => {
	const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (char) {
		var rand = Math.random()*16|0, value = char === 'x' ? rand : (rand&0x3|0x8);
		return value.toString(16);
	});
	return uuid;
};

const getSessionContextItemId = (mode) => {
	switch(mode) {
		case searchModes.get('people'):
			return null;
		case searchModes.get('assignment'):
			return config.work_number;
		case searchModes.get('group'):
		case searchModes.get('groupDetails'):
			return config.group_id;
		case searchModes.get('assessment'):
			return null;
		default:
			return null;
	}
};

const track = (url, data) => {
	$.ajax({
		url: url,
		type: 'POST',
		contentType: 'application/json; charset=UTF-8',
		data: JSON.stringify(data)
	});
};

const addSelectionIds = (workerListCollection, selectionIds, searchResults) => {
	let startPosition = 0;
	if (states.query && states.query.start) {
		startPosition = Number(states.query.start);
	}
	selectionIds.forEach(function (selectionId) {
		if (selectionId) {
			let selectionStr = selectionId.toString();
			workerListCollection.add(new Backbone.Model({
				userNumber: selectionId,
				selectionPosition: searchResults.indexOf(selectionStr) + startPosition,
				searchUuid: states.searchUuid
			}));
		}
	});
};

// compare query with states.query
// check to see if anything is changed between two queries
// specifically, it tries to capture pagination and page size change
const compareQuery = (query) => {

	if (states.query === null) {
		return { isEqual: false, isPagination: false };
	}

	const isEqual = _.isEqual(states.query, query);
	var isPagination = false;
	if (!isEqual) {
		// query changed
		if (!_.isMatch(states.query, { limit: query.limit })) {
			// if page size is not matched
			isPagination = true;
		} else if (!_.isMatch(states.query, { start: query.start })) {
			if (_.isEqual(_.omit(states.query, 'start'), _.omit(query, 'start'))) {
				// we want to make sure pagination is different from "clear this search"
				isPagination = true;
			}
		}
	}
	return { isEqual: isEqual, isPagination: isPagination };
};

class SearchTracker {
	trackSession(version, mode) {
		const sessionUuid = generateUUID();
		states.sessionUuid = sessionUuid;

		const sessionTracking = {
			uuid: sessionUuid,
			version: version,
			sessionContext: mode,
			sessionContextItemId: getSessionContextItemId(mode),
			createdBy: config.userNumber
		};
		track('/searchtracking/session', [sessionTracking]);
	}

	trackSearch(searchVersion, queryData, roundtripTime, hits) {
		// parse query into key-value pairs
		// remove key-value pairs where v is not defined
		const query = parseQuery(queryData);

		const queryComparison = compareQuery(query);
		states.query = Object.assign({}, query);
		if (queryComparison.isEqual || queryComparison.isPagination) {
			// if nothing changes or is pagination, ignore
			return;
		}

		states.searchUuid = generateUUID();

		const searchTracking = {
			uuid: states.searchUuid,
			searchTrackingSessionUuid: states.sessionUuid,
			searchVersion: searchVersion,
			queryTime: null,
			roundtripTime: roundtripTime,
			hits: hits,
			searchType: query.searchType ? query.searchType : 'workers',
			sortType: query.sortby.substring(0, 15),
			sortAsc: !!query.sortby.endsWith('asc')
		};

		// update keyword property
		if ('keyword' in query) {
			searchTracking.keyword = query.keyword.substring(0, 255);
			delete query.keyword;
		}
		// update filters property with remaining filters
		delete query.searchType;
		delete query.sortby;
		searchTracking.filters = JSON.stringify(query);
		track('/searchtracking/search', [searchTracking]);
	}

	// selectionId and searchResults are default to null.
	// When they are null, it means actions are performed on states.selections,
	// which also means the searcher is actively checking workers for action.
	// When both parameters are not null, it means it is a single item action,
	// and the single item is not part of the checked selections.
	trackAction(context, contextItemId, selectionId = null, searchResults = null) {
		let isBulk = false;
		let selections;
		if (selectionId !== null && searchResults !== null) {
			selections = new WorkerListCollection();
			addSelectionIds(selections, [selectionId], searchResults);
		} else {
			selections = states.selections;
			isBulk = true;
		}
		const actions = selections.map(function (selection) {
			return {
				uuid: generateUUID(),
				searchTrackingSearchUuid: selection.get('searchUuid'),
				selectionContext: ActionContext[context],
				selectionContextItemId: contextItemId,
				selectionId: selection.get('userNumber'),
				selectionPosition: selection.get('selectionPosition')
			};
		});
		if (actions.length > 0) {
			track('/searchtracking/action', actions);
		}
		if (isBulk) {
			states.selections.reset();
		}
	}

	trackSelection(selectionIds, searchResults, isChecked, isSelectAll) {
		if (isChecked) {
			// add checked items to selections
			addSelectionIds(states.selections, selectionIds, searchResults);
		} else {
			// remove unchecked items from selections
			if (isSelectAll) {
				// uncheck all
				states.selections.reset();
			} else {
				// find the item and remove
				selectionIds.forEach(function (selectionId) {
					states.selections.remove(states.selections.where({userNumber: selectionId}));
				});
			}
		}
	}
}

export default SearchTracker;

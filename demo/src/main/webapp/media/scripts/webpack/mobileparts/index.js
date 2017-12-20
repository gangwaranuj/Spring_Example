'use strict';

import PartsWithTrackingCollection from '../assignments/parts_withtracking_collection';
import PartsWithTrackingView from '../assignments/parts_withtracking_view';

let partsColl =  new PartsWithTrackingCollection([], {
	workNumber: config.workNumber,
	isMobile : true,
	partsConstants: config.partsConstants
});

new PartsWithTrackingView({
	collection: partsColl,
	partGroup: config.partGroup,
	isMobile: true,
	isReturn: false,
	isNotSentOrDraft: config.isNotSentOrDraft,
	isOwnerOrAdmin: config.isOwnerOrAdmin,
	isSuppliedByWorker: config.isSuppliedByWorker,
	el: '#partsSent'
});

if (config.partGroup.returnRequired) {
	new PartsWithTrackingView({
		collection: partsColl,
		partGroup: config.partGroup,
		isMobile: true,
		isReturn: true,
		isNotSentOrDraft: config.isNotSentOrDraft,
		isOwnerOrAdmin: config.isOwnerOrAdmin,
		isSuppliedByWorker: config.isSuppliedByWorker,
		el: '#partsReturn'
	});
}

partsColl.fetch({ remove: false });

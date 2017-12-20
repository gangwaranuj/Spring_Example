'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		routableGroups: [],
		groupIds: [],
		assignToFirstGroupIds: [],
		showInFeed: false,
		isTemplate: false,
		internalPricing: false,
		assignToFirstResource: false
	}
});

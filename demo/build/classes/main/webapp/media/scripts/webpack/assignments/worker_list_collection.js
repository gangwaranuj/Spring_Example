'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: function () {
		return '/assignments/' + this.id + '/' + this.resourceType;
	},

	initialize: function (models, options) {
		this.id = options.id;
		this.status = options.status;
		this.disablePriceNegotiation = options.disablePriceNegotiation;
		this.isIndividualBundledAssignment = options.isIndividualBundledAssignment;
		this.isParentBundle = options.isParentBundle;
		this.isBuyerAuthorizedToApproveCounter = options.isBuyerAuthorizedToApproveCounter;
		this.isDeputy = options.isDeputy;
		this.resourceType = options.resourceType || 'workers';
	},

	parse: function (response) {
		this.totalLength = response.total_results;
		return response.results;
	}
});

'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	url: '/account/vendor_search_status',

	canList: function () {
		return !this.get('isInVendorSearch') && this.get('hasAtLeastOneWorker') && this.get('hasAtLeastOneDispatcher');
	},

	parse: function (response) {
		return response.data;
	}
});

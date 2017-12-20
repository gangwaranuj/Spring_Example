'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: function () {
		return '/profile-edit/certificationslist?industry=' + this.industryId + '&provider=' + this.providerId;
	},

	comparator: function (certification) {
		return certification.get('name');
	},

	parse: function (response) {
		// the root property of the json response is 'list'
		return response.list;
	}
});

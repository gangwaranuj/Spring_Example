'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: function () {
		return '/profile-edit/licenselist?state=' + this.stateId;
	},

	comparator: function (license) {
		return license.get('name');
	}
});

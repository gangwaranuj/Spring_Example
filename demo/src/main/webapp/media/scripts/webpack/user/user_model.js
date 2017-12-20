'use strict';

import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.Model.extend({
	sync: Backbone.syncWithJSON,
	urlRoot: '/user',
	defaults: {
		fullName: 'A User',
		isCurrentUser: false,
		thumbnail: '',
		isWorker: false
	},

	parse: function (response) {
		response = _.omit(response, _.isNull);
		response.fullName = response.firstName + ' ' + response.lastName;
		return response;
	}
});


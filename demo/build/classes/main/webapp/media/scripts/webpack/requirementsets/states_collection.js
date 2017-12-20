'use strict';

import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.Collection.extend({
	url: '/states',
	comparator: function (state) {
		return state.get('name');
	},
	parse: function (response) {
		// previously, state.id returned the value now contained in shortName
		// this restores compatibility and uniformity to requirement sets
		// form options
		_.each(response, function (state) {
			state.id = state.shortName;
		});
		return response;
	}
});

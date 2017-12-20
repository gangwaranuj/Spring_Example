'use strict';

import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.Collection.extend({
	url: '/requirement_types',

	comparator: function (requirementType) {
		return requirementType.get('humanName');
	},

	filteredFor: function (filter) {
		return this.filter(function(item) {
			return !_.contains(item.get('filters'), filter);
		});
	}
});

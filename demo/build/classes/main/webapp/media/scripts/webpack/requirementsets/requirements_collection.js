'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: function () {
		return '/requirement_sets/' + this.requirementSetId + '/requirements';
	},

	initialize: function (options) {
		this.requirementSetId = options.requirementSetId;
	},

	comparator: function (requirement) {
		return requirement.get('$type') + '|' + requirement.get('name');
	}
});

'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		active: true
	},

	comparator: function (complianceRuleSet) {
		return complianceRuleSet.get('name');
	 }
});

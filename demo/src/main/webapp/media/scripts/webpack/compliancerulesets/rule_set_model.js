'use strict';

import Backbone from 'backbone';
import ComplianceRulesCollection from './rules_collection';

export default Backbone.Model.extend({
	url: '/settings/manage/compliance_rule_sets/list',
	sync: Backbone.syncWithJSON,
	parse: function (response) {
		// Backbone needs a little help with a collection contained in a model
		response.complianceRules = new ComplianceRulesCollection(response.complianceRules);
		return response;
	}
});

'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import Model from './plan_model';
import AdmissionsCollection from './admissions_collection';

export default Backbone.Collection.extend({
	sync: Backbone.syncWithJSON,
	model: Model,
	url: '/admin/plans/list',

	parse: function (response) {
		_.each(response, function (plan) {
			_.each(plan.admissions, function (admission) {
				admission.venue = admission.venue && admission.venue.name;
			});

			plan.admissions = new AdmissionsCollection(plan.admissions);

			// deal with percentage planConfig.
			var percentagePlanConfig = _.find(plan.planConfigs, function(planConfig) {
				return planConfig.type === 'transactionFee';
			});
			if (percentagePlanConfig) { plan.percentage = parseInt(percentagePlanConfig.percentage, 10); }
		});

		return response;
	}
});

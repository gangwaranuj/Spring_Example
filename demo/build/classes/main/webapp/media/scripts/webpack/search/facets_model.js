'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		existingWorkers: [],
		declinedWorkers: [],
		appliedWorkers: [],
		form_id: 'filter_form',
		results_per_page: 50,
		results_start: 0,
		results_limit: 50,
		results_count: 0,
		facet_option_limit: 5,
		facet_option_max: 10,
		facet_option_suggestions: {
			'certifications': [],
			'licenses': []
		},
		filters: {},
		checkedWorkers: [],
		existingWorkersLookup: {},
		declinedWorkersLookup: {},
		appliedWorkersLookup: {},
		reindex_timeout: 750,
		NUMBER_OF_MEMBER_STATUSES : 6
	}
});

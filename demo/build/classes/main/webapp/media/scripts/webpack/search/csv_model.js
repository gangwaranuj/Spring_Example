'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		userAttributes: [
			'userNumber',
			'first_name',
			'last_name',
			'company_name',
			'city',
			'state',
			'postal_code',
			'latitude',
			'longitude',
			'laneString',
			'derivedStatus'
		],

		fields: [
			'Resource ID',
			'First Name',
			'Last Name',
			'Company',
			'City',
			'State',
			'Zip',
			'Latitude',
			'Longitude',
			'Resource Type',
			'Talent Pool Status'
		]
	}
});


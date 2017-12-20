'use strict';

import Backbone from 'backbone';
import _ from 'underscore';

export default Backbone.Model.extend({
	url: '/employer/v2/assignments',
	parse: (response) => response.results[0],

	toJSON() {
		const json = {};
		const basic = this.get('basic').toJSON();
		const location = this.get('clientLocation').toJSON();
		const schedule = this.get('schedule').toJSON();
		const routing = this.get('routing').toJSON();
		const pricing = this.get('pricing').toJSON();
		const deliverables = this.get('deliverables').toJSON();
		const followers = this.get('followers');

		if (!_.isEmpty(location)) {
			json.location = location;
		}

		delete deliverables.description;
		delete deliverables.number;
		delete deliverables.type;

		return Object.assign(json, basic, { schedule, routing, pricing, deliverables, followers });
	}
});

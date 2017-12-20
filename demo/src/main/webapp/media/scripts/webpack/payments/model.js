'use strict';

import Application from '../core';
import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		type: 'FLAT',
		terms: 7,
		mode: 'company',
		flatPrice: 0,
		perHourPrice: 0,
		maxNumberOfHours: 0,
		perUnitPrice: 0,
		maxNumberOfUnits: 0,
		initialPerHourPrice: 0,
		initialNumberOfHours: 0,
		additionalPerHourPrice: 0,
		maxBlendedNumberOfHours: 0
	},

	initialize() {
		this.on('all', (event, model, xhr, options) => {
			Application.Events.trigger(`payments:model:${event}`, model, xhr, options);
		});

		Application.Events.trigger('payments:model:initialize', this);
	}
});


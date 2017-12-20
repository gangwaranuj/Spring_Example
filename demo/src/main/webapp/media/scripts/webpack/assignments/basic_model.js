'use strict';

import Application from '../core';
import Backbone from 'backbone';

export default Backbone.Model.extend({
	initialize() {
		this.on('all', (event, model, xhr, options) => {
			Application.Events.trigger(`assignments:basicModel:${event}`, model, xhr, options);
		});

		Application.Events.trigger('assignments:basicModel:initialize', this);
	}
});

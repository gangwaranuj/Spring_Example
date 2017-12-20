'use strict';

import Template from './templates/details/location.hbs';
import Backbone from 'backbone';
import PartsLocationModel from './parts_withtracking_location_model';

export default Backbone.View.extend({
	model: PartsLocationModel,

	initialize: function () {
		this.locationTemplate = Template;

		this.render();
	},

	render: function () {
		// the props we don't show to user are passed to stringify to exclude them
		var locationViewJson = this.model.format(this.model.toJSON());

		this.$('.parts-table--part-information').append(
			this.locationTemplate({ location: locationViewJson })
		);

		return this;
	}
});

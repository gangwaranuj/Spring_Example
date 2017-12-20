'use strict';

import $ from 'jquery';
import _ from 'underscore';
import OnboardView from './onboard_view';

export default OnboardView.extend({
	initialize: function () {
		this.listenTo(this.model, 'request', this.showSpinner);
		this.listenTo(this.model, 'sync error', this.hideSpinner);


		this.model.fetch({
			validate: false,
			success: _.bind(this.render, this),
			error: _.bind(this.render, this)
		});
	},

	showSpinner: function () {
		this.$('.wm-spinner').show();
	},

	hideSpinner: function () {
		this.$('.wm-spinner').hide();
	}
});

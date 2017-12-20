'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import DashboardAssetView from './dashboard_asset_view';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	el: '#assets',

	initialize: function (options) {
		this.messages = this.$('div.message');
		this.generalAssets = this.$('.general-assets');
		this.description = this.$('input[name=attachment_description]');
	},

	render: function () {
		$.getJSON('/assignments/get_attachments', {
			work_numbers: this.options.selectedWorkNumbers
		}, _.bind(function (data) {
			if (data.successful && !_.isEmpty(data.data)) {
				_.each(data.data, function (asset) {
					this.addOne(asset);
				}, this);
			} else {
				wmNotify({
					type: 'danger',
					message: 'There are no attachments available for removal.'
				});
			}
		}, this));
		return this;
	},

	addOne: function (asset) {
		var view = new DashboardAssetView({
			model: asset,
			selectedWorkNumbers: this.options.selectedWorkNumbers,
			modal: this.options.modal
		});

		$('ul', this.generalAssets).append(view.render().el);
	}
});

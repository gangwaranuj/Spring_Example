'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import Template from '../dashboard/templates/bulk_asset_item.hbs';

export default Backbone.View.extend({
	tagName: 'li',
	events: {
		'click a.remove' : 'removeAsset'
	},

	render: function () {
		$(this.el).html(Template(this.model));

		return this;
	},

	removeAsset: function () {
		if (!confirm('Are you sure you want to remove the selected attachment?')) {
			return;
		}

		$.getJSON('/assignments/remove_attachments', {
			asset_id: this.model.id,
			work_numbers: this.options.selectedWorkNumbers
		}, _.bind(function (data) {
			if (data.successful) {
				this.$el.fadeOut('fast', _.bind(function () {
					this.remove();
					this.trigger('remove', 1);
				}, this));
				this.showSuccess(data.messages);
			} else {
				this.showFailed(data.messages);
			}
		}, this)).fail(this.showFailed);

		return this;
	},

	showFailed: function (errors) {
		_.each(errors, function (theMessage) {
			wmNotify({
				message: theMessage,
				type: 'danger'
			});
		});
	},

	showSuccess: function (message) {
		_.each(message, function (theMessage) {
			wmNotify({
				message: theMessage
			});
		});
	}
});

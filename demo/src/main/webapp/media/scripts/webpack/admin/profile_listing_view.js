'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import ajaxSendInit from '../funcs/ajaxSendInit';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click .approve-listing' : 'approveListing',
		'click .decline-listing' : 'declineListing'
	},

	initialize (options) {
		this.options = options || {};
		ajaxSendInit();
	},

	approveListing () {
		this.updateListing('/admin/manage/users/approve_lane3');
	},

	declineListing () {
		this.updateListing('/admin/manage/users/decline_lane3');
	},

	updateListing (url) {
		$.ajax({
				url: url,
				type: 'POST',
				data: JSON.stringify({ id: this.options.profileId }),
				dataType: 'json',
				contentType: 'application/json'
			})
			.done(function (response) {
				wmNotify({
					message: response.message
				});
			})
			.fail(function (response) {
				wmNotify({
					message: response.message,
					type: 'danger'
				});
			});
	}
});

'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	el: '#bulk-download-modal',
	events: {
		'click #download_assets' : 'submit'
	},

	initialize: function (options) {
		$('#assignment_id_download').val(options.selectedWorkNumbers);
	},

	submit: function () {
		$.getJSON('/assignments/email_assets', {
			work_numbers: $('#assignment_id_download').val()
		}, _.bind(function (response) {
			if (response.successful) {
				wmNotify({
					message: response.messages[0]
				});
			} else {
				wmNotify({
					message: response.messages[0],
					type: 'danger'
				});
			}
			this.options.modal.destroy();
		}, this));

	}
});

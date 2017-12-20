'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import wmSelect from '../funcs/wmSelect';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	el: '#lms-manage-details-ui',

	events: {
		'click #assessment-options input[type="checkbox"]'       : 'toggleOption',
		'click #assessment-notifications input[type="checkbox"]' : 'toggleNotification',
		'click #cta-save-form'                                   : 'saveForm'
	},

	initialize: function (options) {
		this.options = options || {};

		var self = this;

		wmSelect({
			selector: '#notification_list',
			root: this.el
		});


		$('#manage-details-form').ajaxForm({
			dataType: 'json',
			beforeSubmit: function () {
				$('#dynamic_messages').hide();
			},
			success: function(response) {
				if (response.successful) {
					if (self.options.itemCount) {
						window.location = '/lms/view/details/' + response.data.assessment_id;
					} else {
						window.location = '/lms/manage/step2/' + response.data.assessment_id;
					}
				} else {
					$('#manage-details-form button.disabled').removeClass('disabled');

					// Output error messages.
					var messages = $('<ul class="unstyled">');
					$.each(response.messages, function (i, item) {
						$('<li>').text(item).appendTo(messages);
					});
					$('#dynamic_messages').removeClass('success').addClass('error').show()
						.find('div').html(messages);
				}
			}
		});

		this.render();
	},

	render: function () {
		this.toggleOption();
	},

	toggleOption: function () {
		$('#assessment-options input[type="checkbox"]').each(function () {
			var parent = $(this).parent().parent();
			if (!$(this).is(':checked')) {
				// Clear out previously selected values.
				parent.find('input[type="text"]').val('');
			}
		});
	},

	toggleNotification: function (event) {
		var el = $(event.target);

		if (el.attr('name') === 'notifications[submission_received]') {
			$('#assessment-notifications input[name="notifications[submission_received_invited]"]').removeProp('checked');
		} else if (el.attr('name') === 'notifications[submission_received_invited]') {
			$('#assessment-notifications input[name="notifications[submission_received]"]').removeProp('checked');
		}
	},

	saveForm: function (event) {
		$(event.target).closest('form').trigger('submit');
	}
});

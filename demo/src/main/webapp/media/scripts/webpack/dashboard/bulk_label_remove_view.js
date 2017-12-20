'use strict';

import Backbone from 'backbone';
import $ from 'jquery';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	el: '#remove_label_container',
	events: {
		'click #remove_mult_label'      : 'save',
		'change [name=bulk_edit_label]' : 'pickedLabel'
	},

	initialize: function (options) {
		$.unescapeHTML = function (html) {
			return $('<div/>').html(html).text();
		};

		$.unescapeAndParseJSON = function (json) {
			return $.parseJSON($.unescapeHTML(json));
		};
		var labels_json = $.unescapeAndParseJSON($('#json_labels').html());
	},

	save: function () {
		var $selectLabel = this.$('#bulk_edit_label'),
			allLabelIds = _.map($selectLabel[0].selectize.options, function (option) {
				return parseInt(option.value, 10);
			});

		var labelIds = $selectLabel.val() < 0 ?  allLabelIds.join() : $selectLabel.val();

		$.ajax({
			context: this,
			dataType: 'json',
			type: 'POST',
			url: '/assignments/label_remove_multiple',
			data: {
				workNumbers: this.options.selectedWorkNumbers,
				labelIds: labelIds,
				note: this.$('#label_note').val()
			}
		}).done(function (response) {
			if (response.successful) {
				this.options.modal.destroy();
				wmNotify({
					message: response.messages[0]
				});

				Backbone.Events.trigger('getDashboardData');
			} else {
				wmNotify({
					message: 'There was error. Please try again',
					type: 'danger'
				});
			}
		}).fail(function () {
			wmNotify({
				type: 'danger',
				message: 'There was error. Please try again'
			});
		});
	},

	pickedLabel: function () {
		var $removeAllAlert = $('#remove-all-labels-alert');
		$removeAllAlert.empty().addClass('dn');
		if (this.$('[name=bulk_edit_label]').val() === '-1') {
			$removeAllAlert.html('<strong>WARNING:</strong> You have selected to remove all labels from the selected assignments. Submitting this request will remove all labels').removeClass('dn');
		}
	}

});


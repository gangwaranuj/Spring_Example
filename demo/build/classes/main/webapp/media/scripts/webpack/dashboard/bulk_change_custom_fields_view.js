'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import CustomFieldsView from './custom_fields_view';
import wmModal from '../funcs/wmModal';

export default Backbone.View.extend({
	initialize: function (options) {
		this.model = {};

		$.get('/assignments/get_custom_fields', {
			workNumbers: options.selectedWorkNumbers
		}, _.bind(function (data) {
			this.showFieldModel(data, options.selectedWorkNumbers);
		}, this));
	},

	showFieldModel: function (data, selectedWorkNumbers) {
		this.modal = wmModal({
			autorun: true,
			title: 'Bulk Edit Custom Fields',
			destroyOnClose: true,
			content: data
		});

		$('#assignment_id_custom_field').val(selectedWorkNumbers);

		// if workEncoded isn't empty, we've got some customfields to edit!
		if (!_.isEmpty($(data)[0].innerHTML)) {
			this.model = $.parseJSON($('<div/>').html($(data)[0].innerHTML).text());
			// Get ids for all custom field groups being edited and set hidden field
			$('#custom_field_group_ids').val(_.pluck(this.model.customFieldGroups, "id"));
			var workMeta = {
				workId: selectedWorkNumbers,
				workNumber: selectedWorkNumbers,
				millisOffset:  $('#millisecond').val()
			};

			this.customFields = new CustomFieldsView($.extend({}, workMeta, {model: this.model.customFieldGroups}));
			this.customFields.render();
		}
	}
});

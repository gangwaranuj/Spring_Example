'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import CustomFieldsGroupView from './custom_field_group_view';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	el: '#custom-fields',

	events: {
		'click #save_custom_field'            : 'save',
		'change [name="bulk_field_update[]"]' : 'activateSave',
		'click #close-cf-modal'               : 'closeModal'
	},

	initialize: function () {
		$('#save_custom_field').attr('disabled', 'disabled');

		var index = 0;
		// Set index on all custom fields in all custom field groups on assignment
		if ($.isArray(this.model) && this.model.length > 0) {
			_.each(this.model, function(customFieldGroup){
				_.each(customFieldGroup.fields, function (item) {
					item.index = index;
					index++;
				});
			});
		}
	},

	activateSave: function () {
		var checkedAtLeastOne = false;
		$('#custom-fields input[type="checkbox"]').each(function () {
			if ($(this).is(':checked')) {
				$('#save_custom_field').removeAttr('disabled', '');
				checkedAtLeastOne = true;
			}
		});
		if (!checkedAtLeastOne) {
			$('#save_custom_field').attr('disabled', 'disabled');
		}
		return checkedAtLeastOne;
	},

	render: function () {
		if (!this.model) return this;
		var $buyerCustomFields =$('#buyer-custom-fields'),
			$workerCustomFields = $('#resource-custom-fields');

		var workerFieldCount = 0;
		var buyerFieldCount = 0;

		_.each(this.model, function(customFieldGroup){
			var buyerFields = new CustomFieldsGroupView({
				model: customFieldGroup,
				type: 'owner'
			});
			$buyerCustomFields.append(buyerFields.render().el);

			var workerFields = new CustomFieldsGroupView({
				model: customFieldGroup,
				type: 'resource'
			});
			$workerCustomFields.append(workerFields.render().el);

			workerFieldCount += workerFields.size();
			buyerFieldCount += buyerFields.size();
		});

		if (workerFieldCount === 0) {
			$workerCustomFields.hide();
		}

		if (buyerFieldCount === 0) {
			$buyerCustomFields.hide();
		}

		return this;
	},

	save: function (event) {
		event.preventDefault();

		if (this.activateSave()) {
			$('#custom_fields_form').ajaxSubmit({
				dataType: 'json',
				success: function (data) {
					if (data.successful) {
						wmNotify({
							message: data.messages[0]
						});
						$('.wm-modal--close').trigger('click');
						Backbone.Events.trigger('getDashboardData');
					} else {
						wmNotify({
							message: data.messages[0],
							type: 'danger'
						});
					}
				}
			});
		} else {
			wmNotify({
				type: 'danger',
				message: 'No field checkboxes were selected.'
			});
		}
	},

	closeModal: function (event) {
		event.preventDefault();
		this.$('.wm-modal--close').trigger('click');
	}
});

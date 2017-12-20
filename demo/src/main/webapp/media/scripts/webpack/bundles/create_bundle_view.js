'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import AssignmentBundleModel from '../bundles/create_assignment_bundle_model';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';

export default Backbone.View.extend({
	el: '#create-bundle-container',
	tagName: 'div',

	events: {
		'click #submit_create_bundle'      : 'save',
		'click input[name="bundle_radio"]' : 'toggleNewExists'
	},

	initialize: function (options) {},

	toggleNewExists: function (e) {
		var $existingBundleDiv = $('#bundle_existing'),
			$newBundleDiv = $('#bundle_new'),
			$target = $(e.currentTarget),
			$actionButton = $('#submit_create_bundle');

		if ($target.val() === 'new') {
			$existingBundleDiv.hide();
			$newBundleDiv.show();
			$actionButton.text('Create New Bundle');
		} else if ($target.val() === 'existing') {
			$newBundleDiv.hide();
			$existingBundleDiv.show();
			$actionButton.text('Add to Bundle');
		}
	},

	validateBundleInfo: function (formData) {
		// validation directly on the model was fouling up the works because of
		// how old our version of backbone is. Validating bundle fields right here
		// (puke)
		if ($('input:radio[name=bundle_radio]:checked').val() === 'new') {
			var message = '';
			if (!formData.title) {
				message += 'Title is required. ';
			}
			if (!formData.description) {
				message += 'Description is required.';
			}
			if (message) {
				wmNotify({
					message: message,
					type: 'danger'
				});

				return false;
			}
		} else if ($('input:radio[name=bundle_radio]:checked').val() === 'existing' && !$('select[name=id]').val()) {
			wmNotify({
				message: 'Please pick an existing assignment bundle.',
				type: 'danger'
			});
			return false;
		}
		return true;
	},

	save: function (e) {
		e.preventDefault();
		var serializedForm = $(e.currentTarget.form).serializeArray(),
			formData = _.object(_.pluck(serializedForm, 'name'), _.pluck(serializedForm, 'value'));

		if (!this.validateBundleInfo(formData)) {
			return false;
		}

		var modelData = {
			id: formData.id,
			title: formData.title,
			description: formData.description,
			workNumbers: _.pluck(this.options.assignment_models, 'id')
		};

		var assignmentBundleModel = new AssignmentBundleModel();
		assignmentBundleModel.save(modelData, {
			type: 'POST',
			success: _.bind(function (model, resp) {
				var messages = model.get('messages');
				this.redirect(
					resp.data && resp.data.id ? '/assignments/view_bundle/' + resp.data.id : window.location.pathname,
					messages,
					resp.successful ? 'success' : 'error'
				);
			}, this),
			error: _.bind(function (model) {
				var message = model.get('messages') || 'Error creating bundle. Contact Support.';
				this.redirect(window.location.pathname, message, 'error');
			}, this)
		});
	},

	redirect: function (url, msg, type) {
		if (msg) {
			var e = $("<form class='dn'></form>");
			e.attr({
				'action': '/message/create',
				'method': 'POST'
			});
			if (typeof msg === 'string') { msg = [msg]; }
			for (var i=0; i < msg.length; i++) {
				e.append(
					$("<input>").attr({
						'name': 'message[]',
						'value': msg[i]
					}));
			}
			e.append(
				$("<input>").attr({
					'name': 'type',
					'value': type
				}));
			e.append(
				$("<input>").attr({
					'name': 'url',
					'value': url
				}));
			e.append(
				$("<input>").attr({
					'name':'_tk',
					'value':getCSRFToken()
				}));
			$('body').append(e);
			e.submit();
		} else {
			window.location = url;
		}
	}

});

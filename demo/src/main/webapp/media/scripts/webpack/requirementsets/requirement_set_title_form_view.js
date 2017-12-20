'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import Template from './templates/requirement-set-title-form.hbs';
import getCSRFToken from '../funcs/getCSRFToken';

export default Backbone.View.extend({
	template: Template,

	events: {
		'click [data-action="save"]'   : 'save',
		'click [data-action="cancel"]' : 'cancel',
		'change #title'                : 'toggleSave'
	},

	initialize: function (options) {
		this.requirementSet = options.requirementSet;
		this.requirements = this.requirementSet.get('requirements');

		this.requirementSets = options.requirementSets;
		this.parentView = options.parentView;

		this.requirementSet.bind('destroy', this.cancel, this);
		this.requirementSet.bind('change', this.render, this);
		this.requirements.bind('add', this.toggleSave, this);
		this.requirements.bind('remove', this.toggleSave, this);
		this.render();
		this.toggleSave();
	},

	render: function () {
		this.$el.html(this.template({
			requirementSet: this.requirementSet.attributes
		}));
		return this;
	},

	save: function () {
		if (this.disabled) {
			return false;
		}

		this.requirementSet.set(this.serialize());
		var already = this.requirementSets.get(this.requirementSet);
		if (!already) {
			this.requirementSets.add(this.requirementSet);
		}
		this.requirementSet.save({}, {
			success: function () {
				if (this.requirementSet.attributes.groupId) {
					this.redirectWithFlash(this.requirementSet.urlRoot().substring(0, this.requirementSet.urlRoot().lastIndexOf('/')), 'success', 'Successfully updated group requirements.');
				} else {
					this.parentView.$el.slideUp(function () {
						$(this).empty();
					});
				}
				this.$el.undelegate('[data-action="save"]', 'click');
			}.bind(this),
			error: function () {
				alert('Error saving Requirement Set');
			}
		});
	},

	cancel: function () {
		if (this.requirementSet.attributes.groupId) {
			window.location = this.requirementSet.urlRoot().substring(0, this.requirementSet.urlRoot().lastIndexOf('/'));
		} else {
			this.parentView.$el.slideUp(function () {
				$(this).empty();
			});
		}
		this.$el.undelegate('[data-action="cancel"]', 'click');
	},

	toggleSave: function () {
		var hasTitle = this.$('#title').val();
		if (hasTitle) {
			this.$('[data-action="save"]').removeClass('disabled');
			this.disabled = false;
		} else {
			this.$('[data-action="save"]').addClass('disabled');
			this.disabled = true;
		}
	},

	serialize : function () {
		// Strip HTML
		var val = this.$('input[name="name"]').val();
		var tempSelector = $('<div>' + val + '</div>');
		var name = tempSelector[0].textContent;
		var creatorName = $('#creator-name').val();

		return {
			$type: 'RequirementSet',
			name: name,
			required: this.$('input[name="required"]').prop('checked'),
			creatorName: creatorName
		};
	},

	redirectWithFlash : function(url, type, msg) {
		var e = $('<form></form>');
		e.attr({
			'action':'/message/create',
			'method':'POST'
		});
		e.append(
			$('<input>').attr({
				'name': 'message[]',
				'value': msg
			}));
		e.append(
			$('<input>').attr({
				'name': 'type',
				'value': type
			}));
		e.append(
			$('<input>').attr({
				'name': 'url',
				'value': url
			}));
		e.append(
			$('<input>').attr({
				'name': '_tk',
				'value': getCSRFToken()
			}));
		$('body').append(e);
		e.submit();
	}
});

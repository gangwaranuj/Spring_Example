'use strict';

import Template from './templates/save.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import wmNotify from '../funcs/wmNotify';
import ajaxSendInit from '../funcs/ajaxSendInit';

export default Backbone.View.extend({
	template: Template,

	events: {
		'click [data-action="save"]' : 'save'
	},

	initialize: function (options) {
		ajaxSendInit();
		this.eventDispatcher = options.eventDispatcher;
		this.complianceRuleSet = options.complianceRuleSet;
		this.complianceRules = options.complianceRules;
		this.complianceRules.bind('add', this.enableSave, this);
		this.complianceRules.bind('remove', this.enableSave, this);
		this.render();
	},

	render: function () {
		this.$el.html(this.template());
		this.enableSave(false);
		return this;
	},

	enableSave: function (enabled) {
		if (!enabled) {
			this.$('[data-action="save"]').addClass('disabled');
			this.disabled = true;
		} else {
			this.$('[data-action="save"]').removeClass('disabled');
			this.disabled = false;
		}
	},

	save: function (event) {
		event.preventDefault();

		if (this.disabled) { return false; }
		// although complianceRules is originally embedded in complianceRuleSet
		// it gets unhooked along the way
		this.complianceRuleSet.set({ complianceRules: this.complianceRules });
		this.complianceRuleSet.save({}, {
			type: 'POST',
			success: function (model, response) {
				wmNotify({
					message: response.messages[0],
					type: response.successful ? 'success' : 'danger'
				});
			},
			error: function () {
				wmNotify({
					message: 'An error occurred saving Compliance Rules',
					type: 'danger'
				});
			}
		});
		this.enableSave(false);
	}
});

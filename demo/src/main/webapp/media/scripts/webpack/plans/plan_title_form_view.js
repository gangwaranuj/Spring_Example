'use strict';

import $ from 'jquery';
import Template from './templates/plan-title-form.hbs';
import Backbone from 'backbone';
import PlanModel from './plan_model';
import '../dependencies/jquery.serializeObject';

export default Backbone.View.extend({
	el: '#plan-title-form',
	template: Template,

	events: {
		'click [data-action=save]'   : 'save',
		'click [data-action=cancel]' : 'cancel'
	},

	initialize: function (options) {
		options = options || {};
		this.plans = options.plans;
		this.render();
	},

	render: function () {
		if (this.plan) {
			this.$el.html(this.template({
				code: this.plan.get('code'),
				description: this.plan.get('description')
			}));
		}
		return this;
	},

	save: function () {
		// TODO: Saving is simplified in Backbone 1.0. After upgrade, refactor.
		var self = this;

		self.plan.set(self.serialize());

		if (!self.plans.get(self.plan)) {
			self.plans.add(self.plan);
		}
		self.plan.save({}, {
			success: function () {
				Backbone.Events.trigger('planForm:hide');
				self.plan = new PlanModel();
			},
			error: function () {
				alert('Error saving Plan');
			}
		});
	},

	cancel: function () {
		this.plan = new PlanModel();
		Backbone.Events.trigger('planForm:hide');
	},

	serialize: function () {
		return this.$('form:first').serializeObject();
	}
});

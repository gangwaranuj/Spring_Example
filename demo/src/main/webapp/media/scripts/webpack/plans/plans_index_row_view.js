'use strict';

import Template from './templates/plan-row.hbs';
import Backbone from 'backbone';

export default Backbone.View.extend({
	tagName: 'tr',
	template: Template,

	events: {
		'click [data-action="trash"]' : 'trash'
	},

	initialize: function (options) {
		options = options || {};
		this.plan = options.plan;
		this.plan.bind('change', this.render, this);
		this.render();
	},

	render: function () {
		this.$el.html(this.template({
			id: this.plan.get('id') ,
			code: this.plan.get('code'),
			description: this.plan.get('description')
		}));
		return this;
	},

	trash: function (event) {
		var isDelete = window.confirm('Are you sure you want to permanently delete this Plan?');

		if (!isDelete) {
			return false;
		}

		event.preventDefault();

		this.plan.destroy({
			success: function () {
				this.remove();
			}.bind(this),
			error: function () {
				window.alert('error deleting Plan!');
			}
		});
	}
});

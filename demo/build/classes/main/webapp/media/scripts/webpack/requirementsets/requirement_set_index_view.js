'use strict';

import Backbone from 'backbone';
import Template from './templates/requirement-set-index.hbs';

export default Backbone.View.extend({
	template: Template,
	tagName: 'tr',

	events: {
		'click [data-action="trash"]':    'trash',
		'change [data-action="require"]': 'toggleRequire',
		'click [data-action="power-on"]': 'togglePowerOn'
	},

	initialize: function (options) {
		this.model = options.model;
		this.model.bind('change', this.render, this);
		this.render();
	},

	render: function () {
		this.$el.html(this.template({ requirementSet: this.model.attributes }));
		return this;
	},

	trash: function (e) {
		var doIt = confirm('Are you sure you want to permanently delete this Requirement Set. It will be removed from all assignments it is attached to.');

		if (!doIt) {
			return false;
		}

		e.preventDefault();
		var self = this;
		this.model.destroy({
			success: function() {
				self.remove();
			},
			error: function() {
				alert("error deleting Requirement Set");
			}
		});
	},

	toggleRequire: function () {
		this.model.save({required: !this.model.get('required')});
	},

	togglePowerOn: function () {
		this.model.save({active: !this.model.get('active')});
	}
});

'use strict';

import ButtonTemplate from './templates/button.hbs';
import FormTemplate from './templates/abstractcomplianceruleform.hbs';

import Backbone from 'backbone';

export default Backbone.View.extend({
	buttonTemplate: ButtonTemplate,
	template: FormTemplate,

	events: {
		'click [data-action="add"]': 'add'
	},

	initialize: function (options) {
		this.eventDispatcher = options.eventDispatcher;
		this.formType  = options.formType;
		this.formLabel = options.formLabel;
		this.complianceRules = options.complianceRules;
		this.eventDispatcher.on('cart:trash', this.cartTrashHandler, this);
		this.render();
	},

	render: function () {
		throw 'RenderNotImplementedException';
	},

	resetForm: function () {
		throw 'PostRenderNotImplementedException';
	},

	cartTrashHandler: function () {
		throw 'CartTrashHandlerNotImplementedException';
	},

	add: function (e) {
		e.preventDefault();
		this.complianceRules.add(this.getComplianceRule());
		this.resetForm();
	}
});

'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import Template from './templates/cart-item.hbs';

export default Backbone.View.extend({
	template: Template,
	className: 'cart-item',

	events: {
		'click [data-action="trash"]': 'trash'
	},

	initialize: function (options) {
		this.requirement = options.requirement;
		this.requirement.bind('remove', this.trash, this);
		this.requirementTypes = options.requirementTypes;
		this.render();
	},

	render: function() {
		this.$el.html(this.template(this.serialize()));
		return this;
	},

	toggleOption: function(reqType, disable) {
		if (!reqType.get('allowMultiple')) {
			// Disable selection of requirement types that can't have multiple
			$("#requirementTypes option[value='" + reqType.get('name') + "']").attr('disabled', disable);
		}
	},

	toggleRequirement: function(requirement, disable) {
		if (requirement.get('requirable')) {
			// Disable selection of requirements to prevent duplicates
			$("[data-selections='requirables'] option[value='" + requirement.get('requirable').id + "']").prop('disabled', disable);
		}
	},

	serialize: function() {
		var content = this.requirement.get('requirable') && this.requirement.get('requirable').name || this.requirement.get('name');
		var requirementType = this.requirementTypes.findWhere({name: this.requirement.get('$type')});

		this.toggleOption(requirementType, true);

		this.toggleRequirement(this.requirement, true);

		return {
			requirementType: requirementType.get('humanName'),
			content:         content,
			mandatory:       (this.requirement.get('mandatory') ? '*' : '')
		};
	},

	trash: function () {
		var requirementType = this.requirementTypes.findWhere({name: this.requirement.get('$type')});
		this.requirement.collection.remove(this.requirement);
		$(self.el).undelegate('[data-action="trash"]', 'click');
		this.toggleOption(requirementType, false);
		this.toggleRequirement(this.requirement, false);
		this.remove();
	}
});

'use strict';

import Template from './templates/cart-item.hbs';
import $ from 'jquery';
import Backbone from 'backbone';

export default Backbone.View.extend({
	template: Template,
	className: 'cart-item',

	events: {
		'click [data-action="trash"]' : 'trash'
	},

	initialize: function (options) {
		this.eventDispatcher = options.eventDispatcher;
		this.complianceRule = options.complianceRule;
		this.complianceRuleTypes = options.complianceRuleTypes;
		this.render();
	},

	render: function () {
		this.$el.html(this.template(this.serialize()));
		return this;
	},

	toggleOption: function (complianceRuleType, disable) {
		if (!complianceRuleType.get('allowMultiple')) {
			$('#complianceRuleTypes option[value="' + complianceRuleType.get('name') + '"]').attr('disabled', disable);
		}
	},

	serialize: function () {
		var content = this.complianceRule.get('viewLabel');
		var complianceRuleType = this.complianceRuleTypes.findWhere({ name: this.complianceRule.get('$type') });
		this.toggleOption(complianceRuleType, true);

		return {
			complianceRuleType: complianceRuleType.get('humanName'),
			content: content
		};
	},

	trash: function () {
		var complianceRuleType = this.complianceRuleTypes.findWhere({ name: this.complianceRule.get('$type') });
		this.complianceRule.collection.remove(this.complianceRule);
		this.eventDispatcher.trigger('cart:trash', this.complianceRule);
		$(this.el).undelegate('[data-action="trash"]', 'click');
		this.toggleOption(complianceRuleType, false);
		this.remove();
	}
});

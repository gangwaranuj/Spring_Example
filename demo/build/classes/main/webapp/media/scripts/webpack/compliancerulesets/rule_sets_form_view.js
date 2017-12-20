'use strict';

import Template from './templates/compliancerulesetform.hbs';
import Backbone from 'backbone';
import RulesFormView from './rules_form_view';
import RulesCartView from './rules_cart_view';
import RulesSaveView from './rules_save_view';

export default Backbone.View.extend({
	el: '#compliance-rule-set-form',
	template: Template,

	initialize: function (options) {
		this.eventDispatcher = options.eventDispatcher;
		this.complianceRuleSet = options.complianceRuleSet;
		this.complianceRuleTypes = options.complianceRuleTypes;
		this.complianceRules = options.complianceRules;
		this.render();
	},

	render: function () {
		this.$el.html(this.template({}));

		this.$el.find('.compliance-rules-form').html(new RulesFormView({
			eventDispatcher : this.eventDispatcher,
			complianceRules : this.complianceRules,
			complianceRuleTypes : this.complianceRuleTypes
		}).el);

		this.$el.find('.compliance-rules-cart').html(new RulesCartView({
			eventDispatcher : this.eventDispatcher,
			complianceRules : this.complianceRules,
			complianceRuleTypes : this.complianceRuleTypes
		}).el);

		this.$el.find('.compliance-rules-save').html(new RulesSaveView({
			eventDispatcher : this.eventDispatcher,
			complianceRuleSet : this.complianceRuleSet,
			complianceRules : this.complianceRules
		}).el);

		return this;
	}
});

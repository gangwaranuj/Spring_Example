'use strict';

import Backbone from 'backbone';
import RequirementSetTitleFormView from './requirement_set_title_form_view';
import RequirementsFormView from './requirements_form_view';
import RequirementsCartView from './requirements_cart_view';
import Template from './templates/form.hbs';

export default Backbone.View.extend({
	el: '#requirement-set-form',
	template: Template,

	initialize: function (options) {
		this.requirementSets = options.requirementSets;
		this.requirementSet = options.requirementSet;
		this.requirementTypes = options.requirementTypes;
		this.isMandatoryRequirement = options.isMandatoryRequirement;
		this.filter = options.filter;
		this.render();
	},

	render: function() {
		this.$el.html(this.template());

		this.$el.find('.requirement-set-title-form').html(new RequirementSetTitleFormView({
			requirementSet: this.requirementSet,
			requirementSets: this.requirementSets,
			parentView: this
		}).el);

		this.$el.find('.requirements-form').html(new RequirementsFormView({
			requirementSet: this.requirementSet,
			requirementTypes: this.requirementTypes,
			filter: this.filter,
			isMandatoryRequirement: this.isMandatoryRequirement
		}).el);

		this.$el.find('.requirements-cart').html(new RequirementsCartView({
			requirementSet: this.requirementSet,
			requirementTypes: this.requirementTypes
		}).el);

		this.$el.slideDown();

		return this;
	}
});


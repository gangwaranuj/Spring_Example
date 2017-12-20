'use strict';

import Template from './templates/compliancerulesform.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import AssignmentCountComplianceView from './assignment_count_compliance_form_view';

export default Backbone.View.extend({
	template: Template,
	className: 'well-b2',

	events: {
		'change [data-toggle="form"]' : 'changeComplianceTypeForm'
	},

	initialize: function (options) {
		this.eventDispatcher = options.eventDispatcher;
		this.complianceRules = options.complianceRules;
		this.complianceRuleTypes = options.complianceRuleTypes;
		this.render();
	},

	render: function () {
		this.$el.html(this.template({ complianceRuleTypes: this.complianceRuleTypes.models }));
		return this;
	},

	changeComplianceTypeForm: function (e) {
		var changer = this.$(e.currentTarget);
		var placeholder = this.$el.find('[data-placeholder="form"]');
		placeholder.empty();

		var formType = changer.val(),
			formLabel = changer.find('option:selected').text(),
			formView;
		// in the future add cases to this switch case to initialize difference views
		switch (formType) {
			case 'AssignmentCountComplianceRule':
				formView = AssignmentCountComplianceView;
				break;
		}

		if (formView) {
			var form = new formView({
				eventDispatcher: this.eventDispatcher,
				complianceRules: this.complianceRules,
				formType: formType,
				formLabel: formLabel
			});
			placeholder.html(form.el);
			form.resetForm();
			changer.find('option.prompt').remove();
		}
	}
});
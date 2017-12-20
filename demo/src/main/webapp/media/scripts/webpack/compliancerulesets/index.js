import _ from 'underscore';
import $ from 'jquery';
import Backbone from 'backbone';
import Application from '../core';
import ComplianceRuleSetModel from './rule_set_model';
import RuleTypesCollection from './rule_types_collection';
import RuleSetsFormView from './rule_sets_form_view';

const Router = Backbone.Router.extend({

	initialize () {
		$.when(
			this.fetchAndRender()
		).then(() => {
			Backbone.history.start();
		});
	},

	complianceRuleSet () {
		const crs = new ComplianceRuleSetModel();
		return new Promise(
			(resolve, reject) => {
				crs.fetch({
					success (complianceRuleSet) {
						resolve(complianceRuleSet);
					},
					error (model, response) {
						reject(new Error(`Could not fetch the ComplianceRuleSet. Response was: ${response.status}`));
					}
				});
			}
		);
	},

	complianceRuleTypes () {
		const crt = new RuleTypesCollection();
		return new Promise(
			(resolve, reject) => {
				crt.fetch({
					success (complianceRuleTypes) {
						resolve(complianceRuleTypes);
					},
					error (model, response) {
						reject(new Error(`Could not fetch ComplianceRuleTypes. Response was: ${response.status}`));
					}
				});
			}
		);
	},

	fetchAndRender () {
		Promise.all(
			[
				this.complianceRuleSet(),
				this.complianceRuleTypes()
			]
		)
			.then(([complianceRuleSet, complianceRuleTypes]) => {
				new RuleSetsFormView({ // eslint-disable-line no-new
					eventDispatcher: _.extend({}, Backbone.Events),
					complianceRuleTypes,
					complianceRuleSet,
					complianceRules: complianceRuleSet.get('complianceRules')
				});
			})
			.catch(err => console.log(err)); // eslint-disable-line no-console
	}
});

Application.init({ name: 'compliancerulesets', features: config }, Router);

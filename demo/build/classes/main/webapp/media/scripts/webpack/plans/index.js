'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import ajaxSendInit from '../funcs/ajaxSendInit';
import PlanTitleFormView from './plan_title_form_view';
import PlanTransactionFeeView from './plan_transaction_fee_view';
import VenuesCartView from './venues_cart_view';
import PlanFormView from './plan_form_view';
import PlanModel from './plan_model';
import PlansIndexView from './plans_index_view';
import VenuesFormView from './venues_form_view';
import PlansCollection from './plans_collection';
import VenuesCollection from './venues_collection';

const Router = Backbone.Router.extend({
	routes: {
		'new'      : 'newPlan',
		'edit/:id' : 'editPlan'
	},

	initialize: function () {
		ajaxSendInit();

		this.plans = new PlansCollection();
		this.venues = new VenuesCollection();

		this.venues.fetch({
			success: () => {
				new VenuesFormView({
					venues: this.venues
				});

				this.venuesCart = new VenuesCartView({
					venues: this.venues
				});

				this.plans.fetch({
					success: () => {
						new PlansIndexView({
							plans: this.plans
						});

						this.titleForm = new PlanTitleFormView({
							plans: this.plans
						});

						this.planTransactionFee = new PlanTransactionFeeView({
							defaultWorkFeePercentage: config.defaultWorkFeePercentage
						});
						this.planForm = new PlanFormView();

						Backbone.history.start();
					}
				});
			}
		});

	},

	newPlan: function () {
		var plan = new PlanModel();
		// may be a bug in our version of backbone, but even with the default, a new Plan object retains the planConfigs
		plan.set({ planConfigs: [] });

		this.titleForm.plan = plan;
		this.titleForm.render();

		this.venuesCart.plan = plan;
		this.venuesCart.render();

		this.planTransactionFee.plan = plan;
		this.planTransactionFee.render();

		this.planForm.render();
	},

	editPlan: function (id) {
		var plan = this.plans.findWhere({id: parseInt(id, 10)});

		if (plan) {
			this.titleForm.plan = plan;
			this.titleForm.render();

			this.venuesCart.plan = plan;
			this.venuesCart.render();

			this.planTransactionFee.plan = plan;
			this.planTransactionFee.render();

			this.planForm.render();
		} else {
			this.navigate('');
		}
	}
});

export default new Router();

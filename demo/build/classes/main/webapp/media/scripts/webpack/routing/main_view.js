'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmSelect from '../funcs/wmSelect';
import wmNotify from '../funcs/wmNotify';
import Template from './templates/routing_form.hbs';
import { getQueryParams } from '../funcs/utils'
import 'underscore.inflection';

export default Backbone.View.extend({
	template: Template,
	events: {
		'change input[name="send_type"]:radio' : 'toggleOptions',
		'change input[name="show_in_feed"]'    : 'handleShowInFeed',
		'click .search-resources'              : 'handleSearchResources',
		'change select[name="groupIds"]'       : 'getEligibleWorkerCount'
	},
	routingResourceIds: '#routing_resource_ids',
	routingGroupIds: '#routing_groups_ids',
	assignToFirstGroupIds: '#assign_to_first_group_ids',
	routingCompanyIds: '#routing_company_ids',
	showInFeed: 'input[name="show_in_feed"]',
	sendType: 'input[name="send_type"]',
	assignToFirstTalent: 'input[name="assign_to_first_resource_talent"]',
	assignToFirstGroup: 'input[name="assign_to_first_resource_group"]',
	assignToFirstVendor: 'input[name="assign_to_first_resource_vendor"]',
	assignToFirstGlobal: 'input[name="assign_to_first_global"]',
	bulkPublish: 'input[name="bulk_show_in_feed"]',

	initialize: function () {
		this.render();
		this.model.on("change:internalPricing", () => {
			if (this.workerSelect[0] !== undefined) {
				this.workerSelect[0].selectize.clear();
				this.workerSelect[0].selectize.clearCache();
				this.render();
			}
		});

		const params = getQueryParams();
		const vendorNumber = params.vendor;
		const workerNumber = params.user;

		if (vendorNumber) {
			this.$('.wm-accordion--content').addClass('open');
			const talentAccordion = document.getElementsByClassName('wm-accordion--heading routing-option')[0];
			talentAccordion.querySelector('input').click();
			this.vendorSelect[0].selectize.addOption({ company_name: '', companyNumber: vendorNumber, address: '' });
			this.vendorSelect[0].selectize.addItem(vendorNumber);
		} else if (workerNumber) {
			this.$('.wm-accordion--content').addClass('open');
			const talentAccordion = document.getElementsByClassName('wm-accordion--heading routing-option')[0];
			talentAccordion.querySelector('input').click();
			this.workerSelect[0].selectize.addOption({ name: '', id: workerNumber, address: '' });
			this.workerSelect[0].selectize.addItem(workerNumber);
		}
	},

	render: function () {
		this.$el.html(this.template(this.model.toJSON()));
		componentHandler.upgradeAllRegistered();
		wmSelect({ selector: '#routing_groups_ids' });
		wmSelect({ selector: '#assign_to_first_group_ids' });
		this.workerTypeahead();
		this.vendorTypeahead();
		if (!this.model.attributes.isBulk) {
			if (config.form.groupIds) {
				this.$('input[value=direct_send]').prop('checked', true);
				this.$('.wm-accordion--content[data-accordion-name=direct_send_options]').addClass('open');
			}
		}
	},

	toggleOptions: function (event) {
		this.$('.wm-accordion--content').removeClass('open');
		if (event.target.checked) {
			this.$('.wm-accordion--content[data-accordion-name="' + $(event.target).parent().parent().attr('data-accordion-target') + '"]').addClass('open');
		}
	},

	handleShowInFeed: function (event) {
		if (event.target.checked) {
			this.$(this.assignToFirst).prop('checked', false);
		}
	},

	handleSearchResources: function () {
		analytics.track('Routing', {
			action: 'Invite Workers Clicked',
			source: 'on_assignment_create'
		});
	},

	workerTypeahead: function () {
		this.workerSelect = wmSelect({ selector: '#routing_resource_ids' }, {
			valueField: 'id',
			labelField: 'name',
			searchField: 'name',
			options: [],
			loadThrottle: 200,
			allowEmptyOption: true,
			plugins: ['remove_button'],
			render: {
				option: (item) => `<div>${item.name} | ID: ${item.id} | ${item.address}</div>`,
				item: (item) => `<div>${item.name} | ID: ${item.id} | ${item.address}</div>`
			},
			load: (query, callback) => {
				if (!query.length) {
					return callback();
				}
				const searchData = { term: query, internal_only: this.model.attributes.internalPricing };

				$.ajax({
					url: '/search/suggest_users.json',
					traditional: true,
					type: 'GET',
					dataType: 'json',
					data: searchData,
					error: callback,
					success: callback
				});
			}
		});
	},

	vendorTypeahead: function () {
		this.vendorSelect = wmSelect({ selector: '#routing_company_ids' }, {
			valueField: 'companyNumber',
			labelField: 'name',
			searchField: 'name',
			options: [],
			loadThrottle: 200,
			allowEmptyOption: true,
			plugins: ['remove_button'],
			render: {
				option: (item) => `<div>${item.name} | ID: ${item.companyNumber} | ${item.cityStateCountry}</div>`,
				item: (item) => `<div>${item.name} | ID: ${item.companyNumber} | ${item.cityStateCountry}</div>`
			},
			load: function (query, callback) {
				if (!query.length) {
					return callback();
				}

				$.ajax({
					url: '/search/suggest_vendors.json',
					type: 'GET',
					dataType: 'json',
					data: {
						term: query
					},
					error: callback,
					success: callback
				});
			}
		});
	},

	getEligibleWorkerCount: function () {
		if (!this.model.attributes.isTemplate && this.$(this.routingGroupIds).val() !== null &&  this.options.workNumber > 0) {
			$.get('/assignments/' + this.options.workNumber + '/eligible_worker_count', { groupIds: this.$(this.routingGroupIds).val().toString() }, (result) => {
				if (result > 0) {
					this.$('.estimated-worker-count').text(result + (result === 150 ? '+ ' : ' ') + _('worker').pluralize(result) + ' will be invited.');
				} else {
					this.$('.estimated-worker-count').text('');
				}
			});
		} else {
			this.$('.estimated-worker-count').text('');
		}
	}
});

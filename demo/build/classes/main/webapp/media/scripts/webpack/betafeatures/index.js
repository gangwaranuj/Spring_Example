'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import wmModal from '../funcs/wmModal';
import wmNotify from '../funcs/wmNotify';
import wmSelect from '../funcs/wmSelect';
import getCSRFToken from '../funcs/getCSRFToken';
import Template from './templates/index.hbs';
import BetaTemplate from './templates/beta_feature_participation.hbs';

const View = Backbone.View.extend({
	el: '.beta-features-bucket',
	template: Template,
	events: {
		'click .beta-feature'        : 'showBetaFeature',
		'click .beta-feature-action' : 'showBetaFeature',
		'click .beta-feature-save'   : 'updateBetaFeatureParticipation'
	},
	companyIds: '#company_ids',

	initialize: function (options) {
		this.model = {};
		$.get('/admin/beta_features/', function (response) {
			this.model = response;
			this.render();
		}.bind(this), 'json');
	},

	render: function () {
		this.$el.html(this.template(this.model));
	},

	showBetaFeature : function(event) {
		event.preventDefault();
		this.featureName = $(event.target).closest('tr').attr('data-feature-name');
		this.modal = wmModal({
			root: '.beta-features-bucket',
			title: this.featureName,
			content: BetaTemplate(this.model.data[this.featureName]),
			destroyOnClose: true,
			autorun: true,
			controls: [
				{
					text: 'Cancel',
					close: true
				},
				{
					text: 'Save',
					classList: 'beta-feature-save',
					primary: true
				}
			]
		});

		$.get('/search/suggest_companies', function (response) {
			this.companyTypeAhead(response.data.companies);
		}.bind(this), 'json');
	},

	companyTypeAhead: function (companies) {
		wmSelect({ selector: this.companyIds }, {
			multiple: true,
			valueField: 'id',
			labelField: 'name',
			searchField: ['id', 'name'],
			options: companies,
			allowEmptyOption: true,
			render: {
				option: function (item, escape) {
					return '<div>' + escape(item.name + ' | ID: ' + item.id) + '</div>'
				},
				item: function (item, escape) {
					return '<div>' + escape(item.name + ' | ID: ' + item.id) + '</div>'
				}
			}
		});
	},

	updateBetaFeatureParticipation: function () {
		var data = {
			betaFeature: this.model.data[this.featureName].venue.name,
			companyIds: this.$(this.companyIds).val() || []
		};

		$.ajax({
			type: 'POST',
			dataType: 'json',
			contentType: 'application/json',
			url: '/admin/beta_features/update_participation',
			data: JSON.stringify(data),
			beforeSend: function (xhr) {
				xhr.setRequestHeader('X-CSRF-TOKEN', getCSRFToken());
			},
			success: function(result) {
				wmNotify({
					type: 'success',
					message: result.messages[0]
				});
				this.modal.destroy();
				this.initialize();
			}.bind(this),
			error: function(result) {
				wmNotify({
					type: 'danger',
					message: result.messages[0]
				});
			}
		});
	}
});

new View();
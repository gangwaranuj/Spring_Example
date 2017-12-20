'use strict';

import _ from 'underscore';
import $ from 'jquery';
import Backbone from 'backbone';
import wmModal from '../../funcs/wmModal';
import 'jquery-ui';

export default Backbone.View.extend({
	el: '.content',

	events: {
		'click .accounting_submit' : 'submitForm',
		'change #companyId' : 'validate',
		'change #fiscalYear' : 'validate',
		'change #taxStatus' : 'validate'
	},

	initialize: function (options) {
		this.publishedYears = options.publishedYears;

		this.initCompanyAutocomplete();
		this.render();
	},

	requiredFields: ['companyId', 'fiscalYear', 'taxStatus'],

	initCompanyAutocomplete: function () {
		var self = this;

		$('#companyName').autocomplete({
			minLength: 0,
			source: '/admin/manage/profiles/suggest_company',
			focus: function (event, ui) {
				$('#companyName').val(ui.item.value);
				return false;
			},
			select: function (event, ui) {
				$('#companyId').val(ui.item.id);
				$('#companyName').val(ui.item.value);

				self.renderCompanyId();
				self.validate();
				return false;
			},
			search: function () {
				$('#companyId').val('');
				$('#selected_company').text('');
				$('#selected_company').hide();

				self.validate();
			}
		});
	},

	render: function () {
		this.renderCompanyId();

		return this;
	},

	submitWithWarning: function(taxYear) {
		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: wm.templates['modals/confirmAction']({
				message: 'The taxes for ' + taxYear + ' have been published, are you sure you want to proceed?'
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			this.confirmModal.hide();
			this.submit();
		}, this));
	},

	submit: function() {
		$('.accounting_submit').prop('disabled', true);
		$('.alert').fadeOut(1500);
		$('#taxStatusUpdateForm').submit();
	},

	submitForm: function (e) {
		e.preventDefault();

		var newStatus = $('#taxStatus').val();
		var taxYear = $('#fiscalYear').val();

		if (this.publishedYears[newStatus] >= taxYear) {
			this.submitWithWarning(taxYear);
		}
		else {
			this.submit();
		}
	},

	renderCompanyId: function () {
		if ($('#companyId').val()) {
			$('#selected_company').text('(Company ID: ' + $('#companyId').val() + ')');
			$('#selected_company').show();
		}
	},

	validate: function () {
		var valid = _.all(this.requiredFields, function(requiredField) {
			return $('#' + requiredField).val();
		});

		$('.accounting_submit').prop('disabled', !valid);
	}
});

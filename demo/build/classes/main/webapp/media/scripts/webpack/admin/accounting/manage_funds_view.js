'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import 'jquery-ui';


export default Backbone.View.extend({
	el: '.content',

	events: {
		'change #type'             : 'renderDescriptionOptions',
		'change #description'      : 'updateType',
		'click .accounting_submit' : 'submitForm'
	},

	initialize: function () {
		this.initCompanyAutocomplete();

		this.descriptionOptions = {
			'credit':   $('#description [data-tx="credit"]'),
			'cash_out': $('#description [data-tx="cash_out"]')
		};

		this.render();
	},

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
				return false;
			},
			search: function () {
				$('#companyId').val('');
				$('#selected_company').text('');
				$('#selected_company').hide();
			}
		});
	},

	render: function () {
		this.renderCompanyId();
		this.renderDescriptionOptions();

		return this;
	},

	submitForm: function (e) {
		e.preventDefault();
		var description = $('#description');
		if (description.val() === 'debitAdj' || description.val() === 'creditAdj') {
			if (!confirm('Adjustments create register transactions but do not effect balances.\nAre you sure you want to proceed?')) {
				return;
			}
		}

		var total = $('#amount');
		total.val(parseFloat(total.val().replace(/,/g, '')));
		$('#form').submit();
	},

	renderCompanyId: function () {
		if ($('#companyId').val()) {
			$('#selected_company').text('(Company ID: ' + $('#companyId').val() + ')');
			$('#selected_company').show();
		}
	},

	renderDescriptionOptions: function () {
		var txType = $('#type').val().trim();

		$('#description option:not(:first)').remove();

		if (txType.length > 0) {
			$('#description').append(this.descriptionOptions[txType]);
		} else {
			$('#description')
				.append(this.descriptionOptions.credit)
				.append(this.descriptionOptions.cash_out);
		}
	},

	updateType: function (evt) {
		if ($('#type option:first').is(':selected')) {
			var optionType = $(':selected', evt.currentTarget).attr('data-tx');

			$('#type option:[value="' + optionType + '"]').attr('selected', 'selected');
			this.renderDescriptionOptions();
		}
	}
});

'use strict';

import $ from 'jquery';
import _ from 'underscore';
import BaseTaxView from './tax_base_view';
import TaxFormModel from './tax_form_model';
import wmNotify from '../funcs/wmNotify';
import '../dependencies/jquery.serializeObject';
import '../dependencies/jquery.tmpl';

export default BaseTaxView.extend({
	el: '#tax-sign-form',

	events: {
		'click #save-signed-tax-form' : 'saveSignedTaxForm',
		'click #go-back-edit-form'    : 'editTaxForm'
	},

	template: $('#tmpl-tax-sign').template(),

	initialize: function (options) {
		this.options = options;
	},

	render: function (model) {
		this.options.router.currentView = 'sign';
		this.model = model;
		this.$el.html($.tmpl(this.template, this.padFields(this.options.active_tax_entity)));
		$('#sign-date input').datepicker({dateFormat: 'mm/dd/yy', yearRange: '-01:+01'});

		$('#save-signed-tax-form').removeClass('disabled');
		wmNotify({message: 'Please confirm your tax information by providing your electronic signature below.'});
		this.setHeader();
		$('#sign-here input').focus();
	},

	editTaxForm: function () {
		this.options.router.navigate('edit', true);
		$('.sidebar').show();
	},

	isDisabled: function (o) {
		return $(o).is('.disabled');
	},

	saveSignedTaxForm: function (e) {
		if (this.isDisabled(e.target)) return;
		$(e.target).addClass('disabled');

		$.extend(this.options.active_tax_entity, $(this.el).serializeObject());
		var saveModel = new TaxFormModel(this.options.active_tax_entity);

		saveModel.save({}, {
			success: function (model, response) {
				if (!response.successful && response.messages !== null && response.messages.length) {
					_.each(response.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
					$(e.target).removeClass('disabled');
				} else {
					window.location.hash = '';
					window.location.reload(true); // refresh JSP - to view
				}
			},
			error: function (model, response) {
				// TODO: show errors inline
				console.log(response);
			}
		});
	}
});

'use strict';

import $ from 'jquery';
import _ from 'underscore';
import React from 'react';
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import BaseTaxView from './tax_base_view';
import FormModel from './tax_form_model';
import wmNotify from '../funcs/wmNotify';
import wmMaskInput from '../funcs/wmMaskInput';
import ajaxSendInit from '../funcs/ajaxSendInit';
import '../dependencies/jquery.serializeObject';
import '../dependencies/jquery.tmpl';
import 'jquery-form/jquery.form';
import 'jquery-ui';
import WMNationalIdModal from './WMNationalIdModal';
import store from './store';

export default BaseTaxView.extend({
	events: {
		'change input[name=business_flag]'        : 'showEditForm',
		'change select[name=tax_country]'         : 'showEditForm',
		'change input[name=tax_entity_type_code]' : 'toggleLlc',
		'change input[name=tax_number]'           : 'toggleEffectiveDate',
		'change input[name=first_name]'           : 'toggleEffectiveDate',
		'change input[name=middle_name]'          : 'toggleEffectiveDate',
		'change input[name=last_name]'            : 'toggleEffectiveDate',
		'change input[name=business_name_flag]'   : 'toggleBusinessDbaName',
		'click #save-tax-form-btn'                : 'saveTaxForm',
		'click #cancel-edit-tax-form-btn'         : 'cancelEdit'
	},

	template: $('#tmpl-tax-edit-step1').template(),
	template2: $('#tmpl-tax-edit-step2').template(),

	initialize: function (options) {
		this.options = options || {};
		if (this.hasData()) {
			this.prevBusinessFlag = options.active_tax_entity.business_flag;
			this.prevTaxNumber = options.active_tax_entity.tax_number;
			this.prevFirstName = options.active_tax_entity.first_name;
			this.prevMiddleName = options.active_tax_entity.middle_name;
			this.prevLastName = options.active_tax_entity.last_name;
			this.verifiedFlag = options.active_tax_entity.tax_verification_status_code === 'approved';
		}
		if (options.default_country) {
			options.active_tax_entity.tax_country = options.default_country;
		}
	},

	render: function () {
		this.options.router.currentView = 'edit';

		this.hideAllForms();
		$(this.el).html($.tmpl(this.template, this.padFields($.extend(this.options.active_tax_entity, this.options.country))));
		this.showEditForm();
	},

	showHideBusinessDbaName: function (show) {
		var input = $('#business-name-input input[name="business_name"]');
		if (show) {
			$('#business-name-input').show();
			input.prop('disabled', false);
		} else {
			input.prop('disabled', true);
			input.val('');
			$('#business-name-input').hide();
		}
	},

	toggleBusinessDbaName: function (e) {
		return this.showHideBusinessDbaName(e.target.value === 'true');
	},

	toggleLlc: function (e) {
		this.showHideLlc($(e.target).is(':checked') && $(e.target).val() === 'llc');
	},

	showHideLlc: function (show) {
		if (show) {
			$('#llc_type').show();
			$('#llc_type select').prop('disabled', false);
		} else {
			$('#llc_type').hide();
			$('#llc_type select').prop('disabled', true);
			$('#llc_type select').val('');
		}
	},

	isLlc: function (entity) {
		return (entity.tax_entity_type_code === 'llc-c-corp'
		|| entity.tax_entity_type_code === 'llc-s-corp'
		|| entity.tax_entity_type_code === 'llc-part');
	},

	toggleEffectiveDate: function () {
		if (!this.verifiedFlag || !(this.prevBusinessFlag || this.prevTaxNumber || this.prevName)) return;
		var effectiveDate = $('#effective_date');
		if (this.didIrsMatchFieldsChange()) {
			effectiveDate.show();
			effectiveDate.find('input').val('');
		} else {
			effectiveDate.hide();
			effectiveDate.find('input').val('');
		}
	},

	didIrsMatchFieldsChange: function () {
		// check for a change in the value so it doesn't show up if users change the value back to the original
		var businessFlag = $("input[name='business_flag']:checked").val();
		var taxNumber = $("input[name='tax_number']").val();
		var firstName = $("input[name='first_name']").val();
		var middleName = $("input[name='middle_name']").val();
		var lastName = $("input[name='last_name']").val();

		return (businessFlag != this.prevBusinessFlag.toString()
		|| taxNumber != this.prevTaxNumber
		|| firstName != this.prevFirstName
		|| middleName != this.prevMiddleName
		|| lastName != this.prevLastName);
	},

	cancelEdit: function () {
		window.location.hash = '';
		window.location.reload(true); // refresh jsp
	},

	showEditForm: function () {
		var country = $('option:selected', '#country-select').val();
		var selectedEntity = $("input[name='business_flag']:checked");

		if (country != '' && selectedEntity.length) {
			var entityType = (selectedEntity.val() === 'true') ? 'business' : 'individual';
			this.options.active_tax_entity.country = country;
			this.options.active_tax_entity.business_flag = (entityType === 'business');
		}

		$('#tax-form').show();
		$('#tax-edit-form').html($.tmpl(this.template2, this.padFields(this.options.active_tax_entity)));
		$('#save-tax-form-btn').enable();
		this.setHeader();

		// if switching from business to individual, clear irrelevant name fields - looks weird otherwise
		if (this.prevBusinessFlag) {
			if (!this.options.active_tax_entity.business_flag && !$("input[name='first_name']").val() && !$("input[name='middle_name']").val()) {
				$("input[name='last_name']").val("");
			}
		} else {
			if (this.options.active_tax_entity.business_flag)
				$("input[name='last_name']").val(this.options.active_tax_entity.last_name);
		}

		this.showHideLlc(this.isLlc(this.options.active_tax_entity));
		this.showHideBusinessDbaName(this.options.active_tax_entity.business_name_flag);

		wmMaskInput({ root: this.$el, selector: '#ssn input' }, 'ssn');
		wmMaskInput({ root: this.$el, selector: '#ein input' }, 'ein');
		wmMaskInput({ root: this.$el, selector: '#sin input' }, 'sin');
		wmMaskInput({ root: this.$el, selector: '#bin input' }, 'bin');
		wmMaskInput({ root: this.$el, selector: '#canada-postal-code input' }, 'postalCAN');

		$('#effective_date input').datepicker({dateFormat: 'mm/dd/yy', minDate: 0, defaultDate: 0});
		this.toggleEffectiveDate();

		const modalEntry = document.getElementById('foreign-tax-identifier-modal');

		if (modalEntry) {
			render(
				<Provider store={ store }>
					<WMNationalIdModal />
				</Provider>,
				modalEntry
			);
		}
	},

	isDisabled: function (o) {
		return $(o).is('.disabled');
	},

	saveTaxForm: function (event) {
		event.preventDefault();

		if (this.isDisabled(event.target)) return;
		$(event.target).addClass('disabled');
		var llcSelect = $('#llc_type select');
		if ($('[name=tax_entity_type_code]:checked').val() === 'llc') {
			$('input [name=tax_entity_type_code]').val(llcSelect.val());
		} else {
			llcSelect.detach(); // prevent it from being serialized
		}

		var saveModel = new FormModel($('#tax-form').serializeObject());
		if (!llcSelect.length) {
			llcSelect.appendTo($('#llc_type'));
		}

		var self = this;
		new ajaxSendInit();
		saveModel.save({}, {
			success: function (model, response) {
				if (!response.successful) {
					_.each(response.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
					$(event.target).removeClass('disabled');
					scroll(0, 0);
				} else {
					self.hideAllForms();
					$('.sidebar').hide();
					$('.inner-container').addClass('tax-container');
					$(self.el).hide();
					var taxEntity = JSON.parse(response.data.tax_entity);
					$.extend(self.options.active_tax_entity, taxEntity);
					if (taxEntity.tax_country.toLowerCase() === 'usa')
						self.options.router.navigate('sign', true);

					else {
						window.location.hash = '';
						window.location.reload(true); // refresh jsp
					}
				}
			},
			error: function (model, response) {
				// TODO: show errors inline
				console.log(response);
			}
		});
	}
});

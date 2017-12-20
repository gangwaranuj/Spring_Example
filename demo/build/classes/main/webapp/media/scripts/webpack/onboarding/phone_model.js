'use strict';

import _ from 'underscore';
import OnboardModel from './onboard_model';
import splitCamelCase from '../funcs/wmSplitCamelCase';
import PhoneNumber from 'awesome-phonenumber';
import libPhoneNumber from 'google-libphonenumber'; 
import $ from 'jquery';
import t from '@workmarket/translation';

export default OnboardModel.extend({
	defaults: function() {
		return {
			type: 'work',
			code: 609
		};
	},
	requiredFields: ['type', 'code', 'number'],

	validate: function (attributes) {
		const country = _.first(_.filter(this.collection.countryCodes, function (country) {
			return country.id === +attributes.code;
		}));

		var errors = _.reduce(this.requiredFields, function (memo, field) {
			if (!_.has(attributes, field) || !attributes[field]) {
				memo.push({
					name: field,
					message: splitCamelCase(field) + ' is a required field.',
					index: this.collection.indexOf(this)
				});
			}

			return memo;
		}, [], this);

		const regionCode = PhoneNumber.getRegionCodeForCountryCode(country.callingCodeId);
		const number = $('.phone--number').val() || '00';
		const PNF = libPhoneNumber.PhoneNumberFormat;
		const phoneUtil = libPhoneNumber.PhoneNumberUtil.getInstance();
		const pn = new PhoneNumber(number, regionCode);
		const phoneNumber = phoneUtil.parse(number, regionCode);
		const formatted = phoneUtil.format(phoneNumber, PNF.NATIONAL);

		if (!pn.isValid()) {
			errors.push({
				name: 'number',
				message: 'Please enter a valid phone number.',
				index: this.collection.indexOf(this)
			});
			$('.phone--number').parent().attr('data-float-label', t('errors.providePhoneNumber'));
			$('.phone-number-error').css('color', 'red');
		} else {
			let selectionStart = $('.phone--number').prop('selectionStart');
			let selectionEnd = $('.phone--number').prop('selectionEnd');
			if (number.length === selectionStart && formatted.length > number.length) {
				selectionStart = formatted.length;
				selectionEnd = selectionStart;
			}
			$('.phone--number').val(formatted);
			$('.phone--number').parent().attr('data-float-label', 'Number');
			$('.phone--number').parent().css('color', 'grey');
			$('.phone--number').prop('selectionStart', selectionStart);
			$('.phone--number').prop('selectionEnd', selectionEnd);
		}

		if (!_.isEmpty(errors)) {
			return errors;
		}
	}
});

'use strict';

import $ from 'jquery';
import _ from 'underscore';
import BaseTaxView from './tax_base_view';
import wmNotify from '../funcs/wmNotify';
import '../dependencies/jquery.tmpl';

export default BaseTaxView.extend({
	template: $('#tmpl-tax-view').template(),

	initialize: function (options) {
		this.el = options.el;
		this.model = {};
		this.model.attributes = {};
		var fields = $.extend({'tax_entities': this.options.tax_entities}, options.active_tax_entity);
		$.extend(this.model.attributes, fields);
	},

	render: function () {
		this.options.router.currentView = 'view';
		this.showViewForm();
	},

	showViewForm: function () {
		this.hideAllForms();
		var template = $.tmpl(this.template, this.padFields(this.model.attributes), this.formatters);
		$(this.el).html(template);
		this.setHeader();

		_.bindAll(this, 'showEditForm');
		$('#edit-tax-btn').click(this.showEditForm); // div is outside view, so bind manually

		// display notice if they have an entity pending approval
		if (this.options.active_tax_entity.country === 'usa') {
			var unverifiedEntity = this.getSubmittedUnverifiedEntity();
			if (unverifiedEntity) {
				wmNotify({
					message: 'Your tax information submitted on ' + unverifiedEntity.signature_date_string.substr(0, 10) + ' is pending approval by the Internal Revenue Service'
				});
				this.disableEditFormButton();
			}
		}
	},

	showEditForm: function () {
		this.options.router.navigate('edit', true);
	},

	// Attempts to find, and return, an ACTIVE and UNVERIFIED tax entity
	// If one exists it is returned, otherwise returns null
	getSubmittedUnverifiedEntity: function () {
		var unverifiedEntity = null;
		$.each(this.options.tax_entities, function (i, entity) {
			if (entity.active_flag && entity.tax_verification_status_code === 'unverified') {
				unverifiedEntity = entity;
			}
		});
		return unverifiedEntity;
	},

	disableEditFormButton: function () {
		var linkDiv = $('#edit-tax-btn');
		linkDiv.addClass('disabled').attr('disabled', 'disabled');
		linkDiv.addClass('tooltipped tooltipped-n').attr('aria-label', 'Editing is disabled when your tax information is being verified with the IRS');
	},

	formatters: {
		formatSsn: function (ssn) {
			return ssn.replace(/[*0-9]{3}-?[*0-9]{2}-?(\d{4})/, '***-**-' + '$1');
		},
		formatEin: function (ein) {
			return ein.replace(/[*0-9]{2}-?[*0-9]{3}(\d{4})/, '**-***' + '$1');
		},
		formatSin: function (sin) {
			return sin.replace(/[*0-9]{3}-?[*0-9]{3}-?(\d{3})/, '***-***-' + '$1');
		},
		formatBn: function (bn) {
			return bn.replace(/[*0-9]{9}-?[A-Z]+-?(\d{4})/, '********-**-' + '$1');
		},
		formatOther: function (other) {
			return (other.length < 4) ?
				'****' :
			other.substr(0, other.length - 4).replace(/[\d\w]*/, Array(other.length).join('*')) + other.substr(-4);
		}
	}
});

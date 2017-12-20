'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import '../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	tagName: 'div',

	events: {
		'click input[type="checkbox"]' : 'toggleOption'
	},

	initialize: function (options) {
		this.options = options || {};

		this.tmpl_options_singleline = $('#tmpl-options-singleline').template();
		this.tmpl_options_multiline = $('#tmpl-options-multiline').template();
		this.tmpl_options_radio = $('#tmpl-options-radio').template();
		this.tmpl_options_checkboxes = $('#tmpl-options-checkboxes').template();
		this.tmpl_options_dropdown = $('#tmpl-options-dropdown').template();
		this.tmpl_options_date = $('#tmpl-options-date').template();
		this.tmpl_options_phonenumber = $('#tmpl-options-phonenumber').template();
		this.tmpl_options_email = $('#tmpl-options-email').template();
		this.tmpl_options_numeric = $('#tmpl-options-numeric').template();
		this.tmpl_options_asset = $('#tmpl-options-asset').template();
	},

	render: function () {
		switch (this.options.type) {
			case 'singleline':
				$(this.el).html($.tmpl(this.tmpl_options_singleline));
				break;

			case 'multiline':
				$(this.el).html($.tmpl(this.tmpl_options_multiline));
				break;

			case 'radio':
				$(this.el).html($.tmpl(this.tmpl_options_radio));
				break;

			case 'checkboxes':
				$(this.el).html($.tmpl(this.tmpl_options_checkboxes));
				break;

			case 'dropdown':
				$(this.el).html($.tmpl(this.tmpl_options_dropdown));
				break;

			case 'date':
				$(this.el).html($.tmpl(this.tmpl_options_date));
				break;

			case 'phonenumber':
				$(this.el).html($.tmpl(this.tmpl_options_phonenumber));
				break;

			case 'email':
				$(this.el).html($.tmpl(this.tmpl_options_email));
				break;

			case 'numeric':
				$(this.el).html($.tmpl(this.tmpl_options_numeric));
				break;

			case 'asset':
				$(this.el).html($.tmpl(this.tmpl_options_asset));
				break;
		}

		return this;
	},

	toggleOption: function (event) {
		if (event.target) {
			var el = $(event.target);
		} else {
			var el = $(event);
		}

		if (el.is(':checked')) {
			el.parent().parent().find('.additional_fields').show();
			if (el.attr('name') == 'notGraded') {
				$('#manual-grading-notice').hide();
			} else if (el.attr('name') == 'otherAllowed') {
				$('#manual-grading-notice').show();
			}
		} else {
			el.parent().parent().find('.additional_fields').hide();
			if (el.attr('name') == 'notGraded') {
				if (el.attr('name') == 'notGraded') {
					if (this.options.type == 'singleline' ||
						this.options.type == 'multiline' ||
						this.options.type == 'date' ||
						this.options.type == 'phonenumber' ||
						this.options.type == 'email' ||
						this.options.type == 'numeric' ||
						this.options.type == 'asset'
					) {
						$('#manual-grading-notice').show();
					}
				}
			} else if (el.attr('name') == 'otherAllowed') {
				$('#manual-grading-notice').hide();
			}
		}
	}
});

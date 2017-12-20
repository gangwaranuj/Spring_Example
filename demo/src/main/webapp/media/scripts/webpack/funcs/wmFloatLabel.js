'use strict';

import $ from 'jquery';
import _ from 'underscore';

export default function (options) {

	var settings = {
		selector: '[data-float-label]',
		events: 'keyup change',
		classname: '-label',
		errorClassname: '-invalid',
		inputs: 'input, select, textarea',
		autobind: true,
		autorun: true,
		root: document
	};

	if (typeof options === 'object') { _.extend(settings, options); }

	var floatLabel = function (target) {
		if (_.isUndefined(target)) {
			target = this;
		} else if (!_.isElement(target)) {
			target = target.currentTarget;
		}

		var inputs = $(target).find(settings.inputs),
			value = function (input) { return $(input).val(); },
			isInvalid = $(target).hasClass(settings.errorClassname),
			isEmpty = function (value) { return _.isEmpty(value) || _.isNull(value); },
			allFieldsAreEmpty = _.every(inputs, _.compose(isEmpty, value)),
			shouldFloatLabel = !allFieldsAreEmpty || isInvalid;

		$(target).toggleClass(settings.classname, shouldFloatLabel);

		return shouldFloatLabel;
	};

	if (settings.autobind) {
		if (_.isUndefined(settings.selector) || _.isNull(settings.selector)) {
			$(settings.root).on(settings.events, floatLabel);
		} else {
			$(settings.root).on(settings.events, settings.selector, floatLabel);
		}
	}

	if (settings.autorun) {
		return _.each($(settings.root).find(settings.selector), floatLabel);
	} else {
		return false;
	}
};

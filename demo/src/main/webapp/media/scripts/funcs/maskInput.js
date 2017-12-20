/*global _*/

var wm = wm || {};
wm.funcs = wm.funcs || {};

wm.funcs.maskInput = function (options, pattern, maskOptions) {
	'use strict';

	// If arguments are out of order
	if (_.isString(options)) {
		pattern = arguments[0];
		maskOptions = arguments[1];
	}
	// If arguments are in order
	else if (_.isObject(options) || _.isUndefined(options)) {
		pattern = pattern || '';
	}

	var patterns = {
		tel: '(000)000-0000',
		ssn: '000-00-0000',
		ein: '00-0000000',
		sin: '000-000-000',
		bin: '000000000-SS-0000',
		usd: '000.000.000.000.000,00',
		postalCAN: 'S0S 0S0'
	};

	var settings = _.extend({
		selector: '[data-mask]',
		events: 'keyup change',
		autobind: true,
		autorun: true,
		root: document
	}, _.isObject(options) ? options : {});

	var maskSettings = _.extend({}, _.isObject(maskOptions) ? maskOptions : {});

	if (settings.autobind) {
		$(settings.root).on(settings.events, settings.selector, maskInput);
	}

	if (settings.autorun) {
		return _.map($(settings.root).find(settings.selector), maskInput);
	} else {
		return false;
	}

	function maskInput(element) {
		var target;
		if (_.isElement(element)) {
			target = element;
		} else {
			target = element.currentTarget;
		}

		var inputType = target.hasAttribute('type') && target.getAttribute('type');
		var hasInputType = inputType && patterns.hasOwnProperty(inputType);

		var maskPattern = '';
		if (pattern !== '') {
			maskPattern = patterns[pattern] || pattern;
		} else if (hasInputType) {
			maskPattern = patterns[inputType];
		}

		$(target).mask(maskPattern, maskSettings);

		return target;
	}
};

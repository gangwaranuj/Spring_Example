import $ from 'jquery';
import _ from 'underscore';
import 'jquery-mask-plugin';

export default (options, pattern, maskOptions) => {
	// If arguments are out of order
	if (_.isString(options)) {
		[pattern, maskOptions] = arguments; // eslint-disable-line no-param-reassign
	} else if (_.isObject(options) || _.isUndefined(options)) { // If arguments are in order
		pattern = pattern || ''; // eslint-disable-line no-param-reassign
	}

	const patterns = {
		tel: '(000) 000-0000',
		ssn: '000-00-0000',
		ein: '00-0000000',
		sin: '000-000-000',
		bin: '000000000-SS-0000',
		usd: '000.000.000.000.000,00',
		postalCAN: 'S0S 0S0'
	};

	const settings = _.extend({
		selector: '[data-mask]',
		events: 'keyup change',
		autobind: true,
		autorun: true,
		root: document
	}, _.isObject(options) ? options : {});
	const { root, events, selector, autobind, autorun } = settings;

	const maskSettings = _.extend({}, _.isObject(maskOptions) ? maskOptions : {});

	const maskInput = (element) => {
		const target = _.isElement(element) ? element : element.currentTarget;
		const inputType = target.hasAttribute('type') && target.getAttribute('type');
		// eslint-disable-next-line no-prototype-builtins
		const hasInputType = inputType && patterns.hasOwnProperty(inputType);

		let maskPattern = '';
		if (pattern !== '') {
			maskPattern = patterns[pattern] || pattern;
		} else if (hasInputType) {
			maskPattern = patterns[inputType];
		}

		$(target).mask(maskPattern, maskSettings);

		// cursor bug fix
		// phone number mask will incorrectly place cursor after first digit is entered
		// make sure the cursor position is set to the end of the inputType
		if (inputType === 'tel') {
			const val = target.value;
			target.value = '';
			target.value = val;
		}

		return target;
	};

	if (autobind) {
		$(root).on(events, selector, maskInput);
	}

	return autorun && _.map($(root).find(selector), maskInput);
};

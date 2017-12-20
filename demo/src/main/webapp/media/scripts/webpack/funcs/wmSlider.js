

import $ from 'jquery';
import _ from 'underscore';

export default function (options) {
	const settings = Object.assign({
		selector: '[data-slider]',
		events: 'change input',
		autobind: true,
		autorun: true,
		root: document
	}, typeof options === 'object' ? options : {});

	const outputSliderSelection = function (element) {
		let target;

		if (typeof element === 'undefined') {
			target = this;
		} else if (_.isElement(element)) {
			target = element;
		} else {
			target = element.target;
		}

		let slider = $(target).closest('.wm-slider').get(0),
			sliderValue = parseInt(target.value, 10),
			sliderMaxValue = parseInt(target.getAttribute('max')),
			sliderMinValue = parseInt(target.getAttribute('min')),
			sliderUnit = slider.getAttribute('data-slider-unit'),
			sliderUnitsPosition = slider.getAttribute('data-slider-units-position'),
			sliderOutput = getSliderOutput(sliderValue, sliderUnit, sliderUnitsPosition);

		slider.setAttribute('data-slider-value', sliderOutput);

		return true;
	};

	const getSliderOutput = (sliderValue, sliderUnit, sliderUnitsPosition) => {
		if (sliderUnitsPosition === 'first') {
			return `${sliderUnit || ''} ${sliderValue}`;
		} else {
			return `${sliderValue} ${sliderUnit || ''}`;
		}
	};

	if (settings.autobind) {
		$(settings.root).on(settings.events, settings.selector, outputSliderSelection);
	}

	if (settings.autorun) {
		return Array.from($(settings.selector, settings.root)).map(outputSliderSelection);
	}
	return false;
}

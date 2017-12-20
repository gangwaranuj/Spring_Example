import _ from 'underscore';

export default function (labels) {
	var result = '';
	_.each(labels, function (value, key) {
		result += '<span class="label important tooltipped tooltipped-n" aria-label="' + value + '">' + key + '</span>';
	});
};
import _ from 'underscore';

export default function () {
	'use strict';
	var args = _.toArray(arguments),
		options = args.pop();
	var isGreater = _.every(args, function (arg, index) {
		if (index === 0) {
			return true;
		} else {
			return arg < args[index - 1];
		}
	});
	if (isGreater){
		return options.fn(this);
	} else {
		return options.inverse(this);
	}
};

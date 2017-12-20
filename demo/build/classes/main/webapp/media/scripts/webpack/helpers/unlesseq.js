import _ from 'underscore';

export default function () {
	'use strict';
	let args = _.toArray(arguments),
		options = args.pop();
	if (_.every(args, function (arg) { return arg === args[0]; })){
		return options.inverse(this);
	} else {
		return options.fn(this);
	}
};

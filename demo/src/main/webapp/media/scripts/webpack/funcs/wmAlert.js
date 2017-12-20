'use strict';

import wmNotify from '../funcs/wmNotify';
import _ from 'underscore';

export default (options) => {

	var settings = _.extend({
		element: '.wm-alert-center',
		position: 'relative',
		delay: 0,
		offset: {
			x: 0,
			y: 0
		},
		notificationType: 'alert'
	}, _.isObject(options) ? options : {});

	return wmNotify(settings);
};

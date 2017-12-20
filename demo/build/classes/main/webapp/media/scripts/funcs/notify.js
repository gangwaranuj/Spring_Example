/*jshint camelcase: false*/
/*global _*/

var wm = wm || {};
wm.funcs = wm.funcs || {};

wm.funcs.notify = function (options) {
	'use strict';

	var settings = _.extend({
		messages: [],
		title: '',
		type: 'success',
		placement: {
			from: 'top',
			align: 'right'
		},
		offset: {
			x: 20,
			y: 60
		},
		delay: options && options.type === 'danger' ? 0 : 6000,
		template: _.template($('#growl-alert-tmpl').html())({
			type: '{0}',
			callout: '{1}',
			text: '{2}',
			button: options && options.button ? options.button : ''
		}),
		notificationType: 'notify'
	}, _.isObject(options) ? options : {});

	if (_.isString(options)) {
		settings.message = options;
	} else if (_.isArray(options)) {
		settings.messages = options;
	}

	if (settings.message) {
		settings.messages.push(settings.message);
	}

	return settings.messages.map(function (message) {
		var notify = $.notify({
			message: message,
			title: settings.title
		}, settings);
		notify.$ele.addClass('-' + settings.notificationType);
		return notify;
	});
};


wm.funcs.alert = function (options) {
	'use strict';

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

	return wm.funcs.notify(settings);
};

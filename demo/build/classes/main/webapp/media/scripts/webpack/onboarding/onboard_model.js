'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import ajaxSendInit from '../funcs/ajaxSendInit';

export default Backbone.Model.extend({
	initialize: function () {
		ajaxSendInit();
	},

	fields: function () { return []; },

	url: function () {
		return '/onboarding/profiles/' + this.id + '?flds=' + this.fields().join(',');
	},

	set: function (...args) {
		var ga = ga || function () {};

		// ga tracking of completed forms
		if (_.isObject(args[0])) {
			_.each(args[0], (value, key) => ga('send', 'event', 'onboarding', key, value));
		} else {
			analytics.track('Worker Onboarding', {
				action: 'set',
				field: args[0],
				category: 'onboarding',
				label: args[1]
			});
		}

		return Backbone.Model.prototype.set.apply(this, args);
	},

	update: function () {
		var externalObjects = _.map(this.externalFields, function (field) { return this[field]; }, this),
			externalJSON = _.map(externalObjects, function (object) { return object.toJSON(); }),
			attributes = _.defaults(_.object(this.externalFields, externalJSON), this.toJSON());

		if (this.isValid()) {
			return this.save(attributes);
		} else {
			return $.Deferred().reject();
		}
	}
});

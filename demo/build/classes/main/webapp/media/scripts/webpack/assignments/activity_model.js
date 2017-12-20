'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import underscoreToCamelCase from '../funcs/wmUnderscoreToCamelCase';
import moment from 'moment';

export default Backbone.Model.extend({
	sync: function (method, model, options) {
		Backbone.sync.call(this, method, model, _.extend(options, { emulateJSON: false, emulateHTTP: false }));
	},

	defaults: function () {
		return {
			isWorkStatusChange: false,
			isWorkSubStatusChange: false,
			isWorkResourceStatusChange: false,
			isWorkProperty: false,
			isWorkCreated: false,
			isWorkNoteCreated: false,
			isWorkNegotiationRequested: false,
			isWorkNegotiationExpired: false,
			isWorkRescheduleRequested: false,
			isWorkQuestionAsked: false,
			isAlert: false,
			status: '',
			subStatus: new Backbone.Model(),
			rejectionAction: false,
			onBehalfOfUser: null
		};
	},

	parse: function (response) {
		response = _.omit(response, _.isNull);

		// By prepending the `is_` before we pass it into the util function, we'll
		// get something like this back:
		// WORK_RESOURCE_STATUS_CHANGE => isWorkResourceStatusChange
		// which we can then use to directly access the model.
		if (!_.isUndefined(response.type)) {
			var type = underscoreToCamelCase('is_' + response.type);
			response[type] = true;
		}

		// There are several activities which in certain states will be flagged
		// as an alert.
		if (!_.isUndefined(response.status)) {
			var didWorkEnd = _.contains(['Cancelled', 'Void', 'Deleted', 'Cancelled - Payment Pending', 'Cancelled and Paid'], response.status),
				isWorkStatusChangeAlert = response.isWorkStatusChange && didWorkEnd,
				isWorkResourceStatusChangeAlert = response.isWorkResourceStatusChange && response.status === 'cancelled';

			if (isWorkStatusChangeAlert || isWorkResourceStatusChangeAlert) {
				response.isAlert = true;
			}
		}

		if (!_.isUndefined(response.timestamp)) {
			response.timestampDate = moment(response.timestamp).format('M/DD/YY h:mma');
			response.timestamp = moment(response.timestamp).unix();
		}

		if (!_.isUndefined(response.subStatus)) {
			response.subStatus = new Backbone.Model(response.subStatus);
		}

		return response;
	},

	toJSON: function () {
		var json = Backbone.Model.prototype.toJSON.call(this);
		json.subStatus = this.get('subStatus').toJSON();

		return json;
	}
});

'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import trimStringToLimit from '../funcs/wmTrimStringToLimit';

export default Backbone.Model.extend({
	idAttribute: 'uuid',
	sync: function (method, model, options) {
		Backbone.sync.call(this, method, model, _.extend(options, { emulateJSON: false, emulateHTTP: false }));
	},

	validate: function (attributes) {
		var constants = this.collection.partsConstants,
		NAME_MAX = constants.NAME_MAX,
		TRACKING_NUMBER_MAX = constants.TRACKING_NUMBER_MAX,
		PART_VALUE_MAX = constants.PART_VALUE_MAX,
		PART_VALUE_MIN = constants.PART_VALUE_MIN;

		var message = { isReturn: attributes.isReturn };

		if (_.isEmpty(attributes.trackingNumber)) {
			return _.extend(message, { message: 'Please enter a tracking number.' });
		} else if (attributes.trackingNumber.length > TRACKING_NUMBER_MAX) {
			return _.extend(message, { message: 'Tracking Number cannot exceed ' + TRACKING_NUMBER_MAX + ' characters.'});
		}

		if (_.isNaN(Number(attributes.partValue))) {
			return _.extend(message, { message: 'Please enter a part value.' });
		} else if (attributes.partValue > PART_VALUE_MAX || attributes.partValue < PART_VALUE_MIN) {
			return _.extend(message, { message: 'Part value is out of range. Please enter a value between ' + PART_VALUE_MIN + ' and ' + PART_VALUE_MAX });
		}

		if (_.isEmpty(attributes.name)) {
			return _.extend(message, { message: 'Please enter a part name.' });
		} else if (attributes.name.length > NAME_MAX) {
			return _.extend(message, { message: 'Part name cannot exceed ' + NAME_MAX + ' characters.' });
		}
	},

	parse: function (part) {

		// part will either be a hash of part properties
		// OR an ajax response object with meta object
		part = part.data || part;
		part = part.part || part;
		part.trackingStatus = part.trackingStatus || 'n/a'

		return _.extend(part, {
			hasProviderIcon: _.contains(['UPS', 'USPS', 'DHL', 'FEDEX'], part.shippingProvider),
			displayName: trimStringToLimit(part.name, 10),
			trackingNumber: trimStringToLimit(part.trackingNumber, 22),
			providerUrl: this.getProviderUrl(part.shippingProvider, part.trackingNumber)
		});
	},

	getProviderUrl: function (provider, trackingNumber) {
		switch (provider) {
			case 'UPS':
				return 'http://wwwapps.ups.com/WebTracking/track?track=yes&trackNums=' + trackingNumber;
			case 'USPS':
				return 'https://tools.usps.com/go/TrackConfirmAction_input?qtc_tLabels1=' + trackingNumber;
			case 'DHL':
				// this URL will only work for DHL in the US... at a later date,
				// we might want to pass a flag for country code... and give a global number
				// that would look like this:
				// return this.isNational() ? 'http://track.dhl-usa.com/TrackByNbr.asp?ShipmentNumber=' + trackingNumber : 'http://webtrack.dhlglobalmail.com/?mobile=&trackingnumber=' + trackingNumber
				return 'http://track.dhl-usa.com/TrackByNbr.asp?ShipmentNumber=' + trackingNumber;
			case 'FEDEX':
				return 'http://www.fedex.com/Tracking?action=track&tracknumbers=' + trackingNumber;
			default:
				return 'javascript:void(0)';
		}
	}
});

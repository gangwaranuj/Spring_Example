'use strict';

import $ from 'jquery';
import _ from 'underscore';

export default function (options) {

	var settings = _.extend({
		url: '/onboarding/available_assignments',
		required: [
			{
				name: 'lon',
				isValid: function (longitude) {
					return _.isNumber(longitude) || (_.isString(longitude) && !_.isEmpty(longitude));
				}
			},
			{
				name: 'lat',
				isValid: function (latitude) {
					return _.isNumber(latitude) || (_.isString(latitude) && !_.isEmpty(latitude));
				}
			},
			{
				name: 'd',
				isValid: function (distance) {
					return (_.isNumber(distance) && distance >= 0) || (_.isString(distance) && !_.isEmpty(distance));
				}
			},
			{
				name: 'w',
				isValid: function (when) {
					return when === 'all';
				}
			},
			{
				name: 'res',
				isValid: function(includeResults) {
					return _.isBoolean(includeResults);
				}
			}
		]
	}, options);

	var fetchedResults = $.Deferred();

	var isInSettings = _.partial(_.contains, _.keys(settings));
	var isValid = _.every(settings.required, function (requiredParameter) {
		return isInSettings(requiredParameter.name) && requiredParameter.isValid(settings[requiredParameter.name]);
	});

	if (isValid) {
		fetchedResults = $.get(settings.url, _.pick(settings, _.pluck(settings.required, 'name')));
	}

	return isValid ? fetchedResults.promise() : fetchedResults.reject();
};

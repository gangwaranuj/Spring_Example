'use strict';

import $ from 'jquery';
import _ from 'underscore';

export default function (input) {
	var deferred = $.Deferred(),
		reader = new FileReader();

	reader.onloadend = function () {
		deferred.resolve(reader.result);
	};

	if (!_.isUndefined(input)) {
		reader.readAsDataURL(input.files[0]);
		return deferred.promise();
	} else {
		return deferred.reject();
	}
};
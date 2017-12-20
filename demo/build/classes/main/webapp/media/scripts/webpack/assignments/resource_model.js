'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({

	initialize: function (options) {
		this.set({
			userId: options.user_id,
			userNumber: options.user_number,
			latitude: options.latitude,
			longitude: options.longitude
		});
	}
});

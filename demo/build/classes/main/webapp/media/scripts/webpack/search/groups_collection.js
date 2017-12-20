'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/groups/fetch_all',

	parse: function (response) {
		return response.data.groups;
	}
});

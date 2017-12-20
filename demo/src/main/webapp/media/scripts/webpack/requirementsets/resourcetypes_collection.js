'use strict';

import Backbone from 'backbone';

export default  Backbone.Collection.extend({
	url: '/resource_types',

	comparator: function (resourceType) {
		return resourceType.get('name');
	}
});

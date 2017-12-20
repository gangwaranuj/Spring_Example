'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/assignments/upload/mappings.json',
	comparator: function (mapping) {
		return mapping.get('name');
	}
});


'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/assignments/upload/templates.json',
	comparator: function (template) {
		return template.get('name');
	}
});

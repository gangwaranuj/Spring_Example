'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/assignments/upload/labels.json',
	comparator: function (label) {
		return label.get('id');
	}
});

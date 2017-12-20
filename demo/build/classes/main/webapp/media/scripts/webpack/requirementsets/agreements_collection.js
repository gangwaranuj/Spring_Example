'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/agreements/fetch',

	comparator: function (agreement) {
		return agreement.get('name');
	}
});

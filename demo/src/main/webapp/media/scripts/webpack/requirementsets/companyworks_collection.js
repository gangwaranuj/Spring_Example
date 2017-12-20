'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/company_works',
	comparator: function (company) {
		return company.get('id');
	}
});

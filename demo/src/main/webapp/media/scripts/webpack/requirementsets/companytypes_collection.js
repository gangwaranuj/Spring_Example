'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: '/company_types',
	comparator: function (companyType) {
		return companyType.get('name');
	}
});


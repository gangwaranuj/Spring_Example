'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	url: function() {
		return '/profile-edit/certificationslist?industry=' + this.industryId;
	},
	comparator: function(provider) {
		return provider.get('name');
	}
});

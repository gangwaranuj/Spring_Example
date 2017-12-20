'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	defaults: {
		people      : 'people-search',
		group       : 'group',
		groupDetail : 'group-detail',
		assessment  : 'assessment',
		assignment  : 'assignment'
	}
});

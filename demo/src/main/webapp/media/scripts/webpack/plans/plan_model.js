'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	sync: Backbone.syncWithJSON,
	defaults: {
		planConfigs: []
	}
});

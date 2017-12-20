'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	url: '/assignments/create_bundle',
	sync: Backbone.syncWithJSON
});

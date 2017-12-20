'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({
	sync: Backbone.syncWithJSON
});

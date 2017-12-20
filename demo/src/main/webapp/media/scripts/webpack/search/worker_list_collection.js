'use strict';

import Backbone from 'backbone';

export default Backbone.Collection.extend({

	// override add function to avoid duplicate
	add: function (worker) {
		var isDupe = this.any(function (_worker) {
			return _worker.get('userNumber') === worker.get('userNumber');
		});
		if (isDupe) {
			return false;
		} else {
			Backbone.Collection.prototype.add.call(this, worker);
		}
	}
});

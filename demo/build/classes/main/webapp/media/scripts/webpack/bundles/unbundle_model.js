'use strict';

import Backbone from 'backbone';

export default Backbone.Model.extend({
	url: function () {
		return '/assignments/remove_from_bundle/' + this.attributes.assignmentId;
	}
});

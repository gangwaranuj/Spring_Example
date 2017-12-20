'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import ActivityModel from './activity_model';

export default Backbone.Collection.extend({
	model: ActivityModel,
	sync: Backbone.syncWithJSON,
	url: function () {
		return '/assignments/' + this.id + '/activities';
	},

	initialize: function (models, options) {
		_.extend(this, options);
	}
});

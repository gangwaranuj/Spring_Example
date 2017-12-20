'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import MessageModel from './message_model';

export default Backbone.Collection.extend({
	model: MessageModel,
	id: 0,
	isResource: false,
	hasQuestions: false,
	sync: Backbone.syncWithJSON,
	url: function () {
		return '/assignments/' + this.id + (this.hasQuestions ? '/questions' : '/messages');
	},

	initialize: function (models, options) {
		_.extend(this, options);
		this.fetch({ reset: true });
	},

	parse: function (response) {
		return response.results;
	}
});

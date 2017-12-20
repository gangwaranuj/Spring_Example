'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import PartsModel from './parts_withtracking_model';

export default Backbone.Collection.extend({
	model: PartsModel,

	url: function () {
		return this.isMobile ? '/mobile/assignments/parts/' + this.workNumber : '/assignments/' + this.workNumber + '/parts';
	},

	sync: Backbone.syncWithJSON,

	initialize: function (models, options) {
		_.extend(this, options);
	},

	calculateTotalPrice: function (isReturn) {
		var prices = this.where({ isReturn : isReturn }).map(function (part) {
			return part.get('partValue');
		});
		return _.reduce(prices, function (memo, price) {
			return memo + Number(price);
		}, 0).toFixed(2);
	},

	parse: function (response) {
		response = response || {};
		var data = response.data || {};
		return data.parts || [];
	}
});

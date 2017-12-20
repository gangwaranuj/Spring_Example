'use strict';

import Backbone from 'backbone';
import WorkerModel from './resource_model';

let Collection = Backbone.Collection.extend({
	model: WorkerModel,
	byStatus: function (status) {
		return this.filter(function (resource) {
			return resource.get('status') === status;
		});
	},

	byNegotiationStatus: function () {
		return new Collection(
			this.filter(function (resource) {
				return resource.get('negotiation') != null;
			})
		);
	},

	moveToFirst: function (model) {
		this.remove(model);
		this.add(model, {at: 0});
		return this;
	}
});

export default Collection;

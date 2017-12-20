'use strict';

import Backbone from 'backbone';
import DeliverableAssetModel from './deliverable_asset_model';
import _ from 'underscore';

export default Backbone.Collection.extend({
	model: DeliverableAssetModel,
	comparator: function (model) {
		return model.get('position');
	},

	getByPosition: function (index) {
		return _.find(this.models, function (model) {
			return model.get('position') === index;
		});
	},

	removeByPosition: function (index) {
		this.remove(this.getByPosition(index));
	},

	getPositionAtIndex: function (index) {
		return this.at(index).get('position');
	}

});

'use strict';

import Backbone from 'backbone';
import _ from 'underscore';

let Model =  Backbone.Model.extend({
	initialize: function (options) {
		this.set('uriBig', '/asset/' + options.uuid);
		this.set('assetHistory', options.assetHistory || []);

		// If asset has an asset history, and if asset is not rejected, and first asset in the history IS rejected
		// Then this is an updated asset
		// We create a 'rejectedAsset' property to provide some convenience for accessing a rejected asset's properties
		// It's nice to call the same backbone functions on the rejected asset that we do on the updated asset
		if (this.get('assetHistory').length && !options.rejectedOn && _.first(this.get('assetHistory')).rejectedOn) {
			this.set('rejectedAsset', new Model(_.first(options.assetHistory)));
		}
		if (options.largeUuid || options.transformLargeUuid) {
			this.set('uri', '/asset/' + (options.largeUuid || options.transformLargeUuid));
		}
	},

	popAssetFromHistory: function (callbacks) {
		callbacks = callbacks || {};

		var nextAsset = _.first(this.get('assetHistory'));

		// If there's no assets in the history, destroy this asset
		if (!nextAsset) {
			if (typeof callbacks.beforeDestroy === 'function') {
				callbacks.beforeDestroy();
			}
			this.trigger('destroy', this);
		// Otherwise, grab the next asset in the history
		} else {
			nextAsset.assetHistory = _.rest(this.get('assetHistory'));
			this.clear({ silent:true }).set(nextAsset);
		}
		return nextAsset;
	},

	/**
	 *
	 * @param assetToAdd - a JSON representation of the asset to be added. Will be used for the backbone asset constructor
	 */
	pushAssetToHistory: function (assetToAdd) {

		// Get reference to asset history
		var assetHistory = this.get('assetHistory');

		// Remove asset at head's reference to the history
		this.unset('assetHistory');

		// Push asset at head to the front of the asset history
		assetHistory.unshift(this.toJSON());

		// Give new asset a reference to the asset history
		assetToAdd.assetHistory = assetHistory;

		// Initialize new asset as a backbone model
		var deliverableAssetModel = new Model(assetToAdd);

		// Replace new asset into old assets place in the parent collection
		this.clear({ silent:true }).set(deliverableAssetModel.toJSON());
	}

});

export default Model;

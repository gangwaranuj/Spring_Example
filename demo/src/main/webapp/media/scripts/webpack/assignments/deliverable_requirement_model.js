'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import isValidImageExtension from '../funcs/wmIsValidImageExtension';
import isValidFileExtension from '../funcs/wmIsValidFileExtension';

export default Backbone.Model.extend({
	initialize: function () {
		this.FILE_SIZE_EXCEEDED_MESSAGE = 'Uploads cannot exceed ' + (this.get('MAX_UPLOAD_SIZE')/1024/1024) + 'MB.';
		this.positionOfSelectedAsset = 0;

		if (this.isPhotoDeliverableType()) {
			this.VALIDATION_FUNCTION = isValidImageExtension;
			this.UNSUPPORTED_FILE_MESSAGE = this.get('UNSUPPORTED_IMAGE_FILE_MESSAGE');
		} else {
			this.VALIDATION_FUNCTION = isValidFileExtension;
			this.UNSUPPORTED_FILE_MESSAGE = this.get('UNSUPPORTED_FILE_MESSAGE');
		}
	},

	isComplete: function () {
		return this.getNumberOfNonRejectedUploads() >= this.get('numberOfFiles');
	},

	needPlaceholder: function () {
		return this.getPlaceholdersNeeded() > 0;
	},

	getPlaceholdersNeeded: function () {
		return Math.max(this.get('numberOfFiles') - this.deliverableAssets.size(), 0);
	},

	getNumberOfMissingAssets: function () {
		return Math.max(this.get('numberOfFiles') - this.getNumberOfNonRejectedUploads(), 0);
	},

	hasAtLeastOneNonRejectedAsset: function () {
		return _.some(this.deliverableAssets.models, function (deliverableAsset) {
			return _.isUndefined(deliverableAsset.get('rejectedOn'));
		});
	},

	getPositionOfSelectedAsset: function () {
		return this.positionOfSelectedAsset;
	},

	setPositionOfSelectedAsset: function (position) {
		this.positionOfSelectedAsset = position;
		this.trigger('setPositionOfSelectedAsset');
	},

	// The algorithm: ((indexOfSelectedAsset + increment) % size + size) % size;
	incrementPositionOfSelectedAsset: function (byNumber) {
		if (_.isUndefined(byNumber)) {
			 byNumber = 1;
		}

		// If selectedAsset is not present, there are no assets in the collection
		if (!this.getSelectedAsset()) {
			return;
		}

		var nextIndex =
			((this.getIndexOfSelectedAsset() + byNumber) % this.deliverableAssets.length + this.deliverableAssets.length) % this.deliverableAssets.length;
		this.setPositionOfSelectedAsset(this.deliverableAssets.getPositionAtIndex(nextIndex));
	},

	getSelectedAsset: function () {
		return this.deliverableAssets.getByPosition(this.getPositionOfSelectedAsset());
	},

	getIndexOfSelectedAsset: function () {
		return this.deliverableAssets.indexOf(this.getSelectedAsset());
	},

	getNumberOfNonRejectedUploads: function () {
		return this.deliverableAssets.filter(function (asset) {
			return _.isUndefined(asset.get('rejectedOn'));
		}, this).length;
	},


	getNextPosition: function () {
		if (this.deliverableAssets.isEmpty()) {
			return 0;
		}
		return _.max(this.deliverableAssets.map(function (asset) {
			return asset.get('position');
		})) + 1;
	},

	isPhotoDeliverableType: function () {
		return this.get('type') === this.get('DELIVERABLE_TYPES').photos.type;
	},

	/**
	 * @param upload - parameter object containing:
	 *  name - name of file
	 *  size - in bytes (1mb = 1024 * 1024 bytes)
	 */
	validateUpload: function (upload) {
		var uploadErrors = [];

		// extract extension from filename
		if (typeof upload.name !== 'undefined') {
			var extension = upload.name.replace(/^.+\./,'').toLowerCase();
		}

		// File type checking
		if (!extension) {
			uploadErrors.push(upload.name + ': ' + this.get('INVALID_FILE_MESSAGE'));
		} else if (!this.VALIDATION_FUNCTION(extension)) {
			uploadErrors.push(upload.name + ': ' + this.UNSUPPORTED_FILE_MESSAGE);
		}

		// File size checking
		if (upload.size > this.get('MAX_UPLOAD_SIZE')) {
			uploadErrors.push(upload.name + ': ' + this.FILE_SIZE_EXCEEDED_MESSAGE);
		}

		return { name: upload.name, errors: uploadErrors };
	}
});

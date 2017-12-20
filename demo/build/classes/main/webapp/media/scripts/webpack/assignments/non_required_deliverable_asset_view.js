'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import '../funcs/autoresizeTextarea';
import Template from './templates/details/non_required_deliverable_asset.hbs';
import wmModal from '../funcs/wmModal';

/*
 *  Lowest-level view in the Non Required Deliverable hierarchy.
 *  This view represents an actual asset (file).
 */
export default Backbone.View.extend({
	tagName: 'li',
	className: 'non-required-deliverable row',
	events: {
		'mouseenter .deliverable-icon-container'      : 'showOptions',
		'mouseleave .deliverable-icon-container'      : 'hideOptions',
		'click .asset-add-option, .asset-edit-option' : 'addOrEditDescription',
		'click .asset-cancel-option'                  : 'cancelEditDescription',
		'click .asset-save-option'                    : 'saveDescription',
		'click .remove'                               : 'removeAsset'
	},
	template: Template,

	render: function () {
		var filename = this.model.name;
		filename = _.isUndefined(filename) ? '' : filename;
		var extension = filename.split('.').pop();
		if (extension === filename) {
			// If the file does not have an extension, use "MISC" as the extension
			extension = 'MISC';
		}

		this.model = _.extend(this.model, {
			extension: extension,
			// only clients are permitted to futz with documents, active workers can futz with closeout deliverables
			isPermitted: this.model.type === 'attachment' ? this.options.isAdmin : (this.options.isAdmin || this.options.isActiveWorker),
			extensionSize: this.extensionSwitch(extension),
			upperExtension: extension.toUpperCase(),
			containsImageMimeType: _.contains(['image/jpeg', 'image/pjpeg', 'image/jpg', 'image/tiff', 'image/png'], this.model.mimeType),
			visibilitySettings: [
				{'level':0, 'code':'internal', 'icon-code': 'internal', 'description':'Internal Only'},
				{'level':1, 'code':'assigned_worker', 'icon-code': 'assigned-worker','description':'Assigned Worker Only'},
				{'level':2, 'code':'public', 'icon-code': 'public', 'description':'Assigned and Potential Workers'}
			]
			// is admin
		});

		this.$el.html(this.template(this.model));
		this.$assetDescription = this.$el.find('.asset-description');
		this.$assetOptions = this.$el.find('.asset-options');

		return this;
	},

	extensionSwitch: function (extension) {
		var xCoordinate;
		if (!_.isUndefined(extension)) {
			if (_.size(extension) === 3) {
				if (extension === "zip") {
					xCoordinate='x="4.5"'
				} else if (extension === "gif") {
					xCoordinate='x="4"'
				} else if (extension === "txt") {
					xCoordinate='x="3.4"'
				} else if (extension === "png" || extension === "bmp") {
					xCoordinate='x="2.7"'
				} else if (extension === "pdf" || extension === "xls" || extension === "f4v" || extension === "f4a" || extension === "jpg") {
					xCoordinate='x="3"'
				} else if (extension === "mov") {
					xCoordinate='x="1.5"'
				} else if (extension === "mp4" || extension === "m4v" || extension === "m4a" || extension === "mp3") {
					xCoordinate='x="2.5"'
				} else if (extension === "flv") {
					xCoordinate='x="3.5"'
				} else {
					xCoordinate='x="2.3"'
				}
				return xCoordinate + ' y="17.5" font-size="7pt"';
			} else {
				if (extension === "xlsx" || extension === "jpeg") {
					xCoordinate='x="3.5"'
				} else if (extension === "docx") {
					xCoordinate='x="2"'
				} else if (extension === "tiff") {
					xCoordinate='x="5"'
				} else {
					xCoordinate='x="3"'
				}
				return xCoordinate + 'y="17" font-size="5pt"'
			}
		}
	},

	showOptions: function () {
		this.$('.deliverable-icon').addClass('darken');
	},

	hideOptions: function () {
		this.$('.deliverable-icon').removeClass('darken');
	},

	addOrEditDescription: function () {
		this.snapshotDescriptionText();
		this.toggleDescriptionField();
		this.renderEditDescriptionOptionsView();
		this.$assetDescription.autoresizeTextarea();
	},

	saveDescription: function () {
		this.saveDescriptionToServer(function () {
			this.snapshotDescriptionText();
			this.renderReadOnlyDescriptionView();
		});
	},

	cancelEditDescription: function () {
		this.setDescriptionText(this.snapshotText);
		this.$assetDescription.autoresizeTextarea();
		this.renderReadOnlyDescriptionView();
	},

	renderReadOnlyDescriptionView: function () {
		this.toggleDescriptionField({
			textPresentCallback: this.renderDescriptionOptionsView,
			textNotPresentCallback: this.renderNoDescriptionOptionsView
		});
	},

	toggleDescriptionField: function (callbacks) {
		callbacks = callbacks || {};

		if (this.snapshotText !== '') {
			this.$assetDescription.toggleClass('readonly');
			if (callbacks.textPresentCallback) {
				callbacks.textPresentCallback.call(this);
			}
		} else {
			this.$assetDescription.toggle();
			if (callbacks.textNotPresentCallback) {
				callbacks.textNotPresentCallback.call(this);
			}
		}
	},

	snapshotDescriptionText: function () {
		this.snapshotText = this.getDescriptionText();
	},

	getDescriptionText: function () {
		return this.$assetDescription.val().trim();
	},

	setDescriptionText: function (text) {
		this.$assetDescription.text(text);
		this.$assetDescription.val(text);
	},

	removeAsset: function () {
		var modal = wmModal({
			autorun: true,
			title: 'Remove File',
			content: 'Are you sure you want to remove the selected attachment?',
			destroyOnClose: true,
			controls: [
				{
					text: 'No',
					close: true
				},
				{
					text: 'Yes',
					primary: true
				}
			]
		});

		$('.wm-modal .-active').find('.-primary').on('click', () => {
			$.ajax({
				url: '/assignments/remove_attachment',
				data: {
					asset_id: this.model.id,
					work_id: this.model.workNumber
				},
				type: 'POST',
				dataType: 'json',
				context: this
			}).done(function (response) {
				if (response.successful) {
					modal.destroy();
					$(this.el).fadeOut('fast', _.bind(function () {
						var $element = $(this.el);
						$element.remove();
						this.trigger('assetRemoved');
						$element.unbind();
					}, this));
				}
			});
		});
	},

	saveDescriptionToServer: function (callback) {
		$.ajax({
			url: '/assignments/attachment_description/' + this.model.id,
			data: {description: this.getDescriptionText()},
			type: 'POST',
			dataType: 'json',
			context: this
		}).then(function (response) {
			if (response.successful) {
				callback.apply(this);
			}
		});
	},

	renderNoDescriptionOptionsView: function () {
		this.$assetOptions.removeClass('with-description edit-asset-description').addClass('with-no-description');
	},

	renderDescriptionOptionsView: function () {
		this.$assetOptions.removeClass('with-no-description edit-asset-description').addClass('with-description');
	},

	renderEditDescriptionOptionsView: function () {
		this.$assetOptions.removeClass('with-no-description with-description').addClass('edit-description');
	}

});

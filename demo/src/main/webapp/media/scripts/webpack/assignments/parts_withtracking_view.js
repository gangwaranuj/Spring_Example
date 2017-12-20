'use strict';

import PartsTableTemplate from './templates/details/parts_table.hbs';
import MobilePartsHeaderTemplate from './templates/details/mobilePartsHeaderTemplate.hbs';
import DesktopPartsHeaderTemplate from './templates/details/desktopPartsHeaderTemplate.hbs';
import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import PartsLocationModel from './parts_withtracking_location_model';
import PartsLocationView from './parts_withtracking_location_view';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	events : {
		'click ._remove_'                         : 'remove',
		'click .parts-table--add'                 : 'addPart',
		'keyup .parts-table--part-creation input' : 'checkInputs'
	},

	initialize: function (options) {
		this.isMobile = options.isMobile;
		this.isReturn = options.isReturn;
		this.partGroup = options.partGroup || {};
		this.tableTemplate = PartsTableTemplate;

		if (this.isMobile) {
			this.errorTemplate = _.template($('#error-notices-template').html());
			this.headerTemplate = MobilePartsHeaderTemplate;
		} else {
			this.headerTemplate = DesktopPartsHeaderTemplate;
		}

		this.listenTo(this.collection, 'invalid', this.errorMessage);
		this.listenTo(this.collection, 'add destroy', this.renderPartTable);
		this.listenToOnce(this.collection, 'sync', this.render);
	},

	render: function () {
		this.$el.append(this.headerTemplate({
			isReturn : this.isReturn,
			isNotSentOrDraft: this.options.isNotSentOrDraft,
			isOwnerOrAdmin: this.options.isOwnerOrAdmin,
			isSuppliedByWorker: this.options.isSuppliedByWorker,
			partsConstants: this.collection.partsConstants
		}));

		this.renderPartTable();
		this.renderLocation(this.isReturn ? this.partGroup.returnToLocation : this.partGroup.shipToLocation);

		return this;
	},

	remove: function (event) {
		var $row = $(event.currentTarget).closest('tr');
		this.collection.get($row.data('id')).destroy();
		$row.remove();
	},

	renderPartTable: function () {
		this.$('.parts-table').remove();
		this.$('.parts-table--item-name').val('');
		this.$('.parts-table--tracking-number-input').val('');
		this.$('.parts-table--item-price').val('');
		this.$('.parts-table--add').prop('disabled', true);

		var parts = this.collection.where({ return: this.isReturn });
		var partsJSON = _.map(parts, function (part) { return part.toJSON(); });

		if (parts.length) {
			this.$el.append(this.tableTemplate({
				isReturn: this.isReturn,
				totalPrice: this.collection.calculateTotalPrice(this.isReturn),
				parts: partsJSON,
				isNotSentOrDraft: this.options.isNotSentOrDraft,
				isOwnerOrAdmin: true
			}));
		}
	},

	addPart: function () {
		var $name = this.$('.parts-table--item-name'),
			$tnumber = this.$('.parts-table--tracking-number-input'),
			$price = this.$('.parts-table--item-price');

		this.collection.create({
			trackingNumber: $tnumber.val(),
			partValue: $price.val(),
			name: $name.val(),
			isReturn : this.isReturn
		}, {
			wait: true,
			validate: true,
			error: _.bind(function (collection, response) {
				_.each(response.responseJSON.messages, function (message) {
					this.errorMessage(collection, message);
				}, this);
			}, this)
		});
	},

	errorMessage: function (collection, message) {
		if (this.isMobile) {
			if (this.isReturn !== message.isReturn) {
				return;
			} else {
				this.$('.alert-message').remove();
				this.$el.prepend(this.errorTemplate({ messages: { error: message.message }}));
				var clearAlert = function (fade) {
					return this.$('.alert-message').fadeOut(fade);
				};
				return _.delay(_.partial(clearAlert, 500), 3000);
			}
		} else {
			return wmNotify({
				message: message.message,
				type: 'danger'
			});
		}
	},

	checkInputs: function () {
		var $inputs = this.$('.parts-table--part-creation > input');
		var isReady = _.every($inputs, function (input) {
			return !_.isEmpty($.trim($(input).val()));
		});

		this.$('.parts-table--add').prop('disabled', !isReady);
	},

	renderLocation: function (location) {
		location = location || {};
		_.extend(location, {
			distType: this.partGroup.shippingDestinationType,
			isReturn: this.isReturn,
			isSuppliedByWorker: this.partGroup.suppliedByWorker
		});

		var locationModel = new PartsLocationModel(location, { parse: true });
		return new PartsLocationView({
			model: locationModel,
			el: this.el
		});
	}
});

'use strict';

import Template from '../assignments/templates/creation/part_row.hbs';
import $ from 'jquery';
import _ from 'underscore';
import wmNotify from '../funcs/wmNotify';
import trimStringToLimit from '../funcs/wmTrimStringToLimit'

export default {

	/**
	 *
	 * @param {object} options
	 * {string} workNumber,
	 * {object} parts,
	 * {object} partsConstants
	 */
	init: function (options) {
		this.$partsContainer = $('#assignment-parts-logistics');
		this.$requiresPartsToggle = this.$partsContainer.find('input[name="requiresParts"]');
		this.$partsSupplyToggle = this.$partsContainer.find('.supply-parts-radio');
		this.$partsSuppliedByWorkerToggle = this.$partsContainer.find('input[name="partGroup.suppliedByWorker"]');

		this.$shipToLocationContainer = this.$partsContainer.find('.ship-to-location-container');
		this.$shipToTrackingNumberInput = $('#ship-to-tracking-number-input');
		this.$shipToPartNameInput = this.$shipToLocationContainer.find('.part-name-input');
		this.$shipToPartsTable = this.$shipToLocationContainer.find('.parts-table');
		this.$shipToPartsTableBody = this.$shipToPartsTable.find('tbody');
		this.$shipToTrackingNumberAdd = this.$shipToLocationContainer.find('.tracking-number-add');
		this.$shipToLocation = this.$shipToLocationContainer.find('.ship-to-location');

		this.$shippingDestinationType = $('#shipping-destination-type');

		this.$returnLocationContainer = this.$partsContainer.find('.return-location-container');
		this.$returnTrackingNumberInput = $('#return-tracking-number-input');
		this.$returnPartNameInput = this.$returnLocationContainer.find('.part-name-input');
		this.$returnPartsTable = this.$returnLocationContainer.find('.parts-table');
		this.$returnPartsTableBody = this.$returnPartsTable.find('tbody');
		this.$returnTrackingNumberAdd = this.$returnLocationContainer.find('.tracking-number-add');
		this.$returnLocation = this.$returnLocationContainer.find('.return-location');

		this.$workerPartsNotice = this.$partsContainer.find('.worker-parts-notice');
		this.$returnRequiredCheckbox = this.$returnLocationContainer.find('input[name="partGroup.returnRequired"]');
		this.$returnTrackingInput = this.$returnLocationContainer.find('.part-input');

		this.partRowTemplate = Template;

		this.FORM_PROPERTY_NAMES = {
			formPropertyUuid: 'partGroup.parts[x].uuid',
			formPropertyName: 'partGroup.parts[x].name',
			formPropertyNumber: 'partGroup.parts[x].trackingNumber',
			formPropertyProvider: 'partGroup.parts[x].shippingProvider',
			formPropertyPrice: 'partGroup.parts[x].partValue',
			formPropertyIsReturn: 'partGroup.parts[x].return'
		};
		this.partsConstants = options.partsConstants;

		if (!_.isUndefined(options.parts)) {
			this.addParts(options.parts);
		} else {
			this.addPartsAjax(options.workNumber);
		}

		this.$requiresPartsToggle.on('click', _.bind(function (e) {
			var arePartsRequired = $(e.target).is(':checked');
			this.$partsSupplyToggle
				.add(this.$shipToLocationContainer)
				.add(this.$returnLocationContainer)
				.toggle(arePartsRequired);
			if (!arePartsRequired) {
				this.resetForm();
				this.$workerPartsNotice.hide();
			}
		}, this));

		this.$partsSuppliedByWorkerToggle.on('click', _.bind(function (e) {
			var partsAreSuppliedByWorker = $(e.target).val() === 'true';

			this.$shipToLocationContainer.toggle(!partsAreSuppliedByWorker);
			this.$workerPartsNotice.toggle(partsAreSuppliedByWorker);

			if (partsAreSuppliedByWorker) {
				this.clearShippingLocationContainerForm();
			}
		}, this));

		this.$shippingDestinationType.on('change', _.bind(function (e) {
			var isDistTypePickUp = $(e.target).val() === 'PICKUP';
			this.$shipToLocation.toggle(isDistTypePickUp);
			if (!isDistTypePickUp) {
				this.clearShippingLocationForm();
			}
		}, this));

		this.$shipToTrackingNumberAdd.on('click', _.bind(function (e) {
			e.preventDefault();
			this.addTrackingNumberCallback({
				$trackingInput: this.$shipToTrackingNumberInput,
				$partNameInput: this.$shipToPartNameInput,
				$tableBody:  this.$shipToPartsTableBody,
				isReturn: false
			});
		}, this));

		this.$returnTrackingNumberAdd.on('click', _.bind(function (e) {
			e.preventDefault();
			this.addTrackingNumberCallback({
				$trackingInput: this.$returnTrackingNumberInput,
				$partNameInput: this.$returnPartNameInput,
				$tableBody:  this.$returnPartsTableBody,
				isReturn: true
			});
		}, this));

		this.$returnRequiredCheckbox.on('click', _.bind(function (e) {
			var shouldShow = $(e.target).is(':checked');
			this.$returnLocation
				.add(this.$returnTrackingInput)
				.toggle(shouldShow);

			if (!shouldShow) {
				this.$returnPartsTableBody.empty();
				this.$returnPartsTable.hide();
				this.clearReturnLocationForm();
			}
		}, this));
	},

	/**
	 *
	 * @param {object} params
	 * {element} $trackingInput: the tracking number input,
	 * {element} $partName: part name input,
	 * {element} $tableBody: the table body we're adding the part to (either ship to or return)
	 * {boolean} isReturn: is the part a return part
	 */
	addTrackingNumberCallback: function (params) {
		var trackingNumber = params.$trackingInput.val().trim(),
			partName = params.$partNameInput.val().trim();

		var errors = this.validateTrackingInput(trackingNumber, partName);
		if (!_.isEmpty(errors)) {
			this.showErrors(errors);
			return;
		}

		this.shippingProvider = 'OTHER';
		this.newProperties = {
			name: partName,
			trackingNumber: trackingNumber,
			isReturn: params.isReturn,
			shippingProvider: this.shippingProvider,
			isPending: true
		};
		this.addPart(this.newProperties, params.$tableBody);
		this.newPart = params.$tableBody.find('tr').last();

		$.ajax({
			url: '/assignments/detect_shipping_provider/' + encodeURIComponent(trackingNumber),
			type: 'GET',
			dataType: 'json',
			context: this
		}).done(function (response) {
			if (response.successful && response.data && response.data.shippingProviders[0]) {
				this.shippingProvider = response.data.shippingProviders[0];
			}
		}).always(function () {
			this.newPart.remove();
			_.extend(this.newProperties, { shippingProvider: this.shippingProvider, isPending: false });
			this.addPart(this.newProperties, params.$tableBody);
			params.$trackingInput.val('');
			params.$partNameInput.val('');
		});
	},

	/**
	 *
	 * @param {string} trackingNumber
	 * @param {string} partName
	 */
	validateTrackingInput: function (trackingNumber, partName) {
		var errors = [];

		if (trackingNumber === '') {
			errors.push('Tracking number is a required field.');
		} else if (trackingNumber.length > this.partsConstants.TRACKING_NUMBER_MAX) {
			errors.push('Tracking Number cannot exceed ' + this.partsConstants.TRACKING_NUMBER_MAX + ' characters.');
		}

		if (partName === '') {
			errors.push('Part name is a required field.');
		} else if (partName.length > this.partsConstants.NAME_MAX) {
			errors.push('Part name cannot exceed ' + this.partsConstants.NAME_MAX + ' characters.');
		}

		return errors;
	},

	addPartsAjax: function (workNumber) {
		if (_.isUndefined(workNumber)) {
			return;
		}
		$.ajax({
			url: '/assignments/' + workNumber + '/parts',
			type: 'GET',
			dataType: 'json',
			context: this,
			success: function (response) {
				// TODO: Alex - show error message
				if (response.successful) {
					this.addParts(response.data.parts);
				}
			}
		});
	},

	/**
	 *
	 * @param {array} parts - an array of part properties
	 *
	 */
	addParts: function (parts) {
		if (_.isEmpty(parts || [])) {
			return;
		}

		parts = _.groupBy(parts, function (part) {
			// NOTE: Alex - keep '==' since isReturn could be an int or boolean
			return part.isReturn === 'true' || part.isReturn == true;
		});

		var shipToParts = parts.false,
			returnParts = parts.true;

		this.$shipToPartsTableBody.add(this.$returnPartsTableBody).empty();
		this.addPartsToTable(shipToParts, this.$shipToPartsTableBody);
		this.addPartsToTable(returnParts, this.$returnPartsTableBody);
	},

	addPartsToTable: function (parts, $tableBody) {
		if (_.isEmpty(parts || [])) {
			return;
		}

		$tableBody
			.append(_.map(parts, this.buildPart, this).join())
			.closest('.parts-table').show();
		this.addEventHandlersForParts($tableBody);
		this.calculateTotalPartCost($tableBody.closest('.parts-table'));
	},

	/**
	 *
	 * @param {object} part - an object containing a part's properties
	 * @param {element} $tableBody - the table body we want to add the part to
	 */
	addPart: function (part, $tableBody) {
		$tableBody.append(this.buildPart(part, 0));
		$tableBody.closest('.parts-table').show();
		this.addEventHandlersForPart($tableBody.find('tr').last(), $tableBody);
	},

	buildPart: function (part, index) {
		return this.partRowTemplate(
			_.extend(part, this.buildFormProperties(index + this.getNextPartIndex()), {
				partsLabel: part.shippingProvider === 'OTHER' ? '' : 'View package on ' + part.shippingProvider + ' website',
				partsClasses: _.contains(part.shippingProviders, part.shippingProvider) ? '-' + part.shippingProvider.toLowerCase() + ' active': '-untracked',
				displayTrackingNumber: trimStringToLimit(part.trackingNumber, 22),
				providerUrl: this.getProviderUrl(part.shippingProvider, part.trackingNumber),
				shippingProviders: this.partsConstants.SHIPPING_PROVIDERS,
				nameMax: this.partsConstants.NAME_MAX,
				valueMax: this.partsConstants.PART_VALUE_MAX,
				valueMin: this.partsConstants.PART_VALUE_MIN
			})
		);
	},

	getProviderUrl: function (provider, trackingNumber) {
		switch (provider) {
			case 'UPS':
				return 'http://wwwapps.ups.com/WebTracking/track?track=yes&trackNums=' + trackingNumber;
			case 'USPS':
				return 'https://tools.usps.com/go/TrackConfirmAction_input?qtc_tLabels1=' + trackingNumber;
			case 'DHL':
				// this URL will only work for DHL in the US... at a later date,
				// we might want to pass a flag for country code... and give a global number
				// that would look like this:
				// return this.isNational() ? 'http://track.dhl-usa.com/TrackByNbr.asp?ShipmentNumber=' + trackingNumber : 'http://webtrack.dhlglobalmail.com/?mobile=&trackingnumber=' + trackingNumber
				return 'http://track.dhl-usa.com/TrackByNbr.asp?ShipmentNumber=' + trackingNumber;
			case 'FEDEX':
				return 'http://www.fedex.com/Tracking?action=track&tracknumbers=' + trackingNumber;
			default:
				return 'javascript:void(0)';
		}
	},

	getNextPartIndex: function () {
		return this.$shipToPartsTableBody.find('tr').length + this.$returnPartsTableBody.find('tr').length;
	},

	/**
	 * @param {number} index
	 * @returns {object} the partFromProperties hash with the index value injected
	 */
	buildFormProperties: function (index) {
		var partFormProperties = {};
		_.each(this.FORM_PROPERTY_NAMES, function (value, key) {
			partFormProperties[key] = value.replace('x', String(index));
		});
		return partFormProperties;
	},

	addEventHandlersForParts: function ($tableBody) {
		_.each($tableBody.find('tr'), function (part) {
			this.addEventHandlersForPart($(part), $tableBody);
		}, this);
	},

	addEventHandlersForPart: function ($part, $tableBody) {
		var $partPriceInput = $part.find('.item-price');

		$partPriceInput.on('keypress', _.bind(this.onlyPricingCharsHandler, this));
		$partPriceInput.on('keyup', _.bind(this.calculateTotalPartCost, this, $tableBody.closest('.parts-table')));
		$part.find('.wm-icon-trash').on('click', _.bind(this.deletePart, this, $tableBody));
		// TODO: Put alphanumerical mask on tracking number
	},

	onlyPricingCharsHandler: function (e) {
		var character = String.fromCharCode(e.which);

		if (character === ',' || character === '.') {
			return true;
		}
		return $.isNumeric(character);
	},

	calculateTotalPartCost: function ($table) {
		$table.find('.total-price').text(
			_.reduce($table.find('.item-price'), function (memo, price) {
				var number = Number($(price).val().replace(/[,]/,''));
				return memo + (_.isNaN(number) || number > this.partsConstants.PART_VALUE_MAX || number < this.partsConstants.PART_VALUE_MIN ? 0 : number);
			}, 0, this).toFixed(2)
		);
	},

	deletePart: function ($tableBody) {
		event.preventDefault();
		$(event.target).closest('tr').remove();
		if ($tableBody.find('tr').length === 0) {
			var $partsTable = $tableBody.closest('.parts-table');
			$partsTable.find('.total-price').html('0.00');
			$partsTable.hide();
			return;
		}
		this.addParts(this.getPartProperties());
	},

	/**
	 * Returns part properties from both the ship to part table and return parts table
	 * @returns {array}
	 */
	getPartProperties: function () {
		return _.map($('.parts-table tbody tr'), function (part) {
			var $part = $(part);
			return {
				uuid : $part.find('.part-uuid').val(),
				name : $part.find('.name').val(),
				trackingNumber: $part.find('.number').val(),
				shippingProvider: $part.find('.shipping-provider').val(),
				partValue: $part.find('.item-price').val(),
				isReturn: $part.find('.is-return').val()
			};
		});
	},

	clearShippingLocationContainerForm: function () {
		this.$shipToLocationContainer.find('input[type=text]').val('');
		this.$shipToLocationContainer.find('select option:eq(0)').prop('selected', true);
		this.$shipToPartsTableBody.empty();
		this.$shipToPartsTable.hide();
		this.$shipToLocation.hide();
	},

	clearShippingLocationForm: function () {
		this.$shipToLocation.find('input[type="text"]').val('');
		this.$shipToLocation.find('select option:eq(0)').prop('selected', true);
	},

	clearReturnLocationContainerForm: function () {
		this.$returnLocationContainer.find('input[type=text]').val('');
		this.$returnLocationContainer.find('select option:eq(0)').prop('selected', true);
		this.$returnLocationContainer.find('input[type=checkbox]').prop('checked', false);
		this.$returnLocation.hide();
		this.$returnTrackingInput.hide();
	},

	clearReturnLocationForm: function () {
		this.$returnLocation.find('input[type="text"]').val('');
		this.$returnLocation.find('select option:eq(0)').prop('selected', true);
	},

	resetForm: function () {
		this.$partsSuppliedByWorkerToggle.removeAttr('checked').filter('[value="false"]').prop('checked', true);
		this.clearShippingLocationContainerForm();
		this.clearReturnLocationContainerForm();
	},

	showErrors: function (errors) {
		_.each(errors, function (error) {
			wmNotify({
				message: error,
				type: 'danger'
			});
		});
	}
};

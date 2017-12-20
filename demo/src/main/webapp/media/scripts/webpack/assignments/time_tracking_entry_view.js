'use strict';

import AssignmentLabelTemplate from './templates/details/assignment_label_header.hbs';
import EstimatedTimeLogTemplate from './templates/details/estimated_time_log.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import AddCheckoutNoteView from './add_checkout_note_view';
import CompletionBarView from './completion_bar_view';
import wmNotify from '../funcs/wmNotify';
import '../dependencies/jquery.tmpl';
import wmModal from '../funcs/wmModal';

export default Backbone.View.extend({
	tagName: 'tr',
	template: $('#tmpl-time-tracking-entry').template(),
	events: {
		'click .checkin-details .edit'        : 'showCheckinEdit',
		'click .checkin-details .cancel'      : 'showCheckinDetails',
		'click .checkout-details .edit'       : 'showCheckoutEdit',
		'click .checkout-details .cancel'     : 'showCheckoutDetails',
		'click .checkin_action'               : 'submitCheckin',
		'click .checkout_action'              : 'submitCheckout',
		'click .checkout_with_note_action'    : 'getCheckoutNote',
		'click .checkin-details .update'      : 'collectAndSubmitCheckin',
		'click .checkout-details .update'     : 'collectAndSubmitCheckout',
		'click .checkout-details .delete'     : 'deleteCheckout',
		'click .row-delete .wm-icon-trash'    : 'deleteCheckin',
		'mouseover mouseleave .wm-icon-trash' : 'handleHighlightTrackingRow'
	},

	initialize: function () {
		_.bindAll(this, 'render');

		var dateFormat = function () {
			var	token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
				timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
				timezoneClip = /[^-+\dA-Z]/g,
				pad = function (val, len) {
					val = String(val);
					len = len || 2;
					while (val.length < len) val = "0" + val;
					return val;
				};

			// Regexes and supporting functions are cached through closure
			return function (date, mask, utc) {
				var dF = dateFormat;

				// You can't provide utc if you skip other args (use the "UTC:" mask prefix)
				if (arguments.length == 1 && Object.prototype.toString.call(date) == "[object String]" && !/\d/.test(date)) {
					mask = date;
					date = undefined;
				}

				// Passing date through Date applies Date.parse, if necessary
				date = date ? new Date(date) : new Date;
				if (isNaN(date)) throw SyntaxError("invalid date");

				mask = String(dF.masks[mask] || mask || dF.masks["default"]);

				// Allow setting the utc argument via the mask
				if (mask.slice(0, 4) == "UTC:") {
					mask = mask.slice(4);
					utc = true;
				}

				var	_ = utc ? "getUTC" : "get",
					d = date[_ + "Date"](),
					D = date[_ + "Day"](),
					m = date[_ + "Month"](),
					y = date[_ + "FullYear"](),
					H = date[_ + "Hours"](),
					M = date[_ + "Minutes"](),
					s = date[_ + "Seconds"](),
					L = date[_ + "Milliseconds"](),
					o = utc ? 0 : date.getTimezoneOffset(),
					flags = {
						d:    d,
						dd:   pad(d),
						ddd:  dF.i18n.dayNames[D],
						dddd: dF.i18n.dayNames[D + 7],
						m:    m + 1,
						mm:   pad(m + 1),
						mmm:  dF.i18n.monthNames[m],
						mmmm: dF.i18n.monthNames[m + 12],
						yy:   String(y).slice(2),
						yyyy: y,
						h:    H % 12 || 12,
						hh:   pad(H % 12 || 12),
						H:    H,
						HH:   pad(H),
						M:    M,
						MM:   pad(M),
						s:    s,
						ss:   pad(s),
						l:    pad(L, 3),
						L:    pad(L > 99 ? Math.round(L / 10) : L),
						t:    H < 12 ? "a"  : "p",
						tt:   H < 12 ? "am" : "pm",
						T:    H < 12 ? "A"  : "P",
						TT:   H < 12 ? "AM" : "PM",
						Z:    utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
						o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
						S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
					};

				return mask.replace(token, function ($0) {
					return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
				});
			};
		}();

		// Some common format strings
		dateFormat.masks = {
			"default":      "ddd mmm dd yyyy HH:MM:ss",
			shortDate:      "m/d/yy",
			mediumDate:     "mmm d, yyyy",
			longDate:       "mmmm d, yyyy",
			fullDate:       "dddd, mmmm d, yyyy",
			shortTime:      "h:MM TT",
			mediumTime:     "h:MM:ss TT",
			longTime:       "h:MM:ss TT Z",
			isoDate:        "yyyy-mm-dd",
			isoTime:        "HH:MM:ss",
			isoDateTime:    "yyyy-mm-dd'T'HH:MM:ss",
			isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
		};

		// Internationalization strings
		dateFormat.i18n = {
			dayNames: [
				"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
				"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
			],
			monthNames: [
				"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
				"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
			]
		};

		// For convenience...
		Date.prototype.format = function (mask, utc) {
			return dateFormat(this, mask, utc);
		};


	},

	render: function () {
		$(this.el).html($.tmpl(this.template, this.model));

		this.checkinDetails = this.$('.checkin-details');
		this.checkoutDetails = this.$('.checkout-details');
		this.assignmentDetails = $('p.assignment_labels');
		this.timeTrackingDuration = $('#timetracking p.ml');

		if (this.isCheckedIn()) {
			this.showCheckinDetails();
			$('#checkin').show();
			$('#checkin-todo').hide();

			if (this.isCheckedOut()) {
				this.showCheckoutDetails();
				$('#checkin').show();
				$('#checkout_completed').show();
				$('#checkin_completion_list').addClass('completion-success');
				$('#checkout_list_incomplete').hide();
				$('#checkout_list_completed').show();
				$('#checkin-todo').hide();
				$('#checkout_required').hide();
			} else {
				this.showCheckout();
			}
		} else {
			this.showCheckin();
			this.showCheckoutDisabled();
		}

		if(this.isFirstRow()) {
			$(this.el).find('.row-delete i').remove();
		}

		return this;
	},

	getLabelUpdates: function (callback) {
		var url = '/assignments/workflow_status_extras/' + this.options.workNumber;

		var params = {
			id: (this.model) ? this.model.id : null,
			user_number: this.options.userNumber
		};

		$.ajax({
			url: url,
			type: 'GET',
			data: params,
			dataType: 'json',
			context: this,
			success: function (response){
				if (response && response.successful) {
					//update labels with information
					var obj = response.data;
					obj.workNumber = this.options.workNumber;
					obj.subStatuses = JSON.parse(response.data.substatuses);
					var templateLabels = AssignmentLabelTemplate;
					this.assignmentDetails.html(templateLabels(obj));
					var templateDuration = EstimatedTimeLogTemplate;
					this.timeTrackingDuration.html(templateDuration(obj));

					if ($.isFunction(callback)) {
						callback.call(this, response.data);
					}
				}
			}
		});
	},

	isCheckedIn: function() {
		return this.model && this.model.checkedInOn;
	},

	isCheckedOut: function() {
		return this.model && this.model.checkedOutOn;
	},

	isCheckoutNoteRequired: function() {
		return this.options.checkoutNoteRequiredFlag;
	},

	getDateTimeComponents: function(millis) {
		// Milliseconds are coming in as UTC:
		// Since JS doesn't natively support changing the TZ locale,
		// we timeshift the UTC milliseconds by the assignment's location offset,
		// then format the time as "UTC". The TZ is not rendered by JS.
		var date = (millis) ? new Date(millis) : new Date();
		date.setTime(date.getTime() + this.options.millisOffset);
		return {
			'date': date.format('mm/dd/yyyy', true),
			'time': date.format('hh:MMtt', true)
		};
	},

	getDateTimeDistanceComponents: function (millis, distance) {
		var date = (millis) ? new Date(millis) : new Date();
		date.setTime(date.getTime() + this.options.millisOffset);
		return {
			'date': date.format('mm/dd/yyyy', true),
			'time': date.format('hh:MMtt', true),
			'distance': distance
		};
	},

	handleHighlightTrackingRow: function (event) {
		if (event.type === 'mouseover') {
			$(this).parents('tr').addClass('hover');
		} else {
			$(this).parents('tr').removeClass('hover');
		}
	},

	showCheckin: function () {
		this.checkinDetails.html($('#tmpl-time-tracking-entry-checkin').tmpl());
		return false;
	},

	showCheckout: function () {
		this.checkoutDetails.html($('#tmpl-time-tracking-entry-checkout').tmpl());
		return false;
	},

	showCheckinDetails: function () {
		if (this.model.distanceIn) {
			this.checkinDetails.html($('#tmpl-time-tracking-entry-checkin-distance-status').tmpl(this.getDateTimeDistanceComponents(this.model.checkedInOn, this.model.distanceIn)));
		} else {
			this.checkinDetails.html($('#tmpl-time-tracking-entry-checkin-status').tmpl(this.getDateTimeComponents(this.model.checkedInOn)));
		}
		return false;
	},

	showCheckoutDetails: function (event) {
		if (this.model.distanceOut) {
			this.checkoutDetails.html($('#tmpl-time-tracking-entry-checkout-distance-status').tmpl(this.getDateTimeDistanceComponents(this.model.checkedOutOn, this.model.distanceOut)));
		} else {
			this.checkoutDetails.html($('#tmpl-time-tracking-entry-checkout-status').tmpl(this.getDateTimeComponents(this.model.checkedOutOn)));
		}

		if (this.isLastRow()) {
			this.toggleAddActionButton(event, true);
		}
		return false;
	},

	showCheckoutDisabled: function () {
		this.checkoutDetails.html($('#tmpl-time-tracking-entry-checkout-disabled').tmpl());
		return false;
	},

	showCheckinEdit: function () {
		this.checkinDetails.html($('#tmpl-time-tracking-entry-form').tmpl(this.getDateTimeComponents(this.model ? this.model.checkedInOn : null)));
		this.$el.find('.delete').remove();
		$('.time_tracking_date', this.checkinDetails).datepicker({
			dateFormat: 'mm/dd/yy',
			onSelect: function () {
				$('.time_tracking_date', this.checkinDetails).removeClass('placeholder');
			}
		});
		$('.time_tracking_time', this.checkinDetails).calendricalTime({ startDate: $('.time_tracking_time') });
		return false;
	},

	showCheckoutEdit: function (event) {
		this.checkoutDetails.html($('#tmpl-time-tracking-entry-form').tmpl(this.getDateTimeComponents(this.model ? this.model.checkedOutOn : null)));
		$('.time_tracking_date', this.checkoutDetails).datepicker({
			dateFormat: 'mm/dd/yy',
			onSelect: function () {
				$('.time_tracking_date', this.checkoutDetails).removeClass('placeholder');
			}
		});
		$('.time_tracking_time', this.checkoutDetails).calendricalTime({ startDate: $('.time_tracking_time') });

		var $delete = this.$el.find('.delete');
		if(this.isLastRow()) {
			$delete.show();
			this.toggleAddActionButton(event, false);
		} else {
			$delete.hide();
		}
		return false;
	},

	isLastRow: function () {
		return this.isGivenRow('last');
	},

	isFirstRow: function () {
		return this.isGivenRow('first');
	},

	isGivenRow: function(row) {
		//compare text because jQuery selectors return new objects, so equality is always false
		return $(this.el).find('td.checkin-details').text() === $(this.el).parents('tbody').find('tr:' + row).find('td.checkin-details').text() &&
			$(this.el).find('td.checkout-details').text() === $(this.el).parents('tbody').find('tr:' + row).find('td.checkout-details').text();
	},

	actionButtonExists: function() {
		var $lastRow = $('#timetracking-entries').find('tr:last');
		var checkinActionExists = $lastRow.find('.checkin-details .checkin_action').length !== 0;
		var checkoutActionExists = $lastRow.find('.checkout-details .checkout_action').length !== 0;

		return checkinActionExists || checkoutActionExists;
	},

	toggleAddActionButton: function(e, force) {
		if(force !== undefined) {
			$('.add_action').toggle(force);
		} else {
			$('.add_action').toggle();
		}
		return false;
	},


	sendUpdate: function(type, date, time, noteText, callback, failureCallback) {
		var url = (type === 'checkin') ?
			'/assignments/update_checkin/' + this.options.workNumber :
			'/assignments/update_checkout/' + this.options.workNumber;

		var params = {
			id: (this.model) ? this.model.id : null,
			user_number: this.options.userNumber,
			date: date || '',
			time: time || '',
			note_text: noteText || ''
		};

		$.ajax({
			url: url,
			type: 'POST',
			data: params,
			dataType: 'json',
			context: this,
			success: function (response){
				if (response && response.successful) {
					if (!this.model) {
						this.model = {};
					}

					this.model.id = response.data.id;
					if (type === 'checkin') {
						this.model.checkedInOn = response.data.millis;
					} else {
						this.model.checkedOutOn = response.data.millis;
					}

					this.getLabelUpdates();

					if ($.isFunction(callback)) {
						callback.call(this, response.data);
					}
				} else {
					this.trigger('timetracking:error', response.messages);
					this.$('.disabled').removeClass('disabled');

					if ($.isFunction(failureCallback)) {
						failureCallback.call(this, response.data);
					}
				}
			}
		});
	},

	sendDelete: function (type, callback) {
		var url = (type === 'checkin') ?
			'/assignments/delete_checkin/' + this.options.workNumber :
			'/assignments/delete_checkout/' + this.options.workNumber;

		var params = {
			id: (this.model) ? this.model.id : null,
			user_number: this.options.userNumber
		};

		$.ajax({
			url: url,
			type: 'POST',
			data: params,
			dataType: 'json',
			context: this,
			success: function (response){
				if (response && response.successful) {
					if (!this.model) {
						this.model = {};
					}

					this.getLabelUpdates();

					if ($.isFunction(callback)) {
						callback.call(this, response.data);
					}
				} else {
					this.trigger('timetracking:error', response.messages);
					this.$('.disabled').removeClass('disabled');
				}
			}
		});
	},

	deleteCheckin: function (event) {
		if (this.isDisabled(event.target)) {
			return;
		}
		$(event.target).addClass('disabled');

		this.sendDelete(
			'checkin',
			function() {
				this.model.checkedInOn = undefined;
				this.model.checkedOutOn = undefined;
				this.model.id = undefined;

				this.trigger('timetracking:checkin');
				this.showCheckin(event);
				this.showCheckout(event);
				this.showCheckoutDisabled(event);
				var self = this;
				this.$el.hide(200, function () {
					$(this).remove();
					if(!self.actionButtonExists()) {
						self.toggleAddActionButton(event, true);
					}
				});
			}
		);
		return false;
	},

	deleteCheckout: function (event) {
		if (this.isDisabled(event.target)) {
			return;
		}
		$(event.target).addClass('disabled');

		this.sendDelete('checkout', function () {
			this.showCheckout(event);
			this.trigger('timetracking:checkout');
			this.toggleAddActionButton(event, false);
			this.model.checkedOutOn = '';
		});
		return false;
	},

	isDisabled: function (o) {
		return $(o).is('.disabled');
	},

	submitCheckin: function(event) {
		if (this.isDisabled(event.target)) {
			return;
		}
		$(event.target).addClass('disabled');

		this.sendUpdate('checkin', null, null, null, function () {
			this.trigger('timetracking:checkin');
			this.render();
			if(!this.isFirstRow()) {
				$(this.el).find('.row-delete i').show();
			}
		});
		return false;
	},

	collectAndSubmitCheckin: function (event) {
		if (this.isDisabled(event.target)) {
			return;
		}
		$(event.target).addClass('disabled');

		this.sendUpdate(
			'checkin',
			this.$('.checkin-details .time_tracking_date').val(),
			this.$('.checkin-details .time_tracking_time').val(),
			null,
			function() {
				this.trigger('timetracking:checkin');
				this.render();
				if(!this.isFirstRow()) {
					$(this.el).find('.row-delete i').show();
				}
			}
		);
		return false;
	},

	submitCheckout: function (event) {
		if (this.isDisabled(event.target)) {
			return;
		}
		$(event.target).addClass('disabled');

		this.sendUpdate('checkout', null, null, null, function () {
			this.trigger('timetracking:checkout');
			this.render();
			new CompletionBarView().render();
		});

		return false;
	},

	getCheckoutNote: function (e) {
		var self = this;
		e.preventDefault();

		$.ajax({
			type: 'GET',
			url: '/assignments/add_checkout_note/' + this.options.workNumber,
			context: this,
			success: function (response) {
				if (!_.isEmpty(response)) {
					wmModal({
						autorun: true,
						title: 'Add a Note (' + (this.isCheckoutNoteRequired() ? 'required' : 'optional') + ')',
						destroyOnClose: true,
						content: response
					});
					new AddCheckoutNoteView({
						'el': $('#checkout-note'),
						'parentView': self,
						'date': self.getDateTimeComponents()
					});
				}
			}
		});
	},

	collectAndSubmitCheckout: function (event) {
		if (this.isDisabled(event.target)) {
			return;
		}
		$(event.target).addClass('disabled');

		this.sendUpdate(
			'checkout',
			this.$('.checkout-details .time_tracking_date').val(),
			this.$('.checkout-details .time_tracking_time').val(),
			null,
			function() {
				this.trigger('timetracking:checkout');
				this.render();
			}
		);
		return false;
	},

	onError: function (messages) {
		_.each(messages, function (theMessage) {
			wmNotify({
				message: theMessage,
				type: 'danger'
			});
		});
	}
});

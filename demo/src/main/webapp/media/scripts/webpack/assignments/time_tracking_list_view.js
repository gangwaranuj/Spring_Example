'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import TimeTrackingEntryView from './time_tracking_entry_view';
import wmNotify from '../funcs/wmNotify';

export default Backbone.View.extend({
	el: '#timetracking',
	events: {
		'click .add_action' : 'addOne'
	},

	initialize: function () {
		_.bindAll(this, 'render');

		this.entries = [];

		this.addButton = this.$('.add_action');
		this.messages = this.$('div.messages');
		this.vent = this.options.vent;
	},

	render: function() {
		if (!this.model) {
			return this;
		}

		if (this.model.timeTrackingLog && this.model.timeTrackingLog.length) {
			_.each(this.model.timeTrackingLog, this.addOne, this);
		} else {
			this.addOne(null);
		}

		this.toggleAdd();

		this.$el.find('tbody tr:first').find('.row-delete i').remove();
		this.$el.find('tbody tr:not(:first)').find('.row-delete i').show();

		return this;
	},

	addOne: function(entry) {
		var self = this;
		var view = new TimeTrackingEntryView({
			model: entry,
			workNumber: this.options.workNumber,
			millisOffset: this.options.millisOffset,
			checkoutNoteRequiredFlag: this.options.checkoutNoteRequiredFlag,
			userNumber: this.model.user.userNumber});

		view.bind('timetracking:checkin', function (e) { self.onSuccess(e); });
		view.bind('timetracking:checkout', function () { self.toggleAdd(); });
		view.bind('timetracking:checkout', function (e) { self.onSuccess(e); });
		view.bind('timetracking:checkout', function () { self.vent.trigger('work:allow_completion'); });

		view.bind('timetracking:checkout_with_note', function (e) { self.onSuccess(e); });
		view.bind('timetracking:checkout_with_note', function () { self.vent.trigger('work:allow_completion'); });

		view.bind('timetracking:error', function(e) { self.onError(e); });

		this.entries.push(view);
		this.$('#timetracking-entries').append(view.render().el);
		this.toggleAdd();
		this.$el.find('tbody tr:last').find('.row-delete i').hide();
	},

	toggleAdd: function () {
		if (_.last(this.entries).isCheckedOut()) {
			this.addButton.show();
		} else {
			this.addButton.hide();
		}
	},

	onSuccess: function () {
		this.messages.hide();
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

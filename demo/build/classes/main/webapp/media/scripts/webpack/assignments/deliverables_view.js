'use strict';

import Template from './templates/details/deliverableUnorderedList.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import DeliverableRequirementView from './deliverable_requirement_view';
import CompletionBarView from './completion_bar_view'; 

/*
 *   Highest-level view in the deliverables domain.  This is composed of a list of zero or more
 *   deliverable requirements, which in turn are composed of zero or more deliverable assets.
 */
export default Backbone.View.extend({
	el: '#deliverables',
	className: 'documents-two',

	initialize: function (options) {
		this.model = options.model;

		// Templates
		this.deliverableRequirementListTemplate = Template;

		// Document state
		this.$deliverableDeadlineTimer = this.$('#deliverableDeadlineTimer');
		this.$deliverablesContainer = this.$('.documents');
		this.$rejectDeliverableModal = $('.rejectDeliverableModal');
		this.$downloadAllButton = this.$('.deliverables-download-all-icon');
	},

	render: function () {
		// Rendering is handled by the JSP on Sent and Draft status ... move rendering to js exclusively? ... use a template and load ...
		if (this.options.status === 'sent' || this.options.status === 'draft') {
			return this;
		}

		var generatedList = this.deliverableRequirementListTemplate({ cssClass: 'deliverableRequirementDetailsList' });
		$(generatedList).appendTo(this.$deliverablesContainer);
		this.$deliverableRequirementList = $('ul.deliverableRequirementDetailsList', this.$deliverablesContainer);

		this.model.deliverableRequirements.each(this.addDeliverableRequirement, this);

		this.handleDeliverableStateChange();

		if (this.$deliverableDeadlineTimer) {
			this.updateDeliverableCountdownTimer(this.options.assignmentStartTime);
		}

		return this;
	},

	updateDeliverableCountdownTimer: function (assignmentStartTime) {
		var milliInHour = 36e5;
		var milliInMinute = 60000;
		var now = new Date();
		var hoursToComplete = this.model.get('hoursToComplete');
		var convertedHoursToCompleted = hoursToComplete * milliInHour;
		var deliverableDeadline = assignmentStartTime + convertedHoursToCompleted;
		var timeRemaining = deliverableDeadline - now.getTime();

		if (timeRemaining <= 0) {
			// Deadline has passed
			$('.deliverables-timer-text').html('Deliverables deadline has passed. Please submit as soon as possible.');
		} else {
			// Deadline is still active, update timer UI
			var value = '';
			var unit = '';

			var hoursRemaining = Math.floor(timeRemaining / milliInHour);
			if (hoursRemaining > 48) {
				unit = ' days';
				value = Math.floor(hoursRemaining / 24);
			} else {
				unit = (hoursRemaining === 1) ? ' hour ' : ' hours ';
				value = hoursRemaining;
			}

			this.$deliverableDeadlineTimer.html(value + unit);
			var updateAssignmentTime = _.bind(this.updateDeliverableCountdownTimer, this, assignmentStartTime);
			_.delay(updateAssignmentTime, milliInMinute);
		}
	},

	addDeliverableRequirement: function (deliverableRequirement) {
		var deliverableRequirementView = new DeliverableRequirementView({
			model: deliverableRequirement,
			status: this.options.status,
			isWorker: this.options.isWorker,
			isAdmin: this.options.isAdmin,
			isOwner: this.options.isOwner
		});

		this.$deliverableRequirementList.append(deliverableRequirementView.render().el);

		deliverableRequirementView.on('deliverableRequirementStateChange', _.bind(this.handleDeliverableStateChange, this));
	},

	handleDeliverableStateChange: function () {
		var isThereAtLeastOneNonRejectedAsset = this.model.deliverableRequirements.some(function (deliverableRequirement) {
			return deliverableRequirement.hasAtLeastOneNonRejectedAsset();
		}, this);

		this.$downloadAllButton.toggleClass('-hidden', !isThereAtLeastOneNonRejectedAsset);

		var isComplete = this.model.deliverableRequirements.every(function (deliverableRequirement) {
			return deliverableRequirement.isComplete();
		}, this);

		this.updateAssignmentRequirement(isComplete);
	},

	updateAssignmentRequirement: function (isComplete) {

		isComplete = _.isUndefined(isComplete) ? false : isComplete;

		$('#deliverables_incomplete').add('#deliverables_task_incomplete').toggle(!isComplete);
		$('#deliverables_complete').add('#deliverables_task_complete').toggle(isComplete);
		$('#deliverables_completion_list').toggleClass('completion-success', isComplete);

		new CompletionBarView().render();
	}
});

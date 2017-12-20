'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import '../dependencies/jquery.bootstrap-progressbar';

export default Backbone.View.extend({
	render: function () {
		this.requiredTask = $('#completion_table .required-item');
		this.completedTask = $('#completion_table .required-item.completion-success');
		this.totalCompleted = $('#completed_tasks');
		this.totalTasks = $('#total_tasks');
		this.completionDiv = $('#completion_bar');
		this.completionStatus = $('#completion_status');
		this.completionButton = $('button.resource-complete-toggle');
		this.bar = $('#completion-progress-bar');

		var requiredTotal = this.requiredTask.length;
		var completedTotal = this.completedTask.length;

		this.totalCompleted.text(completedTotal);
		this.totalTasks.text(requiredTotal);
		this.bar.attr('aria-valuetransitiongoal', completedTotal);
		this.bar.attr('aria-valuemax', requiredTotal);
		this.completionButton.addClass('disabled');

		if (requiredTotal === 0) {
			this.completionButton.removeClass('disabled');
			return false;
		} else {
			if (completedTotal === requiredTotal) {
				this.completionButton.removeClass('disabled');
				this.completionStatus.html('You have completed all the required tasks for this assignment.<br/> Submit this assignment for approval at the bottom of the page.');
			}
			this.completionDiv.show();
			this.bar.progressbar({
				display_text: 'fill',
				use_percentage: false
			});
		}
	}
});

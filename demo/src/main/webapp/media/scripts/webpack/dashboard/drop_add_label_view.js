'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import NegotiateScheduleView from '../assignments/negotiation_schedule_view';
import wmNotify from '../funcs/wmNotify';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	initialize: function (options) {
		new NegotiateScheduleView({
			'el': this.el,
			'millisOffset': options.millisOffset
		});

		$(this.el).ajaxForm({
			dataType: 'json',
			success: function (data) {
				if (data && data.successful) {
					// First, we find the row to which the label should be added
					$(window.document).find('.assignmentId').each(function () {
						if ($(this).attr('id') === data.data.workNumber) {
							// Then we check if the label already exists on the row
							var exists = false;
							$(this).parent().find('.dragRemove').each(function () {
								var idmatches = $(this).parent().attr('href').match(/([0-9]+)/);
								if ((null != idmatches) && (data.data.labelId === idmatches[1])) {
									exists = true;
									return false;
								}
							});

							// If the label doesn't yet exist, add the label to the row
							if (!exists) {
								// Get the colour of the label
								var labelColour = null;
								$(window.document).find('.dragAdd').each(function () {
									var idmatches = $(this).attr('href').match(/([0-9]+)/);
									if ((null != idmatches) && (idmatches[1] === data.data.labelId)) {
										labelColour = $(this).children('.label.fr').css('backgroundColor');
										return false;
									}
								});

								// Finally, add the new label to the row
								var labelsDiv = $(this).parent().find('.assignment_labels');
								var newLabel = '<a href="/assignments#substatus/' + data.data.labelId + '/managing">' + '<span class="label nowrap dragRemove ' + data.data.workNumber;
								if (null != labelColour) {
									newLabel += '" style="background-color: ' + labelColour + ';';
								}
								newLabel += '">' + data.data.labelDescription + '</span></a>';
								labelsDiv.append(newLabel);
							}

							return false;
						}
					});

					$('.wm-modal--close').trigger('click');
					options.closeCallback();
				} else {
					_.each(data.messages, function (theMessage) {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	}
});

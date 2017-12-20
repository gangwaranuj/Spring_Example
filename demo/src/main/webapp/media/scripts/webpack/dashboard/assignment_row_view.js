'use strict';

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import DropAddLabelView from './drop_add_label_view';
import DropRemoveLabelView from './drop_remove_label_view';
import PaymentModel from './payment_model';
import '../dependencies/jquery.tmpl';
import wmModal from '../funcs/wmModal';

export default Backbone.View.extend({
	tagName: 'div',
	className: 'results-row work',
	template: $('#tmpl-assignment_list').template(),

	events:{
		'click .submit_approve_pay'              : 'actionApprovePay',
		'click .follow'                          : 'actionFollow',
		'click [data-action="removeFromBundle"]' : 'removeFromBundle'
	},

	actionApprovePay: function (event) {
		var pricing = new PaymentModel(this.model.toJSON());
		pricing.performPayment(event);
	},

	actionFollow:function (e) {
		e.preventDefault();

		$.ajax({
			url: $(e.currentTarget).attr('href'),
			type: 'GET',
			dataType: 'json',
			success: function (response) {
				if (response.successful) {
					if (!$(e.currentTarget).hasClass('follow-true')) {
						$(e.currentTarget).addClass('follow-true');
					} else {
						$(e.currentTarget).removeClass('follow-true');
					}
				}
			}
		});
	},

	removeFromBundle: function (e) {
		e.preventDefault();

		var model = this.model.toJSON();
		$.post('/assignments/remove_from_bundle/' + model.parent_id + '/' + model.id)
		.done(function(){
			$($(e.currentTarget).closest('.bundle_info')).remove();
		})
		.fail(function(){

		});
	},

	initialize: function () { },

	render: function () {
		var self = this;

		var cells = $.tmpl(this.template, _.extend(this.model.toJSON()));
		$(this.el).html(cells);

		// Get the assignment id
		var rowId = $(this.el).find('div.assignmentId').attr('id').toString();

		// Add alternating row colors.
		if (this.options.index % 2) {
			$(this.el).addClass('odd');
		} else {
			$(this.el).addClass('even');
		}

		function getLabelId(ele, where) {
			var address = ele.parent().attr(where);

			var idmatches = null;
			if (where === 'href') {
				idmatches = address.match(/([0-9]+)/);
			} else {
				idmatches = address.match(/\_([0-9]+)\_/);
			}

			if (null != idmatches) {
				return idmatches[1];
			} else {
				return null;
			}
		}

		function getAssignmentId(ele) {
			return ele.find('div.assignmentId').attr('id');
		}

		function makeRemoveDraggable(ele, listId) {
			var cloned;

			ele.draggable({
				revert: function () {
					return false;
				},

				start: function (event, ui) {
					cloned = ele.parent().clone();

					// dropped data is set to true if the label is dropped within the assignment row
					ui.helper.data('dropped', false);
					ui.helper.data('isFF', false);

					// Fix for scroll offset in Firefox
					var FF = !(window.mozInnerScreenX == null);
					if (FF) {
						ui.helper.data('scrollTop', $(window).scrollTop());
						ui.helper.data('isFF', true);
					}
				},

				drag: function (event, ui) {
					// Apply scroll offset in Firefox
					if (ui.helper.data('isFF')) {
						ui.position.top += ui.helper.data('scrollTop');
					}
				},

				stop: function (event, ui) {
					if (!ui.helper.data('dropped')) {
						var parent = ele.parent();
						ele.remove();

						makeRemoveDraggable(cloned.children('span'), listId);
						$(self.el).find('.assignment_labels').append(cloned);

						$.ajax({
							type: 'GET',
							url: '/assignments/drop_remove_label/' + rowId + '?labelId=' + listId,
							context: this,
							success: function (response) {
								if (!_.isEmpty(response)) {
									wmModal({
										autorun: true,
										title: $(event.currentTarget).attr('title') || $(event.currentTarget).text(),
										destroyOnClose: true,
										content: response
									});
									new DropRemoveLabelView({
										'el': '#drop_remove_label_form'
									});
									parent.remove();
								}
							}
						});
					} else {
						$(this).animate({left: 0, top: 0});
						return true;
					}
				}
			});
		}

		// Make the assignment row labels removable
		$(this.el).find('.dragRemove').each(function () {
			// Find the label id, and only continue if successful
			var labelId = getLabelId($(this), 'href');
			if (null != labelId) {
				makeRemoveDraggable($(this), labelId);
			}
		});

		// Make the assignment row accept labels from the sidebar
		// Also handles detection of dragging labels off of the row (for deletion)

		// Keeps track of current ajax request, to prevent multiple responses
		var xhr;

		$(this.el).droppable({
			accept: function (d) {
				if (d.hasClass('dragAdd') || (d.hasClass('dragRemove') && d.hasClass(rowId))) {
					return true;
				}
			},

			over: function (event, ui) {
				if ($(ui.draggable).hasClass('dragAdd')) {
					var assignmentId = getAssignmentId($(event.target));
					var labelId = getLabelId($(ui.draggable), 'id');
					if (null != labelId) {
						xhr = $.ajax({
							url: '/assignments/validate_label/' + assignmentId + '/' + labelId,
							type: 'GET',
							dataType: 'json',
							success: function (response, textStatus) {
								var labelsDiv = $(event.target).find('.assignment_labels');

								// Add the validation visual cue
								if (response.successful) {
									labelsDiv.append('<a href="#" class="dropValid"><span>Drop to Add Label</span></a>');
								} else {
									labelsDiv.append('<a href="#" class="dropInvalid"><span>Cannot Add This Label</span></a>');
								}
							}
						});
					}
				} else {
					if ($(ui.draggable).hasClass('dragDelete')) {
						$(ui.draggable).removeClass('dragDelete').addClass('label').addClass('nowrap');
					}
				}
			},

			out: function (event, ui) {
				if ($(ui.draggable).hasClass('dragAdd')) {
					if (typeof xhr !== 'undefined') {
						xhr.abort();
					}

					// Remove visual cue
					$(event.target).find('.dropValid').each(function () {
						$(this).remove();
					});
					$(event.target).find('.dropInvalid').each(function () {
						$(this).remove();
					});
				} else {
					$(ui.draggable).removeClass('label').removeClass('nowrap').addClass('dragDelete');
				}
			},

			drop: function (event, ui) {
				if ($(ui.draggable).hasClass('dragAdd')) {
					if (typeof xhr !== 'undefined') {
						xhr.abort();
					}
					if ($(event.target).find('.dropValid').length !== 0) {
						$(event.target).find('.dropValid').each(function () {
							$(this).remove();
						});

						var assignmentId = getAssignmentId($(event.target));
						var labelId = getLabelId($(ui.draggable), 'id');
						if (null != labelId) {
							$.ajax({
								type: 'GET',
								url: '/assignments/drop_add_label/' + assignmentId + '/' + labelId,
								context: this,
								success: function (response) {
									if (!_.isEmpty(response)) {
										wmModal({
											autorun: true,
											title: $(event.currentTarget).attr('title') || $(event.currentTarget).text(),
											destroyOnClose: true,
											content: response
										});
										const closeCallback = () => {
											$(self.el).find('.dragRemove').each(function () {
												// Make the new label draggable
												var labelId = getLabelId($(this), 'href');
												if (null != labelId) {
													makeRemoveDraggable($(this), labelId);
												}
											});
										};

										new DropAddLabelView({
											'el': '#drop_add_label_form',
											millisOffset: parseInt($('#millisOffset').val()),
											closeCallback: closeCallback
										});



										var label = $.parseJSON($('<div/>').html($('#json_labels').html()).text());

										$('label[for="label_note"]').toggleClass('required', !!label.is_note_required);

										if (label.is_include_instructions && label.instructions) {
											$('#label_note_instructions').show().html(label.instructions);
										} else {
											$('#label_note_instructions').hide().empty();
										}

										if (label.is_schedule_required) {
											$('#label_reschedule').show();
										} else {
											$('#label_reschedule').hide();
										}
									}
								}
							});
						}
					} else if ($(event.target).find('.dropValid').length !== 0) {
						$(event.target).find('.dropInvalid').each(function () {
							$(this).remove();
						});
					}
				} else {
					// For deleting labels, this tells us that we dropped the label within its own assignment
					ui.draggable.data('dropped', true);
				}
			}
		});

		return this;
	}
});

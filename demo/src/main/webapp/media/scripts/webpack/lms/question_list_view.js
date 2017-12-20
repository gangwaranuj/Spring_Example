'use strict';

import Template from '../funcs/templates/confirmAction.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import wmModal from '../funcs/wmModal';
import '../dependencies/jquery.tmpl';
import 'jquery-ui';
import '../dependencies/jquery.autolink';

export default Backbone.View.extend({
	el: '#question-list-container',
	template: $('#tmpl-question-preview').template(),

	initialize: function (options) {
		this.options = options || {};
	},

	render: function () {
		var tmpl;
		var self = this;
		var questions = this.options.questions;

		$(this.el).empty();
		for (var i = 0, size = questions.length; i < size; i++) {
			tmpl = $.tmpl(this.template, questions[i]);

			// Replace URLs with links.
			tmpl.find('.question_prompt').autolink();
			tmpl.find('.question_description').autolink();

			// Attach edit CTA.
			tmpl.find('.cta-edit-question')
				.data('question_id', questions[i].id)
				.click(function () {
					self.editQuestion($(this).data('question_id'));
				});

			// Attach remove CTA.
			tmpl.find('.cta-remove-question')
				.data('question_id', questions[i].id)
				.click(function () {
					self.removeQuestion($(this).data('question_id'), $(this).closest('.question-container'));
				});

			// Make questions sortable.
			$('#question-list-container').sortable({
				handle: 'a.icon-sort',
				containment: '#question-list',
				stop: function () {
					$('#manage-questions-reorder').ajaxSubmit({
						dataType: 'json',
						success: function () {
							self.renumberQuestions();
						}
					});
				}
			});

			$(this.el).append(tmpl);
		}
	},

	addQuestion: function (data, replaceId) {
		// Check if question already exists.
		var index;
		for (var i = 0, size = this.options.questions.length; i < size; i++) {
			if (this.options.questions[i].id === parseInt(replaceId, 10)) {
				index = i;
				break;
			}
		}

		// Replace question if it already exists.
		if (typeof index !== 'undefined') {
			this.options.questions[index] = data;
		} else {
			this.options.questions.push(data);
		}

		this.render();
		this.renumberQuestions();
	},

	editQuestion: function (id) {
		var question = this.getQuestion(id);
		var position = this.getPosition(id);
		this.options.main.questionForm.render(position, question);
	},

	removeQuestion: function (id, el) {
		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: Template({
				message: 'Are you sure you want to remove this question?'
			})
		});

		$('.cta-confirm-yes').on('click', _.bind(function () {
			$.ajax({
				context: this,
				url: '/lms/manage/remove_question/' + this.options.id,
				type: 'POST',
				data: {question_id: id},
				dataType: 'json',
				success: _.bind(function (data) {
					if (data.successful) {
						// Remove from the local questions array.
						this.options.questions = $.grep(this.options.questions, function (item) {
							return item.id !== id;
						});

						// Remove from the DOM.
						el.remove();

						// Renumber the question list.
						this.renumberQuestions();
						this.confirmModal.hide();
					}
				}, this)
			});
		}, this));
	},

	getQuestion: function (id) {
		var question;
		for (var i = 0, size = this.options.questions.length; i < size; i++) {
			if (this.options.questions[i].id === id) {
				question = this.options.questions[i];
				break;
			}
		}
		return question;
	},

	getPosition: function (id) {
		var pos = 0;
		$('input[name="question_ids"]', this.el).each(function () {
			if ($(this).val() === id) {
				return false;
			}
			pos++;
		});
		return (pos + 1);
	},

	renumberQuestions: function () {
		var total = 1;
		$('#question-list-container .question-container').each(function (i) {
			$('.question-number', this).html((i + 1));
			total++;
		});

		// Check if we are currently editing.
		var questionId = $('#question_id').val();
		if (questionId) {
			$('#question-title').html('Edit Question ' + this.getPosition(questionId) + ':');
		} else {
			$('#question-title').html('Add Question ' + total + ':');
		}
	}
});

'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import AnswersListView from './answers_list';
import QuestionOptionsView from './question_options_view';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import '../dependencies/jquery.tmpl';
import qq from '../funcs/fileUploader';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	el: '#question-form',
	template: $('#tmpl-question-form').template(),

	events: {
		'change #question_type'        : 'changeQuestionType',
		'click .cta-remove-attachment' : 'removeAttachment',
		'click #cta-save-question'     : 'saveQuestion'
	},

	initialize: function (options) {
		this.options = options || {};

		var self = this;
		$('#manage-questions-form').ajaxForm({
			dataType: 'json',
			iframe: false, // Force iframe mode off or the file inputs will cause the form to not submit via ajax.
			beforeSubmit: function () {
				$('#dynamic_messages').hide();
			},
			success: function (response) {
				$('#manage-questions-form .btn.disabled').removeClass('disabled');
				if (response.successful) {
					// Add new question to question list.
					self.options.main.questionList.addQuestion(response.data.question, $('#question_id').val());
					self.options.main.addQuestion();
				} else {
					// Output error messages.
					var messages = $('<ul>');
					$.each(response.messages, function (i, item) {
						$('<li>').text(item).appendTo(messages);
					});
					$('#dynamic_messages').removeClass('success').addClass('error').show()
						.find('div').html(messages);
				}
			}
		});
	},

	render: function (position, data) {
		// Set the right form heading.
		var heading;
		if (typeof data !== 'undefined') {
			heading = 'Edit Question ' + position + ':';
		} else {
			heading = 'Add Question ' + position + ':';
		}

		// Render form fields.
		var tmpl = $.tmpl(this.template, {heading: heading});
		$(this.el).html(tmpl);

		// If we are editing, populate the form fields.
		if (typeof data !== 'undefined') {
			this.populateFields(data);
		} else {
			this.changeQuestionType();
		}

		// Create attachment uploader widget.
		var self = this;
		var uploadIndex = 0;
		this.uploader = new qq.FileUploader({
			element: document.getElementById('attachment-uploader'),
			action: '/upload/uploadqq',
			allowedExtensions: ['m4a', 'xls', 'xlsx', 'jpeg', 'xlm', 'pdf', 'xl', 'docm', 'mp4', 'mp3', 'jpe', 'txt', 'xla','xlc', 'xlsm', 'mp2', 'f4a', 'mpga', 'qt', 'f4v', 'zip', 'text', 'form', 'log', 'bmp', 'mov', 'jpg', 'png','m4v', 'flv', 'mpg4', 'doc', 'docx', 'word', 'csv', 'xlt', 'gif'],
			CSRFToken: getCSRFToken(),
			sizeLimit: 150 * 1024 * 1024, // 150MB
			multiple: true,
			template: $('#tmpl-cta-attachment').html(),
			onSubmit: function () {
				$('#dynamic_messages').hide();
			},
			onComplete: function (id, fileName, responseJSON) {
				$(self.uploader._getItemByFileId(id)).remove();
				if (responseJSON.successful) {
					$('#tmpl-upload-item').tmpl({
						name: responseJSON.file_name,
						uuid: responseJSON.uuid,
						index: uploadIndex++
					}).appendTo($('#attachment-list'));
				} else {
					// Output error messages.
					var messages = $('<ul>');
					$.each(responseJSON.errors, function (i, item) {
						$('<li>').text(item).appendTo(messages);
					});
					$('#attachment-messages').removeClass('success').addClass('error').show()
						.find('div').html(messages);
				}
			},
			showMessage: function (message) {
				wmNotify({
					message: message,
					type: 'danger'
				});
			}
		});
	},

	changeQuestionType: function (noChoice) {
		var $questionSpecificFields = $('#question-specific-fields');
		$questionSpecificFields.empty();

		var qval = this.options.questionTypes[$('#question_type').val()];
		switch (qval) {
			case 'radio':
				this.answersList = new AnswersListView({type: qval});
				$questionSpecificFields.append(this.answersList.render().el);
				if (noChoice !== true) {
					this.answersList.addChoice();
				}
				break;

			case 'checkboxes':
				this.answersList = new AnswersListView({type: qval});
				$questionSpecificFields.append(this.answersList.render().el);
				if (noChoice !== true) {
					this.answersList.addChoice();
				}
				break;

			case 'dropdown':
				this.answersList = new AnswersListView({type: qval});
				$questionSpecificFields.append(this.answersList.render().el);
				if (noChoice !== true) {
					this.answersList.addChoice();
				}
				break;
		}

		this.questionOptions = new QuestionOptionsView({type: qval});
		$questionSpecificFields.append(this.questionOptions.render().el);

		// Show manual grading message.
		if (qval === 'singleline' ||
			qval === 'multiline' ||
			qval === 'date' ||
			qval === 'phonenumber' ||
			qval === 'email' ||
			qval === 'numeric' ||
			qval === 'asset'
		) {
			$('#manual-grading-notice').show();
		} else {
			$('#manual-grading-notice').hide();
		}
	},

	removeAttachment: function (event) {
		$(event.target).closest('li').remove();
	},

	populateFields: function (data) {
		$('#question_id').val(data.id);
		$('#question_text').val(data.prompt);
		$('#question_type').val(data.type);

		// Render question type of form. (If it includes choices don't render the empty row)
		this.changeQuestionType(true);

		// Render our choices if any.
		if (data.choices && this.answersList) {
			var type = this.options.questionTypes[data.type];
			for (var i = 0, size = data.choices.length; i < size; i++) {
				if (type === 'checkboxes') {
					this.answersList.addChoice(undefined, data.choices[i]);
				} else {
					this.answersList.addChoice(undefined, data.choices[i]);
				}
			}
		}

		if (data.otherAllowed) {
			$('input[name="otherAllowed"]').prop('checked', true);
			this.questionOptions.toggleOption($('input[name="otherAllowed"]'));
		}

		if (data.maxLength) {
			$('input[name="hasMaxLength"]').prop('checked', true);
			$('input[name="maxLength"]').val(data.maxLength)
				.closest('.additional_fields').show();
		}

		if (data.hint) {
			$('input[name="hasHint"]').prop('checked', true);
			$('input[name="hint"]').val(data.hint)
				.closest('.additional_fields').show();
		}

		if (data.description) {
			$('input[name="hasDescription"]').prop('checked', true);
			$('textarea[name="description"]').val(data.description)
				.closest('.additional_fields').show();
		}

		if (!data.graded) {
			$('input[name="notGraded"]').prop('checked', true);
			this.questionOptions.toggleOption($('input[name="notGraded"]'));
		}

		if (data.incorrectFeedback) {
			$('input[name="hasIncorrectFeedback"]').prop('checked', true);
			$('textarea[name="incorrectFeedback"]').val(data.incorrectFeedback)
				.closest('.additional_fields').show();
		}

		if (data.assets) {
			for (var i = 0, size = data.assets.length; i < size; i++) {
				$('#tmpl-asset-item').tmpl(data.assets[i]).appendTo('#question-assets');
			}
		}
	},

	saveQuestion: function (event) {
		$(event.target).closest('form').trigger('submit');
	}
});

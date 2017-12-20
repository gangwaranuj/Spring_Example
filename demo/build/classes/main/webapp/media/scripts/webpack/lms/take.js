import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import React from 'react';
import { render } from 'react-dom';
import Template from './templates/video_embed.hbs';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import '../dependencies/jquery.tmpl';
import '../dependencies/jquery.autolink';
import qq from '../funcs/fileUploader';
import YouTubeTemplate from './templates/youtube_embed.hbs';
import AjaxSendInit from '../funcs/ajaxSendInit';
import TimeUpModal from './time_up_modal';
import ConfirmSubmissionView from './confirm_submission_view';
import { WMControlledModal } from '@workmarket/front-end-components';
const loadVideoJs = async() => await import(/* webpackChunkName: "videojs" */ 'video.js');

export default Backbone.View.extend({
	el: '#assessment-container',
	template: $('#tmpl-question-item').template(),

	events: {
		'click #cta-next-question, #cta-prev-question'  : 'submitAnswer',
		'click #cta-submit'                             : 'submitAssessment',
		'click .cta-remove-attachment'                  : 'removeAttachment'
	},

	initialize: function (options) {
		AjaxSendInit();
		this.options = options || {};
		this.position = 0;
		if (!_.isEmpty(this.options.responses)) {
			this.position = $.unique($.map( this.options.responses, function(val, i) {
				return val.item.id;
			})).length;
		}

		this.startTimer(options);

		$.fn.placeholder = function (options) {
			let settings = $.extend({
				onClass:false,
				offClass:'placeholder',
				placeholderSupport:(function () {
					return 'placeholder' in document.createElement('input');
				})()
			}, options);
			return this.each(function () {
				var input = this;
				if (!settings['placeholderSupport']) {
					$(input).data('defaultValue', $(input).attr('placeholder'));
					if (settings['offClass'] && $(input).val() == $(input).data('defaultValue')) {
						$(input).addClass(settings['offClass']);
					}
				}
				$(input).bind('focus',
					function () {
						if (!settings['placeholderSupport'] && ($(this).val() == $(this).data('defaultValue'))) {
							if (!settings['offClass'] || $(this).hasClass(settings['offClass'])) {
								$(this).val('');
							}
						}
						if (settings['onClass']) $(this).addClass(settings['onClass']);
						if (settings['offClass']) $(this).removeClass(settings['offClass']);
					}).bind('blur', function () {
					if (!settings['placeholderSupport'] && !$(this).val().length) {
						$(this).val($(this).data('defaultValue'));
						if (settings['offClass']) $(this).addClass(settings['offClass']);
					}
					if (settings['onClass']) $(this).removeClass(settings['onClass']);
				});
				$(input).closest('form').bind('submit', function () {
					if (!settings['offClass'] || $(this).hasClass(settings['offClass'])) {
						$(input).val('');
					}
				});
				if (!settings['placeholderSupport']) {
					$(input).blur();
				}
			});
		};

		this.imageMimeTypes = [
			'image/gif',
			'image/jpeg',
			'image/pjpeg',
			'image/png'
		];

		// Translate thrift question responses to a more friendly structure.
		this.answers = {};
		if (this.options.responses) {
			this.translateResponses(this.options.responses);
		}

		this.render();
	},

	translateResponses: function (responses) {
		this.clearResponses(responses);
		_.each(responses, this.translateResponse, this);
	},

	clearResponses: function (responses) {
		var questionIds = _.keys(_.groupBy(responses, function (res) { return res.item.id; }));
		_.each(questionIds, function (id) {
			this.answers[parseInt(id, 10)] = { answer: [] };
		}, this);
	},

	translateResponse: function (response) {
		var id = response.item.id;
		var answer = this.answers[id],
			questionType = this.options.questionTypes[this.getQuestionType(id)];

		answer.item_id = response.item.id;

		if (questionType === 'radio' || questionType === 'dropdown') {
			if (response.value) {
				answer.other = response.value;
				answer.answer = 'other';
			} else if (response.choice) {
				answer.answer = response.choice.id;
			}

		} else if (questionType === 'checkboxes') {
			if (response.value) {
				answer.other = response.value;
				answer.answer.push('other');
			} else {
				answer.answer.push(response.choice.id);
			}

		} else {
			answer.answer = response.value;
		}
		answer.assets = response.assets;
		this.answers[id] = answer;
	},

	render: function () {
		var assessmentView;

		if (this.options.type === 'survey') {

			// Draw all survey questions on 1 page.
			_.each(this.options.questions, function (question, i) {
				assessmentView = this.renderQuestion(question, i);
				$('#assessment-question').append(assessmentView);
				this.renderMedia(question);
			}, this);

			// Reapply placeholder shim for non-supported browsers.
			$('input[type=text][placeholder],textarea[placeholder]').placeholder();

			_.each($('[data-behavior=uploader]'), this.initUploader, this);

		} else {
			if (this.position === this.options.questions.length) {
				$('#assessment-progress').html('Are you finished?');
				assessmentView = $('#tmpl-assessment-confirm').tmpl();

				$('#assessment-question').html(assessmentView);
			} else {
				$('#assessment-progress').html('Question ' + (this.position + 1) + ' of ' + this.options.questions.length);

				var data = this.options.questions[this.position];
				assessmentView = this.renderQuestion(data, 0);
				$('#assessment-question').html(assessmentView);
				this.renderMedia(data);

				// Reapply placeholder shim for non-supported browsers.
				$('input[type=text][placeholder],textarea[placeholder]').placeholder();

				_.each($('[data-behavior=uploader]'), this.initUploader, this);
			}

			if (this.position === 0) {
				$('#cta-prev-question').addClass('disabled');
			} else {
				$('#cta-prev-question').removeClass('disabled');
			}

			// Check if we've answered all of the questions.
			if (this.position === this.options.questions.length) {
				$('#cta-next-question').addClass('disabled');
			} else {
				$('#cta-next-question').removeClass('disabled');
			}

			if (this.options.allQuestionsAnswered) {
				$('#cta-submit').removeClass('hidden');
			}
		}
	},

	removeAttachment: function (event) {
		$(event.target).closest('li').remove();
	},

	renderQuestion: function (data, itemIndex) {
		var tmpl = $.tmpl(this.template, $.extend({'itemIndex': itemIndex}, data));

		// Replace URLs with links.
		tmpl.find('.question_prompt').autolink();
		tmpl.find('.question_description').autolink();

		// Check if they have a previously submitted answer that we can repopulate with.
		var answers = this.answers[data.id];
		if (answers && answers.answer) {
			if (this.options.questionTypes[data.type] === 'radio') {
				tmpl.find('input[type="radio"][value="' + answers.answer + '"]').prop('checked', true);

			} else if (this.options.questionTypes[data.type] === 'checkboxes') {
				_.each(answers.answer, function (ans) {
					tmpl.find('input[type="checkbox"][value="' + ans + '"]').prop('checked', true);
				});

			} else {
				tmpl.find('[name$=".value"]').val(answers.answer);
			}

			if (answers.other) {
				tmpl.find('[name$=".value"]').val(answers.other);
			}
		}

		// Attach "other" event for dropdowns.
		if (this.options.questionTypes[data.type] === 'dropdown') {
			var responsesSelect = tmpl.find('select[name^="responses"]');
			var otherOption = responsesSelect.find('option[value="other"]');
			responsesSelect.change(function () {
				if ($(this).val() === 'other') {
					$(this).next().removeClass('dni');
				} else {
					$(this).next().addClass('dni');
				}
			});
			if (responsesSelect.val() === 'other') {
				otherOption.removeClass('dni'); // this field is very stubborn
				otherOption.show();
			} else {
				otherOption.addClass('dni');
				otherOption.hide();
			}
		}

		// Render assets.
		if (this.options.questionTypes[data.type] === 'asset') {
			if (answers && answers.assets) {
				for (var i = 0, size = answers.assets.length; i < size; i++) {
					var asset = $('#tmpl-asset-item').tmpl({
						itemIndex       : itemIndex,
						attachmentIndex : i,
						id              : answers.assets[i].id,
						uuid            : answers.assets[i].uuid,
						name            : answers.assets[i].name,
						description     : answers.assets[i].description
					});
					tmpl.find('[data-behavior=uploader-list]').append(asset);
				}
			}
		}

		return tmpl;
	},

	renderMedia: function (data) {
		// Do we have any media to embed?
		if (data.links){
			var embedLinks = [];
			_.each(data.links, function (link) {
				embedLinks.push(link.remoteUri);
			});

			if (embedLinks.length === 1) {
				var embedUrl = embedLinks[0],
					videoIdRegex = /[?|&]v=([a-zA-Z0-9_]+)/,
					videoRegex = /http:\/\/(www\.)?youtube\.com\/watch\?.*v=([a-zA-Z0-9_]+)/,
					video_id = embedUrl.match(videoIdRegex);
				const youTubePlayerContainer = $('#videoplayer_' + data.id);
				youTubePlayerContainer.append(YouTubeTemplate(video_id[1]));
				youTubePlayerContainer.parent().addClass('image');
			}
		} else if (data.assets) {
			var videos = [],
				images = [];

			for (var i = 0, size = data.assets.length; i < size; i++) {
				if ($.inArray(data.assets[i].mimeType, this.imageMimeTypes) !== -1) {
					images.push(data.assets[i]);
				} else {
					var name = data.assets[i].name;

					if (name.lastIndexOf('.') === -1)
						continue;

					var fileType = name.substring(name.lastIndexOf('.') + 1);
					this.supportedVideoTypes = ['mp4', 'm4v', 'f4v', 'mov', 'flv'];

					if (_.contains(this.supportedVideoTypes, fileType)) {
						videos.push({
							video: data.assets[i],
							type: fileType
						});
					}
				}
			}

			// Only embed a video/link if one is found.
			if (videos.length) {
				loadVideoJs()
					.then((videojs) => {
						const $videoDiv = $('#videoplayer_' + data.id);
						videos.forEach((v) => {
							// initialize videojs + set options/config object
							$videoDiv.append(Template(v));
							videojs(v.video.name, {
								width: '512',
								height: '370'
							});
						});
						$videoDiv.parent().addClass('image');
					});
			}

			// Only embed an image if one is found.
			else if (images.length === 1) {
				var img = $('<img>').attr({
					src: images[0].uri,
					style: 'max-height: 270px; max-width: 480px;'
				});
				$('#videoplayer_' + data.id).append(img).addClass('media-player image');
			}
		}
	},

	submitAssessment: function (e) {

		if ($(e.currentTarget).is('.disabled')) return;

		const TEST_TYPE = 'graded';
		// Prompt user to confirm submission of tests, not surveys

		if (this.options.type === TEST_TYPE) {
			render(
				<ConfirmSubmissionView
					open
					onConfirm={ (e) => { this.submitAssessmentConfirmed(e) } }
				/>,
				document.getElementById('confirm-submission-view')
			);
		} else {
			this.submitAssessmentConfirmed(e);
		}
	},

	submitAssessmentConfirmed: function (e) {

			// Format fields into a usable key/value pair.
			// For multiple choice questions we have to stash these into an array.
		var serialized = $(this.el).serializeArray();
		var responses = _.reduce(serialized, function (memo, o) {
			if (!memo[o.name]) {
				memo[o.name] = o.value;
			} else {
				if (_.isArray(memo[o.name])) {
					memo[o.name].push(o.value);
				} else {
					memo[o.name] = [memo[o.name], o.value];
				}
			}
			return memo;
		}, {assignment: this.options.assignment, onBehalfOf: this.options.onBehalfOf});

		var params = {
			url: '/lms/view/complete_assessment/' + this.options.id,
			type: 'POST',
			data: responses,
			dataType: 'json',
			context: this,
			success: function (json) {
				function redirect(url, msg, type) {
					if (msg) {
						var $form = $("<form class='dn'></form>");
						$form.attr({
							'action': '/message/create',
							'method': 'POST'
						});
						if (typeof msg === 'string') { msg = [msg]; }
						for (var i=0; i < msg.length; i++) {
							$form.append(
								$("<input>").attr({
									'name': 'message[]',
									'value': msg[i]
								}));
						}
						$form.append(
							$("<input>").attr({
								'name': 'type',
								'value': type
							}));
						$form.append(
							$("<input>").attr({
								'name': 'url',
								'value': url
							}));
						$form.append(
							$("<input>").attr({
								'name':'_tk',
								'value':getCSRFToken()
							}));
						$('body').append(e);
						$form.submit();
					} else {
						window.location = url;
					}
				}

				if (json.successful) {
					if (this.options.completedReturn) {
						redirect(this.options.completedReturn);
					} else {
						redirect('/lms/view/details/' + this.options.id);
					}
				} else {
					// Output error messages.
					if (json.data.errors != null) {
						Object.keys(json.data.errors).forEach(function (i) {
            	const val = json.data.errors[i];

							var messages = $('<ul>').addClass('unstyled');
							$('<li>').text(val).appendTo(messages);
							$('[data-itemid=' + i + ']')
								.find('.alert').removeClass('success').addClass('error').show()
								.find('div').html(messages);
						});
					} else if (this.questions.length === 0) {
						redirect('/lms/view/details/' + this.options.id);
					}

				}
			}
		};

		// Send question response to the server.
		$.ajax(params);
	},

	submitAnswer: function (e) {
		if ($(e.currentTarget).is('.disabled')) return;

		var direction = $(e.currentTarget).data('direction');

		// If the user is on the "You're done!" page
		if (this.options.questions.length === this.position) {
			this.goBack();
			return;
		} else if (this.options.questionTypes[this.options.questions[this.position].type] === 'segment') {
			direction === 'forward' ? this.goForward() : this.goBack();
			return;
		}

		direction === 'forward' ? this.enableForward(false) : this.enableBackward(false);

		// Format fields into a usable key/value pair.
		// For multiple choice questions we have to stash these into an array.
		var serialized = $(this.el).serializeArray();
		var responses = _.reduce(serialized, function (memo, o) {
			if (!memo[o.name]) {
				memo[o.name] = o.value;
			} else {
				if (_.isArray(memo[o.name])) {
					memo[o.name].push(o.value);
				} else {
					memo[o.name] = [memo[o.name], o.value];
				}
			}
			return memo;
		}, {assignment: this.options.assignment, onBehalfOf: this.options.onBehalfOf});

		$('.alert').hide();

		// Send question response to the server.
		$.ajax({
			url: '/lms/view/submit_answer/' + this.options.id,
			type: 'POST',
			data: responses,
			dataType: 'json',
			traditional: true,
			context: this
		}).success(function (json) {
			if (json.successful) {
				this.translateResponses(json.data.responses);
				$('.alert').hide();
				direction === 'forward' ? this.goForward() : this.goBack();
			} else {
				// Output error messages.
				Object.keys(json.data.errors).forEach(function (i) {
					const val = json.data.errors[i];

					var messages = $('<ul>').addClass('unstyled');
					$('<li>').text(val).appendTo(messages);
					$('[data-itemid=' + i + ']')
						.find('.alert').removeClass('success').addClass('error').show()
						.find('div').html(messages);
				});
				direction === 'forward' ? this.enableForward(true) : this.enableBackward(true);
			}

			this.enableSubmit(json.data.allQuestionsAnswered === true);
		});
	},

	goForward: function () {
		this.enableForward(true);

		if (this.position === this.options.questions.length) {
			return;
		}

		this.position++;
		this.render();
	},

	goBack: function () {
		this.enableForward(true);

		if (this.position === 0) {
			return;
		}

		this.position--;
		this.render();
	},

	enableForward: function (enabled) {
		$('#cta-next-question').toggleClass('disabled', !enabled);
	},

	enableBackward: function (enabled) {
		$('#cta-prev-question').toggleClass('disabled', !enabled);
	},

	enableSubmit: function (enabled) {
		$('#cta-submit').toggleClass('hidden', !enabled);
	},

	getQuestionType: function (id) {
		return _.find(this.options.questions, function (el) { return el.id === id; }).type;
	},

	isAssessmentComplete: function () {
		// this needs to be reworked to ensure all non-graded questions have been completed
		var questions = 0;
		_.each(this.options.questions, function (q) {
			if (q.graded || q.manuallyGraded) {
				questions++;
			}
		});
		return _.size(this.answers) === questions;
	},

	initUploader: function (element) {
		var list = $(element).siblings('[data-behavior=uploader-list]');
		var uploader = new qq.FileUploader({
			element: element,
			action: '/upload/uploadqq',
			allowedExtensions: ['m4a', 'xls', 'xlsx', 'jpeg', 'xlm', 'pdf', 'xl', 'docm', 'mp4', 'mp3', 'jpe', 'txt', 'xla','xlc', 'xlsm', 'mp2', 'f4a', 'mpga', 'qt', 'f4v', 'zip', 'text', 'form', 'log', 'bmp', 'mov', 'jpg', 'png','m4v', 'flv', 'mpg4', 'doc', 'docx', 'word', 'csv', 'xlt', 'gif'],
			CSRFToken: getCSRFToken(),
			sizeLimit: 150 * 1024 * 1024, // 150MB
			multiple: false,
			template: $('#tmpl-cta-attachment').html(),
			onSubmit: function () {
				$('#dynamic_messages').hide();
			},
			onComplete: function (id, fileName, response) {
				$(uploader._getItemByFileId(id)).remove();
				var itemIndex = $(element.parentElement).data('itemindex');
				if (response.successful) {
					$('#tmpl-upload-item').tmpl({
						itemIndex       : itemIndex,
						attachmentIndex : list.children().size(),
						name            : response.file_name,
						uuid            : response.uuid
					}).appendTo(list);
				} else {
					// Output error messages.
					var messages = $('<ul>');
					Object.keys(response.errors).forEach(function (i) {
						const item = response.errors[i];

						$('<li>').text(item).appendTo(messages);
					});
					$('#dynamic_messages')
						.removeClass('success')
						.addClass('error')
						.show()
						.find('div')
						.html(messages);
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

	startTimer: ({
		id,
		durationMinutes,
		timeLeft
	}) => {
		if (!parseInt(durationMinutes, 10)) {
			return;
		}

		const renderTimeUpModal = () => render(
			<TimeUpModal
				id={ parseInt(id, 10) }
				open
			/>,
			document.getElementById('time-up-modal')
		);

		if (timeLeft <= 0) {
			renderTimeUpModal();
			return;
		}

		setTimeout(renderTimeUpModal, timeLeft);
	}
});

'use strict';

import Template from './templates/details/messages.hbs';
import $ from 'jquery';
import Backbone from 'backbone';
import _ from 'underscore';
import scrollBottom from '../funcs/wmScrollBottom';
import jdenticon from '../dependencies/jquery.jdenticon';

export default Backbone.View.extend({
	el: '#messaging',
	events: {
		'click .assignment-messages--send-question'        : 'sendQuestion',
		'keyup .assignment-messages--new-question-content' : 'sendQuestion',
		'click .assignment-messages--send-message'         : 'sendMessage',
		'keyup .assignment-messages--new-message-content'  : 'sendMessage',
		'click .message--new-response'                     : 'newResponse',
		'click .message--post-response'                    : 'sendResponse',
		'keyup .message--new-response-content'             : 'sendResponse',
		'change [name="messaging-filter"]'                 : 'changeFilter',
		'click .message--avatar'                           : 'showProfileModal',
		'click .message--name'                             : 'showProfileModal',
		'click .question-pair--answer-button'              : 'newAnswer'
	},
	recentlyAdded: null,
	activeFilter: 'all',

	initialize: function (options) {
		_.extend(this, options);
		this.template = Template;

		// `this.collection` will be a generic Backbone collection to hold both
		// questions and messages. It won't be the "home" collection to either
		// questions or messages, so modifying one of those models will trigger
		// events on `this.questions` and `this.messages`. We want another,
		// generic collection so that we can sort questions and messages together.
		this.collection = new Backbone.Collection([], {
			comparator: function (model) {
				var message;

				if (model.get('responses').length > 0) {
					message = _.max(model.get('responses'), function (response) { return response.get('createdOn'); });
				} else {
					message = model;
				}

				return message.get('createdOn');
			}
		});

		this.listenTo(this.questions, 'add remove change reset', this.resetCollection);
		this.listenTo(this.messages, 'add remove change reset', this.resetCollection);
		// Listen to all collection events except when validation fails
		this.listenTo(this.collection, 'add remove reset sort change destroy request sync', this.render);

		// When the messaging filter changes, update the collection with the new
		// type of models (questions vs messages vs both)
		this.on('change:filter', this.resetCollection, this);

		// Bind an event handler to the document to listen for clicks outside a
		// message response, which would close the response box.
		$(document).on('click', _.bind(function (event) {
			var message = $(event.target).siblings('.message');
			if (message.length === 0) {
				message = $(event.target).closest('.message');
			}
			this.$('.message').not(message).removeClass('-expanded');
		}, this));

		// Populated the collection, which will trigger a render
		this.resetCollection();
	},

	render: function () {
		// Only workers can ask a question, and they can only ask one question
		// at a time.
		var myQuestions = this.questions.filter(function (question) { return question.get('creator').get('isCurrentUser'); }),
			hasNotAskedAnyQuestions = _.isEmpty(myQuestions),
			allQuestionsAreAnswered = _.every(myQuestions, function (question) { return !_.isEmpty(question.get('responses')); });

		this.$el.html(this.template({
			activeFilter: this.activeFilter,
			canAskQuestion: (hasNotAskedAnyQuestions || allQuestionsAreAnswered) && (this.isResource || this.isDispatcher) && this.status === 'sent',
			canAnswerQuestion: !(this.isResource || this.isDispatcher) && this.activeFilter === 'questions',
			canSendMessage: ((this.isResource || this.isDispatcher) && this.status !== 'sent') || (!this.isResource && !this.isDispatcher && this.activeFilter !== 'questions'),
			recentlyAdded: this.recentlyAdded,
			canSeePrivate: !(this.isResource || this.isDispatcher),
			messages: this.collection.toJSON(),
			activeWorker: this.activeWorker ? this.activeWorker.toJSON() : null,
			isCompany: !this.isResource && !this.isDispatcher,
			isActiveWorker: this.isActiveResource
		}));

		// This will remove the "new" label after 3 seconds
		_.delay(_.bind(this.removeNewLabel, this), 3000);

		// Jump to the bottom of the messages feed
		scrollBottom('.assignment-messages--feed');

		jdenticon();
	},

	removeNewLabel: function () {
		this.$('.-is-new').removeClass('-is-new');
		this.recentlyAdded = null;
	},

	newQuestion: function () {
		this.$('.assignment-messages--question-prompt').addClass('-active');
	},

	newMessage: function () {
		this.$('.assignment-messages--message-prompt').addClass('-active');
	},

	newAnswer: function (event) {
		var question = this.$(event.target).siblings('.message');
		question.addClass('-expanded');
		question.find('.message--new-response-content').trigger('focus');
		question.find('.message--post-response').text('Answer');
		// Jump to the bottom of the messages feed
		scrollBottom('.assignment-messages--feed');
	},

	newResponse: function (event) {
		var message = this.$(event.target).closest('.message');
		message.addClass('-expanded');
		message.find('.message--new-response-content').trigger('focus');
	},

	/* This will add a new question model to the questions collection, POST
	 * the model to the server, save it server side, be returned a more
	 * complete question object, and then update the question model with the
	 * new data.
	 */
	sendQuestion: function (event) {
		var didShiftEnter = event.keyCode === 13 && event.shiftKey,
			didClickSubmit = this.$(event.target).hasClass('assignment-messages--send-question');

		if (didShiftEnter || didClickSubmit) {
			this.recentlyAdded = this.questions.create({
				question: this.$('.assignment-messages--new-question-content').val(),
				isPublic: true,
				isQuestion: true
			}, { wait: true });
		}
	},

	/* This will add a new message model to the messages collection, POST
	 * the model to the server, save it server side, be returned a more
	 * complete message object, and then update the message model with the
	 * new data.
	 */
	sendMessage: function (event) {
		var didShiftEnter = event.keyCode === 13 && event.shiftKey,
			didClickSubmit = this.$(event.target).hasClass('assignment-messages--send-message');

		if (didShiftEnter || didClickSubmit) {
			var parameters = this.specifyPrivacy({ content: this.$('.assignment-messages--new-message-content').val() });

			this.recentlyAdded = this.messages.create(parameters, { wait: true });

			if (this.recentlyAdded.validationError) {
				// Insert the error message, otherwise use the property name
				// Note: we need to set it as an attribute, because using $.data()
				// only adds properties to the jQuery object, it doesn't actually set
				// the attribute onto the element.
				this.$('.assignment-messages--recipients')
					.attr('data-error-label', this.recentlyAdded.validationError)
					// Add the label state to keep the label floated,
					// add the invalid state to turn it red and whatnot
					.addClass('-invalid -label');
			}
		} else {
			this.clearErrorNotifications();
		}
	},

	/* This will add a new message model to the messages collection, POST
	 * the model to the server, save it server side, be returned a more
	 * complete message object, and then update the message model with the
	 * new data.
	 */
	sendResponse: function (event) {
		var didShiftEnter = event.keyCode === 13 && event.shiftKey,
			didClickSubmit = this.$(event.target).hasClass('message--post-response');

		if (didShiftEnter || didClickSubmit) {
			var sendButton = this.$(event.target).closest('.message--response').find('.message--post-response'),
				content = sendButton.siblings('.message--new-response-content').val(),
				parentId, parent, newResponse, didSaveResponse;

			if (sendButton.closest('.question-pair').length) {
				parentId = sendButton.closest('.question-pair').children('.message').data('id');
				newResponse = this.questions.get(parentId);
				didSaveResponse = newResponse.save(this.specifyPrivacy({ answer: content }), {
					patch: true,
					parse: true
				});
				if (didSaveResponse) {
					this.recentlyAdded = newResponse;
				} else {
					return false;
				}
			} else {
				parentId = sendButton.closest('.message-thread').children('.message').data('id');
				parent = this.messages.get(parentId);
				this.recentlyAdded = this.messages.create({
					content: content,
					parentId: parentId,
					isPublic: parent.get('isPublic'),
					isPrivileged: parent.get('isPrivileged'),
					isPrivate: parent.get('isPrivate')
				});
			}
		}
	},

	changeFilter: function (event) {
		this.activeFilter = event.currentTarget.value;
		this.trigger('change:filter');
	},

	/* If the filter is `questions`, reset the collection with questions. If
	 * the filter is `messages`, reset the collection with messages. If the
	 * filter is `all`, reset the collection with both questions and messages.
	 */
	resetCollection: function () {
		var messages, topMessages, responses, results;

		if (this.activeFilter === 'all') {
			messages = _.union(this.messages.models, this.questions.models);
		} else if (this.activeFilter === 'questions') {
			messages = this.questions.models;
		} else if (this.activeFilter === 'privileged-messages') {
			// When the assignment gets assigned, the public filter goes away, so we
			// need a place for the public messages to land.
			messages = this.messages.filter(function (message) {
				var isVisible = message.get('isPrivileged') || message.get('isPublic'),
					creator = message.get('creator'),
					isWrittenByCompany = !creator.get('isWorker'),
					isWrittenByActiveWorker = this.activeWorker && creator.get('id') === this.activeWorker.get('id');

				return isVisible && (isWrittenByCompany || isWrittenByActiveWorker);
			}, this);
		} else if (this.activeFilter === 'private-messages') {
			messages = this.messages.filter(function (message) { return message.get('isPrivate'); });
		} else if (this.activeFilter === 'public-messages') {
			messages = this.messages.filter(function (message) { return message.get('isPublic'); });
		}

		topMessages = _.reject(messages, function (message) { return message.has('parentId'); });
		responses = _.filter(messages, function (message) { return message.has('parentId'); });
		results = _.map(topMessages, function (topMessage) {
			var messageResponses = _.filter(responses, function (response) { return response.get('parentId') === topMessage.get('id'); });
			topMessage.set('responses', _.union(topMessage.get('responses') || [], messageResponses));
			return topMessage;
		});

		this.collection.reset(results);
	},

	showProfileModal: function (event) {
		event.preventDefault();
		var $profilePopup = $('#user-profile-popup');

		$profilePopup.find('.profile-body').empty();
		$profilePopup.find('.profile-spinner').show();
		$.get($(event.currentTarget).attr('href') + '?popup=1', function (result) {
			$profilePopup.find('.profile-spinner').hide();
			$profilePopup.find('.profile-body').html(result);
		});
	},

	clearErrorNotifications: function () {
		this.$('.assignment-messages--recipients').removeClass('-invalid -label');
	},

	specifyPrivacy: function (parameters) {
		if (this.activeFilter === 'private-messages' || (this.activeFilter === 'all' && this.$('[name="private"]').prop('checked'))) {
			_.extend(parameters, { isPrivate: true });
		} else if (this.activeFilter === 'privileged-messages' || this.activeFilter === 'questions' || this.isResource) {
			_.extend(parameters, { isPrivileged: true });
		} else if (this.activeFilter === 'public-messages' || this.activeFilter === 'all') {
			_.extend(parameters, { isPublic: true });
		}

		return parameters;
	}
});

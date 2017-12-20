'use strict';

import Backbone from 'backbone';
import _ from 'underscore';
import QuestionListView from './question_list_view';
import QuestionFormView from './question_form_view';

export default Backbone.View.extend({
	el: '#lms-manage-questions-ui',

	events: {
		'click #cta-add-question'            : 'addQuestion',
		'keypress #manage-questions-reorder' : 'catchKeypress'
	},

	initialize: function (options) {
		// Make sure the questions option is an array.
		if (!_.isArray(this.options.questions)) {
			this.options.questions = [];
		}

		// Initialize main view components.
		this.questionList = new QuestionListView(this.options);
		this.questionForm = new QuestionFormView(this.options);

		// Give access to other views from within each view.
		this.questionList.options.main = this;
		this.questionForm.options.main = this;

		this.render();
	},

	render: function () {
		this.questionList.render();
		this.questionList.renumberQuestions();
		this.addQuestion();
	},

	addQuestion: function () {
		this.questionForm.render(this.questionList.getPosition());
	},

	catchKeypress: function (e) {
		e.preventDefault();
	}
});

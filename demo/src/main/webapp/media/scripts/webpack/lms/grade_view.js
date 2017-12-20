'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import 'jquery-form/jquery.form';

export default Backbone.View.extend({
	el: '#grade-attempt-container',

	events: {
		'click .cta-grade-response-correct'   : 'gradeResponseCorrect',
		'click .cta-grade-response-incorrect' : 'gradeResponseIncorrect',
		'click .cta-edit-response'            : 'editResponse',
		'click #cta-download-assets'          : 'showModal'
	},

	initialize: function (options) {
		this.options = options || {};
		this.render();
	},

	gradeResponseCorrect: function (event) {
		var parent = $(event.target).closest('.score-item');
		this.submitGrade(parent, 1);
	},

	gradeResponseIncorrect: function (event) {
		var parent = $(event.target).closest('.score-item');
		this.submitGrade(parent, 0);
	},

	editResponse: function (event) {
		var parent = $(event.target).closest('.score-item');
		parent.find('.result').hide();
		parent.find('.action').show();
	},

	submitGrade: function(form, correct) {
		$(form).ajaxSubmit({
			data: {correct: correct},
			dataType: 'json',
			success: function(data) {
				if (data.successful) {
					form.find('.action').hide();
					form.find('.result').show().find('.score').html(((correct) ? 'Correct' : 'Incorrect'));
				}
			}
		});
	}
});

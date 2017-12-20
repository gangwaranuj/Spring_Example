'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import '../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	tagName: 'div',
	template: $('#tmpl-answers-table').template(),

	events: {
		'click .cta-add-choice'                    : 'addChoice',
		'click .cta-remove-choice'                 : 'removeChoice',
		'click input.single-choice-correct-outlet' : 'selectCorrectChoice'
	},

	initialize: function (options) {
		this.options = options || {};

		this.answerCount = 0;
		this.tmpl_answer_row = $('#tmpl-answer-row').template();
		this.tmpl_answer_row_multiple = $('#tmpl-answer-row-multiple').template();
	},

	render: function () {
		var tmpl = $.tmpl(this.template);
		this.$el.html(tmpl);

		// Make answers sortable.
		var self = this;
		$('tbody', this.el).sortable({
			handle: '.sort_handle',
			containment: '.answers-table',
			stop: function () {
				self.renumberChoices();
			},
			// This helper will maintain column widths.
			helper: function (e, ui) {
				ui.children().each(function () {
					$(this).width($(this).width());
				});
				return ui;
			}
		});

		return this;
	},

	addChoice: function (event, data) {
		var multiple = this.options.type === 'checkboxes',
				el;
		if (multiple) {
			el = $.tmpl(this.tmpl_answer_row_multiple, {index: this.answerCount});
		} else {
			el = $.tmpl(this.tmpl_answer_row, {index: this.answerCount});
		}
		$('tbody', this.el).append(el);
		$(el).find('input').first().focus();

		// If data was provided, populate fields with it.
		if (typeof data !== 'undefined') {
			el.find('input.choice-id-outlet').val(data.id);
			el.find('input.choice-position-outlet').val(data.position);
			el.find('input.choice-value-outlet').val(data.value);

			if (data.correct) {
				if (multiple) {
					el.find('input.choice-correct-outlet').prop('checked', true);
				} else {
					el.find('input.choice-correct-outlet').val(1);
					el.find('input.single-choice-correct-outlet').prop('checked', true);
				}
			}
		}

		this.renumberChoices();

		++this.answerCount;
	},

	removeChoice: function (event) {
		$(event.target)
			.trigger('mouseleave') // Remove tooltip prior to killing the DOM element; otherwise leaves artifact
			.closest('tr')
			.remove();
		this.renumberChoices();

		--this.answerCount;

		if (this.answerCount <= 0) {
			if (this.options.type === 'checkboxes') {
				this.addChoice();
			} else {
				this.addChoice();
			}
		}
	},

	renumberChoices: function () {
		$('tbody tr', this.el).each(function (i) {
			$('.index', this).html((i + 1) + '.');
			$('input.choice-position-outlet', this).val(i);
			$('input.choice-position-outlet', this).attr('name', 'choices[' + i + '].position');
			$('input.choice-id-outlet', this).attr('name', 'choices[' + i + '].id');
			$('input.choice-value-outlet', this).attr('name', 'choices[' + i + '].value');
			$('input.choice-correct-outlet', this).attr('name', 'choices[' + i + '].correct');
		});
	},

	selectCorrectChoice: function (e) {
		this.$('input.choice-correct-outlet').val(0);
		$(e.target).siblings('input.choice-correct-outlet').val(1);
	}
});

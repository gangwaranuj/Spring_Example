import $ from 'jquery';
import Backbone from 'backbone';
import getCSRFToken from '../funcs/getCSRFToken';
import wmModal from '../funcs/wmModal';
import wmNotify from '../funcs/wmNotify';
import SmsStep1Model from './sms_step_one_model';
import SmsStep2Model from './sms_step_two_model';
import SmsStep1Template from '../profile/templates/sms_step1.hbs';
import SmsStep2Template from '../profile/templates/sms_step2.hbs';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click .activate-sms': 'activateSms',
		'click #sms_change': 'activateSms',
		'click .wm-modal [data-slide]': 'slideModal',
		'click #sms_remove': 'removeSms'
	},

	initialize () {
		this.step1 = new SmsStep1Model();
		this.step2 = new SmsStep2Model();
		this.smsModal = wmModal({
			slides: [
				{
					title: 'Enter Your Phone Number',
					content: SmsStep1Template()
				},
				{
					title: 'Verify Your Phone Number',
					content: SmsStep2Template()
				}
			]
		});
		this.initSelectAllCheckboxes();
		this.initInvoices();
	},

	initInvoices () {
		$('input[id="pay.invoice.due.myassignments"]').on('click', () => {
			$('input[id="pay.invoice.due.mycompany"]').removeAttr('checked');
		});
		$('input[id="pay.invoice.due.mycompany"]').on('click', () => {
			$('input[id="pay.invoice.due.myassignments"]').removeAttr('checked');
		});
	},

	initSelectAllCheckboxes () {
		$('.select-all-checkboxes').on('click', function onClick () {
			$(this).closest('fieldset').find(`input[type=checkbox][rel=${$(this).attr('rel')}]:not([disabled])`).prop('checked', $(this).is(':checked'));
		});

		// initialize the select all boxes such that,
		// if everything is already selected then so is the select all box
		const isAllSelected = function isAllSelected (fieldset, type) {
			// set it to checked if the # of checkboxes == # of checked checkboxes
			return fieldset.find(`input[type=checkbox][rel=${type}][name^=notifications]`).length === fieldset.find(`input[type=checkbox][rel=${type}][name^=notifications]:checked`).length;
		};

		$('fieldset').each(function eachFunc () {
			const fieldset = $(this);

			$(this).find('.select-all-checkboxes').each(function innerEachFunc () {
				$(this).prop('checked', isAllSelected(fieldset, $(this).attr('rel')));
			});
		});

		$('input[type=checkbox][name^=notifications]').on('click', function onClick () {
			const type = $(this).attr('rel');

			if (type) {
				const fieldset = $(this).closest('fieldset');

				if (!$(this).is(':checked')) {
					$(fieldset).find(`.select-all-checkboxes[rel=${type}]`).prop('checked', false);
				} else {
					$(fieldset).find(`.select-all-checkboxes[rel=${type}]`).prop('checked', isAllSelected($(fieldset), type));
				}
			}
		});
	},

	activateSms (event) {
		event.preventDefault();
		this.smsModal.show();
	},

	slideModal (event) {
		this.currentSlide = $(event.currentTarget).closest('.wm-modal--slide');
		const shouldGoForward = event.currentTarget.getAttribute('data-slide') === 'next';
		const shouldFinish = event.currentTarget.getAttribute('data-slide') === 'finish';

		if (shouldGoForward) {
			this.step1.save(
				{ smsPhone: this.$el.find('#smsPhone').val() },
				{
					beforeSend (jqXHR) {
						jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
					}
				}
			)
				.done(this.slideForward.bind(this))
				.fail(this.showErrorMessage.bind(this));
		} else if (shouldFinish) {
			this.step2.save(
				{ code: this.$el.find('#code').val() },
				{
					beforeSend (jqXHR) {
						jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
					}
				}
			)
				.done(this.finishModal.bind(this))
				.fail(this.showErrorMessage.bind(this));
		} else {
			this.slideBack();
		}
	},

	slideForward () {
		this.currentSlide.removeClass('-active');
		this.currentSlide.next().addClass('-active');
	},

	slideBack () {
		this.currentSlide.removeClass('-active');
		this.currentSlide.prev().addClass('-active');
	},

	finishModal () {
		this.smsModal.hide();
		wmNotify({
			message: 'SMS successfully confirmed!'
		});
	},

	showErrorMessage (response) {
		wmNotify({
			message: response.message,
			type: 'danger'
		});
	},

	removeSms () {
		if (confirm('Are you certain you want to remove this SMS phone?')) { // eslint-disable-line no-alert
			$.ajax({
				url: '/mysettings/remove_sms',
				type: 'post',
				beforeSend (jqXHR) {
					jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
				}
			}).done((response) => {
				wmNotify({
					message: response.message
				});
			}).fail((response) => {
				wmNotify({
					message: response.message,
					type: 'danger'
				});
			});
		}
	}
});

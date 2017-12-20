import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import DetailsView from './details_view';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
import ConfirmActionTemplate from '../funcs/templates/confirmAction.hbs';
import '../dependencies/jquery.tmpl';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click #cta-open-wizard': 'render',
		'click #cta-submit-form': 'submitMainForm',
		'click #cta-back': 'handleBack',
		'click #cta-next': 'handleNext',
		'click #cta-add-option': 'showAddOptionForm',
		'click #cta-cancel-add-option': 'hideAddOptionForm',
		'click #cta-save-add-option': 'saveOption',
		'click .delete-duration': 'deleteOption',
		'click #reserve_funds': 'reserveFunds',
		'click .close': 'closeAlert',
		'click #show_terms_preview': 'showPaymentTermsModal'
	},

	initialize (options) {
		this.options = options || {};
		new DetailsView(); // eslint-disable-line no-new
	},

	render () {
		if (this.options.pendingPaymentWorkCount && !this.options.statementsEnabled) {
			this.confirmModal = wmModal({
				autorun: true,
				title: 'Confirm',
				destroyOnClose: true,
				content: ConfirmActionTemplate({
					message: 'You are changing your payment configuration and you currently have unpaid invoices. All existing invoices will remain on the current invoicing scheme with originally selected payment terms. Assignments created after saving your change will use your new payment settings. Are you sure you want to continue?'
				})
			});

			$('.cta-confirm-yes').on('click', () => {
				this.showWizard();
				this.confirmModal.hide();
			});
		} else {
			this.showWizard();
		}
	},

	showWizard () {
		$.ajax({
			type: 'GET',
			url: '/settings/manage/statements_payterms',
			context: this,
			success (response) {
				if (_.isEmpty(response)) {
					return;
				}

				wmModal({
					autorun: true,
					title: 'Terms Configuration Wizard',
					destroyOnClose: true,
					content: response
				});

				this.attachEvents();
			}
		});
	},

	showAddOptionForm (e) {
		$(e.target).hide();
		$('#add-option-form-placeholder').html($('#tmpl-add-option-form').html());
	},

	hideAddOptionForm () {
		$('#cta-add-option').show();
		$('#add-option-form-placeholder').html('');
	},

	saveOption () {
		const compareRows = function compareRows (a, b) {
			const aVal = parseInt($(a).find('input[name=paymentTermsDays]').val(), 10);
			const bVal = parseInt($(b).find('input[name=paymentTermsDays]').val(), 10);

			if (aVal > bVal) {
				return 1;
			}

			if (aVal < bVal) {
				return -1;
			}

			return 0;
		};

		const duration = parseInt($('#cta-option').val(), 10);
		const template = $('#tmpl-payment-option-row').template();
		const available = $(`input[value=${duration}]`, '#durations').length < 1;

		if (!available) {
			$('#option-error').html('Duration already exists.');
			$('#option-error').show();
		} else if (isNaN(duration)) {
			$('#option-error').html('Duration must be a number between 0 and 120.');
			$('#option-error').show();
		} else if (duration < 0 || duration > 120) {
			$('#option-error').html('Duration must between 0 and 120 days.');
			$('#option-error').show();
		} else {
			$('#durations tbody').append($.tmpl(template, { duration }));
			$('#durations tbody').html($('#durations tbody tr').sort(compareRows));

			$('#option-error').hide();
			$('#cta-add-option').show();
			$('#add-option-form-placeholder').html('');
		}
	},

	deleteOption (e) {
		if ($('#durations').find('.wm-icon-trash').length < 2) {
			alert('At least one payment term length must be available'); // eslint-disable-line no-alert
		} else {
			$(e.target).parents('tr').remove();
		}
	},

	submitMainForm (e) {
		e.preventDefault();

		if (!this.options.payTermsEnabled && $('#toggle_enable_pay_terms').is(':checked')) {
			this.confirmModal = wmModal({
				autorun: true,
				title: 'Confirm',
				destroyOnClose: true,
				content: ConfirmActionTemplate({
					message: 'Are you sure you want to save without setting your payment terms?'
				})
			});

			$('.cta-confirm-yes').on('click', () => {
				$('#form_paymenterms').trigger('submit');
			});
		} else {
			$('#form_paymenterms').trigger('submit');
		}
	},

	attachEvents () {
		// Apply Active class to selected payment methods
		$('#payment_terms #wizard_step_method ul li.methods label').mouseup(function mouseUp () {
			$(this).toggleClass('active');
		});

		// Apply Active class to selected payment frequency
		$('#payment_terms #wizard_step_freq ul li.frequency label').mouseup(function mouseUp () {
			$('#payment_terms #wizard_step_freq ul li.frequency label').removeClass('active');
			$(this).addClass('active');
		});

		// Select Remittance duration
		$('#payment_terms #wizard_step_days ul li.delay a.option').mouseup(function mouseUp () {
			$('#payment_terms #wizard_step_days ul li.delay a.option').removeClass('active');
			$(this).addClass('active');
			$('#payment_terms #wizard_step_days select[name=delay]').val(parseInt($(this).text(), 10));
			return false;
		});

		// Trigger the appropriate scheduling view based on payments frequency selection
		$('#payment_terms #wizard_step_freq label.weekly').mouseup(() => {
			$('#pay_schedule').show();
			$('#pay_schedule #freq_weekly').show();
			$('#fifteenDays').show();
			$('#pay_schedule #freq_biweekly').hide();
			$('#pay_schedule #freq_monthly').hide();
		});
		$('#payment_terms #wizard_step_freq label.biweekly').mouseup(() => {
			$('#pay_schedule').show();
			$('#pay_schedule #freq_weekly').hide();
			$('#pay_schedule #freq_biweekly').show();
			$('#pay_schedule #freq_monthly').hide();
			$('#fifteenDays').hide();
		});
		$('#payment_terms #wizard_step_freq label.monthly').mouseup(() => {
			$('#pay_schedule').show();
			$('#pay_schedule #freq_weekly').hide();
			$('#pay_schedule #freq_biweekly').hide();
			$('#pay_schedule #freq_monthly').show();
			$('#fifteenDays').hide();
		});
	},

	handleBack () {
		$('#payment_terms .messages').empty();

		$('#cta-next').html('Next');

		if ($('#wizard_step_freq').is(':visible')) {
			$('#wizard_step_freq').hide();
			$('#wizard_step_type').show();
			$('#cta-back').hide();
		} else if ($('#wizard_step_duration').is(':visible')) {
			$('#wizard_step_duration').hide();
			$('#wizard_step_type').show();
			$('#cta-back').hide();
		} else if ($('#wizard_step_days').is(':visible')) {
			$('#wizard_step_days').hide();
			$('#wizard_step_freq').show();
		} else if ($('#wizard_step_method').is(':visible')) {
			$('#wizard_step_method').hide();
			$('#wizard_step_days').show();
		} else if ($('#wizard_step_autopay').is(':visible')) {
			$('#wizard_step_autopay').hide();
			if ($('#payment_terms .cta-payment-type:checked').val() === 'invoice') {
				$('#wizard_step_duration').show();
			} else {
				$('#wizard_step_method').show();
			}
		} else if ($('#wizard_step_review').is(':visible')) {
			$('#wizard_step_review').hide();
			$('#wizard_step_autopay').show();
		}
	},

	handleNext () {
		const errors = [];
		$('#payment_terms .messages').empty();

		if ($('#wizard_step_type').is(':visible')) {
			if (!$('#payment_terms .cta-payment-type:checked').val()) {
				errors.push('You must first select a payment type.');
			}
			if (!errors.length) {
				if ($('#payment_terms .cta-payment-type:checked').val() === 'invoice') {
					$('#wizard_step_type').hide();
					$('#wizard_step_duration').show();
					$('#cta-back').show();
					$('#cta-next').html('Next');
				} else {
					$('#wizard_step_type').hide();
					$('#wizard_step_freq').show();
					$('#cta-back').show();
					if ($('[name=frequency]:checked').val() === 7) {
						$('#pay_schedule #freq_weekly').show();
						$('#pay_schedule #freq_biweekly').hide();
						$('#pay_schedule #freq_monthly').hide();
					} else if ($('[name=frequency]:checked').val() === 14) {
						$('#pay_schedule #freq_weekly').hide();
						$('#pay_schedule #freq_biweekly').show();
						$('#pay_schedule #freq_monthly').hide();
					} else if ($('[name=frequency]:checked').val() === 30) {
						$('#pay_schedule #freq_weekly').hide();
						$('#pay_schedule #freq_biweekly').hide();
						$('#pay_schedule #freq_monthly').show();
					} else {
						$('#pay_schedule #freq_weekly').hide();
						$('#pay_schedule #freq_biweekly').hide();
						$('#pay_schedule #freq_monthly').hide();
					}
				}
			}
		} else if ($('#wizard_step_duration').is(':visible')) {
			$('#wizard_step_duration').hide();
			$('#wizard_step_autopay').show();
			$('#cta-back').show();
			$('#cta-next').html('Submit');
		} else if ($('#wizard_step_freq').is(':visible')) {
			if ($('#pay_schedule').is(':visible')) {
				if ($('#pay_schedule #freq_weekly').is(':visible') && !$('#pay_schedule #weekdays').val()) {
					errors.push('You must first select a day to issue payments.');
				}
				if ($('#pay_schedule #freq_biweekly').is(':visible')) {
					if (
						!$('#pay_schedule [name=biweeklyCycle]:checked').val() ||
						(
							!$('#pay_schedule #biweekly_weekdays').val() &&
							!$('#pay_schedule #biweekly_set').val()
						) ||
						(
							$('#pay_schedule [name=biweeklyCycle]:checked').val() === 'dayOfWeek' &&
							!$('#pay_schedule #biweekly_weekdays').val()
						) ||
						(
							$('#pay_schedule [name=biweeklyCycle]:checked').val() === 'daysEachMonth' &&
							!$('#pay_schedule #biweekly_set').val()
						)
					) {
						errors.push('You must first select a day to issue payments.');
					}
				}
				if ($('#pay_schedule #freq_monthly').is(':visible') && !$('#pay_schedule #monthdays').val()) {
					errors.push('You must first select a day to issue payments.');
				}
			}
			if (!errors.length) {
				$('#wizard_step_freq').hide();
				$('#wizard_step_days').show();
				$('#cta-back').show();
			}
		} else if ($('#wizard_step_days').is(':visible')) {
			if (!errors.length) {
				$('#wizard_step_days').hide();
				$('#wizard_step_method').show();
				$('#cta-back').show();
			}
		} else if ($('#wizard_step_method').is(':visible')) {
			if (!$('#wizard_step_method .methods input:checked').length) {
				errors.push('You must first select at least one payment method.');
			}
			if (!errors.length) {
				$('#wizard_step_method').hide();
				$('#wizard_step_autopay').show();
				$('#cta-back').show();
			}
		} else if ($('#wizard_step_autopay').is(':visible')) {
			if (!errors.length) {
				if ($('#payment_terms .cta-payment-type:checked').val() === 'invoice') {
					this.submitForm();
					return;
				}

				// Populate review page.
				let frequencyDisp;
				let scheduleDaysDisp;
				const frequency = $('#payment_terms #wizard_step_freq input[name="frequency"]:checked').val();
				const frequencyWeekdays = $('#payment_terms #wizard_step_freq select[name="weekday"]').val();
				const frequencyBiweeklyCycle = $('#payment_terms #wizard_step_freq [name=biweeklyCycle]:checked').val();
				const frequencyBiweeklySet = $('#payment_terms #wizard_step_freq select[name="biweeklySet"]').val();

				switch (frequency) {
				case '7':
					frequencyDisp = 'Weekly <small class="meta">(7 days)</small>';
					scheduleDaysDisp = $(`#payment_terms #wizard_step_freq select[name="biweeklyWeekdays"] option[value="${frequencyWeekdays}"]`).html();
					break;
				case '14':
					frequencyDisp = 'Bi-Weekly <small class="meta">(14 days)</small>';
					if (frequencyBiweeklyCycle === 'daysEachMonth') {
						scheduleDaysDisp = $(`#payment_terms #wizard_step_freq select[name="biweeklySet"] option[value="${frequencyBiweeklySet}"]`).html();
					} else if (frequencyBiweeklyCycle === 'dayOfWeek') {
						scheduleDaysDisp = $('#payment_terms #wizard_step_freq select[name="biweeklyWeekdays"] option:selected').html();
					}
					break;
				case '30':
					frequencyDisp = 'Monthly <small class="meta">(30 days)</small>';
					scheduleDaysDisp = $('#payment_terms #wizard_step_freq select[name="monthDays"]').val();
					break;
				default:
					break;
				}
				const delayDays = $('#payment_terms #wizard_step_days select[name="delay"] :selected').val();
				const paymentMethods = [];
				let maxPaymentDays = 0;
				$('#wizard_step_method .methods input[type="checkbox"]:checked').each(function eachFunc () {
					switch ($(this).attr('id')) {
					case 'cc':
						paymentMethods.push('Credit Card');
						maxPaymentDays = Math.max(maxPaymentDays, 1);
						break;
					case 'wire':
						paymentMethods.push('Wire Transfer');
						maxPaymentDays = Math.max(maxPaymentDays, 3);
						break;
					case 'direct':
						paymentMethods.push('Direct Deposit');
						maxPaymentDays = Math.max(maxPaymentDays, 3);
						break;
					case 'check':
						paymentMethods.push('Check');
						maxPaymentDays = Math.max(maxPaymentDays, 7);
						break;
					case 'prefund':
						paymentMethods.push('Prefund');
						maxPaymentDays = Math.max(maxPaymentDays, 0);
						break;
					default:
						break;
					}
				});

				$('#wizard_question1').html(frequencyDisp);
				$('#wizard_question2').html(scheduleDaysDisp);
				$('#wizard_question3').html(delayDays);
				$('#wizard_question4').html(`${paymentMethods.join(', ')} <small class="meta">(max ${maxPaymentDays} days)</small>`);
				$('#wizard_question5').html((($('#auto_pay_enabled').is(':checked')) ? 'Enabled' : 'Disabled'));

				$('#wizard_step_autopay').hide();
				$('#wizard_step_review').show();
				$('#cta-next').html('Submit');
				$('#cta-back').show();
			}
		} else if ($('#wizard_step_review').is(':visible')) {
			this.submitForm();
			return;
		}

		if (errors.length) {
			_.each(errors, (theMessage) => {
				wmNotify({
					message: theMessage,
					type: 'danger'
				});
			});
		}
	},

	submitForm () {
		$('#payment_terms').ajaxSubmit({
			context: this,
			dataType: 'json',
			success (data) {
				if (data.successful) {
					window.location.reload();
				} else {
					_.each(data.bundle.errors, (theMessage) => {
						wmNotify({
							message: theMessage,
							type: 'danger'
						});
					});
				}
			}
		});
	},

	showPaymentTermsModal (event) {
		event.preventDefault();

		$.ajax({
			type: 'GET',
			url: event.currentTarget.href,
			context: this,
			success (response) {
				if (_.isEmpty(response)) {
					return;
				}

				wmModal({
					autorun: true,
					title: 'Payment Terms Preview',
					destroyOnClose: true,
					content: response
				});
			}
		});
	},

	closeAlert () {
		$('.alert').hide();
	},

	reserveFunds () {
		if (document.getElementById('reserve_funds').checked) {
			$('.alert').show();
		} else {
			$('.alert').hide();
		}
	}

});

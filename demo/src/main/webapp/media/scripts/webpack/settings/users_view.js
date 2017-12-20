import $ from 'jquery';
import 'jquery-ui';
import _ from 'underscore';
import Backbone from 'backbone';
import wmMaskInput from '../funcs/wmMaskInput';
import wmModal from '../funcs/wmModal';
import '../dependencies/jquery.bootstrap-collapse';
import '../dependencies/jquery.calendrical';
import LastTeamAgentWarningTemplate from '../settings/templates/last_team_agent_warning_modal.hbs';

const nonEmployeeWorker = '.toggleEmployeeWorker input:not(#employee-worker input)';

const employeeWorker = '#employee-worker input';

const enable = (input) => {
	$(input).each((index) => {
		$(input)[index].disabled = false;
	});
};

const disable = (input) => {
	$(input).each((index) => {
		$(input)[index].disabled = true;
	});
};

const uncheck = (input) => {
	$(input).each((index) => {
		$(input)[index].checked = false;
	});
};


const toggleRolesAndPayment = () => {
	if ($('.toggleEmployeeWorker input:not(#employee-worker input):checked').length > 0) {
		uncheck(employeeWorker);
		disable(employeeWorker);
	} else {
		enable(employeeWorker);
	}
};

const toggleEmployeeWorkerRole = () => {
	if ($(employeeWorker)[0].checked) {
		uncheck(nonEmployeeWorker);
		disable(nonEmployeeWorker);
		$('#spendLimit').val(0);
		disable('#spendLimit');
	} else {
		enable(nonEmployeeWorker);
		enable('#spendLimit');
	}
};

export default Backbone.View.extend({
	el: 'body',
	events: {
		'click #add-user-outlet': 'submitForm',
		'click #deactivate-user-outlet': 'deactivateModal',
		'change #payment-access-check': 'toggleFundsOption',
		'click #toggle-all': 'toggleAll',
		'click #reactivate-user-outlet': 'reactivateUser',
		'click #role2': 'toggleLane3',
		'change [name="isDispatcher"]': 'checkIfIsLastDispatcher',
		'click #employee-worker': 'toggleEmployeeWorkerRole',
		'change .toggleEmployeeWorker': 'toggleRolesAndPayment'
	},

	initialize (options) {
		this.isLastDispatcher = options.isLastDispatcher;
		wmMaskInput({ selector: '#workPhone' });

		$('#startdate').datepicker({ dateFormat: 'mm/dd/yy' });
		toggleRolesAndPayment();
		toggleEmployeeWorkerRole();
	},

	submitForm () {
		$('#form_user').submit();
	},

	deactivateModal (event) {
		event.preventDefault();

		const getModalContent = $.ajax({
			type: 'GET',
			url: event.currentTarget.href
		});

		$.when(getModalContent).done((response) => {
			if (_.isEmpty(response)) {
				return;
			}

			wmModal({
				autorun: true,
				title: 'Deactivate user',
				destroyOnClose: true,
				content: response
			});
		});
	},

	toggleFundsOption () {
		if ($('#payment-access-check').is(':checked')) {
			$('#funds-access-check').parent().show();
		} else {
			const $fundsAccess = $('#funds-access-check');
			$fundsAccess.attr('checked', false);
			$fundsAccess.parent().hide();
		}
	},

	toggleAll (event) {
		event.preventDefault();
		const active = $(event.target);

		if (active.hasClass('hidden')) {
			active.removeClass('hidden').text('Hide All');
			$('.accordion-body').removeClass('collapse');
		} else {
			active.addClass('hidden').text('Expand All');
			$('.accordion-body').addClass('collapse');
		}
	},

	reactivateUser () {
		$('#deactivate-form').submit();
	},

	toggleLane3 () {
		$('#lane3_container').toggle($('#role2').prop('checked'));
	},

	checkIfIsLastDispatcher (event) {
		if (!$(event.currentTarget).prop('checked') && this.isLastDispatcher) {
			wmModal({
				root: this.$('#dispatcherAccordion'),
				title: 'Action Required',
				autorun: true,
				destroyOnClose: true,
				content: LastTeamAgentWarningTemplate()
			});
			$('#dispatcherAccordion [data-modal-close]').on('click', this.cancelRemoveDispatcherRole.bind(this));
		}
	},

	cancelRemoveDispatcherRole (event) {
		if (!$(event.currentTarget).attr('data-modal-accept')) {
			this.$('input[type="checkbox"][name="isDispatcher"]').prop('checked', true);
		}
	},
	toggleEmployeeWorkerRole,
	toggleRolesAndPayment
});

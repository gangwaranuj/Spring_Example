import $ from 'jquery';
import Backbone from 'backbone';
import wmModal from '../funcs/wmModal';
import '../dependencies/maxlength.min';
import UniqueIDOverrideConfirmTemplate from './templates/unique_external_id_cf.hbs';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click #standard_instructions_flag': 'toggleEnableView',
		'click #standard_terms_flag': 'toggleEnableView',
		'click #enduser_terms': 'toggleEnableView',
		'click #printout_signature': 'toggleEnableView',
		'click #disable_wm_print': 'disablePrintSettings',
		'click #enable_print': 'enablePrintSettings',
		'click #render_preview': 'renderPreview',
		'click #budget_enabled_flag': 'budgetEnabled',
		'click #require_unique_id_flag': 'toggleUniqueIdNameView',
		'blur #unique_id_name': 'toggleUniqueIdNameOverrideModal'
	},

	initialize (options) {
		this.options = options || {};

		this.initToggleView('#standard_instructions_flag');
		this.initToggleView('#standard_terms_flag');
		this.initToggleView('#enduser_terms');
		this.initToggleView('#printout_signature');
		this.disablePrintSettings();
		this.enablePrintSettings();

		$('#standard_instructions').maxlength({ // TODO: Localization - refer to http://keith-wood.name/maxlength.html, used in manage/index.jsp
			feedback: '.wordsLeft',
			useInput: true,
			words: true
		});
		this.toggleUniqueIdNameView();
	},

	renderPreview () {
		wmModal({
			title: 'Resource Badge Preview',
			autorun: true,
			content: '/media/images/badge_demo.png'
		});
	},

	initToggleView (flag) {
		this.viewToggleHelper($(flag));
	},

	toggleEnableView (event) {
		this.viewToggleHelper($(event.target));
	},

	viewToggleHelper (e) {
		const well = e.data('well');

		if (e.is(':checked')) {
			$(well).removeAttr('readonly');
		} else {
			$(well).attr('readonly', 'readonly');
		}
	},

	disablePrintSettings () {
		if ($('#disable_wm_print').is(':checked')) {
			$('#printout-settings').addClass('dn');
			$('#buyer_printout_help').removeClass('dn');
		}
	},

	enablePrintSettings () {
		if ($('#enable_print').is(':checked')) {
			$('#printout-settings').removeClass('dn');
			$('#buyer_printout_help').addClass('dn');
		}
	},

	budgetEnabled () {
		if (this.options.isBudgetEnabledFlag) {
			const disable = confirm('Are you sure you want to disable your project budget feature? Disabling this feature will remove all current budget values from all your projects.'); // eslint-disable-line no-alert
			if (!disable) {
				$('#budget_enabled_flag').attr('checked', 'checked');
			}
		}
	},

	toggleUniqueIdNameView () {
		const requireUniqueIdFlagChecked = $('#require_unique_id_flag').is(':checked');
		$('#unique_id_name_view').toggleClass('dn', !requireUniqueIdFlagChecked);
		if (!requireUniqueIdFlagChecked) {
			if ($('#unique_id_name').val()) {
				$('#unique_id_name').val('');
			}
		}
	},

	toggleUniqueIdNameOverrideModal () {
		if ($('#unique_id_name_active').val() && $('#unique_id_name').val() !== $('#unique_id_name_active').val()) {
			const data = {
				currentLabel: $('#unique_id_name_active').val(),
				newLabel: $('#unique_id_name').val()
			};
			const modal = wmModal({
				title: 'Unique ID Label',
				content: UniqueIDOverrideConfirmTemplate(data),
				autorun: true,
				destroyOnClose: true,
				customHandlers: [
					{
						event: 'click',
						selector: '[data-modal-close]',
						callback: () => {
							const activeLabel = $('#unique_id_name_active').val();
							$('#unique_id_name').val(activeLabel);	// reset the input to current label
							$('#unique_id_name').focus();
						}
					}
				],
				controls: [
					{
						text: 'No',
						close: true
					},
					{
						text: 'Yes',
						primary: true,
						classList: 'unique_id_save'
					}
				]
			});

			$('.unique_id_save').on('click', () => {
				modal.hide();
			});
		}
	}
});

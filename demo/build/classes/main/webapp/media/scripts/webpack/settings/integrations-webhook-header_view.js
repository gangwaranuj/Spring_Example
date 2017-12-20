import $ from 'jquery';
import Backbone from 'backbone';
import getCSRFToken from '../funcs/getCSRFToken';
import wmModal from '../funcs/wmModal';
import ConfirmActionModalTemplate from '../templates/modals/confirmAction.hbs';

export default Backbone.View.extend({
	template: $('#web_hook_header_template'),

	events: {
		'click .web-hook-delete-header': 'removeWebHookHeader'
	},

	initialize (options) {
		this.options = options;

		this.render();
	},

	render () {
		$(this.el).html(this.template.tmpl(this.options));

		// bind events
		this.delegateEvents(this.events);
	},

	removeWebHookHeader () {
		const headerId = $(this.el).find('[rel="webHookHeader.id"]').val();

		this.confirmModal = wmModal({
			autorun: true,
			title: 'Confirm',
			destroyOnClose: true,
			content: ConfirmActionModalTemplate({
				message: 'Are you sure you want to delete this header?'
			})
		});

		$('.cta-confirm-yes').on('click', () => {
			if (headerId) {
				$.ajax({
					context: this,
					type: 'POST',
					url: `/mmw/integration/delete_web_hook_header/${headerId}`,
					beforeSend (jqXHR) {
						jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
					},
					success: () => {
						this.remove();
						this.confirmModal.hide();
					}
				});
			} else {
				this.remove();
				this.confirmModal.hide();
			}
		});
	}
});

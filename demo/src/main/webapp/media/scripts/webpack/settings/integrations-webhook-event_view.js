import $ from 'jquery';
import Backbone from 'backbone';

export default Backbone.View.extend({
	template: $('#web_hook_event_template'),

	events: {
		'click .web-hook-toggle-variables': 'toggleVariables'
	},

	initialize (options) {
		this.options = options;

		this.render();
	},

	render () {
		this.$el.html(this.template.tmpl(this.options));
		const self = this;

		this.getWebHookList().sortable({
			handle: '.web-hook-sort',
			start (event, ui) {
				ui.placeholder.height(ui.item.height());
			},
			update () {
				const webHooks = self.getWebHookList().children();

				for (let i = 0; i < webHooks.length; i += 1) {
					const id = $(webHooks[i]).find('[name="id"]').val();

					if (id) {
						$.ajax({
							type: 'POST',
							url: `/mmw/integration/update_web_hook_call_order/${id}`,
							data: { callOrder: i }
						});
					}
				}
			}
		});

		// bind events
		this.delegateEvents(this.events);
	},

	getWebHookList () {
		return this.$el.find('.web-hooks');
	},

	addWebHook (webHookView) {
		this.getWebHookList().append(webHookView.el);
	},

	toggleVariables () {
		const variableToggler = this.$el.find('.web-hook-toggle-variables');
		const variableList = this.$el.find('.web-hook-variable-list');

		if (variableList.is(':visible')) {
			variableList.hide();
			variableToggler.text(variableToggler.text().replace('Hide', 'Show'));
		} else {
			variableList.show();
			variableToggler.text(variableToggler.text().replace('Show', 'Hide'));
		}
	},

	onWebHookRemoved () {
		if (this.getWebHookList().children().length === 0) {
			this.$el.hide();
		}
	}
});

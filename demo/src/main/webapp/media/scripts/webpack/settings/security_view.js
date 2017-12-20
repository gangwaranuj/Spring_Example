import $ from 'jquery';
import Backbone from 'backbone';
import ListItemView from './security_list_item_view';

export default Backbone.View.extend({
	el: 'body',

	events: {
		'click [name=authorizeByInetAddress]': 'render',
		'click [data-behavior=add-ip]': 'create',
		'keypress [name=address]': 'createOnEnter'
	},

	initialize (options) {
		const self = this;

		this.collection = new Backbone.Collection(options.ipsJson);

		this.IP_ADDRESS = /\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/;
		this.MASK_FORMAT = new RegExp(`${this.IP_ADDRESS.source}/${this.IP_ADDRESS.source}`);
		this.CIDR_FORMAT = new RegExp(`${this.IP_ADDRESS.source}/d{1,3}`);

		this.table = this.$('table');
		this.input = this.$('[name=address]');
		this.submit = this.$('.actions [type=submit]');

		this.collection.bind('all', this.render, this);
		this.collection.bind('add', this.addOne, this);

		if (this.collection.length > 0) {
			this.table.find('tbody tr').each((i, el) => {
				new ListItemView({ // eslint-disable-line no-new
					model: self.collection.at(i),
					el: $(el)
				});
			});
		}

		this.render();
	},

	render () {
		if (this.collection.length > 0) {
			this.table.show();
			this.$('.no-results').hide();
			this.submit.prop('disabled', false);
		} else {
			this.table.hide();
			this.$('.no-results').show();
			this.submit.prop('disabled', $('[name=authorizeByInetAddress]').is(':checked'));
		}

		this.$('fieldset.settings').toggle(this.$('[name=authorizeByInetAddress]').is(':checked'));
	},

	createOnEnter (e) {
		if (e.keyCode !== 13) {
			return;
		}
		if (!this.input.val()) {
			return;
		}
		this.create(e);
	},

	create (e) {
		e.preventDefault();

		const a = this.input.val();

		if (!(a.match(this.IP_ADDRESS) || a.match(this.MASK_FORMAT) || a.match(this.CIDR_FORMAT))) {
			this.input.addClass('fieldError');
			return;
		}

		this.input.removeClass('fieldError');
		this.collection.add([{ inetAddress: a }]);
		this.input.val('');
	},

	addOne (ip) {
		const view = new ListItemView({ model: ip });
		this.table.find('tbody').append(view.render().el);
	}

});

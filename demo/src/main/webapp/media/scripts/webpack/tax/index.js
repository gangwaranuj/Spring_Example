'use strict';

import $ from 'jquery';
import Application from '../core';
import _ from 'underscore';
import Backbone from 'backbone';
import EditView from './tax_edit_view';
import SignView from './tax_sign_view';
import ViewView from './tax_view_view';

const Router = Backbone.Router.extend({
	routes: {
		'edit': 'showEdit',
		'sign': 'showSign',
		'view': 'showView'
	},
	currentView : 'edit',

	initialize: function () {
		this.options = {
			el: '#tax-form',
			tax_entities: Application.Data.taxEntities,
			is_masquerading: Application.Data.isMasquerading
		};

		if (Application.Data.defaultCountry) {
			this.options.default_country = Application.Data.defaultCountry;
		}

		this.options.router = this;

		if (this.hasTaxEntities()) {
			var self = this;
			$.each(this.options.tax_entities, function (i, entity) {
				if (entity.active_flag)
					self.options.active_tax_entity = entity;
			});
		}
		this.options.active_tax_entity = this.options.active_tax_entity || {};

		this.editView = new EditView(this.options);
		this.signView = new SignView($.extend(this.options, {el: '#tax-sign-form'}));
		this.viewView = new ViewView($.extend(this.options, {el: '#tax-view-form'}));

		if ((!_.isEmpty(this.options.active_tax_entity)) && this.options.active_tax_entity.id) {
			this.showView();
		} else {
			this.showEdit();
		}
		Backbone.history.start();
	},

	hasTaxEntities: function () {
		return !$.isEmptyObject(this.options.tax_entities);
	},

	showEdit: function () {
		this.editView.render({});
	},

	showSign: function () {
		this.signView.render({});
	},

	showView: function () {
		this.viewView.render();
	}

});

Application.init(config, Router);

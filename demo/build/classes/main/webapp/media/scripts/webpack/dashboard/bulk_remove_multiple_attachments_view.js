'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import DashboardAssetListView from '../assignments/dashboard_asset_list_view';
import wmModal from '../funcs/wmModal';
import Template from './templates/bulk_remove_attachment.hbs';

export default Backbone.View.extend({

	initialize: function (options) {
		this.modal = wmModal({
			autorun: true,
			title: 'Remove Attachments',
			destroyOnClose: true,
			content: Template()
		});

		this.assets = new DashboardAssetListView({
			selectedWorkNumbers: options.selectedWorkNumbers,
			modal: this.modal
		});
		this.assets.render();

		$('#assignment_ids_remove_attachments').val(options.selectedWorkNumbers);
	}

});

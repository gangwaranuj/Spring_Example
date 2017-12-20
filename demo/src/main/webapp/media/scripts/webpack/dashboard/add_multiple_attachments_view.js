'use strict';

import $ from 'jquery';
import Backbone from 'backbone';
import AssetListView from '../assignments/asset_list_view';
import wmModal from '../funcs/wmModal';
import Template from './templates/bulk_add_attachment.hbs'

export default Backbone.View.extend({

	initialize: function (options) {
		this.modal = wmModal({
			autorun: true,
			title: options.count > 1 ? 'Bulk Add Attachments to (' + options.count + ') Assignments' : 'Add Attachment to Assignment',
			destroyOnClose: true,
			content: Template()
		});

		this.assets = new AssetListView({
			selectedWorkNumbers: options.selectedWorkNumbers,
			modal: this.modal
		});
		this.assets.render();

		$('#assignment_ids_attachments').val(options.selectedWorkNumbers);
	}
});


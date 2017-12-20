'use strict';

import Template from './templates/details/document_asset.hbs';
import $ from 'jquery';
import _ from 'underscore';
import NonRequiredDeliverableAssetView from './non_required_deliverable_asset_view';

export default NonRequiredDeliverableAssetView.extend({
	events: _.defaults({
		'click .visibility-option': 'toggleVisibility'
	}, NonRequiredDeliverableAssetView.prototype.events),
	template: Template,

	render: function () {
		NonRequiredDeliverableAssetView.prototype.render.call(this);

		this.$assetVisibility = this.$el.find('.asset-visibility');
		this.$assetVisibilityToggle = this.$assetVisibility.find('.toggle-visibility');
		this.$visibilityDropdown = this.$assetVisibility.find('.dropdown-menu');
		this.$visibilitySelection = this.$assetVisibility.find('.visibility-selection');

		this.setVisibilitySelection(this.model.visibilityCode, this.model.visibilityDescription);

		return this;
	},

	toggleVisibility: function (e) {
		var dataSource = $(e.target).closest('a'),
			visibilityCode = dataSource.data('visibility-code'),
			visibilityDescription = dataSource.data('visibility-description');

		this.setVisibilitySelection(visibilityCode, visibilityDescription);
		$.ajax({
			url: '/assignments/update_document_visibility',
			data: {
				asset_id: this.model.id,
				work_number: this.model.workNumber,
				visibility_code: this.$visibilitySelection.val()
			},
			type: 'POST',
			dataType: 'json',
			context: this
		}).then(function (response) {
			if (!response.successful) {
				this.showErrors(response.errors);
			}
		});
	},

	setVisibilitySelection: function (visibilityCode, visibilityDescription) {
		this.$assetVisibilityToggle.children().hide();
		this.$assetVisibilityToggle.find('.' + visibilityCode).show();
		this.$assetVisibility.attr('aria-label', visibilityDescription);
		this.$visibilitySelection.val(visibilityCode);

		this.$visibilityDropdown.find('.checkmark').hide();
		this.$visibilityDropdown.find('[data-visibility-code=' + visibilityCode + ']').find('.checkmark').show();
	}
});

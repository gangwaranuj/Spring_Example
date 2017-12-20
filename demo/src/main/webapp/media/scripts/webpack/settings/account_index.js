import $ from 'jquery';
import 'jquery-form/jquery.form'; // eslint-disable-line
import Backbone from 'backbone';
import _ from 'underscore';
import GooglePlaces from '../funcs/googlePlaces';
import wmModal from '../funcs/wmModal';
import wmNotify from '../funcs/wmNotify';
import getCSRFToken from '../funcs/getCSRFToken';
import AccountVendorSearchStatusTemplate from '../templates/account/vendor-search-status.hbs';
import AddToFileManagerTemplate from './templates/add_to_filemanager.hbs';
import qq from '../funcs/fileUploader';
import '../dependencies/maxlength.min';

export default function () {
	new GooglePlaces('#company_form'); // eslint-disable-line no-new
	$('#overview').maxlength({
		max: 1000
	});

	const uploader = new qq.FileUploader({
		element: document.getElementById('file-uploader'),
		action: '/account/logoupload',
		allowedExtensions: ['jpg', 'jpeg', 'gif', 'png', 'bmp'],
		CSRFToken: getCSRFToken(),
		sizeLimit: 2 * 1024 * 1024, // 2MB
		multiple: false,
		template: $('#qq-uploader-tmpl').html(),
		onSubmit () {
			$('#photo_upload_messages').hide();
		},
		onComplete (id, fileName, responseJSON) {
			$(uploader._getItemByFileId(id)).remove(); // eslint-disable-line no-underscore-dangle
			if (responseJSON.successful) {
				if (responseJSON.asset) {
					$('.avatar_thumbnail')
						.prop('src', responseJSON.asset.uri)
						.parent()
						.find('div')
						.show();
				}

				if (responseJSON.asset_id) {
					wmModal({
						autorun: true,
						destroyOnClose: true,
						title: 'Add file to File Manager',
						content: AddToFileManagerTemplate(),
						controls: [
							{
								text: 'Cancel',
								close: true
							},
							{
								text: 'Save',
								primary: true,
								classList: 'file-manager-save'
							}
						]
					});

					$('#asset_id').val(responseJSON.asset_id);

					$('.file-manager-save').on('click', () => {
						$('#addto_filemanager_form').ajaxSubmit({
							dataType: 'json',
							beforeSend (jqXHR) {
								jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
							},
							success (responseText) {
								$('#company_form a.disabled').removeClass('disabled');
								if (responseText && !responseText.successful) {
									wmNotify({
										message: 'There was an error adding this file to the filemanager.',
										type: 'danger'
									});
								}
								window.location.reload();
							}
						});
					});
				}
			} else {
				let tmpStr = '<ul class="unstyled">';
				Object.keys(responseJSON.errors).forEach((key) => {
					tmpStr += `<li>${responseJSON.errors[key]}</li>`;
				});
				tmpStr += '</ul>';
				$('#photo_upload_messages div').html(tmpStr);
				$('#photo_upload_messages').addClass('error').removeClass('success').show();
			}
		}
	});

	$('#submit_action').on('click', (e) => {
		e.preventDefault();
		$('#company_form').trigger('submit');
	});

	$('.remove-logo').on('click', () => {
		$.ajax({
			url: '/account/logodelete',
			type: 'POST',
			beforeSend (jqXHR) {
				jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
			}
		}).done(() => {
			window.location.reload();
		});
	});

	const VendorSearchStatusModel = Backbone.Model.extend({
		url: '/account/vendor_search_status',

		canList () {
			/**
			 * Added the second condition set for buyer side to be able to opt in
			 * to be listed in vendor search if not done so
			 * during the onboarding process.
			 *
			 * This function will return TRUE for both worker and buyer companies
			 * to be able to opt into vendor search:
			 * if worker company has at least one worker AND at least one dispatcher
			 * AND has NOT opted in to be listed in vendor search
			 * 	OR
			 * if buyer company has not opted in to be listed in vendor search
			 * during the onboariding process.
			 */
			return (!this.get('isInVendorSearch') && this.get('hasAtLeastOneWorker') && this.get('hasAtLeastOneDispatcher')) ||
				(!this.get('isInVendorSearch') && this.get('isBuyer'));
		},

		parse (response) {
			return response.data;
		}
	});

	const VendorSearchStatusView = Backbone.View.extend({
		el: '.vendor-search-status',
		events: {
			'click .vendor-search-status--list': 'list',
			'click .vendor-search-status--remove': 'remove'
		},

		initialize () {
			this.listenTo(this.model, 'change', this.render);
			this.model.fetch();
		},

		render () {
			const model = _.extend({ canList: this.model.canList() }, this.model.toJSON());
			this.$el.html(AccountVendorSearchStatusTemplate(model));
		},

		list () {
			this.model.save(
				{ isInVendorSearch: true },
				{
					beforeSend (jqXHR) {
						jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
					}
				}
			);
			analytics.track('Settings', { // eslint-disable-line no-undef
				action: 'addVendorToSearch'
			});
		},

		remove () {
			this.model.save(
				{ isInVendorSearch: false },
				{
					beforeSend (jqXHR) {
						jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
					}
				}
			);
		}
	});

	new VendorSearchStatusView({ // eslint-disable-line no-new
		model: new VendorSearchStatusModel()
	});
}

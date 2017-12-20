var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.deliverables = function (workNumber, deliverableBaseJson, allowMobileSignature) {
	'use strict';

	var getDeliverables = function (workNumber) {
		$.get('/mobile/assignments/assets/list/' + workNumber, function (response) {
			$(function () {
				if (!_.isEmpty(response.data.closingAssets)) {
					var closingAssets = response.data.closingAssets;
					var $resourceTemplate = $('#resource-deliverables-list-template').html();

					_.each(closingAssets, function (asset) {
						var $individualAssetContainer = $('.' + asset.deliverableRequirementId + '-' + asset.position);
						var $lastDelivRejected = $('.' + asset.deliverableRequirementId + '-' + asset.position + ' .uploaded-' + asset.type + ' .asset-details').data('rejected');

						if ($lastDelivRejected === true && asset.rejected === false) {
							asset.updated = true;
						}

						var filename = asset.name;
						filename = _.isUndefined(filename) ? '' : filename;
						var extension = filename.split('.').pop();
						if (extension === filename) {
							// If the file does not have an extension, use "MISC" as the extension
							extension = 'MISC';
						}
						asset = _.extend(asset, { extension: extension });
						// if there's deliverables over the required amount, create a section for em
						if (!$individualAssetContainer.length) {
							var $additionalContainer = $('#additional-deliverable-container-template').html();
							var $newContainer = _.template($additionalContainer)({ asset: asset } );
							$('.individual-deliverable-container-' + asset.deliverableRequirementId).append($newContainer);
							$('.' + asset.deliverableRequirementId + '-' + asset.position).html(_.template($resourceTemplate)({ asset: asset } ));
						} else {
							$individualAssetContainer.html(_.template($resourceTemplate)({ asset: asset }));
						}
					});
				}
			});
			updateDeliverableCounts(deliverableBaseJson);
		});
	};

	var updateDeliverableCounts = function (deliverableBaseJson) {
		_.each(deliverableBaseJson, function (group) {
			var uploadedCount = $('[data-parent-id=' + group.id + '][data-uuid]').length,
				rejectedCount = $('[data-parent-id=' + group.id + '][data-rejected=true]').length,
				updatedCount = $('[data-parent-id=' + group.id + '][data-updated=true]').length,
				$countContainer = $('.' + group.id + '-count');

			if (uploadedCount) {
				$('.-' + group.type + '-tap').hide();
				$('.-' + group.type + '-click').show();
				if (uploadedCount >= group.number_of_files) {
					if (rejectedCount) {
						$countContainer.html(rejectedCount + ' rejected ' + group.type);
						if (updatedCount) {
							$countContainer.append('<span class="updated-deliv-count"> ' + updatedCount + ' updated ' + group.type  + '</span>');
						}
					} else {
						$('.completed-' + group.id).show();
						$('.additional-' + group.id).show();
						$('.-' + group.id + '-tap').hide();
					}
				} else {
					var missingCount = group.number_of_files - uploadedCount;
					var delivType = group.type === 'sign_off' ? 'sign off' : group.type;
					$countContainer.html(missingCount + ' ' + delivType + _.pluralize(' deliverable', missingCount) + ' missing');
				}
			}
		});
	};

	var $assetList = $('.deliverable-base');
	var $deliverableTmpl = $('#new-deliverable-section').html();
	var formattedDate = function (date) {
		var newDate = new Date(date);
		return newDate.getMonth() + 1  + '/' + newDate.getDate() + '/' + newDate.getFullYear();
	};

	getDeliverables(workNumber);
	$assetList.append(_.template($deliverableTmpl)({ deliverableBaseReqs: deliverableBaseJson }));

	var $deliverablesWrap = $('.deliverables-wrap');

	$deliverablesWrap.on('click', '.popup-open-deliv', function () {
		setupReviewDeliv($(this));
	});

	var setupReviewDeliv = function (asset) {
		$('.upload-replacement-button, .remove-deliverable, .rejected-details, .download-original-button').hide();

		var assetType = asset.data('type'),
			mimeType = asset.data('mime-type'),
			deliverableGroupId = asset.data('parent-id'),
			position = asset.data('position'),
			deliverableUuid = asset.data('uuid'),
			deliverableBorder = $('.preview-container .deliverable-border-preview'),
			deliverableName = asset.data('name'),
			deliverableUploadedBy = asset.data('uploaded-by'),
			deliverableUploadDate = asset.data('upload-date'),
			deliverableUri = asset.data('uri'),
			deliverableRejected = asset.data('rejected'),
			deliverableRejectedOn = asset.data('rejected-on'),
			deliverableRejectedReason = asset.data('rejected-reason'),
			deliverableUpdated = asset.data('updated'),
			popupSelector = asset.data('popup-selector'),
			removeSetup = $('.remove-deliverable'),
			emptyDetails = $('.empty-details'),
			uploadedDetails = $('.uploaded-details'),
			deliverableImage = $('.deliverable-preview'),
			uploadDeliverable = $('.upload-original-button'),
			rejectedReasonDiv = $('.rejected-reason');

		if (deliverableUuid == null) {
			uploadedDetails.hide();
			emptyDetails.show();
			deliverableImage.hide();
			deliverableBorder.html(asset.html());
			deliverableBorder.removeClass('-other-instance -photos-instance -sign_off-instance -survey-instance').addClass('-' + assetType + '-instance');
			$('.empty-popup-container').html(asset.html());
			uploadDeliverable.show();
			$('.preview-not-available').hide();
		} else {
			if (_.contains(['image/jpeg', 'image/pjpeg', 'image/jpg', 'image/gif', 'image/tiff', 'image/png'], mimeType)) {
				$('.deliverable-preview').css('background-image', 'url(' + deliverableUri + ')');
				deliverableBorder.empty();
				deliverableImage.show();
				$('.preview-not-available').hide();
				deliverableImage.show();
			} else {
				deliverableBorder.html(asset.html());
				deliverableImage.hide();
				$('.preview-not-available').show();
			}

			emptyDetails.hide();
			$('.upload-name').html(deliverableName);
			$('.uploaded-by').html(deliverableUploadedBy);
			$('.upload-date').html(formattedDate(deliverableUploadDate));
			uploadedDetails.show();
			uploadDeliverable.hide();

			if (deliverableRejected === true) {
				$('.upload-replacement-button').show();
				$('.rejected-details').show();
				$('.rejected-date').html('Rejected on ' + formattedDate(deliverableRejectedOn));
				if (deliverableRejectedReason.length) {
					rejectedReasonDiv.html(deliverableRejectedReason);
				} else {
					rejectedReasonDiv.empty();
				}

			} else {
				$('.download-original-button').attr('href', '/asset/download/' + deliverableUuid);
				$('.remove-deliverable, .download-original-button').show();
			}
		}

		$('.asset_type').val(assetType).trigger('change');
		$('.deliverable_group_id').val(deliverableGroupId);
		$('.position').val(position);

		removeSetup.attr('data-uuid', deliverableUuid);
		handleSpecialStatus(deliverableRejected, '.rejected-popup-container');
		handleSpecialStatus(deliverableUpdated, '.updated-popup-container');

		if (popupSelector.length) {
			$(popupSelector + ', .popup-background').addClass('active');
		}
	};

	var handleSpecialStatus = function (status, containerClass) {
		$(containerClass).hide();
		if (status === true) {
			$(containerClass).show();
		}
		if (containerClass === '.rejected-popup-container') {

		}
	};

	$deliverablesWrap.on('click', '.popup-open-additional', function () {
		var popupSelector = $(this).data('popup-selector'),
			deliverableGroupId = $(this).data('parent-id'),
			assetType = $(this).data('type'),
			position = $('.uploaded-' + assetType).length;

		$('.deliverable_group_id').val(deliverableGroupId);
		$('.asset_type').val(assetType).trigger('change');
		$('.position').val(position);

		if (popupSelector.length) {
			$(popupSelector + ', .popup-background').addClass('active');
		}
	});

	$('#review-deliverable-popup').on('click', '.popup-open-replacement', function () {
		var popupSelector = $(this).data('popup-selector'),
			$deliverableGroupId = $('.deliverable_group_id'),
			$assetType = $('.asset_type');

		$('.popup-content, .popup-background').removeClass('active');
		if (popupSelector.length) {
			$(popupSelector + ', .popup-background').addClass('active');
		}

		$deliverableGroupId.val($deliverableGroupId.val());
		$assetType.val($assetType.val());
	});

	$(document).on('click', '.remove-deliverable', function () {
		var assetId = $(this).data('uuid');
		removeAsset(assetId, workNumber);
	});

	$(document).on('click', '.carousel-deliv', function () {
		var $direction = $(this).data('direction'),
		$deliverableGroupId = $('.deliverable_group_id').val(),
		$order = $('.position').val(),
		$currentAsset = $('.deliverables-wrap').find('.' + $deliverableGroupId + '-' + $order + '.' + $deliverableGroupId + '-block'),
		$typeCount = $('.' + $deliverableGroupId + '-block').length - 1;

		nextAsset($direction, $deliverableGroupId, $typeCount, $currentAsset);
	});

	var nextAsset = function ($direction, $deliverableGroupId, $typeCount, $currentAsset) {
		var $holder =  (($direction === 'left') ? $currentAsset.prev('.deliverable-block') : $currentAsset.next('.deliverable-block')),
			$foundAsset = $holder.find('.asset-details'),
			$foundIcon = $holder.find('.icon-instance'),
			$nextAsset;


		if ($foundAsset.length) {
			$nextAsset = $foundAsset;
		} else if  ($foundIcon.length) {
			$nextAsset = $foundIcon;
		} else {
			$holder = (($direction === 'left') ? $('.' + $deliverableGroupId + '-' + $typeCount) : $('.' + $deliverableGroupId + '-0'));
			$foundAsset = $holder.find('.asset-details');
			$foundIcon = $holder.find('.icon-instance');
			$nextAsset = (($foundAsset.length) ? $foundAsset : $foundIcon);
		}
		setupReviewDeliv($nextAsset);
	};

	var removeAsset = function (assetId, workNumber) {
		if (!confirm('Are you sure you want to delete the selected attachment?')) return;

		$.get('/mobile/assignments/assets/remove/' + workNumber + '/' + assetId, function (response) {
			if (response.successful) {
				trackEvent('mobile','attachment','remove');
				location.reload();
			} else {
				showErrorMessages(response.messages);
			}
		});

		return this;
	};

	return function () {
		FastClick.attach(document.body);

		var doesAddAttachmentPopupExist = $('#add-attachment-popup').length;
		var doesReviewDeliverablePopupExist = $('#review-deliverable-popup').length;

		if (doesAddAttachmentPopupExist || doesReviewDeliverablePopupExist) {
			$('#upload-file').on('change', function () {
				var $uploadButton = $('.upload-button');
				var isFileSelected = !_.isEmpty($(this).val());
				$uploadButton.toggleClass('default-button', isFileSelected);
				$uploadButton.prop('disabled', !isFileSelected);
			});

			$('#add-attachment-form').one('submit', function () {
				showSpinner();
				trackEvent('mobile', 'attachment', 'add');
			});

			if (doesAddAttachmentPopupExist) {
				if (allowMobileSignature) {
					$('.asset_type').on('change', function() {
						$('.mobile-signature').toggle(_.contains(['sign_off', 'other'], $('.asset_type').val()));
					});
				}

				$('.get-signature').on('click', function() {
					window.location.href = '/mobile/assignments/signature/'+ workNumber +'?reqId=' + $('.deliverable_group_id').val() + '&pos=' + $('.position').val();
				});
			}
		}
	};

};

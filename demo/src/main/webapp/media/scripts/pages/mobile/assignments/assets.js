var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.assets = function (workNumber, allowMobileSignature) {

	var $assetsContainer = $('.closing-assets-container');
	var getAssets = function (workNumber) {
		$.get("/mobile/assignments/assets/list/" + workNumber, function (response, status) {
			$(function () {
				if (!_.isEmpty(response.data.closingAssets)) {
					var closingAssets = response.data.closingAssets;
					if (closingAssets.length) {
						var $resourceTemplate = $('#resource-asset-list-template').html();
						$('#resource-count').text(closingAssets.length);
						$closingAssetsContainer = $assetsContainer;
						$closingAssetsContainer.append(_.template($resourceTemplate)({ assets: response.data.closingAssets }));
					}
				}

				var $additionalInfo = $('#asset-metadata-template').html();
				$('.asset-info').append(_.template($additionalInfo)({assetInfo : response.data}));

			});
		});
	};

	getAssets(workNumber);

	var $assetsPage = $('.assets-page');
	var formattedDate = function (date) {
		var newDate = new Date(date);
		return newDate.getMonth() + 1  + '/' + newDate.getDate() + '/' + newDate.getFullYear();;
	};
	$assetsPage.on('click', '.popup-open-asset', function () {
		setupReviewDeliv($(this));
	});

	var setupReviewDeliv = function (asset) {

		var assetType = asset.data('type'),
			deliverableUuid = asset.data('uuid'),
			deliverableUploadedBy = asset.data('uploaded-by'),
			deliverableUploadDate = asset.data('upload-date'),
			deliverableUri = asset.data('uri'),
			popupSelector = asset.data('popup-selector'),
			removeSetup = $('.remove-asset');

			$('.deliverable-preview').attr('src', deliverableUri).show();
			$('.uploaded-by').html(deliverableUploadedBy);
			$('.upload-date').html(formattedDate(deliverableUploadDate));
			$('.uuid').val(deliverableUuid);
			$('.asset_type').val(assetType);
			$('.download-original-button').attr('href', '/asset/download/' + deliverableUuid);
			removeSetup.attr('data-uuid', deliverableUuid);

			if (popupSelector.length) {
				$(popupSelector + ', .popup-background').addClass('active');
			}
	};

	$(document).on('click', '.carousel-deliv', function () {
		var $direction = $(this).data('direction'),
			$assetId = $('.uuid').val(),
			$assetCount = parseInt($('#resource-count').text()),
			$currentAsset = $assetsContainer.find("[data-asset-id='" + $assetId + "']");

		nextAsset($direction, $assetCount, $currentAsset);
	});

	var nextAsset = function ($direction, $assetCount, $currentAsset) {
		var $holder =  (($direction === 'left') ? $currentAsset.prev('.attachment-block') : $currentAsset.next('.attachment-block')),
			$foundAsset = $holder.find('.asset-thumb'),
			$foundAssetNoPrev = $holder.find('.asset-details'),
			$nextAsset;


		if ($foundAsset.length) {
			$nextAsset = $foundAsset;
		} else if  ($foundAssetNoPrev.length) {
			$nextAsset = $foundAssetNoPrev;
		} else {
			var $firstLast = ($direction === 'left') ? 'last-child' : 'first-child';
			$nextAsset = $('.closing-assets-container > :' + $firstLast).find('.popup-open-asset');
		}
		setupReviewDeliv($nextAsset);
	};


	$('.asset-list').on('click', '.asset-remove', function () {
		removeAsset($(this).closest('li').data('asset-id'), workNumber);
	});

	$('.popup-content').on('click', '.remove-asset', function () {
		var assetId = $(this).data('uuid');
		removeAsset(assetId, workNumber);
	});

	var removeAsset = function (assetId, workNumber) {
		if (!confirm('Are you sure you want to delete the selected attachment?')) return;

		$.get("/mobile/assignments/assets/remove/" + workNumber + "/" + assetId, function (response) {
			if (response.successful) {
				var $resourceCount = $('#resource-count');
				var $remainingCount = $('#remaining-asset-count');
				$assetsContainer.find("[data-asset-id='" + assetId + "']").remove();
				$('.popup-content, .popup-background').removeClass('active');
				$resourceCount.text(parseInt($resourceCount.text()) - 1);
				$remainingCount.text(parseInt($remainingCount.text()) + 1);
				trackEvent('mobile','attachment','remove');
				showSuccessMessages(response.messages);
			} else {
				showErrorMessages(response.messages);
			}
		});
		return this;
	};

	return function () {
		FastClick.attach(document.body);

		if ($('#add-attachment-popup').length) {
			if (allowMobileSignature) {
				$('.asset_type').on('change', function () {
					$('.mobile-signature').toggle(_.contains(['sign_off', 'other'], $('.asset_type').val()));
				});
			}

			$('.get-signature').on('click', function () {
				window.location.href = '/mobile/assignments/signature/'+ workNumber +'?reqId=' + $('.deliverable_group_id').val() + '&pos=' + $('.position').val();
			});

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
		}
	}
};

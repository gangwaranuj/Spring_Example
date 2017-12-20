var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};
wm.pages.mobile.assignments = wm.pages.mobile.assignments || {};

wm.pages.mobile.assignments.documents = function (workNumber) {

	var getDocuments = function (workNumber) {
		$.get("/mobile/assignments/assets/list/" + workNumber, function(response, status) {
			$(function () {
				if (!_.isEmpty(response.data.buyerAssets)) {
					var buyerAssets = response.data.buyerAssets;
					if (buyerAssets.length) {
						var $buyerTemplate = $('#buyer-documents-list-template').html();
						$('#buyer-count').text(buyerAssets.length);
						$('.buyer-assets-container').append(_.template($buyerTemplate)({ assets: buyerAssets }));
					}
				}
			});
		});
	};

	getDocuments(workNumber);

};

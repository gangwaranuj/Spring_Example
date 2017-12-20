var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.mobile = wm.pages.mobile || {};

// TODO: Remove this once the QuickBooks promotion is over.
var qbSVG = '<svg style="width: 1em; height: 1em; vertical-align: top;" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:cc="http://creativecommons.org/ns#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:svg="http://www.w3.org/2000/svg" xmlns="http://www.w3.org/2000/svg" version="1.1" id="svg2" xml:space="preserve" width="145.66266" height="145.664" viewBox="0 0 145.66266 145.664"> <metadata id="metadata8"> <rdf:RDF> <cc:Work rdf:about=""> <dc:format>image/svg+xml</dc:format><dc:type rdf:resource="http://purl.org/dc/dcmitype/StillImage"/> <dc:title></dc:title> </cc:Work> </rdf:RDF> </metadata> <defs id="defs6"> <clipPath clipPathUnits="userSpaceOnUse" id="clipPath18"><path d="M 0,612 792,612 792,0 0,0 Z" id="path16"/></clipPath> </defs> <g transform="matrix(1.3333333,0,0,-1.3333333,145.66266,72.831998)" id="g90"><path id="path92" style="fill:#2ca01c;fill-opacity:1;fill-rule:nonzero;stroke:none" d="m 0,0 c 0,-30.168 -24.455,-54.624 -54.623,-54.624 -30.168,0 -54.624,24.456 -54.624,54.624 0,30.168 24.456,54.624 54.624,54.624 C -24.455,54.624 0,30.168 0,0"/></g> <g transform="matrix(1.3333333,0,0,-1.3333333,69.536403,115.8164)" id="g94"><path id="path96" style="fill:#ffffff;fill-opacity:1;fill-rule:nonzero;stroke:none" d="m 0,0 -7.643,0 0,46.391 -6.836,0 c -7.782,0 -14.113,-6.331 -14.113,-14.113 0,-7.783 6.331,-14.114 14.113,-14.114 l 1.991,0 0,-7.642 -1.991,0 c -11.997,0 -21.756,9.759 -21.756,21.756 0,11.996 9.759,21.756 21.756,21.756 l 14.479,0 z"/></g> <g transform="matrix(1.3333333,0,0,-1.3333333,95.322803,101.78747)" id="g98"><path id="path100" style="fill:#ffffff;fill-opacity:1;fill-rule:nonzero;stroke:none" d="m 0,0 -14.479,0 0,54.034 7.643,0 0,-46.391 6.836,0 c 7.782,0 14.113,6.331 14.113,14.113 0,7.782 -6.331,14.113 -14.113,14.113 l -1.991,0 0,7.643 1.991,0 c 11.997,0 21.756,-9.759 21.756,-21.756 C 21.756,9.76 11.997,0 0,0"/></g> </svg>';

wm.pages.mobile.notifications = function (page) {
	var isFetchingListData = false;
	var currentPage = page;
	var hasMore = true;
	var maxScrollY = 0;
	var $content;

	var getNotifications = function (page) {
		if (!hasMore) { return; }

		isFetchingListData = true;

		$.get("/mobile/notifications/list/" + page, function(response, status) {
			if (!_.isEmpty(response.data.notifications)) {
				$(function () {
					var $template = $('#notification-template').html();
					var newNotifications = response.data.notifications;
					hasMore = response.data.pagination.hasMore;

					// TODO: Remove this once the QuickBooks promotion is over.
					var today = new Date();
					if (
						!hasMore
						&& today.getFullYear() === 2016
						&& today.getMonth() === 3
						&& (today.getDate() === 8 || today.getDate() === 18)
					) {
						newNotifications.unshift({
						    "id": 0,
						    "createdOn": 1460088000000,
						    "modifiedOn": null,
						    "modifierId": null,
						    "creatorId": null,
						    "creatorNumber": null,
						    "modifierNumber": null,
						    "deleted": false,
						    "notificationType": {
						        "code": "workmarket.marketing",
						        "description": null,
						        "configurableFlag": false,
						        "isDefault": false,
						        "isBullhornDefault": false,
						        "emailFlag": null,
						        "followFlag": null,
						        "bullhornFlag": null,
						        "pushFlag": null,
						        "smsFlag": null,
						        "userNotificationFlag": false,
						        "voiceFlag": false,
						        "dispatchEmailFlag": null,
						        "dispatchBullhornFlag": null,
						        "dispatchPushFlag": null,
						        "dispatchSmsFlag": null,
						        "dispatchVoiceFlag": false,
						        "default": false,
						        "bullhornDefault": false,
						        "configurable": false
						    },
						    "displayMessage": '<a href="https://selfemployed.intuit.com/workmarket?utm_source=workmarket&utm_medium=IPD&utm_content=announcement&cid=IPD_workmarket_announcement_QBSE&utm_email=" target="_blank">Your next tax payment is due April 18, 2016. Save money on your taxes with QuickBooks Self-Employed. Try it free. ' + qbSVG + '</a>',
						    "modalMessage": null,
						    "sticky": true,
						    "notificationStatus": {
						        "code": "published",
						        "description": null
						    },
						    "fromUser": null,
						    "user": null,
						    "viewedAt": 1459728000000,
						    "viewed": false,
						    "createdOnString": "2016-04-08T00:00:00.000Z",
						    "modifiedOnString": null,
						    "idHash": "intuit-quickbooks-promo",
						    "encryptedId": "intuit-quickbooks-promo"
						});
					}

					$('.notifications').append(_.template($template)({ notifications: newNotifications }));
					currentPage++;
					$('.zero-notifications').hide();
					$('.notifications').show();
				});
			} else {
				$('.zero-notifications').show();
				$('.notifications').hide();
			}
		});

		isFetchingListData = false;
	};

	// Initial load
	getNotifications(page);

	return function() {
		FastClick.attach(document.body);

		$content = $('.content');
		maxScrollY = window.scrollY;

		$('.up').on('click', function () {
			$('html, body').animate({scrollTop:0}, 'fast');
			return false;
		});

		$(document).on('scroll', function () {
			$('.up').toggle($(document).scrollTop() > 300);

			if (isFetchingListData) {
				return false;
			}

			if (window.scrollY > maxScrollY) {
				// scrolling past previous max point, so check if fetch needed
				maxScrollY = window.scrollY;

				if ((window.scrollY + (3 * window.innerHeight)) >= $content.height()) {
					_.once(getNotifications(currentPage));
				} else {
					return false;
				}
			}
		});
	};
};

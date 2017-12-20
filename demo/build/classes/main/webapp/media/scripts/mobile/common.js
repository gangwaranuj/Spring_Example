function geo_distance(lat1, lon1, lat2, lon2) {
	var R = 6371; // km
	var dLat = (lat2-lat1) * (Math.PI / 180);
	var dLon = (lon2-lon1) * (Math.PI / 180);
	var lat1 = lat1 * (Math.PI / 180);
	var lat2 = lat2 * (Math.PI / 180);

	var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
	var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	var d = R * c;
	var mPerKm = 0.621371;
	return d * mPerKm;
}

var isNative = ($.cookie('wm-app-platform') != null);

function showMessages(aTemplate, messagez) {
	var template = _.template($(aTemplate).html())({ messages: messagez });
	$('.alert-message').remove();
	$('#public-message').addClass('active').html(template);
}

var showSuccessMessages = _.once(_.partial(showMessages, '#success-notices-template'));
var showErrorMessages = _.once(_.partial(showMessages, '#error-notices-template'));

function format_currency(num) {
	num = (num || 0).toString().replace(/\$|\,/g, '');
	if (!isFinite(num))
		num = "0";

	sign = (num == (num = Math.abs(num)));
	num = Math.floor(num * 100 + 0.50000000001);
	cents = num % 100;
	num = Math.floor(num / 100).toString();

	if (cents < 10)
		cents = "0" + cents;

	for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
		num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3));

	return (((sign) ? '' : '-') + '$' + num + '.' + cents);
}

function showSpinner() {
	var spinner = document.getElementById('spinner-container');
	if (spinner.offsetWidth > 0 || spinner.offsetHeight > 0) { return; } // already visible, don't show again

	//This script creates a new CanvasLoader instance and places it in the wrapper div
	var cl = new CanvasLoader('spinner-container');
	cl.setColor('#f7951d');     // default is '#000000'
	cl.setShape('roundRect');   // default is 'oval'
	cl.setDiameter(60);         // default is 40
	cl.setDensity(20);          // default is 40
	cl.setRange(0.7);           // default is 1.3
	cl.setSpeed(0.8);             // default is 2
	cl.show();

	spinner = document.getElementById('spinner-container');
	spinner.parentNode.style.display = 'block';
}

function hideSpinner () {
	var spinner = document.getElementById('spinner-container');
	if (spinner !== null) {
		spinner.parentNode.style.display = 'none';
	}
}

function redirect(url, msg, type) {
	if (msg) {
		var e = $("<form class='dn'></form>");
		e.attr({
			'action': '/message/create',
			'method': 'POST'
		});
		if (typeof msg === 'string') { msg = [msg]; }
		for (var i=0; i < msg.length; i++) {
			e.append(
				$("<input>").attr({
					'name': 'message[]',
					'value': msg[i]
				}));
		}
		e.append(
			$("<input>").attr({
				'name': 'type',
				'value': type
			}));
		e.append(
			$("<input>").attr({
				'name': 'url',
				'value': url
			}));
		e.append(
			$("<input>").attr({
				'name':'_tk',
				'value':getCSRFToken()
			}));
		$('body').append(e);
		e.submit();
	} else {
		window.location = url;
	}
}

$(document).on('ready', function () {
	//collapsible divider
	$('.show').on('click', function () {
		$(this).toggleClass('active');
	});

	// popup stuff
	$(document).on('click', '.popup-open', function () {
		var popupSelector = $(this).data('popup-selector');
		if(popupSelector.length) {
			$(popupSelector + ', .popup-background').addClass('active');
		}
	});

	$('.popup-close, .popup-background').on('click', function () {
		$('.popup-content, .popup-background').removeClass('active');
	});

	//panel opening variables
	var $panel = $('#wm-panel-page');
	var $background = $('#panel-background');

	var stashPanel = function () {
			$('.active-nav-button').removeClass('active-nav-button');
			$panel.removeClass('open-panel');
			$background.hide();
	};
	var openPanel = function () {
		$panel.addClass('open-panel');
		$('.wmpanel-button').addClass('active-nav-button');
		$background.show();
	};

	$('.wmpanel-button').on('click', function () {
		if ($(this).hasClass('active-nav-button')) {
			stashPanel();
		} else {
			$(this).addClass('active-nav-button');
			openPanel();
		}
		$background.on('click', stashPanel);
	});

	$(document).one('click', '.spin', showSpinner);
});

window.onbeforeunload = hideSpinner;

$(document).on('pageinit pageshow', 'div:jqmData(role="page"), div:jqmData(role="dialog")', function () {
	var is_uiwebview = /(iPhone|iPod|iPad).*AppleWebKit(?!.*Safari)/i.test(navigator.userAgent);
	if (is_uiwebview) {
		$(".full_site_link").hide();
	}
});

$(function () {
	$.ajaxSetup({ cache:false });

	$.datepicker.setDefaults({
		dayNamesMin:['S', 'M', 'T', 'W', 'T', 'F', 'S'],
		changeMonth:true,
		changeYear:true,
		numberOfMonths:2,
		showButtonPanel:true,
		showOn:'both',
		buttonImageOnly:true,
		buttonImage:mediaPrefix + '/images/icons/calendar-gray.gif',
		buttonText:'Calendar'
	});

	// Override: Set date upon click "Today" button.
	$.datepicker._gotoToday = function (id) {
		var target = $(id);
		var inst = this._getInst(target[0]);
		if (this._get(inst, 'gotoCurrent') && inst.currentDay) {
			inst.selectedDay = inst.currentDay;
			inst.drawMonth = inst.selectedMonth = inst.currentMonth;
			inst.drawYear = inst.selectedYear = inst.currentYear;
		}
		else {
			var date = new Date();
			inst.selectedDay = date.getDate();
			inst.drawMonth = inst.selectedMonth = date.getMonth();
			inst.drawYear = inst.selectedYear = date.getFullYear();
			this._setDateDatepicker(target, date);
			this._selectDate(id, this._getDateDatepicker(target));
		}
		this._notifyChange(inst);
		this._adjustDate(target);
	};

	$('input[type=text][placeholder],textarea[placeholder]').placeholder();

	// Fix to allow text inputs/selects inside of labels to be clicked and not fire off the label's event.
	$('label input[type="text"], label select').on('click', function (e) {
		e.preventDefault();
	});
});

(function($) {
	$.fn.cellRenderer = function(options) {
		var self = this;
		return function(row) {
			var table = $(row.oSettings.nTable);
			var meta = table.data('meta');
			return $(self).tmpl({
				data: row.aData[row.iDataColumn],
				meta: meta[row.iDataRow]
			}).html();
		};
	};
})(jQuery);

// post a message to be displayed after a redirect
function redirectWithFlash(url, type, msg) {
	var e = $('<form></form>');
	e.attr({
		'action':'/message/create',
		'method':'POST'
	});
	e.append(
		$('<input>').attr({
			'name': 'message[]',
			'value': msg
		}));
	e.append(
		$('<input>').attr({
			'name': 'type',
			'value': type
		}));
	e.append(
		$('<input>').attr({
			'name': 'url',
			'value': url
		}));
	e.append(
		$('<input>').attr({
			'name': '_tk',
			'value': getCSRFToken()
		}));
	$('body').append(e);
	e.submit();
}

function showModal(e, params) {
	if ($(e.currentTarget).is('.disabled')) {
		e.preventDefault();
		return false;
	}

	$.colorbox($.extend({
		href: e.currentTarget.href,
		title: $(e.currentTarget).attr('title') || $(e.currentTarget).text(),
		transition: 'none'
	}, params));

	e.preventDefault();
	return false;
}


function close_message(el) {
	$(el).parent().fadeOut(250);
}

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

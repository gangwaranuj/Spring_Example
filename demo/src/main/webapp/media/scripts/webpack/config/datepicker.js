'use strict';

import $ from 'jquery';
import 'jquery-ui';

$.datepicker.setDefaults({
	dayNamesMin:['S', 'M', 'T', 'W', 'T', 'F', 'S'],
	changeMonth:true,
	changeYear:true,
	numberOfMonths:2,
	showButtonPanel:true,
	showOn:'both',
	buttonImageOnly:true,
	buttonImage: window.mediaPrefix + '/images/icons/calendar-gray.gif',
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

import $ from 'jquery';
import Backbone from 'backbone';
import 'datatables.net';
import Application from '../core';
import PreBuiltReportingRouter from './reports_router';
import MainRouter from './main_router';
import BackgroundView from './evidence_report_view';
import CertificationView from './cert_evidence_report_view';
import InsuranceView from './insurance_evidence_report_view';
import LicenseView from './license_evidence_report_view';
import StatisticsPage from './statistics_page';
import '../config/datepicker';

Application.init({ name: 'reports', features: config }, () => {});

/* eslint-disable no-new */
switch (config.mode) {
case 'statistics':
	StatisticsPage(config.companyId);
	break;
case 'manage':
	new MainRouter({
		mode: config.mode,
		savedReportKey: config.savedReportKey,
		name: config.name,
		filterTypes: {
			dateRange: config.filterTypes.dateRange
		}
	});
	Backbone.history.start();
	break;
case 'results':
	$.fn.dataTableExt.sErrMode = 'throw';
	$.extend($.fn.dataTableExt.oSort, {
		'currency-pre': function (a) {
			a = (a === '-') ? 0 : a.replace(/[^\d\-\.]/g, '');
			return parseFloat(a);
		},

		'currency-asc': function (a, b) {
			return a - b;
		},

		'currency-desc': function (a, b) {
			return b - a;
		}
	});

	$.fn.dataTableExt.oSort['currency-asc'] = function func (a, b) {
		let x, y;

		/* Remove any commas (assumes that if present all strings will have a fixed number of d.p) */
		x = (a === '-' || a === '--' || a === '' || a.toLowerCase().replace('/', '') === 'na') ? -1 : a.replace(/,/g, '');
		y = (b === '-' || b === '--' || b === '' || b.toLowerCase().replace('/', '') === 'na') ? -1 : b.replace(/,/g, '');

		/* Remove the currency sign */
		if (typeof x === 'string' && isNaN(parseFloat(x.substr(0, 1)))) {
			x = x.substring(1);
		}
		if (typeof y === 'string' && isNaN(parseFloat(y.substr(0, 1)))) {
			y = y.substring(1);
		}

		/* Parse and return */
		x = parseFloat(x);
		y = parseFloat(y);

		return x - y;
	};
	$.fn.dataTableExt.oSort['currency-desc'] = function (a, b) {
		let x, y;

		/* Remove any commas (assumes that if present all strings will have a fixed number of d.p) */
		x = (a === '-' || a === '--' || a === '' || a.toLowerCase().replace('/', '') === 'na') ? -1 : a.replace(/,/g, '');
		y = (b === '-' || b === '--' || b === '' || b.toLowerCase().replace('/', '') === 'na') ? -1 : b.replace(/,/g, '');

		/* Remove the currency sign */
		if (typeof x === 'string' && isNaN(parseFloat(x.substr(0, 1)))) {
			x = x.substring(1);
		}
		if (typeof y === 'string' && isNaN(parseFloat(y.substr(0, 1)))) {
			y = y.substring(1);
		}

		/* Parse and return */
		x = parseFloat(x);
		y = parseFloat(y);

		return y - x;
	};
	new MainRouter({
		mode: config.mode,
		savedReportKey: config.savedReportKey,
		name: config.name,
		filterTypes: {
			dateRange: config.filterTypes.dateRange
		}
	});
	Backbone.history.start();
	break;
case 'background':
	new BackgroundView({
		groupId: config.groupId,
		recipientEmail: config.recipientEmail,
		screeningType: config.screeningType
	});
	break;
case 'certification':
	new CertificationView({
		groupId: config.groupId,
		recipientEmail: config.recipientEmail,
		screeningType: config.screeningType
	});
	break;
case 'insurance':
	new InsuranceView({
		groupId: config.groupId,
		recipientEmail: config.recipientEmail,
		screeningType: config.screeningType
	});
	break;
case 'license':
	new LicenseView({
		groupId: config.groupId,
		recipientEmail: config.recipientEmail,
		screeningType: config.screeningType
	});
	break;
default :
	new PreBuiltReportingRouter();
	Backbone.history.start({ pushState: true });
}

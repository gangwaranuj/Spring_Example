var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.certifications = wm.pages.admin.certifications || {};

wm.pages.admin.certifications.review = function () {
	var
		vendors_list_obj,
		certifications_list_obj,
		userscertifications_list_obj,

		vendorListFilters,
		certificationListFilters,
		serCertificationListFilter,

		meta = {},

		applyListFilter,
		clearListFilter,
		changeListFilter,
		renderList,

		renderVendorActionCell,
		renderCertificationActionCell,
		renderUserCertificationActionCell,
		renderUserLicenseNumberCell;

	applyListFilter = {
		vendor: function () {
			vendorListFilters = $('#vendor-list-filter-form').serializeArray();
			vendors_list_obj.fnDraw();
		},
		certification: function () {
			certificationListFilters = $('#certification-list-filter-form').serializeArray();
			certifications_list_obj.fnDraw();
		},
		userCertification: function () {
			userCertificationListFilter = $('#userCertification-list-filter-form').serializeArray();
			userscertifications_list_obj.fnDraw();
		}
	};

	clearListFilter = {
		vendor: function () {
			vendorListFilters = [];
			vendors_list_obj.fnDraw();
		},
		certification: function () {
			certificationListFilters = [];
			certifications_list_obj.fnDraw();
		},
		userCertification: function () {
			userCertificationListFilter = [];
			userscertifications_list_obj.fnDraw();
		}
	};

	changeListFilter = function (event) {
		if ($(this).val() !== '') {
			applyListFilter[event.data.filtersName]();
		} else {
			clearListFilter[event.data.filtersName]();
		}
	};

	renderVendorActionCell = function (row) {
		return $('#vendor-action-cell-tmpl').tmpl({
			meta: meta.vendor[row.iDataRow]
		}).html();
	};

	renderCertificationActionCell = function (row) {
		return $('#certification-action-cell-tmpl').tmpl({
			meta: meta.certification[row.iDataRow]
		}).html();
	};

	renderUserCertificationActionCell = function (row) {
		return $('#userCertification-action-cell-tmpl').tmpl({
			meta: meta.userCertification[row.iDataRow]
		}).html();
	};

	renderUserLicenseNumberCell = function (row) {
		return $('#userCertification-number-cell-tmpl').tmpl({
			data: row.aData[row.iDataColumn],
			meta: meta.userCertification[row.iDataRow]
		}).html();
	};

	renderList = function (fnCallback, filterList) {
		return function (json) {
			if (json.errors === void 0) {
				meta[filterList] = json.aMeta;
				_.each(json.aaData, function (element, index) {
					json.aaData[index][2] = '<a href="/profile/' + json.aMeta[index]['user_number']+'">' + element[2] + '<\/a>';
				});
			} else {
				// TODO[Joe]: Find better error handling than creating an emptied object
				json = {
					aFilters: {},
					aMeta: [],
					aaData: [],
					iTotalDisplayRecords: 0,
					iTotalRecords: 0
				};
			}
			fnCallback(json);
		};
	};

	return function () {
		// Run the filters on load.
		vendorListFilters = $('#vendor-list-filter-form').serializeArray();
		certificationListFilters = $('#certification-list-filter-form').serializeArray();
		userCertificationListFilter = $('#userCertification-list-filter-form').serializeArray();

		$('#vendor-list-filter').on('change', { filtersName: 'vendor' }, changeListFilter);
		$('#certification-list-filter').on('change', { filtersName: 'certification' }, changeListFilter);
		$('#userCertification-list-filter').on('change', { filtersName: 'userCertification' }, changeListFilter);

		vendors_list_obj = $('#vendor-list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 50,
			'aaSorting': [[1,'desc']],
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [6]},
				{'fnRender': renderVendorActionCell, 'aTargets': [6]}
			],
			'sAjaxSource': '/admin/certifications/pending_vendors',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				// Apply filters.
				_.each(vendorListFilters, function (element) {
					aoData.push(element);
				});
				$.getJSON( sSource, aoData, renderList(fnCallback, 'vendor'));
			}
		});

		certifications_list_obj = $('#certification-list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 50,
			'aaSorting': [[1,'desc']],
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [6]},
				{'fnRender': renderCertificationActionCell, 'aTargets': [6]}
			],
			'sAjaxSource': '/admin/certifications/unverified_certifications',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				// Apply filters.
				_.each(certificationListFilters, function (element) {
					aoData.push(element);
				});
				$.getJSON( sSource, aoData, renderList(fnCallback, 'certification'));
			}
		});

		userscertifications_list_obj = $('#userCertification-list').dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': true,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 50,
			'aaSorting': [[1,'desc']],
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [8]},
				{'fnRender': renderUserCertificationActionCell, 'aTargets': [8]},
				{'fnRender': renderUserLicenseNumberCell, 'aTargets': [4]}
			],
			'sAjaxSource': '/admin/certifications/unverified_usercertifications',
			'fnServerData': function ( sSource, aoData, fnCallback ) {
				// Apply filters.
				_.each(userCertificationListFilter, function (element) {
					aoData.push(element);
				});
				$.getJSON( sSource, aoData, renderList(fnCallback, 'userCertification'));
			}
		});
	};
};
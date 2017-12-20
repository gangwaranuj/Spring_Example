import $ from 'jquery';
import 'datatables.net';
import getCSRFToken from '../funcs/getCSRFToken';

export default function (isArchive) {
	if (isArchive) {
		$('#data_list').dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			bFilter: false,
			iDisplayLength: 50,
			aaSorting: [[1, 'desc']],
			bProcessing: true,
			bServerSide: true,
			sAjaxSource: '/notifications/archive.json',
			fnServerData (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, (json) => {
					fnCallback(json);
				});
			}
		});
	} else {
		let meta;

		const table = $('#notifications_list').dataTable({
			sPaginationType: 'full_numbers',
			bLengthChange: false,
			bFilter: false,
			iDisplayLength: 50,
			aoColumnDefs: [
				{ bSortable: false, aTargets: [2] },
				{
					mRender: (data, type, val, metaData) => {
						return $('#link-archive-tmpl').tmpl({
							data,
							meta: meta[metaData.row]
						}).html();
					},
					aTargets: [2]
				},
				{
					mRender: (data, type, val, metaData) => {
						return meta[metaData.row].bulkUploadJS ? meta[metaData.row].bulkUploadJS : data;
					},
					aTargets: [0]
				}
			],
			aaSorting: [[1, 'desc']],
			bProcessing: true,
			bServerSide: true,
			sAjaxSource: '/notifications/active.json',
			fnServerData (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, (json) => {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});

		const $dropdown = $('#notifications_list');
		$dropdown.delegate('a.archive-action', 'click', function onClick (event) {
			event.preventDefault();
			$.ajax({
				url: $(this).attr('href'),
				type: 'post',
				dataType: 'json',
				beforeSend (jqXHR) {
					jqXHR.setRequestHeader('x-csrf-token', getCSRFToken());
				}
			}).success((data) => {
				if (data.successful) {
					table.fnDraw();
				}
			});
			return false;
		});


		$dropdown.delegate('.view-upload', 'click', (event) => {
			const postFormFactory = (function onClick () {
				let _html = ''; // eslint-disable-line no-underscore-dangle
				let _uri = ''; // eslint-disable-line no-underscore-dangle

				const ret = {
					init () {
						_html = `<input type="hidden" name="_tk" id="_tk" value="${getCSRFToken()}">`;
						return this;
					},
					uri (uri) {
						_uri = uri;
						return this;
					},
					add (name, vals) {
						const values = [].concat(vals);
						values.forEach((value) => {
							_html += `<input type="hidden" name="${name}" value="${value}">`;
						});
						return this;
					},
					build () {
						return $('<form>', {
							html: _html,
							action: _uri,
							method: 'POST'
						}).appendTo(document.body);
					}
				};

				ret.init();
				return ret;
			}());

			const form = postFormFactory
				.uri('/assignments/bulk_send')
				.add('ids', $(event.currentTarget).data('worknumbers'));
			form.build().submit();
		});

		$dropdown.delegate('.error-upload', 'click', (event) => {
			function redirectWithFlash (url, type, msg) {
				const e = $('<form></form>');
				e.attr({
					action: '/message/create',
					method: 'POST'
				});
				e.append(
					$('<input>').attr({
						name: 'message[]',
						value: msg
					}));
				e.append(
					$('<input>').attr({
						name: 'type',
						value: type
					}));
				e.append(
					$('<input>').attr({
						name: 'url',
						value: url
					}));
				e.append(
					$('<input>').attr({
						name: '_tk',
						value: getCSRFToken()
					}));
				$('body').append(e);
				e.submit();
			}

			const displayMessage = `<strong>The following errors were found with the source file and mapping:</strong>${$(event.currentTarget).data('message').replace(/Line /g, '<hr>Line ')}`;
			redirectWithFlash('/assignments/upload', 'error', displayMessage);
		});
	}
}

var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.concerns = wm.pages.admin.concerns || {};

wm.pages.admin.concerns = function (sAjaxSource) {
	var table, meta, templates, $dataList, $concernMessages, $resolveMessages;

	function cellRenderer(template) {
		return function (row) {
			return $(template).tmpl({
				data: row.aData[row.iDataColumn],
				meta: meta[row.iDataRow]
			}).html();
		};
	}

	function reOpen() {
		$.post($(this).attr('href'), function (data) {
			if (data.successful) {
				table.fnDraw();
			} else {
				displayMessages($concernMessages, data);
			}
		});

		return false;
	}

	function resolve() {
		$.colorbox({
			href: $(this).attr('href'),
			title: $(this).text(),
			innerWidth: 600,
			innerHeight: 200,
			onComplete: function () {
				$('#concern-resolve-form .submit').on('click', function () {
					$(this).closest('form').trigger('submit');
				});

				$('#concern-resolve-form').ajaxForm({
					dataType: 'json',
					success: function (data) {
						if (data.successful) {
							table.fnDraw();
							$.colorbox.close();
						} else {
							displayMessages($resolveMessages, data);
							$('#concern-resolve-form .submit').removeClass('disabled');
							$.colorbox.resize();
						}
					}
				});
			}
		});

		return false;
	}

	function displayMessages($element, data) {
		if (data.successful) {
			data = {
				data: [data.message]
			};
			$element.removeClass('error').addClass('success');
		} else {
			data = {
				data: data.errors
			};
			$element.removeClass('success').addClass('error');
		}
		$element.find('.content').html(_.template(templates.concernMessages.html())(data));
		$element.addClass('active');
	}

	function sendMessage() {
		$.colorbox({
			href: this.href,
			title: $(this).attr('title'),
			innerWidth: 500,
			onComplete: function () {
				$('#message-form').ajaxForm({
					dataType: 'json',
					success: function (data) {
						displayMessages($concernMessages, data);
						$.colorbox.close();
					}
				});
				$('#message-form .cancel').on('click', function () {
					$.colorbox.close();
				});
			}
		});

		return false;
	}

	return function () {
		$dataList = $('#data_list');
		$concernMessages = $('.concern-messages');
		$resolveMessages = $('#concern_resolve_messages');

		templates = {
			concernMessages: $('#concern-messages-tmpl')
		};

		table = $dataList.dataTable({
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bSort': true,
			'iDisplayLength': 50,
			'aoColumnDefs': [
				{'bSortable': false, 'aTargets': [3]},
				{'fnRender': cellRenderer('#cell-reporter-tmpl'), 'aTargets': [1]},
				{'fnRender': cellRenderer('#cell-action-tmpl'), 'aTargets': [3]}
			],
			"aaSorting": [[ 0, "desc" ]],
			'bProcessing': true,
			'bServerSide': true,
			'sAjaxSource': sAjaxSource,
			'fnServerData': function (sSource, aoData, fnCallback) {
				_.each($('#data_filter_form').serializeArray(), function (element) {
					aoData.push(element);
				});

				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json)
				});
			}
		});

		$('#data_filter_form input, #data_filter_form select').on('change', function () {
			table.fnDraw();
		});

		$dataList.on('click', '.reopen-action', reOpen);
		$dataList.on('click', '.resolve-action', resolve);
		$dataList.on('click', '.send-message-action', sendMessage);
	};
};

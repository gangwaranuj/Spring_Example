var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.manage = wm.pages.admin.manage || {};
wm.pages.admin.manage.company = wm.pages.admin.manage.company || {};

wm.pages.admin.manage.company.overview = function (companyId) {
	'use strict';

	return function () {
		$('#edit_company_info').click(function () {
			$('#edit_company_info_container').show();
		});

		// Tag user.
		$('#add_comment_to_company_link').colorbox({
			inline: true,
			href: '#add_company_comment_popup',
			title: 'Add your comment below',
			transition: 'none',
			innerHeight: 220,
			innerWidth: 500
		});

		$('#add_comment_to_company_form').ajaxForm({
			dataType: 'json',
			success: function (responseText) {
				if (responseText.successful) {
					location.reload();
				} else {
					$('#add_comment_to_company_form a.disabled').removeClass('disabled');
					var tmpStr = responseText.errors.reduce(function (memo, value) {
						return memo + '<li>' + value + '</li>';
					}, '<ul>') + '</ul>';
					$('#add_comment_to_company_form .message div').html(tmpStr);
					$('#add_comment_to_company_form .message').show();
				}
			}
		});

		// Get any comments this user has.
		$.ajax({
			url: '/admin/manage/company/get_company_comments',
			global: false,
			type: 'GET',
			data: { id: companyId },
			dataType: 'json',
			success: function (data) {
				if (data.successful) {
					buildCompanyComments(data.results);
				}
			}
		});


		// Get any attachments this company has.

		$.getJSON('/admin/manage/company/get_company_attachments/' + companyId, function (data) {
			if (data.successful) {
				buildCompanyAttachments(data.results);

				$('a[name=delete_company_attachment]').on('click', function () {
					deleteCompanyAttachment(this);
				});
			}
		});


		// AP Limit
		$('#edit_ap_limit').colorbox({
			inline: true,
			href: '#edit_ap_limit_popup',
			title: 'Modify AP Limit',
			transition: 'none',
			innerHeight: 250,
			innerWidth: 500
		});

		$('#edit_ap_limit_form').ajaxForm({
			dataType: 'json',
			success: function (responseText) {
				if (responseText.successful) {
					location.reload();
				} else {
					$('#edit_ap_limit_form a.disabled').removeClass('disabled');
					var tmpStr = responseText.errors.reduce(function (memo, value) {
						return memo + '<li>' + value + '</li>';
					}, '<ul>') + '</ul>';
					$('#edit_ap_limit_form .message div').html(tmpStr);
					$('#edit_ap_limit_form .message').show();
				}
			}
		});
	};

	/**
	 * Build out user comments
	 */
	function buildCompanyComments(data) {
		if (data.length > 0) {
			var container = $('#add_company_comment_table tbody');
			if (container) {
				container.empty();
				for (var i in data) {
					$('#add_company_comment_tmpl').tmpl({
						name: data[i].name,
						id: data[i].id,
						comment: data[i].comment,
						date: data[i].date,
						user_id: data[i].user_id
					}).appendTo(container);
				}
			}
		}
	}

	$('#add_company_comment_table').on('click', '.delete_company_comment', function () {
		deleteCompanyComment(this);
	});

	function deleteCompanyComment(el) {
		var str = $(el).attr('id');
		var strArray = str.split('_', 2);
		var commentId = strArray.pop();

		if (confirm('Are you sure you want to delete this comment?')) {
			$.ajax({
				url: '/admin/manage/company/delete_comment_of_company',
				dataType: 'json',
				global: true,
				data: { id: commentId },
				type: 'POST',
				success: function (data) {
					if (data.successful) {
						$(el).parent().parent().hide();
						$('.alert div').html(data.messages[0]);
						$('.alert').show().removeClass('alert-error').addClass('alert-success');
					} else {
						$('.alert div').html('An error occurred when deleting the comment.');
						$('.alert').show().removeClass('alert-success').addClass('alert-error');

					}
				}
			});
		} else {
			return false;
		}
	}


	/**
	 * Build out company attachments
	 */
	function buildCompanyAttachments(data) {
		if (data.length > 0) {
			var container = $('#add_company_attachment_table tbody');
			if (container) {
				container.empty();
				for (var i in data) {
					$('#add_company_attachment_tmpl').tmpl({
						id: data[i].id,
						uri: data[i].uri,
						name: data[i].name,
						description: data[i].description,
						date: data[i].date
					}).appendTo(container);
				}
			}
		}
	}


	/**
	 * Deletes an attachment
	 */
	function deleteCompanyAttachment(el) {
		var str = $(el).attr('id'),
			strArray = str.split('_', 2),
			assetId = strArray.pop();

		if (confirm('Are you sure you want to delete this attachment?')) {
			$.ajax({
				url: '/admin/manage/company/delete_attachment_of_company/' + companyId,
				type: 'POST',
				data: JSON.stringify({ id: assetId }),
				dataType: 'json',
				contentType: 'application/json'
			})
			.done(function (response) {
				wm.funcs.notify({
					message: response.message
				});
			})
			.fail(function (response) {
				wm.funcs.notify({
					message: response.message,
					type: 'danger'
				});
			});
		} else {
			return false;
		}
	}
};

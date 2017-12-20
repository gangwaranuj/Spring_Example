var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.forums = wm.pages.admin.forums || {};

wm.pages.admin.forums.flaggedPosts = function () {
	return function () {

		var actionTemplate = _.template($('#tmpl-actions').html());

		var meta, datatable_obj = $('#flaggedPostsTable').dataTable({
			'aaSorting': [[3, 'asc']],
			'sPaginationType': 'full_numbers',
			'bLengthChange': true,
			'bFilter': false,
			'bStateSave': false,
			'bProcessing': true,
			'bServerSide': true,
			'iDisplayLength': 50,
			'bSort': true,
			'sAjaxSource': '/admin/forums/flagged_posts',
			'aoColumnDefs': [
				{
					'fnRender': renderPostLink,
					'bSortable': false,
					'aTargets': [0],
					'sClass': 'forums-admin-flagged-column'
				},
				{
					'fnRender': renderActions,
					'bSortable': false,
					'aTargets': [4]
				},
				{
					'bSortable': false,
					'aTargets': [1]
				}
			],
			'fnServerData': function (sSource, aoData, fnCallback) {
				$.getJSON(sSource, aoData, function (json) {
					meta = json.aMeta;
					fnCallback(json);
				});
			}
		});

		function renderPostLink(row) {
			if (meta[row.iDataRow].rootId == null) {
				return '<a href="/forums/post/' + meta[row.iDataRow].postId  + '">' + row.aData[row.iDataColumn] + '</a>';
			}
			else {
				return '<a href="/forums/post/' + meta[row.iDataRow].rootId  + "#commentBox" + meta[row.iDataRow].postId + '">' + row.aData[row.iDataColumn] + '</a>';
			}

		}

		function renderActions(row) {
			var postId = meta[row.iDataRow].postId;
			var creatorId = row.aData[row.iDataColumn];
			return actionTemplate({
				postId: postId,
				creatorId: creatorId
			});
		}

		datatable_obj.fnDraw();

		$('#flaggedPostsTable').on('click', '.admin-forums-review', function(e) {
			e.preventDefault();
			var postId = $(this).data('post-id');
			var sure = confirm('Are you sure you want to unflag this post?');
			if (!sure) {
				return;
			}
			$.ajax({
				url: '/admin/forums/unflag/' + postId,
				type: 'POST',
				dataType: 'json',
				success: function (response) {
					datatable_obj.fnDraw();
				}
			});

		});

		$('#flaggedPostsTable').on('click', '.admin-forums-ban', function(e) {
			e.preventDefault();
			var userId = $(this).data('user-id');
			var sure = confirm('Are you sure you want to ban this user?');
			if (!sure) {
				return;
			}
			var reason = prompt('Enter the reason for the ban:');
			$.ajax({
				url: "/admin/forums/ban/" + userId,
				type: "POST",
				data: {
					reason: reason
				},
				dataType: "json",
				success: function (response) {
					datatable_obj.fnDraw();
				}
			});
		});

		$('#flaggedPostsTable').on('click', '.admin-forums-delete', function(e) {
			e.preventDefault();
			var postId = $(this).data('post-id');
			var sure = confirm('Are you sure you want to delete this post?');
			if (!sure) {
				return;
			}
			$.ajax({
				url: "/admin/forums/delete/" + postId,
				type: "POST",
				dataType: "json",
				success: function (response) {
					datatable_obj.fnDraw();
				}
			});
		});

	};
};

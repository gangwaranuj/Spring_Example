var wm = wm || {};
wm.pages = wm.pages || {};
wm.pages.admin = wm.pages.admin || {};
wm.pages.admin.buildinfo = wm.pages.admin.buildinfo || {};

wm.pages.admin.buildinfo.index = function () {
	'use strict';

	return function () {
		function getSortedKeys(jsonMigrationInfo) {
			var keys = Object.keys(jsonMigrationInfo);
			keys.sort(function(x, y) {
				return y-x;
			});
			return keys;
		}

		$.ajax({
			url: '/admin/buildinfo/migrationinfo',
			type: 'GET',
			dataType: 'json',
			success: function(jsonData){
				$("#migrations_ajax_loader").hide();
				var jsonMigrationInfo = jsonData.data;
				var migrationInfos = [];

				if (jsonData.successful == true && jsonMigrationInfo) {
					var sortedKeys = getSortedKeys(jsonMigrationInfo);

					$.each(sortedKeys, function (ix, key) {
						migrationInfos.push(jsonMigrationInfo[key]);
					});

					$('#numberOfMigrations').html(sortedKeys.length);
					var migrationTemplate = _.template($('#migrationInfoTemplate').html());
					$('#latest_migrations').html(migrationTemplate({migrationInfos: migrationInfos}));
				} else {
					$('#latest_migrations').html('Oops ... Could not retrieve database migrations data.');
				}
			},
			error: function () {
				$('#latest_migrations').html('Request Failed!');
			}
		});
	}
};

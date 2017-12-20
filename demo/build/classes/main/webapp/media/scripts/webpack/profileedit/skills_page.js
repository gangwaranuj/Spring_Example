'use strict';

import $ from 'jquery';
import wmNotify from '../funcs/wmNotify';
import 'jquery-ui';
import Template from './templates/browse_list_empty.hbs'
import '../dependencies/jquery.safeEnter';
import '../dependencies/jquery.tmpl';

export default (dataObj, links) => {
	let data = dataObj || {
			'skills': [],
			'description': null
		};

	const linkObj = Object.assign({}, {
		saveLink: '/profile-edit/save_skills',
		browseLink: '/profile-edit/browse_skills',
		suggestLink: '/v2/suggest/skill'
	}, links);

	const DEFAULT_SKILL_LEVEL = 50;
	let current_autocomplete_skill_selection = null;

	const skillExists = function (skill) {
		var exists = $.grep(data.skills, function (value) {
			if (skill.id == '') {
				return value.name == skill.name;
			} else {
				return value.id == skill.id;
			}
		});
		return (exists.length > 0);
	};

	const actionContinueSave = function () {

		$.ajax({
			url: linkObj.saveLink,
			data: JSON.stringify(data),
			dataType: 'json',
			type: 'POST',
			contentType: "application/json; charset=utf-8",
			success: function (response) {
				if (response.successful) {
					wmNotify({ message: response.messages[0] });
				} else {
					wmNotify({
						message: response.messages[0],
						type: 'danger'
					});
				}
			}}
		);
		return false;
	};

	const actionAddSkillAutocomplete = function () {
		if ($('#outlet-skill-autocomplete-field').val()) {
			if (current_autocomplete_skill_selection && current_autocomplete_skill_selection.label == $('#outlet-skill-autocomplete-field').val()) {
				addSkill({
					'id': current_autocomplete_skill_selection.id,
					'name': current_autocomplete_skill_selection.label,
					'level': DEFAULT_SKILL_LEVEL
				});

				// Disable in the browse list
				$('#browse-list li').each(function (i, item) {
					if ($(item).data('skill').id == current_autocomplete_skill_selection.id)
						$('a', item).addClass('disabled');
				});
			} else {
				addSkill({
					'id': '',
					'name': $('#outlet-skill-autocomplete-field').val(),
					'level': DEFAULT_SKILL_LEVEL
				});
			}
			$('#outlet-skill-autocomplete-field').val('');
			$('#outlet-skill-autocomplete-selected')
				.text('')
				.hide();
		}
		return false;
	};

	const actionSkillIndustrySelect = function () {
		return function () {
			const industry_id = $('#outlet-skill-industry-select').val();
			$('#outlet-skill-autocomplete-field').autocomplete({
				source: function (request, response) {
					$.getJSON(linkObj.suggestLink, {
						q: request.term,
						industry_id: industry_id
					}, function (data) {
						data = data.results.map(function (obj, index) {
							return {
								id: obj.id,
								uuid: obj.uuid,
								value: obj.name,
								label: obj.name,
								name: obj.name
							}
						});

						let matcher = new RegExp( `^${$.ui.autocomplete.escapeRegex(request.term)}`, 'i' );
						response($.grep(data, function(title) { return matcher.test(title.value); }) );
					});
				}
			});

			$.getJSON(linkObj.browseLink, {'industry_id': industry_id}, function (json, textStatus) {
				$('#browse-list').empty();

				if (json.skills.length == 0) {
					$('#browse-list').append(Template());
					return;
				}

				if (json.skills.length < 25) {
					$('#browse-list').append(Template());
				}

				$.each(json.skills, function (index, value) {
					$('#template-browse-list-item').tmpl({'skill': value, 'exists': skillExists(value)})
						.data('skill', value)
						.appendTo('#browse-list');
				});
			});
		}
	};

	const init = function () {
		$('#outlet-continue-step3').click(actionContinueSave);

		$('#skills_form').bind('submit', function () {
			return false;
		});

		// Skill widgets
		$('#skill-list').delegate('.outlet-skill-list-item-remove', 'click', function () {
			var item = $(this).closest('li');
			var skill = item.data('skill');
			item.remove();

			data.skills = $.grep(data.skills, function (value) {
				return value.id != skill.id;
			});

			// Re-enable if in the browse list
			$('#browse-list li').each(function (i, item) {
				if ($(item).data('skill').id == skill.id)
					$('a', item).removeClass('disabled');
			});

			return false;
		});
		
		// Prevents commas in textfield
		$("#outlet-skill-autocomplete-field").keypress(function(e) {
			if (String.fromCharCode(e.which).match(/,/)) e.preventDefault();
		});

		// Type-ahead skills autocomplete
		$('#outlet-skill-autocomplete-button').click(actionAddSkillAutocomplete);
		$('#outlet-skill-autocomplete-field').autocomplete({
			minLength: 1,
			source: function (request, response) {
				$.getJSON(linkObj.suggestLink, {
					q: request.term
				}, function (data) {
					data = data.results.map(function (obj, index) {
						return {
							id: obj.id,
							uuid: obj.uuid,
							value: obj.name,
							label: obj.name,
							name: obj.name
						}
					});

					let matcher = new RegExp( `^${$.ui.autocomplete.escapeRegex(request.term)}`, 'i' );
					response($.grep(data, function(title) { return matcher.test(title.value); }) );
				});
			},
			focus: function (event, ui) {
				$('#outlet-skill-autocomplete-field').val(ui.item.value);
				current_autocomplete_skill_selection = ui.item;
				return false;
			},
			select: function (event, ui) {
				addSkill({
					'id': ui.item.id,
					'name': ui.item.value,
					'level': DEFAULT_SKILL_LEVEL
				});

				// Disable in the browse list
				$('#browse-list li').each(function (i, item) {
					if ($(item).data('skill').id == ui.item.id)
						$('a', item).addClass('disabled');
				});

				$('#outlet-skill-autocomplete-field').val('');
				$('#outlet-skill-autocomplete-selected')
					.text('')
					.hide();

				return false;
			},
			search: function (event, ui) {
			}
		});
		$('#outlet-skill-autocomplete-field').listenForEnter().bind('pressedEnter', actionAddSkillAutocomplete);

		// Browse skills
		$('#outlet-skill-industry-select').change(actionSkillIndustrySelect());
		if ($('#outlet-skill-industry-select').val()) {
			actionSkillIndustrySelect()();
		}

		$('#browse-list').delegate('.outlet-browse-list-item', 'click', function () {
			if ($(this).is('.disabled')) return;

			$(this).addClass('disabled');

			var item = $(this).parent();
			var skill = item.data('skill');
			addSkill(skill);
			return false;
		});

		$('#skill-list').empty();
		$.each(data.skills, function (index, value) {
			$('#template-skill-list-item').tmpl({'skill': value})
				.data('skill', value)
				.appendTo('#skill-list');
		});
	};

	const addSkill = function (skill) {
		if (skillExists(skill)) return;

		data.skills.push(skill);
		$('#template-skill-list-item').tmpl({'skill': skill})
			.data('skill', skill)
			.appendTo('#skill-list');
	};

	init();
}


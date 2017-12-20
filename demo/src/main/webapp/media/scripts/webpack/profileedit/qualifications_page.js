'use strict';

import $ from 'jquery';
import 'jquery-ui';


export default (dataObj) => {
	let data = dataObj || {
			qualifications: [],
			skills: {},
			jobTitle: '',
			industry: '1060' //general
		};

	let recommendedSkills = [];

	const initRecommenders = function () {
		$.each(data.qualifications, function(index, skill) {
			addSkill(skill.id, skill.name, false, skill.type, true);
		});

		setJobTitle(data.jobTitle);
		displaySkills();
		skillsTypeahead();
		titlesTypeahead();
		$('#save-qualifications').click(function () {
			save();
		});
	};

	const skillsTypeahead = function () {
		$('.skills-autocomplete').autocomplete({
			minLength: 2,
			autoFocus: true,
			source: function (request, response) {
				$.getJSON('/v2/suggest/skill', {
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
			response: function( event, ui ) {
				//add the user's term first
				ui.content.unshift({
					id: null,
					uuid: null,
					label: event.target.value,
					value: event.target.value,
					name: event.target.value
				});
			},
			select: function (event, ui) {
				event.stopImmediatePropagation();
				addSkill(ui.item.id, ui.item.value, false, 'SKILL');
				$('.skills-autocomplete').val('');
				return false;
			},
		}).keydown(function(event){
			if(event.keyCode == 13) { //they pressed enter after adding custom skill
				event.stopImmediatePropagation();
				let element = $('.skills-autocomplete');
				addSkill(null, element.val(), false, 'SKILL');
				element.val('');
				return false;
			}
		});

		updateRecommendedSkills(true);
	};

	const titlesTypeahead = function () {
		$('#job-title-autocomplete').autocomplete({
			minLength: 2,
			source: function (request, response) {
				$.getJSON('/v2/suggest/jobTitle', {
					q: request.term
				}, function (data) {
					data = data.results.map(function (obj, index) {
						return {
							value: obj.name,
							label: obj.value
						}
					});

					let matcher = new RegExp( `^${$.ui.autocomplete.escapeRegex(request.term)}`, 'i' );
					response($.grep(data, function(title) { return matcher.test(title.value); }) );
				});
			},
			change: function( event, ui ) {
				setJobTitle(event.target.value);
			},
			select: function (event, ui) {
				$('#job-title-autocomplete').val(ui.item.value);
				setJobTitle(ui.item.value);
				return false;
			},
		});
	};

	const setJobTitle = function (title) {
		$('#job-title-autocomplete').val(title);
		data.jobTitle = title;
		updateRecommendedSkills(true);
		toggleError();
		$('#job-title-autocomplete').removeClass('fieldError');
	};

	const addSkill = function (id, name, recommended, type, silent) {
		name = $.trim(name);
		if (name !== '' && ((id === null && data.skills[name] === undefined) || data.skills[id] === undefined)) {
			const skill = {
				id: id,
				name: name,
				recommended: recommended,
				type: type
			};

			data.skills[id === null ? name : id] = skill;
			if (!silent) {
				displaySkills();
				updateRecommendedSkills(true);
			} else {
				updateRecommendedSkills();
			}
		}
	};

	const displaySkills = () => {
		let html = '';
		Object.keys(data.skills).forEach((key) => {
			const {
				recommended,
				name,
				id,
				type
			} = data.skills[key];
			html += `<li class="skill" recommended=${recommended} type=${type} skill-id=${id}>${name}</li>`;
		});

		$('.skills-list').html(html);
		$('.your-skills li').click((e) => {
			const skill = $(e.currentTarget);
			removeSkill(skill.attr('skill-id'), skill.text());
		});
	};

	const removeSkill = function (id, name) {
		let key = id == 'null' ? name : id; // jquery text returns string null if null
		let skill = data.skills[key];
		delete data.skills[key];

		if (skill.recommended) {
			recommendedSkills.push(skill);
			displayRecommendedSkills();
		}

		displaySkills();
	};

	const displayRecommendedSkills = () => {
		let html = '';
		$.each(recommendedSkills.slice(0, 10), (index, obj) => {
			if (data.skills[obj.id] === undefined && data.skills[obj.name] === undefined) {
				html += `<li class='skill' recommended=true type='${obj.type}' skill-id=${obj.id}>${obj.name}</li>`;
			}
		});

		$('.skills').html(html);
		$('.skills li').click((e) => {
			const skill = $(e.currentTarget);
			addSkill(skill.attr('skill-id'), skill.text(), true, skill.attr('type'));
		});
	};

	const updateRecommendedSkills = (force) => {
		if (force || recommendedSkills.length === 0) {
			const requestData = {
				jobTitle: data.jobTitle,
				offset: 0,
				limit: 10,
				industries: [data.industry],
				selectedSkills: Object.keys(data.skills).map(key => data.skills[key].name),
				definedSkills: [],
				removedSkills: []
			};

			$.ajax({
				url: '/v2/recommend/skill',
				type: 'POST',
				dataType: 'json',
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(requestData)
			}).done((response) => {
				recommendedSkills = response.results;
				displayRecommendedSkills();
			});
		}
	};

	const isValid = () => {
		return $.trim(data.jobTitle) !== '';
	};


	const toggleError = (error) => {
		if (error) {
			$('#qualification-errors').html(error).show();
		} else {
			$('#qualification-errors').hide();
		}
	};

	const save = () => {
		if (isValid()) {
			$('#job-title-autocomplete').removeClass('fieldError');
			toggleError();

			const requestData = {
				jobTitle: data.jobTitle,
				qualifications: Object.keys(data.skills).map(key => data.skills[key])
			};

			$.ajax({
				url: '/profile-edit/qualifications',
				type: 'POST',
				dataType: 'json',
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(requestData)
			}).done((response) => {
				if ($.trim(response) !== 'true') {
					toggleError('Error Saving Your Details');
				} else {
					$('#qualification-success').fadeIn().delay(5000).fadeOut();
				}
			});
		} else {
			$('#job-title-autocomplete').addClass('fieldError');
			toggleError('You must add a job title');
		}
	};
	initRecommenders();
};


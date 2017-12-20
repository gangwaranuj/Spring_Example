'use strict';

import Template from './templates/industries.hbs';
import _ from 'underscore';
import OnboardSlideView from './onboard_slide_view';
import wmFloatLabel from '../funcs/wmFloatLabel';
import $ from 'jquery';
import 'jquery-ui';
import wmSelect from '../funcs/wmSelect';

export default OnboardSlideView.extend({
	el: '#industriesView .wm-modal--content',
	template: Template,
	events: _.defaults({
		'change .other-industry-value': 'setOtherName',
		'change .industries': 'setData',
		'click .skills li': 'addSkill',
		'click .your-skills li': 'removeSkill'
	}, OnboardSlideView.prototype.events),

	initialize: function () {
		this.listenTo(this.model, 'request', this.showSpinner);
		this.listenTo(this.model, 'sync error', this.hideSpinner);
		this.model.fetch({ success: _.bind(this.render, this) });
		this.skillMap = { };
		this.jobTitleMap = { };
	},

	render: function () {
		let skillsNeeded = this.model.skillsRequirement.minSkills - this.model.skills.models.length;
		this.$el.html(this.template({
			industries: this.model.industries.models,
			skills: this.model.skills.models,
			recommendedSkills: this.model.recommendedSkills.first(this.model.skillsRequirement.maxRecommendations),
			jobTitle: this.model.jobTitle,
			skillsLeft: this.model.skillsRequirement.maxSkills - this.model.skills.models.length,
			skillsNeeded: skillsNeeded > 0 ? skillsNeeded : 0
		}));
		// Trigger all the floating label animations for populated fields
		wmFloatLabel({ root: this.el });
		this.initSkillsAutoComplete();
		this.initTitlesAutoComplete();
		this.trigger('render');
		this.toggleSkillsView();
		return this;
	},

	setData: function (event) {
		event.stopImmediatePropagation();

		var element = event.target;
		if (element.name === 'industries') {
			var industryId = parseInt(element.value, 0);

			this.model.industries.each(function (model) {
				model.set('checked', model.get('id') === industryId);
			});

			this.toggleSkillsView();
		}
	},

	setOtherName: function (event) {
		event.stopImmediatePropagation();
		var element = event.target,
			id = parseInt(element.previousSibling.previousSibling.id, 10),
			model = this.model.industries.find(function (option) {
				return option.get('id') === id;
			});

		model.set('otherName', element.value);
	},

	setJobTitle: function (title) {
		this.model.jobTitle = title;
		this.updateRecommendedSkills(true);
		this.toggleSkillsView();
	},

	toggleSkillsView: function () {
		let skillsView = $('.skills-search');
		if (this.model.industries.where({'checked': true}).length > 0
			&& this.model.jobTitle && !_.isEmpty(this.model.jobTitle.name)) {
			skillsView.show();
		} else {
			skillsView.hide();
		}
	},

	updateRecommendedSkills: function (force) {
		if (force || this.model.recommendedSkills.length === 0) {
			var requestData = {
				jobTitle: this.model.jobTitle && this.model.jobTitle.name ? this.model.jobTitle.name : '',
				offset: this.model.skillsRequirement.offset,
				limit: this.model.skillsRequirement.maxRecommendations,
				industries: this.model.industries.where({'checked': true})
					.map(function (model) {
						return model.get('id');
					}),
				removedSkills: this.model.removedSkills
					.map(function (model) {
						return model.get('name');
					}),
				selectedSkills: this.model.skills
					.filter(function (model) {
						return model.get('id') > 0;
					})
					.map(function (model) {
						return model.get('name');
					}),
				definedSkills: this.model.skills
					.filter(function (model) {
						return model.get('id') == null;
					})
					.map(function (model) {
						return model.get('name');
					})
			};
			$.ajax({
				url: '/v2/recommend/skill',
				type: 'POST',
				dataType: 'json',
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(requestData)
			}).done((data) => {
				this.model.clearRecommendedSkills();
				$.each(data.results, (index, obj) => {
					this.model.addRecommendedSkill(obj.id, obj.name, obj.type);
				});
				this.render();
			});
		}
	},

	addSkill: function (e) {
		if (this.model.skills.models.length < this.model.skillsRequirement.maxSkills) {
			let skill = $(e.currentTarget);
			this.model.addSkill(skill.attr('skill-id'), skill.text(), skill.attr('type'));
			this.render();
			this.updateRecommendedSkills();
		}
	},

	removeSkill: function (e) {
		let skill = $(e.currentTarget);
		this.model.removeSkill(null, skill.attr('skill-id'), skill.attr('type'), skill.text());
		this.render();
		this.updateRecommendedSkills();
	},

	initSkillsAutoComplete: function () {
		wmSelect({ selector: '.skills-autocomplete' }, {
			valueField: 'value',
			labelField: 'value',
			searchField: 'value',
			options: [],
			hideSelected: false,
			persist: false,
			delimiter: ',',
			loadThrottle: 200,
			allowEmptyOption: false,
			closeAfterSelect: true,
			create: (input) => ({id: null, value: input}),
			createOnBlur: true,
			onChange: (value) => {
				if (value && value.length > 0 &&
					this.model.skills.models.length < this.model.skillsRequirement.maxSkills) {
					var id = (typeof this.skillMap[value] === 'undefined') ? 0 : this.skillMap[value];
					this.model.addSkill(id, value);
					this.updateRecommendedSkills(true);
					this.render();
				}
				var thisSelectize = $('.skills-autocomplete')[0].selectize;
				thisSelectize.clear();
				thisSelectize.clearOptions();
				thisSelectize.renderCache = {};
				thisSelectize.loadedSearches = {};
			},
			load: (query, callback) => {
				if (query.length < 2) {
					return callback();
				}
				$.ajax({
					url: '/v2/suggest/skill',
					type: 'GET',
					dataType: 'json',
					data: {q: query},
					error: () => {
						this.skillMap = { };
						callback();
					},
					success: (res) => {
						$.each(res.results, function() {
							this.value = this.name;
						});
						this.skillMap = { };
						res.results.forEach((entry) => {
							this.skillMap[entry.value] = entry.id;
						});
						callback(res.results);
					}
				});
			}
		})[0].selectize;
	},

	initTitlesAutoComplete: function () {
		wmSelect({ selector: '.jobtitle-autocomplete' }, {
			valueField: 'name',
			labelField: 'name',
			searchField: 'name',
			options: [],
			hideSelected: false,
			persist: true,
			maxItems: 1,
			closeAfterSelect: true,
			loadThrottle: 200,
			allowEmptyOption: false,
			create: (value) => ({uuid: null, name: value}),
			createOnBlur: true,
			onChange: (value) => {
				var item = {uuid: this.jobTitleMap[value], name: value};
				this.setJobTitle(item);
				return item;
			},
			load: (query, callback) => {
				if (query.length < 2) {
					return callback();
				}
				$.ajax({
					url: '/v2/suggest/jobTitle',
					type: 'GET',
					dataType: 'json',
					data: {q: query},
					error: () => {
						this.jobTitleMap = { };
						callback();
					},
					success: (res) => {
						this.jobTitleMap = { };
						res.results.forEach((entry) => {
							this.jobTitleMap[entry.name] = entry.uuid;
						});
						callback(res.results);
					}
				});
			}
		})[0].selectize;
	}
});

'use strict';

import _ from 'underscore';
import BackBone from 'backbone';
import OnboardModel from './onboard_model';
import IndustryCollection from './industry_collection';
import SkillCollection from './skill_collection';
import SkillModel from './skill_model';
export default OnboardModel.extend({
	step: 3,
	externalFields: ['industries', 'skills', 'jobTitle'],

	fields: function () {
		return this.externalFields;
	},

	initialize: function (attrs) {
		this.industries = new IndustryCollection();
		this.jobTitle = {uuid: null, name: null};
		this.skills = new SkillCollection();
		this.recommendedSkills = new SkillCollection();
		this.removedSkills = new SkillCollection();
		this.id = this.industries.id = attrs.id;
		this.skillsRequirement = {
			maxSkills: 10,
			minSkills: 3,
			offset: 0,
			maxRecommendations: 10
		}
	},

	parse: function (response) {
		this.industries.reset(response.industries);
		delete response.industries;

		this.skills.reset(response.skills);
		delete response.skills;

		this.jobTitle = response.jobTitle;
		delete response.jobTitle;

		return _.omit(response, _.isNull);
	},

	validate: function () {
		var errors = [];
		if (!this.industries.isValid()) {
			errors.push({
				name: 'industries',
				message: 'At least one industry must be chosen.'
			});
		}

		if (this.skills.length < this.skillsRequirement.minSkills) {
			errors.push({
				name: 'skills',
				message: 'At least one skill must be chosen.'
			});
		}

		if (errors.length > 0) {
			return errors;
		}
	},
	addSkill: function (skillId, skillName, type) {
		// skillId could be 0, so we can't use "if (skillId) {...}"
		// on the other hand, it may never be "undefined", but instead "null"
		if (skillId !== undefined) {
			let skill = this.addSkillToCollection(this.skills, skillId, skillName, skillId !== 0, type);
			this.removeRecommendedSkill(skill);
		}
	},
	addRecommendedSkill: function (skillId, skillName, type) {
		if (skillId !== undefined) {
			let skill = this.skills.find(function (option) {
				return option.get('id') === skillId && option.get('type') === type;
			});

			skill = skill || this.removedSkills.find(function (option) {
				return option.get('id') === skillId && option.get('type') === type;
			});

			if (!skill) { //not already in user skills and not in removed skills
				this.addSkillToCollection(this.recommendedSkills, skillId, skillName, true, type);
			}
		}
	},
	addSkillToCollection: function(collection, skillId, skillName, recommended, type) {
		let skill = collection.find(function (option) {
			return option.get('id') === skillId && option.get('type') === type;
		});

		if (!skill || skillId === 0) {
			skill = new SkillModel({
				id: skillId === 0 ? null : skillId,
				name: skillName,
				recommended: recommended,
				type: type
			});

			collection.add(skill);
		}

		return skill;
	},
	removeSkillFromCollection: function (collection, skill, skillId, type, name) {
		let isNameSearch = _.isEmpty(skillId);
		skill = skill || collection.find(function (option) {
				return isNameSearch ? option.get('name') === name : option.get('id') === skillId;
			})
		collection.remove(skill);
		return skill;
	},
	removeSkill: function (skill, skillId, type, name) {
		skill = this.removeSkillFromCollection(this.skills, skill, skillId, type, name);
		if (skill.get('recommended') === true) {
			this.addSkillToCollection(this.removedSkills, skill.get('id'), skill.get('name'), true, type);
		}
	},
	removeRecommendedSkill: function (skill, skillId, type) {
		this.removeSkillFromCollection(this.recommendedSkills, skill, skillId, type);
	},
	clearRecommendedSkills: function () {
		this.recommendedSkills.reset();
	},
	update: function () {
		var externalObjects = _.map(this.externalFields, function (field) { return this[field]; }, this),
		externalJSON = _.map(externalObjects, function (object) {
			return object instanceof BackBone.Model ? object.toJSON() : object;
		}),
		attributes = _.defaults(_.object(this.externalFields, externalJSON), this.toJSON());

		if (this.isValid()) {
			return this.save(attributes);
		} else {
			return $.Deferred().reject();
		}
	}
});

import moment from 'moment';
import React from 'react';
import { WMMenuItem } from '@workmarket/front-end-components';

export const daysOfTheWeek = [
	'Sunday',
	'Monday',
	'Tuesday',
	'Wednesday',
	'Thursday',
	'Friday',
	'Saturday'
];

const formatFrequencyModifierString = (frequency, frequencyModifier) => {
	switch (frequency) {
	case 'Daily':
		if (frequencyModifier === '1') return 'daily';
		if (frequencyModifier === '2') return 'every other day';
		return `every ${frequencyModifier} days`;
	case 'Weekly':
		if (frequencyModifier === '1') return 'every week';
		if (frequencyModifier === '2') return 'every other week';
		return `every ${frequencyModifier} weeks`;
	case 'Monthly':
		if (frequencyModifier === '1') return 'every month';
		if (frequencyModifier === '2') return 'every other month';
		return `every ${frequencyModifier} months`;
	default:
		return 'every unit';
	}
};

export const disabledRangeObj = {
	valid: false,
	reason: 'We do not currently support recurring assignments that have arrival windows.'
};

export const formatDailyString = (startDate, endDate, frequencyModifier) => {
	return `This assignment will occur ${formatFrequencyModifierString('Daily', frequencyModifier)} from ${startDate.format('MMMM Do YYYY')} to ${endDate.format('MMMM Do YYYY')}`;
};

// Takes string from the schedule branch of the redux store and modifies it for moment.
export const adjustStartDate = (startDate) => {
  if (startDate == null) { // eslint-disable-line
	return moment().set('hour', 12);
}
	return moment(new Date(`${startDate.slice(0, 10)}`)).set('hour', 12);
};

export const formatWeekString = (
  days,
  endType,
  endDate,
  frequency,
  frequencyModifier,
  ) => {
	const dateToUse = endDate;
	let weekString = daysOfTheWeek.filter((day, i) => days[i] === true);
	if (weekString.length === 0) return { valid: false, reason: 'Weeks cannot recur without any selected days' };
	if (weekString.length === 1) {
		return {
			valid: true,
			reason: `This assignment will occur ${formatFrequencyModifierString(frequency, frequencyModifier)} on ${weekString[0]} until ${dateToUse.format('MMMM Do YYYY')}.`
		};
	}
	if (weekString.length === 2) {
		return {
			valid: true,
			reason: `This assignment will occur ${formatFrequencyModifierString(frequency, frequencyModifier)} on ${weekString[0]} and ${weekString[1]} until ${dateToUse.format('MMMM Do YYYY')}.`
		};
	}
	weekString[weekString.length - 1] = `and ${weekString[weekString.length - 1]}`;
	weekString = weekString.join(', ');
	return {
		valid: true,
		reason: `This assignment will occur ${formatFrequencyModifierString(frequency, frequencyModifier)} on ${weekString} until ${dateToUse.format('MMMM Do YYYY')}`
	};
};

export const formatMonthlyString = (
  endDate,
  startDate,
  frequency,
  frequencyModifier,
) => {
	const dateToUse = endDate;
	return `This assignment will start on ${startDate.format('MMMM Do YYYY')} and then occur ${formatFrequencyModifierString(frequency, frequencyModifier)} on the ${startDate.format('Do')} until ${dateToUse.format('MMMM Do YYYY')}`;
};

export const formatDate = (date) => {
	const now = moment(date).format('MMMM Do YYYY');
	return now;
};

export const formatEndText = (repeat, type) => {
	if (type === 'Weekly') return 'Ending after a certain number of weekly occurrences';
	if (type === 'Daily') return 'Ending after a certain number of daily occurrences';
	return 'Ending after a certain number of occurrences';
};

export const unitHash = {
	Daily: 'days.',
	Weekly: 'weeks.',
	Monthly: 'months.'
};

export const createChild = truth => (<div style={ { paddingLeft: '1em' } }><span> {truth.reason} </span></div>);

export const populateDropDown = (max, mod = 1) => {
	const repeatItems = [];
	for (let i = 0; i < max - 1; i += 1) {
		const stringI = (i + mod).toString();
		repeatItems.push(
			<WMMenuItem primaryText={ stringI } value={ stringI } key={ i } />,
  );
	}
	return repeatItems;
};

export const findDropDownMenuAndFix = () => {
  // This function goes and finds all HTML Nodes that need Z-Index fixing,
  // and apply changes to make the material uin components work again.
	const body = document.body;
	const suspectDivs = [];
	body.childNodes.forEach((node) => {
		if (node.nodeType === 1 && node.style.zIndex === '2000') suspectDivs.push(node);
	});
	suspectDivs.forEach((suspect) => {
    suspect.style.zIndex = '10002'; // eslint-disable-line
    suspect.style.pointerEvents = 'visible'; // eslint-disable-line
    suspect.childNodes[0].style.pointerEvents = 'all'; // eslint-disable-line
	});
};

/*global context*/

// import TableRichRow from 'table-rich-row/main';
import Application from '../core';
import $ from 'jquery';
import wmSelect from '../funcs/wmSelect';
import actionMenu from '../funcs/wmActionMenu';
// import wmTags from '../funcs/wmTags';
// import wmFilters from '../funcs/wmFilters';
import wmTabs from '../funcs/wmTabs';
import wmNotify from '../funcs/wmNotify';
import wmModal from '../funcs/wmModal';
// import wmTooltips from '../funcs/wm-tooltips';
import wmSearchFilter from '../funcs/wmSearchFilter';
import '../config/datepicker';

Application.init(context, () => {});

let modal, slidesModal, searchFilter, tableData;

modal = wmModal({
	title: 'Work Market Modal',
	fixedScroll: true,
	content: [
		'<p>A single slide modal, with controls and fixed scrolling.</p>',
		'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
		'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
		'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
		'<label>Input</label><input type="text" />'

	].join(''),
	controls: [
		{
			text: 'Cancel',
			close: true,
			classList: ''
		},
		{
			text: 'Save',
			primary: true,
			classList: ''
		}
	]
});

slidesModal = wmModal({
	title: 'Work Market Modal',
	showProgress: true,
	slides: [
		{
			title: 'Slide 1',
			fixedScroll: true,
			content: [
				'<p>A modal with two slides, a fixed scrolling container, and controls.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>'
			].join(''),
			controls: [
				{
					text: 'Cancel',
					close: true,
					classList: ''
				},
				{
					text: 'Continue',
					primary: true,
					forward: true,
					classList: ''
				}
			]
		},
		{
			title: 'Slide 2',
			fixedScroll: true,
			content: [
				'<p>A modal with two slides, a fixed scrolling container, and controls.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>',
				'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam a enim eros. Vivamus sollicitudin volutpat mattis. Duis et dignissim lorem. Nam tincidunt lectus ac leo euismod, eget fringilla velit sodales. Fusce nec dui imperdiet lacus laoreet bibendum a sit amet neque. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ut eros lorem. Nunc nulla tortor, vestibulum sit amet odio ut, tincidunt malesuada nibh. Fusce et vehicula orci.</p>'
			].join(''),
			controls: [
				{
					text: 'Back',
					back: true,
					classList: ''
				},
				{
					text: 'Finish',
					primary: true,
					classList: '',
					forward: true
				}
			]
		}
	]
});

$('#modals').find('.test-modal').on('click', modal.toggle);
$('#modals').find('.test-slides').on('click', slidesModal.toggle);
actionMenu();
// wmTags();
// wmFilters('.filter-group');
wmTabs();
searchFilter = wmSearchFilter({
	el: '.search-demo',
	filters: [
		{
			'name':'client',
			'title':'Client',
			'template': 'default',
			'options': [
				{ 'id':'12345', 'title':'Client 1', 'avatar': 'http://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50' },
				{ 'id':'55676', 'title':'Client 2', 'avatar': 'http://www.gravatar.com/avatar/205e460b479e2e5b48aec07710c08d50' }
			]
		},
		{
			'name':'status',
			'title':'Status',
			'template': 'default',
			'options':[
				{ 'id':'1', 'title':'Status 1' },
				{ 'id':'2', 'title':'Status 2' }
			]
		},
		{
			'name':'labels',
			'title':'Labels',
			'template': 'default',
			'options':[
				{'id':'55', 'title':'Labels 1'},
				{'id':'56', 'title':'Labels 2'}
			]
		},
		{
			'name':'talent',
			'title':'Talent',
			'template': 'default',
			'options':[
				{'id':'1543567', 'title':'Talent 1', 'label': 'experienced'},
				{'id':'6666325', 'title':'Talent 2', 'label': 'new'}
			]
		},
		{
			'name':'projects',
			'title':'Projects',
			'template': 'default',
			'options':[
				{'id':'542', 'title':'Projects 1'},
				{'id':'665', 'title':'Projects 2'}
			]
		}
	]
});

searchFilter.addFilter({
	'name':'owner',
	'title':'Owner',
	'template': 'default',
	'options':[
		{'id':'8915654', 'title':'Owner 1'},
		{'id':'4425674', 'title':'Owner 2'}
	]
});

$('.clear-search-filter').on('click', (event) => {
	event.preventDefault();
	searchFilter.reset();
});
$('.show-search-filter').on('click', (event) => {
	event.preventDefault();
	console.log(searchFilter.getFilterObject());
});

wmSelect({ selector: '.wm-select.-single' });
wmSelect({ selector: '.wm-select.-multiple' }, {
	plugins: ['remove_button'],
	maxItems: null
});

document.querySelector('.trigger-snackbar').addEventListener('click', () => {
	wmNotify({
		message: 'You successfully read this important alert message.',
		actionHandler: () => {},
		actionText: 'Do Something'
	});
});

// wmTooltips('#wm-tooltip-bottom');
// wmTooltips('#wm-tooltip-top');
// wmTooltips('#wm-tooltip-right');
// wmTooltips('#wm-tooltip-left');
// wmTooltips('.wm-tooltip-no-object');

tableData = [];

for (let i = 0; i < 20; i++) {
	let item = {
		id: i
	};
	for (let j = 0; j < 10; j++) {
		if (j % 2) {
			item[`Column ${j}`] = Math.random().toString(36).replace(/[^a-z]+/g, '').substr(0, 15);
		} else {
			item[`Column ${j}`] = Math.random();
		}
	}
	tableData.push(item);
}

// let tableRichRow = new TableRichRow({data: tableData});

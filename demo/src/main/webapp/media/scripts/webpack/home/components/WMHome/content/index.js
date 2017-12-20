const mediaPrefix = window.mediaPrefix;
const openModal = async() => {
	const loadAssignmentCreationModal = async() => {
		const module = await import(/* webpackChunkName: "newAssignmentCreation" */ '../../../../assignments/creation_modal');
		return module.default;
	};
	loadAssignmentCreationModal().then(CreationModal => new CreationModal());
};

export default [
	{
		icon: `${mediaPrefix}/images/find.talent.svg`,
		title: 'Find Talent',
		contentCopy: 'Invite your current freelancers or browse the talent marketplace to find the right talent for the job.',
		buttonText: 'Find Talent',
		buttonLink: '/search?ref=boxsearch&keyword=',
		videoLink: 'https://player.vimeo.com/video/186274020',
		videoTime: '1:00',
		tileLink: '/search'
	},
	{
		icon: `${mediaPrefix}/images/talent.pools.home.svg`,
		title: 'Curate Talent Pools',
		contentCopy: 'Curate groups of pre-selected talent for future assignments.',
		buttonText: 'Curate Talent Pools',
		buttonLink: '/groups/create',
		videoLink: 'https://player.vimeo.com/video/186285541',
		videoTime: '1:51',
		tileLink: '/groups/manage'
	},
	{
		icon: `${mediaPrefix}/images/new.assignment.svg`,
		title: 'Create Assignments',
		contentCopy: 'Create an assignment, upload multiple assignments in bulk, or create a project with multiple assignments.',
		buttonText: 'Create Assignment',
		buttonLink: openModal,
		videoLink: 'https://player.vimeo.com/video/186273927',
		videoTime: '2:10',
		tileLink: '/assignments#status/inprogress/managing'
	},
	{
		icon: `${mediaPrefix}/images/payment.svg`,
		title: 'Payment Center',
		contentCopy: 'Configure your payment settings, view invoices, and manage your funds.',
		buttonText: 'Fund Your Account',
		buttonLink: '/payments/invoices/payables/due',
		videoLink: 'https://player.vimeo.com/video/186273498',
		videoTime: '1:41',
		tileLink: '/payments'
	}
];

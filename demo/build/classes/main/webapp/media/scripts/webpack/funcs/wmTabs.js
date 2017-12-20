import $ from 'jquery';

export default (options) => {
	const settings = Object.assign({
		selector: '.wm-tab',
		event: 'click',
		activeClass: '-active',
		root: document,
		autoBind: true,
		autoRun: true
	}, typeof options === 'object' ? options : {});

	const { root, event, selector, activeClass } = settings;

	const initialize = ({ currentTarget }) => {
		Array.from(currentTarget.parentNode.children).forEach((node) => {
			const contentSelector = node.getAttribute('data-content');
			const content = root.querySelector(contentSelector);

			if (node === currentTarget) {
				node.classList.add(activeClass);
				if (content) {
					content.classList.add(activeClass);
				}
			} else {
				node.classList.remove(activeClass);
				if (content) {
					content.classList.remove(activeClass);
				}
			}
		});
	};

	$(root).on(event, selector, initialize);
	return $(selector, root);
};

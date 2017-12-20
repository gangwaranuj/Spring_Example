import $ from 'jquery';
import wmModal from '../funcs/wmModal';

export default () => {
	$('#get-sp-metadata').on('click', (event) => {
		event.preventDefault();

		const dataSelector = $('#spMetadata');
		const metadata = dataSelector.text();
		const rows = metadata.split('\n').length;
		const modal = wmModal({
			title: 'Work Market Metadata',
			content: `<textarea style="border:none;height:400px;width:800px;" rows="${rows}" readonly>${dataSelector.text()}</textarea>`,
			controls: [
				{
					text: 'OK',
					close: true,
					classList: ''
				}
			]
		});
		modal.toggle();
	});
};

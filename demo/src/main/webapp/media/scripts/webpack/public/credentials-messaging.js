export const getMessageIcon = (type) => {
	let iconName;

	switch (type) {
	case 'info':
		iconName = 'info_outline';
		break;
	case 'success':
		iconName = 'check_circle';
		break;
	case 'error':
		iconName = 'highlight_off';
		break;
	default:
		throw new Error('Messaging icon type must be one of: "info", "success", or "error"');
	}

	return `<i
		class="
			material-icons
			credentials__messages-icon
			credentials__messages-icon--${type}
		"
	>
		${iconName}
	</i>`;
};

export const showFormMessaging = ($container) => {
	if ($container.find('.alert').length) {
		$container.removeClass('credentials__messages--hidden');

		if ($container.find('.alert-error').length) {
			$container.addClass('credentials__messages--error');
			$container.prepend(getMessageIcon('error'));
		} else if ($container.find('.alert-success').length) {
			$container.addClass('credentials__messages--success');
			$container.prepend(getMessageIcon('success'));
		} else {
			$container.prepend(getMessageIcon('info'));
		}
	}
};

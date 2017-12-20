export default (options) => {
	const snackbarContainer = document.querySelector('.mdl-js-snackbar');

	const isSnackbarReady = () => window.MaterialSnackbar && snackbarContainer.MaterialSnackbar;

	const initSnackbar = () => {
		const snacks = Array.isArray(options) ? options : [options];

		return snacks.map((snack) => {
			const snackbarOptions = typeof snack === 'string' ? { message: snack } : snack;
			snackbarContainer.MaterialSnackbar.showSnackbar(snackbarOptions);
		});
	};

	if (isSnackbarReady()) {
		initSnackbar();
	} else {
		const snackCheck = setInterval(() => {
			// ensure that material design scripts have been executed
			if (isSnackbarReady()) {
				clearInterval(snackCheck);
				initSnackbar();
			}
			return null;
		}, 1000);
	}
};

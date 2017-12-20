const spacing = '2.375em';

export default {
	actions: {
		textAlign: 'right',
		margin: '1em 0 0'
	},

	inlineNotification: {
		marginBottom: spacing
	},

	paper: {
		padding: spacing,
		marginBottom: '1em',

		last: {
			marginBottom: '3em',
			padding: `${spacing}`
		}
	},

	price: {
		textAlign: 'right',
		margin: spacing,
		marginBottom: 0,
		fontSize: '1.25em'
	},

	formError: {
		margin: `${spacing} ${spacing} -${spacing} ${spacing}`,
		textAlign: 'right',
		color: 'red',
		fontSize: '0.875em',

		icon: {
			verticalAlign: 'middle'
		}
	},

	formActions: {
		padding: spacing,
		paddingTop: 0,
		display: 'flex',
		alignItems: 'center',
		alignContent: 'center',
		justifyContent: 'space-between',

		termsAction: {
			textDecoration: 'none',
			color: '#53b3f3',
			fontWeight: 500
		}
	},

	container: {
		display: 'flex'
	},

	intro: {
		title: {
			fontWeight: 'normal'
		}
	}
};

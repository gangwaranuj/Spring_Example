import {
	commonStyles
} from '@workmarket/front-end-components';

const {
	blue,
	orange
} = commonStyles.colors.baseColors;

export default {
	wrapper: {
		display: 'flex',
		flexDirection: 'column',
		justifyContent: 'center',
		alignItems: 'center',
		padding: '16px'
	},
	title: {
		fontSize: '20px',
		textAlign: 'center',
		paddingTop: '24px'
	},
	img: {
		width: '100px',
		height: '100px'
	},
	text: {
		fontSize: '14px',
		paddingBottom: '16px',
		paddingLeft: '60px',
		paddingRight: '60px'
	},
	links: {
		display: 'flex',
		flexDirection: 'row',
		fontSize: '13px',
		cursor: 'pointer'
	},
	textField: {
		color: orange,
		marginTop: '16px',
		marginBottom: '16px'
	},
	modalContent: {
		display: 'inline-block',
		maxWidth: '100%',
		marginTop: '24px',
		height: '600px'
	},
	learnLink: {
		textDecoration: 'none',
		color: blue
	},
	actionField: {
		paddingTop: '8px',
		paddingBottom: '16px'
	},
	dropDown: {
		marginTop: '8px',
		marginBottom: '16px'
	},
	container: {
		maxWidth: '700px',
		margin: 'auto'
	}
};

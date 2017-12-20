import {
	commonStyles
} from '@workmarket/front-end-components';

const {
	blue,
	orange,
	green
} = commonStyles.colors.baseColors;

export default {
	wrapper: {
		padding: '16px'
	},
	title: {
		fontSize: '20px',
		textAlign: 'center',
		paddingTop: '24px'
	},
	img: {
		width: '100px',
		height: '100px',
		display: 'block',
		margin: 'auto'
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
	doneIcon: {
		fontSize: '70px',
		width: '70px',
		display: 'block',
		margin: 'auto',
		color: green
	},
	uploaderWrapper: {
		display: 'flex',
		width: '200px'
	},
	uploader: {
		margin: '100px'
	},
	radioButtons: {
		display: 'flex',
		flexDirection: 'row'
	},
	topCheckbox: {
		marginTop: '16px'
	},
	bottomCheckbox: {
		marginBottom: '16px'
	},
	modalContent: {
		display: 'inline-block',
		maxWidth: '100%',
		marginTop: '24px',
		height: '600px'
	},
	modalAgreement: {
		display: 'inline-block',
		maxWidth: '100%',
		marginTop: '24px',
		height: '480px'
	},
	modalAutoClose: {
		display: 'inline-block',
		maxWidth: '100%',
		marginTop: '24px',
		height: '211px'
	},
	modalRatings: {
		display: 'inline-block',
		maxWidth: '100%',
		marginTop: '24px',
		height: '721.32px'
	},
	modalPrintout: {
		display: 'inline-block',
		maxWidth: '100%',
		marginTop: '24px',
		height: '714.67px'
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
	logoText: {
		fontSize: '17px'
	},
	errors: {
		marginTop: '2em'
	},
	container: {
		maxWidth: '700px',
		margin: 'auto',
		marginBottom: '100px'
	}
};

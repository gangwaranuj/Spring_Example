import {
	commonStyles
} from '@workmarket/front-end-components';

export default {
	container: {
		marginBottom: '20px',
		minHeight: '400px'
	},
	link: {
		color: commonStyles.colors.baseColors.blue,
		cursor: 'pointer',
		textDecoration: 'none',
		':hover': {
			color: commonStyles.colors.baseColors.orange
		}
	},
	activeLink: {
		color: commonStyles.colors.baseColors.orange,
		cursor: 'pointer',
		textDecoration: 'none',
		':hover': {
			color: commonStyles.colors.baseColors.orange
		}
	},
	editIcon: {
		color: commonStyles.colors.baseColors.lightGrey,
		fontSize: '1.25em',
		marginRight: '0.5em',
		position: 'relative',
		top: '3px'
	},
	newButton: {
		float: 'right',
		marginTop: '0.25em',
		marginRight: '1em',
		boxShadow: 'none'
	},
	listHeader: {
		fontSize: '1.5em',
		color: commonStyles.colors.baseColors.darkGrey
	},
	emptyOrInactiveText: {
		width: '370px',
		fontSize: '20px',
		lineHeight: '1.2',
		textAlign: 'center',
		color: commonStyles.colors.baseColors.grey,
		display: 'block',
		margin: 'auto'
	}
};

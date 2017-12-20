import {
	commonStyles
} from '@workmarket/front-end-components';

const { blue } = commonStyles.colors.baseColors;

export default {
	card: {
		backgroundColor: 'white',
		width: '700px',
		margin: 'auto',
		marginBottom: '2em'
	},
	cardHeader: {
		display: 'flex',
		alignItems: 'center'
	},
	cardHeaderText: {
		order: 2
	},
	cardHeaderTitle: {
		fontSize: '20px',
		fontWeight: 500,
		color: blue
	},
	cardIcon: {
		width: '100px',
		height: '100px',
		margin: '0 20px',
		order: 1
	},
	completedIcon: {
		order: 3,
		margin: '0 0 0 auto',
		alignSelf: 'flex-start'
	},
	actions: {
		padding: '15px',
		borderTop: '1px solid #eee',
		display: 'flex',
		justifyContent: 'flex-end'
	}
};

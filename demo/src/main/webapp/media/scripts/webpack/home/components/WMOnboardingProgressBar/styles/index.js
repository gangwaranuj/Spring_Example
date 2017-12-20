import {
	commonStyles
} from '@workmarket/front-end-components';

const { baseColors } = commonStyles.colors;

export default {
	container: {
		display: 'flex',
		flexDirection: 'column',
		height: '110px',
		width: '960px',
		border: '2px solid #53b3f3',
		justifyContent: 'space-around',
		padding: '0 15px 0 20px',
		margin: '20px 0'
	},
	row: {
		display: 'flex',
		justifyContent: 'space-between',
		alignItems: 'center'
	},
	topRow: {
		justifyContent: 'flex-start'
	},
	progressPercentageText: {
		fontWeight: '600'
	},
	progressBar: {
		width: '370px'
	},
	button: {
		width: '185px',
		backgroundColor: baseColors.green
	},
	videoIcon: {
		width: '22px',
		height: '22px',
		fontSize: '22px',
		cursor: 'pointer'
	},
	welcomeText: {
		width: '209px',
		margin: '0 0 0 7px',
		fontSize: '14px',
		color: '#53b3f3',
		fontWeight: '600',
		letterSpacing: '0.5px',
		cursor: 'pointer'
	}
};

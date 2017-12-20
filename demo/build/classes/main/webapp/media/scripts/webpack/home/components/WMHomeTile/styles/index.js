import {
	commonStyles
} from '@workmarket/front-end-components';

const { baseColors } = commonStyles.colors;

export default {
	card: {
		width: '225px'
	},
	cardTextInner: {
		display: 'flex',
		flexFlow: 'column nowrap',
		justifyContent: 'space-between',
		alignItems: 'center',
		alignContent: 'center',
		color: 'rgba(100, 107, 111, 0.87)',
		fontWeight: 'normal',
		width: '185px',
		textAlign: 'center',
		margin: '0 auto',
		height: '360px'
	},
	tileClickableArea: {
		cursor: 'pointer',
		display: 'flex',
		flexFlow: 'column nowrap',
		alignItems: 'center',
		alignContent: 'center',
		justifyContent: 'space-between',
		height: '220px'
	},
	icon: {
		width: '80px',
		height: '80px'
	},
	cardTitle: {
		fontSize: '20px',
		height: '27px',
		fontWeight: '300'
	},
	cardSeparator: {
		width: '185px',
		margin: '0 auto',
		height: '2px',
		backgroundColor: baseColors.lightGrey
	},
	cardCopy: {
		height: '60px',
		fontSize: '12px',
		maxWidth: '185px'
	},
	cardButton: {
		width: '185px'
	},
	videoLinkContainer: {
		display: 'flex',
		alignItems: 'center',
		color: '#53b3f3',
		width: '100%',
		cursor: 'pointer'
	},
	videoIcon: {
		margin: '0 5px'
	}
};

import {
	commonStyles
} from '@workmarket/front-end-components/';

const {
	white,
	charcoalGrey,
	offWhite,
	green
} = commonStyles.colors.baseColors;

export default {
	formRow: {
		margin: '1em 0'
	},
	cardText: {
		maxWidth: '590px',
		margin: 'auto',
		padding: '0 0 20px',

		icons: {
			display: 'flex',
			margin: '0 -5px',
			justifyContent: 'space-between'
		}
	},
	fundingTile: selected => ({
		width: '100%',
		height: '100px',
		margin: '0 5px',
		background: selected ? white : offWhite,
		color: charcoalGrey,
		textAlign: 'center',
		display: 'flex',
		flexDirection: 'column',
		justifyContent: 'center',
		alignItems: 'center',
		boxSizing: 'content-box',
		cursor: 'pointer',
		borderRadius: '3px',
		border: selected ? `3px solid ${green}` : `3px solid ${offWhite}`
	}),
	tileTime: {
		display: 'flex',
		justifyContent: 'space-between',
		alignItems: 'center'
	},
	tileTitle: selected => ({
		fontSize: '14px',
		color: selected ? green : charcoalGrey
	}),
	timeIcon: {
		fontSize: '12px'
	},
	timeText: {
		fontSize: '12px'
	},
	imageWrapper: {
		display: 'flex',
		justifyContent: 'flex-end'
	},
	image: {
		maxHeight: '150px'
	},
	fundingType: {
		margin: '0 0 20px'
	}
};

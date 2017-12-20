import {
	commonStyles
} from '@workmarket/front-end-components';

const {
	blue
} = commonStyles.colors.baseColors;

export default {
	page: {
		paddingBottom: '5em'
	},

	navigation: {
		display: 'flex',
		alignItems: 'center',
		justifyContent: 'space-between'
	},

	appBar: {
		icon: {
			color: '#ffffff'
		}
	},

	drawerContent: {
		padding: '55px'
	},

	button: {
		color: blue
	}
};

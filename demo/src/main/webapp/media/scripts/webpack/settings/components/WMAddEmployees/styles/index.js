import {
	commonStyles
} from '@workmarket/front-end-components';

const {
	orange,
	white,
	lightGrey,
	charcoalGrey
} = commonStyles.colors.baseColors;

export default {
	tabs: {
		backgroundColor: white,
		borderBottom: `1px solid ${lightGrey}`
	},

	tab: {
		color: charcoalGrey
	},

	inkBar: {
		backgroundColor: orange
	}
};

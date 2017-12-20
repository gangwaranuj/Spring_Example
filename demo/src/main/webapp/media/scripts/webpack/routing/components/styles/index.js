import {
	commonStyles
} from '@workmarket/front-end-components';

const {
	lightGrey
} = commonStyles.colors.baseColors;

export default {
	icon: {
		fontSize: '1.25em',
		color: lightGrey,
		margin: '0 0.25em',
		cursor: 'pointer'
	},
	title: {
		display: 'flex',
		flexDirection: 'row',
		margin: '0.5em 0'
	},
	subtext: {
		display: 'flex',
		flexDirection: 'row',
		margin: '0.5em 0'
	}
};

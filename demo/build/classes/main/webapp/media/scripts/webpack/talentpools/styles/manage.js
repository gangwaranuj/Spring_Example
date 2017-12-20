import {
	commonStyles
} from '@workmarket/front-end-components';

export default {
	drawerContainer: {
		position: 'relative',
		width: '100%',
		height: '100%',
		overflowX: 'hidden'
	},
	drawerHeader: {
		background: commonStyles.colors.baseColors.orange,
		position: 'absolute',
		height: '155px',
		width: '100%',
		zIndex: '10'
	},
	drawerCreateHeader: {
		background: commonStyles.colors.baseColors.orange,
		position: 'absolute',
		height: '110px',
		width: '100%',
		zIndex: '10'
	},
	drawerBody: {
		height: 'calc(100% - 155px)',
		position: 'absolute',
		overflowX: 'hidden',
		top: '155px',
		width: '100%'
	},
	drawerCreateBody: {
		height: 'calc(100% - 110px)',
		position: 'absolute',
		top: '110px',
		width: '100%'
	}
};

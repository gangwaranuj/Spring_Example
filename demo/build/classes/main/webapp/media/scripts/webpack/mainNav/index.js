import React, { Component } from 'react';
import injectTapEventPlugin from 'react-tap-event-plugin';
import { render } from 'react-dom';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import Navigation from './components/Navigation';
import ajaxSendInit from '../funcs/ajaxSendInit';
import translate from '../funcs/translation';

if (document.querySelector('meta[name="isDesktop"]').getAttribute('content') === 'true') {
	injectTapEventPlugin();

	// fix for ajax requests on components
	ajaxSendInit();

	const muiTheme = getMuiTheme({
		palette: {
			accent1Color: '#f7961d'
		}
	});

	const loadConfig = () => {
		// from /partials/navigation/app.jsp
		const navConfig = window.navConfig;
		const isBuyer = navConfig.currentUser.buyer;

		if (isBuyer) {
			navConfig.navMenuItems = [
				{
					label: translate("translation.work"),
					iconPath: 'M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z',
					items: [
						{ href: '/assignments/', title: translate("translation.assignments") },
						{ href: '/assignments/?launchAssignmentModal&introdisabled', title: translate("translation.new_assignment") },
						{ href: '/projects', title: translate("translation.projects") },
						{ href: '/addressbook', title: translate("translation.contact_manager") },
						{ href: '/assignments/upload', title: translate("translation.work_upload_tm") },
						{ href: '/realtime', title: translate("translation.realtime") }
					]
				},
				{
					label: translate("translation.talent"),
					iconPath: 'M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z',
					items: [
						{ href: '/search/', title: translate("translation.find_talent") },
						{ href: '/groups', title: translate("translation.talent_pools") },
						{ href: '/invitations', title: translate("translation.recruiting") },
						{ href: '/lms/manage/list_view', title: translate("translation.tests") },
						{ href: '/lms/manage/surveys', title: translate("translation.surveys") }
					]
				},
				{
					label: translate("translation.payments"),
					iconPath: 'M4 10v7h3v-7H4zm6 0v7h3v-7h-3zM2 22h19v-3H2v3zm14-12v7h3v-7h-3zm-4.5-9L2 6v2h19V6l-9.5-5z',
					items: [
						{ href: '/payments', title: translate("translation.overview") },
						{ href: '/funds/add', title: translate("translation.add_funds") },
						{ href: '/funds/accounts', title: translate("translation.accounts") }
					]
				},
				{
					label: translate("translation.reports"),
					iconPath: 'M23 8c0 1.1-.9 2-2 2-.18 0-.35-.02-.51-.07l-3.56 3.55c.05.16.07.34.07.52 0 1.1-.9 2-2 2s-2-.9-2-2c0-.18.02-.36.07-.52l-2.55-2.55c-.16.05-.34.07-.52.07s-.36-.02-.52-.07l-4.55 4.56c.05.16.07.33.07.51 0 1.1-.9 2-2 2s-2-.9-2-2 .9-2 2-2c.18 0 .35.02.51.07l4.56-4.55C8.02 9.36 8 9.18 8 9c0-1.1.9-2 2-2s2 .9 2 2c0 .18-.02.36-.07.52l2.55 2.55c.16-.05.34-.07.52-.07s.36.02.52.07l3.55-3.56C19.02 8.35 19 8.18 19 8c0-1.1.9-2 2-2s2 .9 2 2z',
					items: [
						{ href: '/reports/statistics', title: translate("translation.dashboard") },
						{ href: '/reports/custom/manage', title: translate("translation.new_report") },
						{ href: '/reports', title: translate("translation.all_reports") }
					]
				}
			];
		} else {
			navConfig.navMenuItems = [
				{
					label: translate("translation.talent_profile"),
					iconPath: 'M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z',
					items: [
						{ href: '/profile', title: translate("translation.my_profile") },
						{ href: '/lms/view', title: translate("translation.tests") },
						{ href: '/search-groups', title: translate("translation.talent_pools") },
						{ href: '/workerservices', title: translate("translation.worker_services") }
					]
				},
				{
					label: translate("translation.work"),
					iconPath: 'M20 6h-4V4c0-1.11-.89-2-2-2h-4c-1.11 0-2 .89-2 2v2H4c-1.11 0-1.99.89-1.99 2L2 19c0 1.11.89 2 2 2h16c1.11 0 2-.89 2-2V8c0-1.11-.89-2-2-2zm-6 0h-4V4h4v2z',
					items: [
						{ href: '/assignments', title: translate("translation.my_work") },
						{ href: '/worker/browse', title: translate("translation.find_work") },
						{ href: '/assignments?calendar=on', title: translate("translation.calendar") },
						{ href: '/ratings', title: translate("translation.ratings") }
					]
				},
				{
					label: translate("translation.payments"),
					iconPath: 'M4 10v7h3v-7H4zm6 0v7h3v-7h-3zM2 22h19v-3H2v3zm14-12v7h3v-7h-3zm-4.5-9L2 6v2h19V6l-9.5-5z',
					items: [
						{ href: '/payments', title: translate("translation.overview") },
						{ href: '/funds/accounts', title: translate("translation.accounts") }
					]
				},
				{
					label: translate("translation.reports"),
					iconPath: 'M23 8c0 1.1-.9 2-2 2-.18 0-.35-.02-.51-.07l-3.56 3.55c.05.16.07.34.07.52 0 1.1-.9 2-2 2s-2-.9-2-2c0-.18.02-.36.07-.52l-2.55-2.55c-.16.05-.34.07-.52.07s-.36-.02-.52-.07l-4.55 4.56c.05.16.07.33.07.51 0 1.1-.9 2-2 2s-2-.9-2-2 .9-2 2-2c.18 0 .35.02.51.07l4.56-4.55C8.02 9.36 8 9.18 8 9c0-1.1.9-2 2-2s2 .9 2 2c0 .18-.02.36-.07.52l2.55 2.55c.16-.05.34-.07.52-.07s.36.02.52.07l3.55-3.56C19.02 8.35 19 8.18 19 8c0-1.1.9-2 2-2s2 .9 2 2z',
					items: [
						{ href: '/reports', title: translate("translation.overview") },
						{ href: '/reports/custom/manage', title: translate("translation.new_report") }
					]
				}
			];
		}

		return navConfig;
	};

	class NavigationApp extends Component {
		render () {
			return (
				<MuiThemeProvider muiTheme={ muiTheme }>
					<Navigation config={ loadConfig() } />
				</MuiThemeProvider>
			);
		}
	}
	render(<NavigationApp />, document.querySelector('#wm-main-nav'));
}

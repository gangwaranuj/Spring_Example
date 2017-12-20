/* eslint-disable no-new */
import $ from 'jquery';
import Application from '../core';
import UsersManagePage from './users_manage';
import NotificationsPage from './notifications_page';
import PaycycleView from './paycycle_wizard_view';
import SettingsView from './settings_main_view';
import UsersView from './users_view';
import LabelsView from './labels_view';
import SecurityView from './security_view';
import IntegrationsView from './integrations_view';
import AccountPage from './account_index';
import ManageAgreementsPage from './agreements_index';
import AgreementsView from './agreements_view';
import HoursPage from './hours_page';
import AutotaskPage from './autotask_page';
import FileManagerPage from './filemanager_page';
import Screening from './screening';
import NotificationsListPage from './notifications-list-page';
import ManageBlocked from './manage_blocked_page';
import SSOPage from './sso_page';
import TemplatesPage from './templates_page';
import EditCustomFieldsPage from './custom_fields_edit_page';
import IndexCustomFieldsPage from './index_custom_fields_page';
import DevicesPage from './devices_page';
import AcceptAgreementsPage from './accept_agreements_page';
import '../funcs/snippetBuilder';

Application.init({ name: 'settings', features: config }, () => {});

switch (config.mode) {
case 'devices':
	DevicesPage();
	break;
case 'filemanager':
	FileManagerPage();
	break;
case 'manage-blocked':
	ManageBlocked();
	break;
case 'notifications-list':
	NotificationsListPage(config.isArchive);
	break;
case 'screening':
	Screening(config.usaPrice, config.intlPrice);
	break;
case 'autotask':
	AutotaskPage();
	break;
case 'hours':
	HoursPage();
	break;
case 'manageAgreements':
	ManageAgreementsPage();
	break;
case 'acceptAgreements':
	AcceptAgreementsPage(config.versionId);
	break;
case 'agreementsView':
	AgreementsView();
	break;
case 'usersManage':
	UsersManagePage(config.hasBusinessTaxInfo);
	break;
case 'feed':
	$('#snippet-builder').snippetBuilder({
		modalTemplate: '#modal-tmpl',
		textarea: '#snippet',
		preview: '#preview',
		companyId: config.companyId
	});
	break;
case 'notifications':
	new NotificationsPage();
	break;
case 'paycycle':
	new PaycycleView({
		pendingPaymentWorkCount: config.pendingPaymentWorkCount,
		statementsEnabled: config.statementsEnabled,
		payTermsEnabled: config.payTermsEnabled
	});
	break;
case 'users':
	new UsersView({
		isLastDispatcher: config.isLastDispatcher
	});
	break;
case 'labels':
	new LabelsView();
	break;
case 'security':
	new SecurityView({
		ipsJson: config.ipsJson
	});
	break;
case 'integrations':
	$.escapeHTML = function escapeHtml (html) {
		return $('<div/>').text(html).html();
	};
	new IntegrationsView({
		webHookClientId: config.webHookClientId
	});
	break;
case 'account':
	AccountPage();
	break;
case 'sso':
	SSOPage();
	break;
case 'templates':
	TemplatesPage(config.isActiveTemplates);
	break;
case 'editCustomFields':
	EditCustomFieldsPage(config.jsonFieldGroup);
	break;
case 'indexCustomFields':
	IndexCustomFieldsPage(config.isActiveCustomFields);
	break;
case 'settings':
default:
	new SettingsView({
		isBudgetEnabledFlag: config.isBudgetEnabledFlag
	});
}

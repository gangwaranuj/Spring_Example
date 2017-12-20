'use strict';

import LicensesReviewPage from './licenses_review_page';
import ProfilePage from './profile_page';
import LockedCompanies from './unlock_company';
import TagsMainView from './tags_main_view';
import AccountingDetailsMainView from './accounting/details_main_view';
import WorkMarketInvoicesView from './accounting/workmarket_invoices_view';
import ManageFundsView from './accounting/manage_funds_view';
import TaxStatusUpdateView from './accounting/vor_nvor_update_view';
import CompanyFinancesView from './manage/company/finances_view';
import FeaturesView from './manage/company/features_view';
import CompanyView from './manage/subscriptions/company_view';
import ReportingView from './manage/subscriptions/reporting_view';
import TaxIssuanceReport from './tax/tax_earnings_view';
import TaxIssuanceForm from './tax/tax_forms_view';
import EarningsDetailView from './tax/tax_earnings_detail_view';
import EarningsDetailsReportView from './tax/tax_earnings_details_report_view';
import TaxServiceDetailView from './tax/tax_service_detail_view';
import SubscriptionApprovalQueueView from './manage/subscriptions/approval_view';
import UserEditPage from './user_edit_page';
import UserAddPage from './user_add_page';
import LatestForumPostsView from './forums/latest_posts_view';
import DevFeatureTogglePage from './feature_toggles_page';
import jdenticon from '../dependencies/jquery.jdenticon';
import '../dependencies/jquery.fileupload';
import '../dependencies/jquery.iframe-transport';
import '../dependencies/jquery.bootstrap-dropdown';

setTimeout(jdenticon, 1000);

const config = window.config || {};

switch (config.mode) {
	case 'licensesReview':
		new LicensesReviewPage();
		break;
	case 'profile':
		new ProfilePage(config.profileId);
		break;
	case 'tags':
		new TagsMainView({
			tagType: config.tagType
		});
		break;
	case 'lockedCompanies':
		new LockedCompanies();
		break;
	case 'details':
		new AccountingDetailsMainView({
			nonSubscriptionInvoiceLineItemTypes : config.nonSubscriptionInvoiceLineItemTypes,
			noPlanSubscriptionInvoiceTypeCodes: config.noPlanSubscriptionInvoiceTypeCodes
		});
		break;
	case 'invoices':
		new WorkMarketInvoicesView();
		break;
	case 'manageFunds':
		new ManageFundsView();
		break;
	case 'taxStatusUpdate':
		new TaxStatusUpdateView({
			publishedYears: config.publishedYears
		});
		break;
	case 'manageFinances':
		new CompanyFinancesView();
		break;
	case 'features':
		new FeaturesView();
		break;
	case 'companySubscription':
		new CompanyView({
			pricingType: config.pricingType,
			subscriptionStatus: config.subscriptionStatus,
			subscriptionCanRenew: config.subscriptionCanRenew,
			nextPossibleUpdateDate: config.nextPossibleUpdateDate,
			subscriptionTiers: config.subscriptionTiers,
			subscriptionAddOns: config.subscriptionAddOns,
			subscriptionServiceConfigs: config.subscriptionServiceConfigs,
			isVendorOfRecord: config.isVendorOfRecord,
			hasServiceType: config.hasServiceType
		});
		break;
	case 'subscriptionReporting':
		new ReportingView();
		break;
	case 'taxIssuanceReport':
		new TaxIssuanceReport({
			canPublish: config.canPublish
		});
		break;
	case 'taxIssuanceForm':
		new TaxIssuanceForm({
			canPublish: config.canPublish
		});
		break;
	case 'earningsDetail':
		new EarningsDetailView({
			canPublish: config.canPublish
		});
		break;
	case 'earningsDetailsReport':
		new EarningsDetailsReportView({
			canPublish: config.canPublish
		});
		break;
	case 'taxServiceDetail':
		new TaxServiceDetailView({
			canPublish: config.canPublish
		});
		break;
	case 'subscriptionApprovalQueue':
		new SubscriptionApprovalQueueView();
		break;
	case 'userEdit':
		new UserEditPage();
		break;
	case 'userAdd':
		new UserAddPage();
		break;
	case 'latestForumPosts':
		new LatestForumPostsView();
		break;
	case 'devFeatureToggles':
		DevFeatureTogglePage();
		break;
}

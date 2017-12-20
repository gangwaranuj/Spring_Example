'use strict';

import LedgerPage from './ledger_page';
import OfflineLedgerPage from './offline_ledger_page';
import WithdrawView from './withdraw_funds_view';
import AddFundsView from './add_funds_view';
import InvoicesView from './invoices_view';
import NewAccountPage from './new_account_page';
import IndexPage from './index_page';
import GenerateInvoicePage from './generate_invoice_page';
import GccPage from './gcc_page';
import AccountsListPage from './accounts_list_page';
import AllocateBudgetPage from './allocate_budget_page';
import AddPricingContainer from './containers/add_pricing';
import PricingReducer from './reducers/pricing';

export default {
	LedgerPage,
	OfflineLedgerPage,
	WithdrawView,
	AddFundsView,
	InvoicesView,
	NewAccountPage,
	IndexPage,
	GenerateInvoicePage,
	GccPage,
	AccountsListPage,
	AllocateBudgetPage,
	AddPricingContainer,
	PricingReducer
};

package com.workmarket.api.internal.service;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import com.workmarket.api.internal.service.accounting.AccountingException;
import com.workmarket.api.internal.service.accounting.BuyerCommitmentCommand;
import com.workmarket.api.internal.service.accounting.BuyerWorkFee2Command;
import com.workmarket.api.internal.service.accounting.BuyerWorkFee3Command;
import com.workmarket.api.internal.service.accounting.BuyerWorkPaymentCommand;
import com.workmarket.api.internal.service.accounting.RemoveFromGeneralCommand;
import com.workmarket.api.internal.service.accounting.SellerWorkPaymentCommand;
import com.workmarket.biz.plutus.gen.Messages.Invoice;
import com.workmarket.biz.plutus.gen.Messages.InvoiceItem;
import com.workmarket.biz.plutus.gen.Messages.Order;
import com.workmarket.biz.plutus.gen.Messages.OrderItem;
import com.workmarket.biz.plutus.gen.Messages.UserIdentity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegisterSummaryFields;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.account.RegisterTransactionType;
import com.workmarket.domains.model.account.payment.PaymentConfiguration;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.invoice.InvoicePagination;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.payments.service.BillingService;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.account.PaymentConfigurationService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AccountingServiceIT extends BaseServiceIT {
  private static final long INVOICE_DUE_DATE = System.currentTimeMillis();

  @Autowired private UserService userService;
  @Autowired private AccountingService accountingService;
  @Autowired private BillingService billingService;
  @Autowired private PaymentConfigurationService paymentConfigurationService;


  @Test
  public void holdFunds_Error_UserSpendLimit() throws Exception {
    final User buyer = newEmployeeWithCashBalance();
    buyer.setSpendLimit(new BigDecimal("0.00"));
    userService.saveOrUpdateUser(buyer);
    try {
      accountingService.holdFunds(ImmutableList.of(createOrder(buyer, "10.00", "0.00", "1.00", "0.10")));
      fail("Holding funds should fail for low user spend limit");
    } catch (AccountingException ignored) { }
  }


  @Test
  public void holdFunds_Error_AvailableCash() throws Exception {
    final User buyer = newEmployeeWithCashBalance("0.00");
    buyer.setSpendLimit(new BigDecimal("10.00"));
    userService.saveOrUpdateUser(buyer);
    try {
      accountingService.holdFunds(ImmutableList.of(createOrder(buyer, "10.00", "0.00", "1.00", "0.10")));
      fail("Holding funds should fail for low available cash");
    } catch (AccountingException ignored) { }
  }


  @Test
  public void holdFunds_Error_GeneralCash() throws Exception {
    final User buyer = newEmployeeWithCashBalance("10.00");
    buyer.setSpendLimit(new BigDecimal("10.00"));
    userService.saveOrUpdateUser(buyer);
    accountRegisterService.removeFundsFromGeneral(buyer.getCompany().getId(), new BigDecimal("10.00"));
    try {
      accountingService.holdFunds(ImmutableList.of(createOrder(buyer, "10.00", "0.00", "1.00", "0.10")));
      fail("Holding funds should fail for low general cash");
    } catch (AccountingException ignored) { }
  }


  @Test
  public void holdFunds_Success_AllFromDepositedCash() throws Exception {
    final User buyer = newEmployeeWithCashBalance("10.00");
    buyer.setSpendLimit(new BigDecimal("10.00"));
    userService.saveOrUpdateUser(buyer);

    final AccountRegisterSummaryFields expectedSummaryFields = accountRegisterService
        .getAccountRegisterSummaryFields(buyer.getCompany().getId());

    accountingService.holdFunds(ImmutableList.of(createOrder(buyer, "3.00", "0.00", "1.00", "0.10")));

    final AccountRegisterSummaryFields actualSummaryFields = accountRegisterService
        .getAccountRegisterSummaryFields(buyer.getCompany().getId());

    expectedSummaryFields.setAvailableCash(expectedSummaryFields.getAvailableCash().subtract(new BigDecimal("4.10")));
    expectedSummaryFields.setDepositedCash(expectedSummaryFields.getDepositedCash().subtract(new BigDecimal("4.10")));
    expectedSummaryFields.setPendingCommitments(expectedSummaryFields.getPendingCommitments().add(new BigDecimal("4.10")));
    expectedSummaryFields.setGeneralCash(expectedSummaryFields.getGeneralCash().subtract(new BigDecimal("4.10")));

    assertEquals(expectedSummaryFields, actualSummaryFields);

    final Collection<RegisterTransaction> transactions = accountRegisterService
        .findAllRegisterTransactions(buyer.getCompany().getId());

    checkTransaction(transactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-4.10");
    checkTransaction(transactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-4.10");
  }


  @Test
  public void holdFunds_Success_FromDepositedCashAndWithdrawableCash() throws Exception {
    final User seller = newContractorIndependentlane4Ready();
    final User buyer = newFirstEmployeeWithCashBalance();
    final Work work = newWorkWithAutoPay(buyer.getId());

    workRoutingService.addToWorkResources(work.getId(), seller.getId());
    workService.acceptWork(seller.getId(), work.getId());
    workService.updateWorkProperties(work.getId(),
        CollectionUtilities.newStringMap("resolution", "Complete the assignment"));

    authenticationService.setCurrentUser(seller);
    workService.completeWork(work.getId(), new CompleteWorkDTO());

    authenticationService.setCurrentUser(buyer);
    workService.closeWork(work.getId());

    accountRegisterService.addFundsToRegisterFromWire(seller.getCompany().getId(), "100.00");

    final AccountRegisterSummaryFields expectedSummaryFields = accountRegisterService
        .getAccountRegisterSummaryFields(seller.getCompany().getId());

    final BigDecimal toTakeFromWithdrawableCash = expectedSummaryFields
        .getWithdrawableCash().divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    final BigDecimal amount1 = expectedSummaryFields.getDepositedCash().add(toTakeFromWithdrawableCash);
    final BigDecimal amount2 = new BigDecimal("1.00");
    final BigDecimal totalAmount = amount1.add(amount2);

    accountingService.holdFunds(ImmutableList.of(createOrder(seller, amount1.toPlainString(), "0.00", amount2.toPlainString(), "0.00")));

    final AccountRegisterSummaryFields actualSummaryFields = accountRegisterService
        .getAccountRegisterSummaryFields(seller.getCompany().getId());

    expectedSummaryFields.setAvailableCash(expectedSummaryFields.getAvailableCash().subtract(totalAmount));
    expectedSummaryFields.setDepositedCash(BigDecimal.valueOf(0, expectedSummaryFields.getDepositedCash().scale()));
    expectedSummaryFields.setWithdrawableCash(expectedSummaryFields.getWithdrawableCash().subtract(toTakeFromWithdrawableCash.add(amount2)));
    expectedSummaryFields.setPendingCommitments(expectedSummaryFields.getPendingCommitments().add(totalAmount));
    expectedSummaryFields.setGeneralCash(expectedSummaryFields.getGeneralCash().subtract(totalAmount));

    assertEquals(expectedSummaryFields, actualSummaryFields);

    final Collection<RegisterTransaction> transactions = accountRegisterService
        .findAllRegisterTransactions(seller.getCompany().getId());

    checkTransaction(transactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, totalAmount.negate().toPlainString());
    checkTransaction(transactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, totalAmount.negate().toPlainString());
  }


  @Test
  public void holdFunds_Success_IgnoreAlreadyOnHold() throws Exception {
    final User buyer = newEmployeeWithCashBalance("10.00");
    buyer.setSpendLimit(new BigDecimal("10.00"));
    userService.saveOrUpdateUser(buyer);

    final AccountRegisterSummaryFields expectedSummaryFields = accountRegisterService
        .getAccountRegisterSummaryFields(buyer.getCompany().getId());

    final Order order1 = createOrder(buyer, "3.00", "1.00", "1.00", "0.10");
    final Order order2 = createOrder(buyer, "2.00", "0.00", "1.00", "0.10");
    accountingService.holdFunds(ImmutableList.of(order1));
    accountingService.holdFunds(ImmutableList.of(order1, order2));

    final AccountRegisterSummaryFields actualSummaryFields = accountRegisterService
        .getAccountRegisterSummaryFields(buyer.getCompany().getId());

    expectedSummaryFields.setAvailableCash(expectedSummaryFields.getAvailableCash().subtract(new BigDecimal("8.20")));
    expectedSummaryFields.setDepositedCash(expectedSummaryFields.getDepositedCash().subtract(new BigDecimal("8.20")));
    expectedSummaryFields.setPendingCommitments(expectedSummaryFields.getPendingCommitments().add(new BigDecimal("8.20")));
    expectedSummaryFields.setGeneralCash(expectedSummaryFields.getGeneralCash().subtract(new BigDecimal("8.20")));

    assertEquals(expectedSummaryFields, actualSummaryFields);

    final Collection<RegisterTransaction> transactions = accountRegisterService
        .findAllRegisterTransactions(buyer.getCompany().getId());

    // hold order 1
    checkTransaction(transactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-5.10");
    checkTransaction(transactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-5.10");

    // hold order 2
    checkTransaction(transactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-3.10");
    checkTransaction(transactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-3.10");
  }


  @Test
  public void holdFunds_Success_IgnoreAlreadySettled() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);
    laneService.addUserToCompanyLane2(seller.getId(), buyer.getCompany().getId());

    final AccountRegisterSummaryFields buyerExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields sellerExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller.getCompany().getId());

    final Order order1 = createOrder(buyer, seller, "90.91", "9.09", "2.00", "0.20");
    final Order order2 = createOrder(buyer, seller, "5.00", "1.00", "3.00", "0.30");
    accountingService.holdFunds(ImmutableList.of(order1, order2));
    accountingService.settleHold(ImmutableList.of(order1));
    accountingService.holdFunds(ImmutableList.of(order1));

    final AccountRegisterSummaryFields buyerActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields sellerActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller.getCompany().getId());

    buyerExpectedSummaryFields.setAvailableCash(buyerExpectedSummaryFields.getAvailableCash().subtract(new BigDecimal("111.50")));
    buyerExpectedSummaryFields.setDepositedCash(buyerExpectedSummaryFields.getDepositedCash().subtract(new BigDecimal("111.50")));
    buyerExpectedSummaryFields.setPendingCommitments(buyerExpectedSummaryFields.getPendingCommitments().add(new BigDecimal("9.30")));
    buyerExpectedSummaryFields.setGeneralCash(buyerExpectedSummaryFields.getGeneralCash().subtract(new BigDecimal("111.50")));
    buyerExpectedSummaryFields.setActualCash(buyerExpectedSummaryFields.getActualCash().subtract(new BigDecimal("102.20")));
    buyerExpectedSummaryFields.setAssignmentThroughput(buyerExpectedSummaryFields.getAssignmentThroughput().add(new BigDecimal("92.91")));
    final BigDecimal swThroughputAmount = buyerExpectedSummaryFields.getAssignmentSoftwareThroughput().add(new BigDecimal("92.91"));
    buyerExpectedSummaryFields.setAssignmentSoftwareThroughput(swThroughputAmount);
    buyerExpectedSummaryFields.setAssignmentVorThroughput(swThroughputAmount);

    sellerExpectedSummaryFields.setAvailableCash(sellerExpectedSummaryFields.getActualCash().add(new BigDecimal("92.91")));
    sellerExpectedSummaryFields.setGeneralCash(sellerExpectedSummaryFields.getGeneralCash().add(new BigDecimal("92.91")));
    sellerExpectedSummaryFields.setWithdrawableCash(sellerExpectedSummaryFields.getWithdrawableCash().add(new BigDecimal("92.91")));
    sellerExpectedSummaryFields.setActualCash(sellerExpectedSummaryFields.getActualCash().add(new BigDecimal("92.91")));

    assertEquals(buyerExpectedSummaryFields, buyerActualSummaryFields);
    assertEquals(sellerExpectedSummaryFields, sellerActualSummaryFields);

    final Collection<RegisterTransaction> buyerTransactions = accountRegisterService.findAllRegisterTransactions(buyer.getCompany().getId());
    final Collection<RegisterTransaction> sellerTransactions = accountRegisterService.findAllRegisterTransactions(seller.getCompany().getId());

    // hold order 1
    checkTransaction(buyerTransactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-102.20");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-102.20");

    // hold order 2
    checkTransaction(buyerTransactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-9.30");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-9.30");

    // settle order 1, order item 1
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-9.09");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-90.91");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-100.00");
    checkTransaction(sellerTransactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "90.91");

    // settle order 1, order item 2
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-0.20");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-2.00");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-2.20");
    checkTransaction(sellerTransactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "2.00");
  }


  @Test
  public void settleHold_Error_NonMatchingAmount() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);
    final Order order = createOrder(buyer, seller, "100.00", "0.00", "1.00", "0.10");
    accountingService.holdFunds(ImmutableList.of(order));
    final Order changedOrder = order.toBuilder()
        .setOrderItem(0, OrderItem.newBuilder()
            .setAmount("101.00")
            .build())
        .build();
    try {
      accountingService.settleHold(ImmutableList.of(changedOrder));
      fail("Settling hold should fail for settle amount not matching hold amount");
    } catch (AccountingException ignored) { }
  }


  @Test
  public void settleHold_Error_WrongLaneType() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);
    laneService.addUserToCompanyLane1(seller.getId(), buyer.getCompany().getId());

    final Order order = createOrder(buyer, seller, "90.91", "9.09", "1.00", "0.10");
    accountingService.holdFunds(ImmutableList.of(order));
    try {
      accountingService.settleHold(ImmutableList.of(order));
      fail("Settling hold should fail for wrong lane type");
    } catch (AccountingException ignored) { }
  }


  @Test
  public void settleHold_Success_Lane2() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller1 = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);
    final User seller2 = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);
    laneService.addUserToCompanyLane2(seller1.getId(), buyer.getCompany().getId());
    laneService.addUserToCompanyLane2(seller2.getId(), buyer.getCompany().getId());

    final AccountRegisterSummaryFields buyerExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields seller1ExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller1.getCompany().getId());
    final AccountRegisterSummaryFields seller2ExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller2.getCompany().getId());

    final Order order1 = createOrder(buyer, seller1, "90.91", "9.09", "1.00", "0.10");
    final Order order2 = createOrder(buyer, seller2, "5.00", "1.00", "2.00", "0.20");
    final Order order3 = createOrder(buyer, seller1, "10.00", "2.00", "1.00", "0.10");
    accountingService.holdFunds(ImmutableList.of(order1, order2, order3));
    accountingService.settleHold(ImmutableList.of(order1, order2));

    final AccountRegisterSummaryFields buyerActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields seller1ActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller1.getCompany().getId());
    final AccountRegisterSummaryFields seller2ActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller2.getCompany().getId());

    buyerExpectedSummaryFields.setAvailableCash(buyerExpectedSummaryFields.getAvailableCash().subtract(new BigDecimal("122.40")));
    buyerExpectedSummaryFields.setDepositedCash(buyerExpectedSummaryFields.getDepositedCash().subtract(new BigDecimal("122.40")));
    buyerExpectedSummaryFields.setPendingCommitments(buyerExpectedSummaryFields.getPendingCommitments().add(new BigDecimal("13.10")));
    buyerExpectedSummaryFields.setGeneralCash(buyerExpectedSummaryFields.getGeneralCash().subtract(new BigDecimal("122.40")));
    buyerExpectedSummaryFields.setActualCash(buyerExpectedSummaryFields.getActualCash().subtract(new BigDecimal("109.30")));
    buyerExpectedSummaryFields.setAssignmentThroughput(buyerExpectedSummaryFields.getAssignmentThroughput().add(new BigDecimal("98.91")));
    final BigDecimal swThroughputAmount = buyerExpectedSummaryFields.getAssignmentSoftwareThroughput().add(new BigDecimal("98.91"));
    buyerExpectedSummaryFields.setAssignmentSoftwareThroughput(swThroughputAmount);
    buyerExpectedSummaryFields.setAssignmentVorThroughput(swThroughputAmount);

    seller1ExpectedSummaryFields.setAvailableCash(seller1ExpectedSummaryFields.getActualCash().add(new BigDecimal("91.91")));
    seller1ExpectedSummaryFields.setGeneralCash(seller1ExpectedSummaryFields.getGeneralCash().add(new BigDecimal("91.91")));
    seller1ExpectedSummaryFields.setWithdrawableCash(seller1ExpectedSummaryFields.getWithdrawableCash().add(new BigDecimal("91.91")));
    seller1ExpectedSummaryFields.setActualCash(seller1ExpectedSummaryFields.getActualCash().add(new BigDecimal("91.91")));

    seller2ExpectedSummaryFields.setAvailableCash(seller2ExpectedSummaryFields.getActualCash().add(new BigDecimal("7.00")));
    seller2ExpectedSummaryFields.setGeneralCash(seller2ExpectedSummaryFields.getGeneralCash().add(new BigDecimal("7.00")));
    seller2ExpectedSummaryFields.setWithdrawableCash(seller2ExpectedSummaryFields.getWithdrawableCash().add(new BigDecimal("7.00")));
    seller2ExpectedSummaryFields.setActualCash(seller2ExpectedSummaryFields.getActualCash().add(new BigDecimal("7.00")));

    assertEquals(buyerExpectedSummaryFields, buyerActualSummaryFields);
    assertEquals(seller1ExpectedSummaryFields, seller1ActualSummaryFields);
    assertEquals(seller2ExpectedSummaryFields, seller2ActualSummaryFields);

    final Collection<RegisterTransaction> buyerTransactions = accountRegisterService.findAllRegisterTransactions(buyer.getCompany().getId());
    final Collection<RegisterTransaction> seller1Transactions = accountRegisterService.findAllRegisterTransactions(seller1.getCompany().getId());
    final Collection<RegisterTransaction> seller2Transactions = accountRegisterService.findAllRegisterTransactions(seller2.getCompany().getId());

    // hold order 1
    checkTransaction(buyerTransactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-101.10");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-101.10");

    // hold order 2
    checkTransaction(buyerTransactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-8.20");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-8.20");

    // hold order 3
    checkTransaction(buyerTransactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-13.10");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-13.10");

    // settle order 1, order item 1
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-9.09");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-90.91");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-100.00");
    checkTransaction(seller1Transactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "90.91");

    // settle order 1, order item 2
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-0.10");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-1.00");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-1.10");
    checkTransaction(seller1Transactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "1.00");

    // settle order 2, order item 1
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-1.00");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-5.00");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-6.00");
    checkTransaction(seller2Transactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "5.00");

    // settle order 2, order item 2
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-0.20");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-2.00");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-2.20");
    checkTransaction(seller2Transactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "2.00");
  }


  @Test
  public void settleHold_Success_Lane3() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);
    laneService.addUserToCompanyLane3(seller.getId(), buyer.getCompany().getId());

    final AccountRegisterSummaryFields buyerExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields sellerExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller.getCompany().getId());

    final Order order = createOrder(buyer, seller, "90.91", "9.09", "1.00", "0.10");
    accountingService.holdFunds(ImmutableList.of(order));
    accountingService.settleHold(ImmutableList.of(order));

    final AccountRegisterSummaryFields buyerActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields sellerActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller.getCompany().getId());

    buyerExpectedSummaryFields.setAvailableCash(buyerExpectedSummaryFields.getAvailableCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setDepositedCash(buyerExpectedSummaryFields.getDepositedCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setGeneralCash(buyerExpectedSummaryFields.getGeneralCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setActualCash(buyerExpectedSummaryFields.getActualCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setAssignmentThroughput(buyerExpectedSummaryFields.getAssignmentThroughput().add(new BigDecimal("91.91")));
    final BigDecimal swThroughputAmount = buyerExpectedSummaryFields.getAssignmentSoftwareThroughput().add(new BigDecimal("91.91"));
    buyerExpectedSummaryFields.setAssignmentSoftwareThroughput(swThroughputAmount);
    buyerExpectedSummaryFields.setAssignmentVorThroughput(swThroughputAmount);

    sellerExpectedSummaryFields.setAvailableCash(sellerExpectedSummaryFields.getActualCash().add(new BigDecimal("91.91")));
    sellerExpectedSummaryFields.setGeneralCash(sellerExpectedSummaryFields.getGeneralCash().add(new BigDecimal("91.91")));
    sellerExpectedSummaryFields.setWithdrawableCash(sellerExpectedSummaryFields.getWithdrawableCash().add(new BigDecimal("91.91")));
    sellerExpectedSummaryFields.setActualCash(sellerExpectedSummaryFields.getActualCash().add(new BigDecimal("91.91")));

    assertEquals(buyerExpectedSummaryFields, buyerActualSummaryFields);
    assertEquals(sellerExpectedSummaryFields, sellerActualSummaryFields);

    final Collection<RegisterTransaction> buyerTransactions = accountRegisterService.findAllRegisterTransactions(buyer.getCompany().getId());
    final Collection<RegisterTransaction> sellerTransactions = accountRegisterService.findAllRegisterTransactions(seller.getCompany().getId());

    // hold order 1
    checkTransaction(buyerTransactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-101.10");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-101.10");

    // settle order 1, order item 1
    checkTransaction(buyerTransactions, BuyerWorkFee3Command.REGISTER_TRANSACTION_TYPE, "-9.09");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-90.91");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-100.00");
    checkTransaction(sellerTransactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "90.91");

    // settle order 1, order item 2
    checkTransaction(buyerTransactions, BuyerWorkFee3Command.REGISTER_TRANSACTION_TYPE, "-0.10");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-1.00");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-1.10");
    checkTransaction(sellerTransactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "1.00");
  }


  @Test
  public void settleHold_Success_IgnoreNotOnHold() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);
    laneService.addUserToCompanyLane2(seller.getId(), buyer.getCompany().getId());

    final AccountRegisterSummaryFields buyerExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields sellerExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller.getCompany().getId());

    final Order order1 = createOrder(buyer, seller, "90.91", "9.09", "1.00", "0.10");
    final Order order2 = createOrder(buyer, seller, "5.00", "1.00", "1.00", "0.10");
    accountingService.holdFunds(ImmutableList.of(order1));
    accountingService.settleHold(ImmutableList.of(order1, order2));

    final AccountRegisterSummaryFields buyerActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields sellerActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller.getCompany().getId());

    buyerExpectedSummaryFields.setAvailableCash(buyerExpectedSummaryFields.getAvailableCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setDepositedCash(buyerExpectedSummaryFields.getDepositedCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setGeneralCash(buyerExpectedSummaryFields.getGeneralCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setActualCash(buyerExpectedSummaryFields.getActualCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setAssignmentThroughput(buyerExpectedSummaryFields.getAssignmentThroughput().add(new BigDecimal("91.91")));
    final BigDecimal swThroughputAmount = buyerExpectedSummaryFields.getAssignmentSoftwareThroughput().add(new BigDecimal("91.91"));
    buyerExpectedSummaryFields.setAssignmentSoftwareThroughput(swThroughputAmount);
    buyerExpectedSummaryFields.setAssignmentVorThroughput(swThroughputAmount);

    sellerExpectedSummaryFields.setAvailableCash(sellerExpectedSummaryFields.getActualCash().add(new BigDecimal("91.91")));
    sellerExpectedSummaryFields.setGeneralCash(sellerExpectedSummaryFields.getGeneralCash().add(new BigDecimal("91.91")));
    sellerExpectedSummaryFields.setWithdrawableCash(sellerExpectedSummaryFields.getWithdrawableCash().add(new BigDecimal("91.91")));
    sellerExpectedSummaryFields.setActualCash(sellerExpectedSummaryFields.getActualCash().add(new BigDecimal("91.91")));

    assertEquals(buyerExpectedSummaryFields, buyerActualSummaryFields);
    assertEquals(sellerExpectedSummaryFields, sellerActualSummaryFields);

    final Collection<RegisterTransaction> buyerTransactions = accountRegisterService.findAllRegisterTransactions(buyer.getCompany().getId());
    final Collection<RegisterTransaction> sellerTransactions = accountRegisterService.findAllRegisterTransactions(seller.getCompany().getId());

    // hold order 1
    checkTransaction(buyerTransactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-101.10");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-101.10");

    // settle order 1, order item 1
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-9.09");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-90.91");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-100.00");
    checkTransaction(sellerTransactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "90.91");

    // settle order 1, order item 2
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-0.10");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-1.00");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-1.10");
    checkTransaction(sellerTransactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "1.00");
  }


  @Test
  public void settleHold_Success_IgnoreAlreadySettled() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);
    laneService.addUserToCompanyLane2(seller.getId(), buyer.getCompany().getId());

    final AccountRegisterSummaryFields buyerExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields sellerExpectedSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller.getCompany().getId());

    final Order order1 = createOrder(buyer, seller, "90.91", "9.09", "1.00", "0.10");
    final Order order2 = createOrder(buyer, seller, "5.00", "1.00", "1.00", "0.10");
    accountingService.holdFunds(ImmutableList.of(order1));
    accountingService.settleHold(ImmutableList.of(order1, order2));
    accountingService.settleHold(ImmutableList.of(order1));

    final AccountRegisterSummaryFields buyerActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(buyer.getCompany().getId());
    final AccountRegisterSummaryFields sellerActualSummaryFields = accountRegisterService.getAccountRegisterSummaryFields(seller.getCompany().getId());

    buyerExpectedSummaryFields.setAvailableCash(buyerExpectedSummaryFields.getAvailableCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setDepositedCash(buyerExpectedSummaryFields.getDepositedCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setGeneralCash(buyerExpectedSummaryFields.getGeneralCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setActualCash(buyerExpectedSummaryFields.getActualCash().subtract(new BigDecimal("101.10")));
    buyerExpectedSummaryFields.setAssignmentThroughput(buyerExpectedSummaryFields.getAssignmentThroughput().add(new BigDecimal("91.91")));
    final BigDecimal swThroughputAmount = buyerExpectedSummaryFields.getAssignmentSoftwareThroughput().add(new BigDecimal("91.91"));
    buyerExpectedSummaryFields.setAssignmentSoftwareThroughput(swThroughputAmount);
    buyerExpectedSummaryFields.setAssignmentVorThroughput(swThroughputAmount);

    sellerExpectedSummaryFields.setAvailableCash(sellerExpectedSummaryFields.getActualCash().add(new BigDecimal("91.91")));
    sellerExpectedSummaryFields.setGeneralCash(sellerExpectedSummaryFields.getGeneralCash().add(new BigDecimal("91.91")));
    sellerExpectedSummaryFields.setWithdrawableCash(sellerExpectedSummaryFields.getWithdrawableCash().add(new BigDecimal("91.91")));
    sellerExpectedSummaryFields.setActualCash(sellerExpectedSummaryFields.getActualCash().add(new BigDecimal("91.91")));

    assertEquals(buyerExpectedSummaryFields, buyerActualSummaryFields);
    assertEquals(sellerExpectedSummaryFields, sellerActualSummaryFields);

    final Collection<RegisterTransaction> buyerTransactions = accountRegisterService.findAllRegisterTransactions(buyer.getCompany().getId());
    final Collection<RegisterTransaction> sellerTransactions = accountRegisterService.findAllRegisterTransactions(seller.getCompany().getId());

    // hold order 1
    checkTransaction(buyerTransactions, BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE, "-101.10");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-101.10");

    // settle order 1, order item 1
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-9.09");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-90.91");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-100.00");
    checkTransaction(sellerTransactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "90.91");

    // settle order 1, order item 2
    checkTransaction(buyerTransactions, BuyerWorkFee2Command.REGISTER_TRANSACTION_TYPE, "-0.10");
    checkTransaction(buyerTransactions, BuyerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "-1.00");
    checkTransaction(buyerTransactions, RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE, "-1.10");
    checkTransaction(sellerTransactions, SellerWorkPaymentCommand.REGISTER_TRANSACTION_TYPE, "1.00");
  }


  @Test
  public void getFee_Success_Transactional() throws Exception {
    final User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();

    final Order order1 = createOrder(buyer, "90.91", "0.00", "1.00", "0.00");
    final Order order2 = createOrder(buyer, "60.00", "0.00", "1.00", "0.00");
    final ImmutableList<Order> orders = ImmutableList.of(order1, order2);

    final List<Order> expectedOrders = ImmutableList.of(
        order1.toBuilder()
            .setOrderItem(0, order1.getOrderItem(0).toBuilder().setFee("9.09"))
            .setOrderItem(1, order1.getOrderItem(1).toBuilder().setFee("0.10"))
            .build(),
        order2.toBuilder()
            .setOrderItem(0, order2.getOrderItem(0).toBuilder().setFee("6.00"))
            .setOrderItem(1, order2.getOrderItem(1).toBuilder().setFee("0.10"))
            .build());
    final List<Order> actualOrders = accountingService.getFee(orders);
    assertThat(actualOrders, is(expectedOrders));
  }


  @Test
  public void getFee_Success_Subscription() throws Exception {
    final User buyer = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();

    PaymentConfiguration paymentConfiguration = buyer.getCompany().getPaymentConfiguration();
    paymentConfiguration.setAccountPricingType(new AccountPricingType(AccountPricingType.SUBSCRIPTION_PRICING_TYPE));
    accountPricingService.updatePaymentConfigurationAccountServiceType(paymentConfiguration, new AccountServiceType(AccountServiceType.VENDOR_OF_RECORD));
    paymentConfigurationService.savePaymentConfiguration(paymentConfiguration);

    final Order order1 = createOrder(buyer, "90.91", "0.00", "1.00", "0.10");
    final Order order2 = createOrder(buyer, "60.00", "0.00", "1.00", "0.10");
    final ImmutableList<Order> orders = ImmutableList.of(order1, order2);

    final List<Order> expectedOrders = ImmutableList.of(
        order1.toBuilder()
            .setOrderItem(0, order1.getOrderItem(0).toBuilder().setFee("0.00"))
            .setOrderItem(1, order1.getOrderItem(1).toBuilder().setFee("0.00"))
            .build(),
        order2.toBuilder()
            .setOrderItem(0, order2.getOrderItem(0).toBuilder().setFee("0.00"))
            .setOrderItem(1, order2.getOrderItem(1).toBuilder().setFee("0.00"))
            .build());
    final List<Order> actualOrders = accountingService.getFee(orders);
    assertThat(actualOrders, is(expectedOrders));
  }


  @Test
  public void createInvoices_Success() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);

    final Invoice invoice = createInvoice(buyer, seller, "90.91", "9.09");

    accountingService.createInvoices(ImmutableList.of(invoice));

    final InvoicePagination invoicePagination = billingService.findAllInvoicesByCompany(buyer.getCompany().getId(), new InvoicePagination());
    checkInvoice(invoicePagination.getResults(), buyer, seller, "90.91", "9.09", false);
  }


  @Test
  public void createInvoices_Success_IgnoredAlreadyCreated() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);

    final Invoice invoice1 = createInvoice(buyer, seller, "90.91", "9.09");
    final Invoice invoice2 = createInvoice(buyer, seller, "5.00", "1.00");

    accountingService.createInvoices(ImmutableList.of(invoice1, invoice2));
    accountingService.createInvoices(ImmutableList.of(invoice1));

    final InvoicePagination invoicePagination = billingService.findAllInvoicesByCompany(buyer.getCompany().getId(), new InvoicePagination());
    checkInvoice(invoicePagination.getResults(), buyer, seller, "90.91", "9.09", false);
    checkInvoice(invoicePagination.getResults(), buyer, seller, "5.00", "1.00", false);
  }


  @Test
  public void payInvoices_Success() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);

    final Invoice invoice1 = createInvoice(buyer, seller, "90.91", "9.09");
    final Invoice invoice2 = createInvoice(buyer, seller, "5.00", "1.00");

    accountingService.createInvoices(ImmutableList.of(invoice1));
    accountingService.payInvoices(ImmutableList.of(invoice1, invoice2));

    final InvoicePagination invoicePagination = billingService.findAllInvoicesByCompany(buyer.getCompany().getId(), new InvoicePagination());
    checkInvoice(invoicePagination.getResults(), buyer, seller, "90.91", "9.09", true);
    checkInvoice(invoicePagination.getResults(), buyer, seller, "5.00", "1.00", false, 0);
  }


  @Test
  public void payInvoices_Success_IgnoreAlreadyPaid() throws Exception {
    final User buyer  = newFirstEmployeeWithCashBalanceAndPaymentTermsEnabled();
    final User seller = newContractorIndependentLane4ReadyWithCashBalanceWithTaxEntity(false, true);

    final Invoice invoice1 = createInvoice(buyer, seller, "90.91", "9.09");
    final Invoice invoice2 = createInvoice(buyer, seller, "5.00", "1.00");

    accountingService.createInvoices(ImmutableList.of(invoice1, invoice2));
    accountingService.payInvoices(ImmutableList.of(invoice1, invoice2));
    accountingService.payInvoices(ImmutableList.of(invoice1));

    final InvoicePagination invoicePagination = billingService.findAllInvoicesByCompany(buyer.getCompany().getId(), new InvoicePagination());
    checkInvoice(invoicePagination.getResults(), buyer, seller, "90.91", "9.09", true);
    checkInvoice(invoicePagination.getResults(), buyer, seller, "5.00", "1.00", true);
  }


  private static Order createOrder(
      final User buyer,
      final String amount1, final String fee1,
      final String amount2, final String fee2) {
    return createOrder(buyer, null, amount1, fee1, amount2, fee2);
  }


  private static Order createOrder(
      final User buyer, final User seller,
      final String amount1, final String fee1,
      final String amount2, final String fee2) {
    final Order.Builder orderBuilder = Order.newBuilder()
        .setUuid(UUID.randomUUID().toString())
        .setPayer(UserIdentity.newBuilder()
            .setUserUuid(buyer.getUuid())
            .setCompanyUuid(buyer.getCompany().getUuid())
            .build())
        .addOrderItem(OrderItem.newBuilder()
            .setAmount(amount1)
            .setFee(fee1)
            .build())
        .addOrderItem(OrderItem.newBuilder()
            .setAmount(amount2)
            .setFee(fee2)
            .build());
    if (seller != null) {
      orderBuilder.setPayee(UserIdentity.newBuilder()
          .setUserUuid(seller.getUuid())
          .setCompanyUuid(seller.getCompany().getUuid())
          .build());
    }
    return orderBuilder.build();
  }


  private static Invoice createInvoice(final User buyer, final User seller, final String amount, final String fee) {
    return Invoice.newBuilder()
        .setUuid(UUID.randomUUID().toString())
        .setPayer(UserIdentity.newBuilder()
            .setUserUuid(buyer.getUuid())
            .setCompanyUuid(buyer.getCompany().getUuid())
            .build())
        .setPayee(UserIdentity.newBuilder()
            .setUserUuid(seller.getUuid())
            .setCompanyUuid(seller.getCompany().getUuid())
            .build())
        .setScheduledDate(INVOICE_DUE_DATE)
        .addInvoiceItem(InvoiceItem.newBuilder()
            .setAmount(amount)
            .setFee(fee)
            .build())
        .build();
  }


  private static void checkTransaction(
      final Collection<RegisterTransaction> transactions,
      final RegisterTransactionType type,
      final String amount) {
    checkTransaction(transactions, type, amount, 1);
  }


  private static void checkTransaction(
      final Collection<RegisterTransaction> transactions,
      final RegisterTransactionType type,
      final String amount,
      final int times) {
    assertEquals(
        times,
        FluentIterable
            .from(transactions)
            .filter(new Predicate<RegisterTransaction>() {
              @Override
              public boolean apply(final RegisterTransaction rt) {
                return rt.getRegisterTransactionType().getCode()
                    .equals(type.getCode()) &&
                    rt.getAmount().compareTo(new BigDecimal(amount)) == 0;
              }
            })
            .size());
  }


  private void checkInvoice(
      final Collection<com.workmarket.domains.model.invoice.Invoice> invoices,
      final User buyer,
      final User seller,
      final String amount,
      final String fee,
      final boolean isPaid) {
    checkInvoice(invoices, buyer, seller, amount, fee, isPaid, 1);
  }


  private static Calendar getMidnight(final Calendar calendar) {
    return new LocalDate(calendar).toDateTimeAtStartOfDay().toGregorianCalendar();
  }


  private static Calendar getMidnight() {
    return new LocalDate().toDateTimeAtStartOfDay().toGregorianCalendar();
  }


  private static void checkInvoice(
      final Collection<com.workmarket.domains.model.invoice.Invoice> invoices,
      final User buyer,
      final User seller,
      final String amount,
      final String fee,
      final boolean isPaid,
      final int times) {
    assertEquals(
        times,
        FluentIterable
            .from(invoices)
            .filter(new Predicate<com.workmarket.domains.model.invoice.Invoice>() {
              @Override
              public boolean apply(final com.workmarket.domains.model.invoice.Invoice classicInvoice) {
                return classicInvoice.getCompany().getId().equals(buyer.getCompany().getId()) &&
                    classicInvoice.getActiveWorkResourceId().equals(seller.getId()) &&
                    classicInvoice.getBalance().compareTo(new BigDecimal(amount).add(new BigDecimal(fee))) == 0 &&
                    classicInvoice.getRemainingBalance().compareTo(isPaid ? BigDecimal.ZERO : new BigDecimal(amount).add(new BigDecimal(fee))) == 0 &&
                    classicInvoice.getWorkPrice().compareTo(new BigDecimal(amount)) == 0 &&
                    classicInvoice.getWorkBuyerFee().compareTo(new BigDecimal(fee)) == 0 &&
                    classicInvoice.getDueDate().equals(DateUtils.truncate(DateUtilities.getCalendarFromMillis(INVOICE_DUE_DATE), Calendar.SECOND)) &&
                    classicInvoice.getInvoiceStatusType().getCode().equals(isPaid ? InvoiceStatusType.PAID : InvoiceStatusType.PAYMENT_PENDING) &&
                    classicInvoice.getPaymentFulfillmentStatusType().getCode().equals(isPaid ? PaymentFulfillmentStatusType.FULFILLED : PaymentFulfillmentStatusType.PENDING_FULFILLMENT) &&
                    (!isPaid || getMidnight(classicInvoice.getPaymentDate()).equals(getMidnight()));
              }
            }).size());
  }
}

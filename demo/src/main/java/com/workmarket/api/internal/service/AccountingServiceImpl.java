package com.workmarket.api.internal.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import com.workmarket.api.internal.dao.PlutusInvoiceInvoiceDAO;
import com.workmarket.api.internal.dao.PlutusOrderRegisterTransactionDAO;
import com.workmarket.api.internal.model.PlutusInvoiceInvoice;
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
import com.workmarket.biz.plutus.gen.Messages.Order.Builder;
import com.workmarket.biz.plutus.gen.Messages.OrderItem;
import com.workmarket.biz.plutus.gen.Messages.UserIdentity;
import com.workmarket.common.api.exception.BadRequest;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.model.account.GeneralTransaction;
import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.invoice.InvoiceStatusType;
import com.workmarket.domains.model.invoice.PaymentFulfillmentStatusType;
import com.workmarket.domains.model.lane.LaneType;
import com.workmarket.domains.payments.dao.AccountRegisterDAO;
import com.workmarket.domains.payments.dao.InvoiceDAO;
import com.workmarket.service.business.LaneService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.NumberUtilities;
import com.workmarket.utility.RandomUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
public class AccountingServiceImpl implements AccountingService {
  @Autowired private UserDAO userDAO;
  @Autowired private CompanyDAO companyDAO;
  @Autowired private AccountRegisterDAO accountRegisterDAO;
  @Autowired private InvoiceDAO invoiceDAO;
  @Autowired private PlutusOrderRegisterTransactionDAO plutusOrderRegisterTransactionDAO;
  @Autowired private PlutusInvoiceInvoiceDAO plutusInvoiceInvoiceDAO;

  @Autowired private LaneService laneService;
  @Autowired private PricingService pricingService;
  @Autowired private AuthenticationService authenticationService;

  @Autowired private BuyerCommitmentCommand buyerCommitmentCommand;
  @Autowired private BuyerWorkFee2Command buyerWorkFee2Command;
  @Autowired private BuyerWorkFee3Command buyerWorkFee3Command;
  @Autowired private BuyerWorkPaymentCommand buyerWorkPaymentCommand;
  @Autowired private RemoveFromGeneralCommand removeFromGeneralCommand;
  @Autowired private SellerWorkPaymentCommand sellerWorkPaymentCommand;

  private static final Log logger = LogFactory.getLog(AccountingServiceImpl.class);

  @Override
  public void holdFunds(final Collection<Order> orders) {
    for (final Order order : orders) {
      checkOrder(order, OrderCheck.HOLD);

      final AccountRegister buyerAccountRegister = getAccountRegister(order.getPayer());

      final Collection<RegisterTransaction> onHoldTransactions = plutusOrderRegisterTransactionDAO.findAllRegisterTransactionsByPlutusOrderUuid(order.getUuid());
      final boolean isOnHoldOrSettled = FluentIterable
          .from(onHoldTransactions)
          .anyMatch(new Predicate<RegisterTransaction>() {
            @Override
            public boolean apply(final RegisterTransaction registerTransaction) {
              return registerTransaction.getRegisterTransactionType().getCode().equals(BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE.getCode());
            }
          });

      if (!isOnHoldOrSettled) {
        doHoldFunds(buyerAccountRegister, order);
      }
    }
  }


  private void doHoldFunds(final AccountRegister accountRegister, final Order order) {
    logger.info(String.format("Holding funds for Plutus order [%s]", order.getUuid()));

    final BigDecimal totalAmount = getTotalAmount(order);

    final User buyer = getUser(order.getPayer());
    if (buyer.getSpendLimit().compareTo(totalAmount) < 0) {
      throw new AccountingException(String.format("Insufficient user spend limit [%.02f]", buyer.getSpendLimit()));
    }
    if (accountRegister.getAccountRegisterSummaryFields().getAvailableCash().compareTo(totalAmount) < 0) {
      throw new AccountingException(String.format("Insufficient available cash [%.02f]", accountRegister.getAvailableCash()));
    }
    if (accountRegister.getAccountRegisterSummaryFields().getGeneralCash().compareTo(totalAmount) < 0) {
      throw new AccountingException(String.format("Insufficient general cash [%.02f]", accountRegister.getAccountRegisterSummaryFields().getGeneralCash()));
    }

    final RegisterTransaction buyerCommitmentTransaction = new RegisterTransaction();
    buyerCommitmentTransaction.setPendingFlag(true);
    buyerCommitmentTransaction.setAmount(totalAmount.negate());
    buyerCommitmentCommand.execute(accountRegister, buyerCommitmentTransaction, order.getUuid());

    final GeneralTransaction removeFromGeneralTransaction = new GeneralTransaction();
    removeFromGeneralTransaction.setPendingFlag(true);
    removeFromGeneralTransaction.setAmount(totalAmount.negate());
    removeFromGeneralTransaction.setParentTransaction(buyerCommitmentTransaction);
    removeFromGeneralCommand.execute(accountRegister, removeFromGeneralTransaction, order.getUuid());
  }


  @Override
  public void settleHold(final Collection<Order> orders) throws AccountingException {
    for (final Order order : orders) {
      checkOrder(order, OrderCheck.SETTLE);

      final AccountRegister buyerAccountRegister = getAccountRegister(order.getPayer());
      final AccountRegister sellerAccountRegister = getAccountRegister(order.getPayee());

      final Collection<RegisterTransaction> onHoldTransactions = plutusOrderRegisterTransactionDAO.findAllRegisterTransactionsByPlutusOrderUuid(order.getUuid());
      final boolean isOnHold = FluentIterable
          .from(onHoldTransactions)
          .anyMatch(new Predicate<RegisterTransaction>() {
            @Override
            public boolean apply(final RegisterTransaction registerTransaction) {
              return registerTransaction.getRegisterTransactionType().getCode().equals(BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE.getCode()) &&
                  registerTransaction.isPending();
            }
          });

      if (isOnHold) {
        doSettleHold(buyerAccountRegister, sellerAccountRegister, order, onHoldTransactions);
      }
    }
  }


  private void doSettleHold(
      final AccountRegister buyerAccountRegister,
      final AccountRegister sellerAccountRegister,
      final Order order,
      final Collection<RegisterTransaction> onHoldTransactions) {
    logger.info(String.format("Settling hold for Plutus order [%s]", order.getUuid()));

    BigDecimal holdAmount = BigDecimal.ZERO;
    for (final RegisterTransaction rt : onHoldTransactions) {
      if (rt.getRegisterTransactionType().getCode().equals(BuyerCommitmentCommand.REGISTER_TRANSACTION_TYPE.getCode())) {
        holdAmount = NumberUtilities.roundMoney(rt.getAmount()).negate();
        buyerCommitmentCommand.reverse(buyerAccountRegister, rt);
      }
      else if (rt.getRegisterTransactionType().getCode().equals(RemoveFromGeneralCommand.REGISTER_TRANSACTION_TYPE.getCode())) {
        removeFromGeneralCommand.reverse(buyerAccountRegister, rt);
      }
    }

    final BigDecimal settleAmount = getTotalAmount(order);
    if (holdAmount.compareTo(settleAmount) != 0) {
      throw new AccountingException(String.format("Settle amount [%s] does not equal to hold amount [%s]", settleAmount, holdAmount));
    }

    final User buyer = getUser(order.getPayer());
    final User seller = getUser(order.getPayee());
    final LaneType laneType = laneService.getLaneTypeForUserAndCompany(seller.getId(), buyer.getCompany().getId());

    for (final OrderItem orderItem : order.getOrderItemList()) {
      final BigDecimal price = getPrice(orderItem);
      final BigDecimal fee = getFee(orderItem);

      final RegisterTransaction buyerWorkPaymentTransaction = new RegisterTransaction();
      buyerWorkPaymentTransaction.setPendingFlag(false);
      buyerWorkPaymentTransaction.setAmount(price.negate());
      buyerWorkPaymentCommand.execute(buyerAccountRegister, buyerWorkPaymentTransaction, order.getUuid());

      final RegisterTransaction buyerWorkFeeTransaction = new RegisterTransaction();
      buyerWorkFeeTransaction.setPendingFlag(false);
      buyerWorkFeeTransaction.setAmount(fee.negate());
      switch (laneType) {
        case LANE_2:
          buyerWorkFee2Command.execute(buyerAccountRegister, buyerWorkFeeTransaction, order.getUuid());
          break;
        case LANE_3:
        case LANE_4:
          buyerWorkFee3Command.execute(buyerAccountRegister, buyerWorkFeeTransaction, order.getUuid());
          break;
        default:
          throw new AccountingException("Payee of lane type 2, 3, or 4 expected");
      }

      final RegisterTransaction buyerRemoveFromGeneralTransaction = new RegisterTransaction();
      buyerRemoveFromGeneralTransaction.setPendingFlag(false);
      buyerRemoveFromGeneralTransaction.setAmount(NumberUtilities.roundMoney(price.add(fee)).negate());
      removeFromGeneralCommand.execute(buyerAccountRegister, buyerRemoveFromGeneralTransaction, order.getUuid());

      final RegisterTransaction sellerWorkPaymentTransaction = new RegisterTransaction();
      sellerWorkPaymentTransaction.setPendingFlag(false);
      sellerWorkPaymentTransaction.setAmount(price);
      sellerWorkPaymentCommand.execute(sellerAccountRegister, sellerWorkPaymentTransaction, order.getUuid());
    }
  }


  @Override
  public List<Order> getFee(final List<Order> orders) {
    return FluentIterable
        .from(orders)
        .transform(new Function<Order, Order>() {
          @Override
          public Order apply(final Order order) {
            checkOrder(order, OrderCheck.FEE);
            return doGetFee(order);
          }
        })
        .toList();
  }


  private Order doGetFee(final Order order) {
    logger.info(String.format("Getting fee for Plutus order [%s]", order.getUuid()));

    final User buyer = getUser(order.getPayer());
    final Company buyerCompany = buyer.getCompany();

    final Builder orderBuilder = order.toBuilder();
    for (int i = 0; i < order.getOrderItemCount(); ++i) {
      final OrderItem orderItem = order.getOrderItem(i);
      orderBuilder
          .setOrderItem(i, orderItem.toBuilder()
              .setFee(NumberUtilities.roundMoney(pricingService.calculateBuyerNetMoneyFee(buyerCompany, new BigDecimal(orderItem.getAmount()))).toPlainString())
              .build());
    }

    return orderBuilder.build();
  }


  @Override
  public void createInvoices(final Collection<Invoice> invoices) {
    for (final Invoice invoice : invoices) {
      checkInvoice(invoice, InvoiceCheck.CREATE);

      final Collection<com.workmarket.domains.model.invoice.Invoice> prevInvoices =
          plutusInvoiceInvoiceDAO.findAllInvoicesByPlutusInvoiceUuid(invoice.getUuid());
      if (prevInvoices.isEmpty()) {
        doCreateInvoice(invoice);
      }
    }
  }


  private void doCreateInvoice(final Invoice invoice) {
    logger.info(String.format("Creating classic invoice for Plutus invoice [%s]", invoice.getUuid()));

    final User buyer = getUser(invoice.getPayer());
    final User seller = getUser(invoice.getPayee());

    final BigDecimal invoiceBalance = getTotalAmount(invoice);

    com.workmarket.domains.model.invoice.Invoice classicInvoice = new com.workmarket.domains.model.invoice.Invoice();
    classicInvoice.setCompany(buyer.getCompany());
    classicInvoice.setBalance(invoiceBalance);
    classicInvoice.setRemainingBalance(invoiceBalance);
    classicInvoice.setDueDate(DateUtilities.getCalendarFromMillis(invoice.getScheduledDate()));
    classicInvoice.setActiveWorkResourceId(seller.getId());
    classicInvoice.setWorkPrice(getPrice(invoice));
    classicInvoice.setWorkBuyerFee(getFee(invoice));
    classicInvoice.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAYMENT_PENDING));
    classicInvoice.setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.PENDING_FULFILLMENT));

    int randomNumber = RandomUtilities.nextIntInRangeWithSeed(1, 1000000, System.currentTimeMillis());
    String invoiceNumber = String.format("%05d", randomNumber);
    classicInvoice.setInvoiceNumber(invoiceNumber);
    classicInvoice.setDescription("Invoice #" + invoiceNumber);
    invoiceDAO.saveOrUpdate(classicInvoice);

    Long invoiceId = classicInvoice.getId();
    invoiceNumber = String.format("%s-%05d", buyer.getCompany().getCompanyNumber(), invoiceId);
    classicInvoice.setInvoiceNumber(invoiceNumber);
    classicInvoice.setDescription("Invoice #" + invoiceNumber);
    invoiceDAO.saveOrUpdate(classicInvoice);

    plutusInvoiceInvoiceDAO.saveOrUpdate(new PlutusInvoiceInvoice(invoice.getUuid(), classicInvoice));
  }


  @Override
  public void payInvoices(final Collection<Invoice> invoices) {
    for (final Invoice invoice : invoices) {
      checkInvoice(invoice, InvoiceCheck.PAY);

      final Collection<com.workmarket.domains.model.invoice.Invoice> prevInvoices =
          plutusInvoiceInvoiceDAO.findAllInvoicesByPlutusInvoiceUuid(invoice.getUuid());
      for (final com.workmarket.domains.model.invoice.Invoice classicInvoice : prevInvoices) {
        if (!classicInvoice.isPaid()) {
          doPayInvoice(classicInvoice, invoice);
        }
      }
    }
  }


  private void doPayInvoice(final com.workmarket.domains.model.invoice.Invoice classicInvoice, final Invoice invoice) {
    logger.info(String.format("Paying classic invoice for Plutus invoice [%s]", invoice.getUuid()));

    classicInvoice.setInvoiceStatusType(new InvoiceStatusType(InvoiceStatusType.PAID));
    classicInvoice.setPaymentFulfillmentStatusType(new PaymentFulfillmentStatusType(PaymentFulfillmentStatusType.FULFILLED));
    classicInvoice.setRemainingBalance(BigDecimal.ZERO);
    classicInvoice.setPaymentDate(DateUtilities.getCalendarNow());
    classicInvoice.setPaidBy(authenticationService.getCurrentUser());

    invoiceDAO.saveOrUpdate(classicInvoice);
  }


  private enum OrderCheck {
    HOLD,
    SETTLE,
    FEE
  }


  private static void checkOrder(final Order order, final OrderCheck check) {
    if (StringUtils.isBlank(order.getUuid())) {
      throw new BadRequest("Order UUID must be provided");
    }

    for (final OrderItem orderItem : order.getOrderItemList()) {
      try {
        if (new BigDecimal(orderItem.getAmount()).signum() <= 0) {
          throw new NumberFormatException();
        }
      } catch (NumberFormatException e) {
        throw new BadRequest("Positive OrderItem amount must be provided");
      }
      if (StringUtils.isNotBlank(orderItem.getFee())) {
        try {
          if (new BigDecimal(orderItem.getFee()).signum() < 0) {
            throw new NumberFormatException();
          }
        } catch (NumberFormatException e) {
          throw new BadRequest("Positive OrderItem fee must be provided");
        }
      }
    }

    final UserIdentity payer = order.getPayer();
    if (payer == null) {
      throw new BadRequest("Payer identity must be provided");
    }
    if (StringUtils.isBlank(payer.getUserUuid())) {
      throw new BadRequest("Payer UUID must be provided");
    }
    if (StringUtils.isBlank(payer.getCompanyUuid())) {
      throw new BadRequest("Payer company UUID must be provided");
    }

    if (check == OrderCheck.SETTLE) {
      final UserIdentity payee = order.getPayee();
      if (payee == null) {
        throw new BadRequest("Payee identity must be provided");
      }
      if (StringUtils.isBlank(payee.getUserUuid())) {
        throw new BadRequest("Payee UUID must be provided");
      }
      if (StringUtils.isBlank(payee.getCompanyUuid())) {
        throw new BadRequest("Payee company UUID must be provided");
      }
    }
  }


  private enum InvoiceCheck {
    CREATE,
    PAY
  }


  private static void checkInvoice(final Invoice invoice, InvoiceCheck check) {
    if (StringUtils.isBlank(invoice.getUuid())) {
      throw new BadRequest("Invoice UUID must be provided");
    }

    if (check == InvoiceCheck.CREATE) {
      for (final InvoiceItem invoiceItem : invoice.getInvoiceItemList()) {
        try {
          if (new BigDecimal(invoiceItem.getAmount()).signum() <= 0) {
            throw new NumberFormatException();
          }
        } catch (NumberFormatException e) {
          throw new BadRequest("Positive InvoiceItem amount must be provided");
        }
        if (StringUtils.isNotBlank(invoiceItem.getFee())) {
          try {
            if (new BigDecimal(invoiceItem.getFee()).signum() < 0) {
              throw new NumberFormatException();
            }
          } catch (NumberFormatException e) {
            throw new BadRequest("Positive InvoiceItem fee must be provided");
          }
        }
      }

      final UserIdentity payer = invoice.getPayer();
      if (payer == null) {
        throw new BadRequest("Payer identity must be provided");
      }
      if (StringUtils.isBlank(payer.getUserUuid())) {
        throw new BadRequest("Payer UUID must be provided");
      }
      if (StringUtils.isBlank(payer.getCompanyUuid())) {
        throw new BadRequest("Payer company UUID must be provided");
      }

      final UserIdentity payee = invoice.getPayee();
      if (payee == null) {
        throw new BadRequest("Payee identity must be provided");
      }
      if (StringUtils.isBlank(payee.getUserUuid())) {
        throw new BadRequest("Payee UUID must be provided");
      }
      if (StringUtils.isBlank(payee.getCompanyUuid())) {
        throw new BadRequest("Payee company UUID must be provided");
      }

      if (invoice.getScheduledDate() == 0) {
        throw new BadRequest("Scheduled date must be provided");
      }
    }
  }


  private AccountRegister getAccountRegister(final UserIdentity identity) {
    final Long companyId = companyDAO.findByUuid(identity.getCompanyUuid()).getId();
    if (companyId == null) {
      throw new AccountingException(String.format("Invalid company UUID [%s]", identity.getCompanyUuid()));
    }
    return accountRegisterDAO.findByCompanyId(companyId, true);
  }


  private User getUser(final UserIdentity identity) {
    final User user = userDAO.findByUuid(identity.getUserUuid());
    if (user == null) {
      throw new AccountingException(String.format("Invalid user UUID [%s]", identity.getUserUuid()));
    }
    return user;
  }


  private static BigDecimal getPrice(final OrderItem orderItem) {
    return NumberUtilities.roundMoney(new BigDecimal(orderItem.getAmount()));
  }


  private static BigDecimal getPrice(final InvoiceItem invoiceItem) {
    return NumberUtilities.roundMoney(new BigDecimal(invoiceItem.getAmount()));
  }


  private static BigDecimal getFee(final OrderItem orderItem) {
    return NumberUtilities.roundMoney(new BigDecimal(StringUtils.defaultIfBlank(orderItem.getFee(), "0.00")));
  }


  private static BigDecimal getFee(final InvoiceItem invoiceItem) {
    return NumberUtilities.roundMoney(new BigDecimal(StringUtils.defaultIfBlank(invoiceItem.getFee(), "0.00")));
  }


  private static BigDecimal getTotalAmount(final Order order) {
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (final OrderItem orderItem : order.getOrderItemList()) {
      final BigDecimal price = getPrice(orderItem);
      final BigDecimal fee = getFee(orderItem);
      totalAmount = NumberUtilities.roundMoney(totalAmount.add(NumberUtilities.roundMoney(price.add(fee))));
    }
    return totalAmount;
  }


  private static BigDecimal getTotalAmount(final Invoice invoice) {
    BigDecimal totalAmount = BigDecimal.ZERO;
    for (final InvoiceItem invoiceItem : invoice.getInvoiceItemList()) {
      final BigDecimal price = getPrice(invoiceItem);
      final BigDecimal fee = getFee(invoiceItem);
      totalAmount = NumberUtilities.roundMoney(totalAmount.add(NumberUtilities.roundMoney(price.add(fee))));
    }
    return totalAmount;
  }


  private static BigDecimal getPrice(final Invoice invoice) {
    BigDecimal price = BigDecimal.ZERO;
    for (final InvoiceItem invoiceItem : invoice.getInvoiceItemList()) {
      price = NumberUtilities.roundMoney(price.add(getPrice(invoiceItem)));
    }
    return price;
  }


  private static BigDecimal getFee(final Invoice invoice) {
    BigDecimal fee = BigDecimal.ZERO;
    for (final InvoiceItem invoiceItem : invoice.getInvoiceItemList()) {
      fee = NumberUtilities.roundMoney(fee.add(getFee(invoiceItem)));
    }
    return fee;
  }
}

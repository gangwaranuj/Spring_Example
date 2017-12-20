package com.workmarket.api.internal.service;

import com.workmarket.api.internal.service.accounting.AccountingException;
import com.workmarket.biz.plutus.gen.Messages.Invoice;
import com.workmarket.biz.plutus.gen.Messages.Order;

import java.util.Collection;
import java.util.List;

public interface AccountingService {
  /**
   * Commit to pay (i.e. hold funds) for the total amount in the given orders.
   * @throws AccountingException if there are not enough funds
   */
  void holdFunds(final Collection<Order> orders) throws AccountingException;


  /**
   * Pay all orders previously committed via {@link AccountingService#holdFunds(java.util.Collection)}.
   */
  void settleHold(final Collection<Order> orders) throws AccountingException;


  /**
   * Populate the fee for all OrderItems of each Order.
   */
  List<Order> getFee(List<Order> orders);

  /**
   * Create a classic invoice for each given Plutus invoice.
   */
  void createInvoices(final Collection<Invoice> invoices);


  /**
   * Mark all classic invoices created via {@link AccountingService#createInvoices(java.util.Collection)} as paid.
   */
  void payInvoices(final Collection<Invoice> invoices);
}

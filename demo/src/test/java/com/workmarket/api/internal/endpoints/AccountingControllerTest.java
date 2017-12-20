package com.workmarket.api.internal.endpoints;

import com.google.common.collect.ImmutableList;

import com.workmarket.api.internal.accounting.gen.Messages.CreateInvoicesRequest;
import com.workmarket.api.internal.accounting.gen.Messages.CreateInvoicesResponse;
import com.workmarket.api.internal.accounting.gen.Messages.GetFeeRequest;
import com.workmarket.api.internal.accounting.gen.Messages.GetFeeResponse;
import com.workmarket.api.internal.accounting.gen.Messages.HoldFundsRequest;
import com.workmarket.api.internal.accounting.gen.Messages.HoldFundsResponse;
import com.workmarket.api.internal.accounting.gen.Messages.PayInvoicesRequest;
import com.workmarket.api.internal.accounting.gen.Messages.PayInvoicesResponse;
import com.workmarket.api.internal.accounting.gen.Messages.SettleHoldRequest;
import com.workmarket.api.internal.accounting.gen.Messages.SettleHoldResponse;
import com.workmarket.api.internal.service.AccountingService;
import com.workmarket.biz.plutus.gen.Messages.Invoice;
import com.workmarket.biz.plutus.gen.Messages.InvoiceItem;
import com.workmarket.biz.plutus.gen.Messages.Order;
import com.workmarket.biz.plutus.gen.Messages.OrderItem;
import com.workmarket.biz.plutus.gen.Messages.UserIdentity;
import com.workmarket.common.api.vo.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccountingControllerTest {
  private static final String BUYER_COMPANY_UUID  = "53d39811-d67c-4c83-b1cf-3fc4e6b9c02e";
  private static final String SELLER_COMPANY_UUID = "ad2d5595-83fe-11e6-aa29-1245ac8288e9";
  private static final String BUYER_USER_UUID     = "ae410403-d482-9fe1-d41c-82bac313fdf1";
  private static final String SELLER_USER_UUID    = "6d3e3215-dd7d-4907-9b59-209aca8b054e";
  private static final String ORDER_UUID          = "d4ed805f-565b-447e-858b-a1543321d839";
  private static final String INVOICE_UUID        = "ec4f1949-69e2-4b1a-b177-d60e7e0cdca7";

  @Mock private AccountingService accountingService;

  @InjectMocks private AccountingController accountingController;

  @Test
  public void holdFunds() throws Exception {
    final Order order = Order.newBuilder()
        .setUuid(ORDER_UUID)
        .setPayer(UserIdentity.newBuilder()
            .setUserUuid(BUYER_USER_UUID)
            .setCompanyUuid(BUYER_COMPANY_UUID)
            .build())
        .addOrderItem(OrderItem.newBuilder()
            .setAmount("300")
            .build())
        .build();
    final HoldFundsRequest holdFundsRequest = HoldFundsRequest.newBuilder()
        .addOrder(order)
        .build();
    final Response<HoldFundsResponse> response = accountingController.holdFunds(holdFundsRequest);
    verify(accountingService).holdFunds(eq(ImmutableList.of(order)));
    assertTrue(response.getResults().get(0).getStatus().getSuccess());
  }


  @Test
  public void settleHold() throws Exception {
    final Order order = Order.newBuilder()
        .setUuid(ORDER_UUID)
        .setPayer(UserIdentity.newBuilder()
            .setUserUuid(BUYER_USER_UUID)
            .setCompanyUuid(BUYER_COMPANY_UUID)
            .build())
        .setPayee(UserIdentity.newBuilder()
            .setUserUuid(SELLER_USER_UUID)
            .setCompanyUuid(SELLER_COMPANY_UUID)
            .build())
        .addOrderItem(OrderItem.newBuilder()
            .setAmount("300")
            .build())
        .build();
    final SettleHoldRequest settleHoldRequest = SettleHoldRequest.newBuilder()
        .addOrder(order)
        .build();
    final Response<SettleHoldResponse> response = accountingController.settleHold(settleHoldRequest);
    verify(accountingService).settleHold(eq(ImmutableList.of(order)));
    assertTrue(response.getResults().get(0).getStatus().getSuccess());
  }


  @Test
  public void getFee() throws Exception {
    final Order order = Order.newBuilder()
        .setUuid(ORDER_UUID)
        .setPayer(UserIdentity.newBuilder()
            .setUserUuid(BUYER_USER_UUID)
            .setCompanyUuid(BUYER_COMPANY_UUID)
            .build())
        .setPayee(UserIdentity.newBuilder()
            .setUserUuid(SELLER_USER_UUID)
            .setCompanyUuid(SELLER_COMPANY_UUID)
            .build())
        .addOrderItem(OrderItem.newBuilder()
            .setAmount("300")
            .build())
        .build();
    final GetFeeRequest getFeeRequest = GetFeeRequest.newBuilder()
        .addOrder(order)
        .build();
    final Response<GetFeeResponse> response = accountingController.getFee(getFeeRequest);
    verify(accountingService).getFee(eq(ImmutableList.of(order)));
    assertTrue(response.getResults().get(0).getStatus().getSuccess());
  }


  @Test
  public void createInvoices() throws Exception {
    final Invoice invoice = Invoice.newBuilder()
        .setUuid(INVOICE_UUID)
        .setPayer(UserIdentity.newBuilder()
            .setUserUuid(BUYER_USER_UUID)
            .setCompanyUuid(BUYER_COMPANY_UUID)
            .build())
        .setPayee(UserIdentity.newBuilder()
            .setUserUuid(SELLER_USER_UUID)
            .setCompanyUuid(SELLER_COMPANY_UUID)
            .build())
        .setScheduledDate(System.currentTimeMillis())
        .addInvoiceItem(InvoiceItem.newBuilder()
            .setAmount("300")
            .build())
        .build();
    final CreateInvoicesRequest createInvoicesRequest = CreateInvoicesRequest.newBuilder()
        .addInvoice(invoice)
        .build();
    final Response<CreateInvoicesResponse> response = accountingController.createInvoices(createInvoicesRequest);
    verify(accountingService).createInvoices(eq(ImmutableList.of(invoice)));
    assertTrue(response.getResults().get(0).getStatus().getSuccess());
  }


  @Test
  public void payInvoices() throws Exception {
    final Invoice invoice = Invoice.newBuilder()
        .setUuid(INVOICE_UUID)
        .build();
    final PayInvoicesRequest payInvoicesRequest = PayInvoicesRequest.newBuilder()
        .addInvoice(invoice)
        .build();
    final Response<PayInvoicesResponse> response = accountingController.payInvoices(payInvoicesRequest);
    verify(accountingService).payInvoices(eq(ImmutableList.of(invoice)));
    assertTrue(response.getResults().get(0).getStatus().getSuccess());
  }
}

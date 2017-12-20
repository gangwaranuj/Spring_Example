package com.workmarket.api.internal.endpoints;

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
import com.workmarket.api.internal.accounting.gen.Messages.Status;
import com.workmarket.api.internal.service.AccountingService;
import com.workmarket.api.internal.service.accounting.AccountingException;
import com.workmarket.common.api.vo.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping
public class AccountingController extends MicroserviceBaseController {
  @Autowired AccountingService accountingService;

  @RequestMapping(
      value = "/v2/internal/accounting/hold-funds/",
      method = POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  Response<HoldFundsResponse> holdFunds(@RequestBody final HoldFundsRequest request) {
    final HoldFundsResponse.Builder responseBuilder = HoldFundsResponse.newBuilder()
        .setStatus(Status.newBuilder()
            .setSuccess(true)
            .build());
    try {
      accountingService.holdFunds(request.getOrderList());
    }
    catch (AccountingException e) {
      responseBuilder.setStatus(Status.newBuilder()
          .setSuccess(false)
          .addMessage(e.getMessage())
          .build());
    }
    return Response.valueWithResult(responseBuilder.build());
  }


  @RequestMapping(
      value = "/v2/internal/accounting/settle-hold/",
      method = POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  Response<SettleHoldResponse> settleHold(@RequestBody final SettleHoldRequest request) {
    final SettleHoldResponse.Builder responseBuilder = SettleHoldResponse.newBuilder()
        .setStatus(Status.newBuilder()
            .setSuccess(true)
            .build());
    try {
      accountingService.settleHold(request.getOrderList());
    }
    catch (AccountingException e) {
      responseBuilder.setStatus(Status.newBuilder()
          .setSuccess(false)
          .addMessage(e.getMessage())
          .build());
    }
    return Response.valueWithResult(responseBuilder.build());
  }


  @RequestMapping(
      value = "/v2/internal/accounting/get-fee/",
      method = POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  Response<GetFeeResponse> getFee(@RequestBody final GetFeeRequest request) {
    final GetFeeResponse.Builder responseBuilder = GetFeeResponse.newBuilder()
        .setStatus(Status.newBuilder()
            .setSuccess(true)
            .build());
    try {
      responseBuilder.addAllOrder(accountingService.getFee(request.getOrderList()));
    }
    catch (AccountingException e) {
      responseBuilder.setStatus(Status.newBuilder()
          .setSuccess(false)
          .addMessage(e.getMessage())
          .build());
    }
    return Response.valueWithResult(responseBuilder.build());
  }


  @RequestMapping(
      value = "/v2/internal/accounting/create-invoices/",
      method = POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  Response<CreateInvoicesResponse> createInvoices(@RequestBody final CreateInvoicesRequest request) {
    final CreateInvoicesResponse.Builder responseBuilder = CreateInvoicesResponse.newBuilder()
        .setStatus(Status.newBuilder()
            .setSuccess(true)
            .build());
    try {
      accountingService.createInvoices(request.getInvoiceList());
    }
    catch (AccountingException e) {
      responseBuilder.setStatus(Status.newBuilder()
          .setSuccess(false)
          .addMessage(e.getMessage())
          .build());
    }
    return Response.valueWithResult(responseBuilder.build());
  }


  @RequestMapping(
      value = "/v2/internal/accounting/pay-invoices/",
      method = POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  Response<PayInvoicesResponse> payInvoices(@RequestBody final PayInvoicesRequest request) {
    final PayInvoicesResponse.Builder responseBuilder = PayInvoicesResponse.newBuilder()
        .setStatus(Status.newBuilder()
            .setSuccess(true)
            .build());
    try {
      accountingService.payInvoices(request.getInvoiceList());
    }
    catch (AccountingException e) {
      responseBuilder.setStatus(Status.newBuilder()
          .setSuccess(false)
          .addMessage(e.getMessage())
          .build());
    }
    return Response.valueWithResult(responseBuilder.build());
  }
}

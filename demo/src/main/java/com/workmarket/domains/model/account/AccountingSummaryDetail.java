package com.workmarket.domains.model.account;

import java.math.BigDecimal;
import java.util.Calendar;

public class AccountingSummaryDetail {

  private BigDecimal amount;
  private String invoiceType;
  private String invoiceNumber;
  private String companyName;
  private Calendar invoiceOn;
  private Calendar invoiceDueDate;
  private Calendar paymentDate;

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getInvoiceType() {
    return invoiceType;
  }

  public void setInvoiceType(String invoiceType) {
    this.invoiceType = invoiceType;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public Calendar getInvoiceOn() {
    return invoiceOn;
  }

  public void setInvoiceOn(Calendar invoiceOn) {
    this.invoiceOn = invoiceOn;
  }

  public Calendar getInvoiceDueDate() {
    return invoiceDueDate;
  }

  public void setInvoiceDueDate(Calendar invoiceDueDate) {
    this.invoiceDueDate = invoiceDueDate;
  }

  public Calendar getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(Calendar paymentDate) {
    this.paymentDate = paymentDate;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoice_number) {
    this.invoiceNumber = invoice_number;
  }
}

package com.workmarket.api.internal.model;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.model.invoice.Invoice;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="plutusInvoiceInvoice")
@Table(name="plutus_invoice_invoice")
@AuditChanges
public class PlutusInvoiceInvoice extends AuditedEntity {
  private static final long serialVersionUID = 1L;

  private String plutusInvoiceUuid;
  private Invoice invoice;

  public PlutusInvoiceInvoice() {
  }

  public PlutusInvoiceInvoice(final String plutusInvoiceUuid, final Invoice invoice) {
    this.plutusInvoiceUuid = plutusInvoiceUuid;
    this.invoice = invoice;
  }

  @Column(name = "plutus_invoice_uuid")
  public String getPlutusInvoiceUuid() {
    return plutusInvoiceUuid;
  }

  public void setPlutusInvoiceUuid(final String plutusInvoiceUuid) {
    this.plutusInvoiceUuid = plutusInvoiceUuid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invoice_id", referencedColumnName = "id", updatable = false)
  public Invoice getInvoice() {
    return invoice;
  }

  public void setInvoice(final Invoice invoice) {
    this.invoice = invoice;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("plutus_invoice_uuid", getPlutusInvoiceUuid())
        .append("invoice_id", getInvoice().getId())
        .toString();
  }
}

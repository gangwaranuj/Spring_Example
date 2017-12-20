package com.workmarket.api.internal.model;

import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="plutusOrderRegisterTransaction")
@Table(name="plutus_order_register_transaction")
@AuditChanges
public class PlutusOrderRegisterTransaction extends AuditedEntity {
  private static final long serialVersionUID = 1L;

  private String plutusOrderUuid;
  private RegisterTransaction registerTransaction;

  public PlutusOrderRegisterTransaction() {
  }

  public PlutusOrderRegisterTransaction(final String plutusOrderUuid, final RegisterTransaction registerTransaction) {
    this.plutusOrderUuid = plutusOrderUuid;
    this.registerTransaction = registerTransaction;
  }

  @Column(name = "plutus_order_uuid")
  public String getPlutusOrderUuid() {
    return plutusOrderUuid;
  }

  public void setPlutusOrderUuid(final String orderUuid) {
    this.plutusOrderUuid = orderUuid;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "register_transaction_id", referencedColumnName = "id", updatable = false)
  public RegisterTransaction getRegisterTransaction() {
    return registerTransaction;
  }

  public void setRegisterTransaction(RegisterTransaction registerTransaction) {
    this.registerTransaction = registerTransaction;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("plutus_order_uuid", getPlutusOrderUuid())
        .append("register_transaction_id", getRegisterTransaction().getId())
        .toString();
  }
}

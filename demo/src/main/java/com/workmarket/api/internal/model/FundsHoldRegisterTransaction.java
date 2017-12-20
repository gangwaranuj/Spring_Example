package com.workmarket.api.internal.model;

import com.workmarket.domains.model.account.RegisterTransaction;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="fundsHoldRegisterTransaction")
@Table(name="funds_hold_register_transaction")
@AuditChanges
public class FundsHoldRegisterTransaction extends AuditedEntity {
  private static final long serialVersionUID = 1L;

  private FundsHold fundsHold;
  private RegisterTransaction registerTransaction;


  public FundsHoldRegisterTransaction() {
  }


  public FundsHoldRegisterTransaction(final FundsHold fundsHold, final RegisterTransaction registerTransaction) {
    this.fundsHold = fundsHold;
    this.registerTransaction = registerTransaction;
  }


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "funds_hold_id", referencedColumnName = "id", updatable = false)
  public FundsHold getFundsHold() {
    return fundsHold;
  }


  public void setFundsHold(FundsHold fundsHold) {
    this.fundsHold = fundsHold;
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
        .append("id", getId())
        .append("funds_hold_id", getFundsHold().getId())
        .append("register_transaction_id", getRegisterTransaction().getId())
        .toString();
  }
}

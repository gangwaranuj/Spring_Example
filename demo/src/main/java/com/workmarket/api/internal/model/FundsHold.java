package com.workmarket.api.internal.model;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.audit.AuditedEntity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="fundsHold")
@Table(name="funds_hold")
@AuditChanges
public class FundsHold extends AuditedEntity {
  private static final long serialVersionUID = 1L;

  private String uuid;

  @Column(name = "uuid", updatable = false)
  public String getUuid() {
    if (uuid == null) {
      setUuid(UUID.randomUUID().toString());
    }
    return uuid;
  }

  public void setUuid(final String uuid) {
    this.uuid = uuid;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("id", getId())
        .append("uuid", getUuid())
        .toString();
  }
}

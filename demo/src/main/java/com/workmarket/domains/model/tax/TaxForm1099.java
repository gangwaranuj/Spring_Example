package com.workmarket.domains.model.tax;

import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Entity(name = "tax_form_1099")
@Table(name = "tax_form_1099")
@AuditChanges
public class TaxForm1099 extends AbstractTaxReport {

	private static final long serialVersionUID = 3755229618970497717L;

	private TaxForm1099Set taxForm1099Set;
	private BigDecimal amount = BigDecimal.ZERO;


	@Fetch(FetchMode.JOIN)
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "tax_form_1099_set_id", referencedColumnName = "id")
	public TaxForm1099Set getTaxForm1099Set() {
		return taxForm1099Set;
	}

	public void setTaxForm1099Set(TaxForm1099Set taxForm1099Set) {
		this.taxForm1099Set = taxForm1099Set;
	}

	@Column(name = "amount", nullable = false)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Transient
	public String getTaxFormPDFName() {
		if (getTaxForm1099Set() != null) {
			return String.format("%s%d%s", Constants.TAX_FORM_1099_PDF_FILENAME_PREFIX, getTaxForm1099Set().getTaxYear(), Constants.PDF_EXTENSION);
		}
		return null;
	}

}

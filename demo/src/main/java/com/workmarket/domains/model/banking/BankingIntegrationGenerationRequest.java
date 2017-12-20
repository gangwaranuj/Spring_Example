package com.workmarket.domains.model.banking;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.account.BankAccountTransaction;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.CollectionUtilities;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


@Entity(name = "banking_integration_generation_request")
@Table(name = "banking_integration_generation_request")
@AuditChanges
@NamedQueries({
		@NamedQuery(name = "bankingintegrationgenerationrequest.get", query = "from banking_integration_generation_request r join fetch r.bankAccountTransactions t join fetch t.bankAccount b join fetch b.company where r.id = :id"),
		@NamedQuery(name = "bankingintegrationgenerationrequest.find", query = "from banking_integration_generation_request bigr " +
				"join fetch bigr.bankingIntegrationGenerationRequestType " +
				"join fetch bigr.bankingIntegrationGenerationRequestStatus " +
				"left join fetch bigr.assets " +
				"where 	bigr.bankingIntegrationGenerationRequestType.code = :type " +
				"and 	bigr.deleted = 0 order by bigr.requestDate desc"),
		@NamedQuery(name = "bankingintegrationgenerationrequest.findByStatus", query = "from banking_integration_generation_request " +
				" where bankingIntegrationGenerationRequestStatus.code = :status and deleted = 0 "),
		@NamedQuery(name = "bankingintegrationgenerationrequest.findByTypeAndStatus", query = "from banking_integration_generation_request " +
				" where bankingIntegrationGenerationRequestStatus.code = :status" +
				" and bankingIntegrationGenerationRequestType.code = :type and deleted = 0 ")
})
public class BankingIntegrationGenerationRequest extends DeletableEntity {

	private static final long serialVersionUID = 1L;

	private User requestor;
	private BankingIntegrationGenerationRequestStatus bankingIntegrationGenerationRequestStatus;
	private BankingIntegrationGenerationRequestType bankingIntegrationGenerationRequestType;
	private Calendar requestDate;
	private String notes;
	private Set<Asset> assets = Sets.newLinkedHashSet();
	private List<BankAccountTransaction> bankAccountTransactions = Lists.newArrayList();
	private String batchNumber;

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "requestor_id", referencedColumnName = "id", nullable = false, updatable = false)
	public User getRequestor() {
		return requestor;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "banking_integration_generation_request_status_code", referencedColumnName = "code", nullable = false)
	public BankingIntegrationGenerationRequestStatus getBankingIntegrationGenerationRequestStatus() {
		return bankingIntegrationGenerationRequestStatus;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "banking_integration_generation_request_type_code", referencedColumnName = "code", nullable = false)
	public BankingIntegrationGenerationRequestType getBankingIntegrationGenerationRequestType() {
		return bankingIntegrationGenerationRequestType;
	}

	@Column(name = "request_date", nullable = false)
	public Calendar getRequestDate() {
		return requestDate;
	}

	@Column(name = "notes", nullable = false)
	public String getNotes() {
		return notes;
	}

	@OneToMany
	@JoinTable(name = "banking_file_asset_association",
			joinColumns = @JoinColumn(name = "banking_integration_generation_request_id"),
			inverseJoinColumns = @JoinColumn(name = "asset_id"))
	public Set<Asset> getAssets() {
		return assets;
	}


	@ManyToMany
	@JoinTable(name = "banking_integration_transaction_association",
			joinColumns = {@JoinColumn(name = "banking_integration_generation_request_id")},
			inverseJoinColumns = {@JoinColumn(name = "bank_account_transaction_id")})
	public List<BankAccountTransaction> getBankAccountTransactions() {
		return bankAccountTransactions;
	}

	/**
	 * There really only should be one asset - so this will get the first asset in the collection
	 *
	 * @return
	 */
	@Transient
	public Asset getAsset() {
		return CollectionUtilities.first(assets);
	}

	public void setAssets(Set<Asset> assets) {
		this.assets = assets;
	}

	public void setRequestor(User requestor) {
		this.requestor = requestor;
	}

	public void setBankingIntegrationGenerationRequestStatus(
			BankingIntegrationGenerationRequestStatus bankingIntegrationGenerationRequestStatus) {
		this.bankingIntegrationGenerationRequestStatus = bankingIntegrationGenerationRequestStatus;
	}

	public void setBankingIntegrationGenerationRequestType(
			BankingIntegrationGenerationRequestType bankingIntegrationGenerationRequestType) {
		this.bankingIntegrationGenerationRequestType = bankingIntegrationGenerationRequestType;
	}

	public void setRequestDate(Calendar requestDate) {
		this.requestDate = requestDate;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setBankAccountTransactions(List<BankAccountTransaction> bankAccountTransactions) {
		this.bankAccountTransactions = bankAccountTransactions;
	}

	@Column(name = "batch_number", nullable = false)
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	@Transient
	public void addAsset(Asset asset) {
		if (asset != null) {
			if (assets != null) {
				assets.add(asset);
			} else {
				setAssets(Sets.newHashSet(asset));
			}
		}
	}

}

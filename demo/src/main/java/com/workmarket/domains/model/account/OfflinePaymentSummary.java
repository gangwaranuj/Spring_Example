package com.workmarket.domains.model.account;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class OfflinePaymentSummary {

	private BigDecimal offlineTransVor  = BigDecimal.ZERO;
	private BigDecimal offlineTransVorHistorical = BigDecimal.ZERO;
	private BigDecimal offlineTransNvor = BigDecimal.ZERO;
	private BigDecimal offlineTransNvorHistorical = BigDecimal.ZERO;
	private BigDecimal offlineSubsVor = BigDecimal.ZERO;
	private BigDecimal offlineSubsVorHistorical = BigDecimal.ZERO;
	private BigDecimal offlineSubsNvor = BigDecimal.ZERO;
	private BigDecimal offlineSubsNvorHistorical = BigDecimal.ZERO;

	@Column(name = "offline_trans_vor")
	public BigDecimal getOfflineTransVor() {
		return offlineTransVor;
	}

	public void setOfflineTransVor(BigDecimal offlineTransVor) {
		this.offlineTransVor = offlineTransVor;
	}

	@Column(name = "offline_trans_vor_historical")
	public BigDecimal getOfflineTransVorHistorical() {
		return offlineTransVorHistorical;
	}

	public void setOfflineTransVorHistorical(BigDecimal offlineTransVorHistorical) {
		this.offlineTransVorHistorical = offlineTransVorHistorical;
	}

	@Column(name = "offline_trans_nvor")
	public BigDecimal getOfflineTransNvor() {
		return offlineTransNvor;
	}

	public void setOfflineTransNvor(BigDecimal offlineTransNvor) {
		this.offlineTransNvor = offlineTransNvor;
	}

	@Column(name = "offline_trans_nvor_historical")
	public BigDecimal getOfflineTransNvorHistorical() {
		return offlineTransNvorHistorical;
	}

	public void setOfflineTransNvorHistorical(BigDecimal offlineTransNvorHistorical) {
		this.offlineTransNvorHistorical = offlineTransNvorHistorical;
	}

	@Column(name = "offline_subs_vor")
	public BigDecimal getOfflineSubsVor() {
		return offlineSubsVor;
	}

	public void setOfflineSubsVor(BigDecimal offlineSubsVor) {
		this.offlineSubsVor = offlineSubsVor;
	}

	@Column(name = "offline_subs_vor_historical")
	public BigDecimal getOfflineSubsVorHistorical() {
		return offlineSubsVorHistorical;
	}

	public void setOfflineSubsVorHistorical(BigDecimal offlineSubsVorHistorical) {
		this.offlineSubsVorHistorical = offlineSubsVorHistorical;
	}

	@Column(name = "offline_subs_nvor")
	public BigDecimal getOfflineSubsNvor() {
		return offlineSubsNvor;
	}

	public void setOfflineSubsNvor(BigDecimal offlineSubsNvor) {
		this.offlineSubsNvor = offlineSubsNvor;
	}

	@Column(name = "offline_subs_nvor_historical")
	public BigDecimal getOfflineSubsNvorHistorical() {
		return offlineSubsNvorHistorical;
	}

	public void setOfflineSubsNvorHistorical(BigDecimal offlineSubsNvorHistorical) {
		this.offlineSubsNvorHistorical = offlineSubsNvorHistorical;
	}
}
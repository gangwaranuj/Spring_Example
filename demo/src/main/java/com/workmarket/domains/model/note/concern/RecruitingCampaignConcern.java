package com.workmarket.domains.model.note.concern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="campaignConcern")
@DiscriminatorValue("campaign")
@AuditChanges
public class RecruitingCampaignConcern extends Concern {

	private static final long serialVersionUID = 1L;

	private RecruitingCampaign campaign;
	private String userName;
	private String email;

	public RecruitingCampaignConcern() {
		super();
	}

	public RecruitingCampaignConcern(String message, RecruitingCampaign campaign) {
		super(message);
		this.campaign = campaign;
	}

	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name="campaign_id", referencedColumnName="id")
	public RecruitingCampaign getCampaign() {
		return campaign;
	}

	public void setCampaign(RecruitingCampaign campaign) {
		this.campaign = campaign;
	}

	@Column(name = "user_name", length = 255)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "email", length = Constants.EMAIL_MAX_LENGTH)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Transient
	public String getType() {
		return "campaign";
	}

	@Override
	@Transient
	public Long getEntityId() {
		return campaign.getId();
	}

	@Override
	@Transient
	public String getEntityNumber() {
		return campaign.getId().toString();
	}
}

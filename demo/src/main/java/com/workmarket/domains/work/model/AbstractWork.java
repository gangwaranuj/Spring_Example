package com.workmarket.domains.work.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.configuration.ConfigurationService;
import com.workmarket.configuration.Constants;
import com.workmarket.data.annotation.TrackChanges;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.DeliverableRequirementGroup;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkPrice;
import com.workmarket.domains.model.WorkResource;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.account.WorkFeeConfiguration;
import com.workmarket.domains.model.account.pricing.AccountPricingServiceTypeEntity;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.account.pricing.AccountServiceType;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.model.assessment.WorkAssessmentAssociation;
import com.workmarket.domains.model.asset.WorkAssetAssociation;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.domains.model.customfield.SavedWorkCustomField;
import com.workmarket.domains.model.customfield.WorkCustomFieldGroupAssociation;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.fulfillment.FulfillmentStrategy;
import com.workmarket.domains.model.invoice.Invoice;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.model.requirementset.RequirementSet;
import com.workmarket.domains.work.model.project.Project;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.EncryptionUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "abstractWork")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Table(name = "work")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("AW")
@AuditChanges
@Access(AccessType.FIELD)
@EntityListeners(EnsuresUuid.class)
public abstract class AbstractWork extends DeletableEntity implements HasUuid {

	private static final long serialVersionUID = 1L;

	@Size(min = 0, max = 255)
	@Column(name = "title", nullable = true, length = 255)
	private String title;

	@Size(min = 0, max = Constants.TEXT_LONG)
	@Column(name = "description", nullable = true)
	private String description;

	@Size(min = 0, max = Constants.TEXT_LONG)
	@Column(name = "instructions", nullable = true)
	private String instructions;

	@Column(name = "private_instructions")
	private Boolean privateInstructions = false;

	@Column(name = "address_onsite_flag", nullable = true, length = 1)
	@Type(type = "yes_no")
	private Boolean addressOnsiteFlag;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "address_id", referencedColumnName = "id", nullable = true)
	private Address address;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id")
	private List<WorkPrice> priceHistory = Lists.newArrayList();

	@NotNull
	private FullPricingStrategy pricingStrategy = new FullPricingStrategy();

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "from", column = @Column(name = "schedule_from", nullable = true)),
		@AttributeOverride(name = "through", column = @Column(name = "schedule_through", nullable = true))
	})
	private DateRange schedule;

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "work_status_type_code", referencedColumnName = "code", nullable = false)
	private WorkStatusType workStatusType = new WorkStatusType(WorkStatusType.DRAFT);

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id")
	@Where(clause = "deleted = 0")
	private Set<WorkSubStatusTypeAssociation> workSubStatusTypeAssociations = Sets.newHashSet();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id")
	@Where(clause = "deleted = 0 AND resolved = 0")
	private Set<WorkSubStatusTypeAssociation> unResolvedWorkSubStatusTypeAssociations = Sets.newHashSet();

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "buyer_user_id", referencedColumnName = "id")
	private User buyer;

	@Column(name = "cancelled_on", nullable = true)
	private Calendar cancelledOn;

	@Size(min = Constants.WORK_NUMBER_IDENTIFIER_LENGTH, max = Constants.WORK_NUMBER_IDENTIFIER_LENGTH)
	@Column(name = "work_number", nullable = false, unique = true)
	private String workNumber;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_id")
	private Set<WorkResource> workResources = Sets.newHashSet();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity_id")
	private Set<WorkAssetAssociation> assetAssociations = Sets.newLinkedHashSet();

	@OneToMany
	@JoinColumn(name = "work_id")
	@Where(clause = "deleted = 0")
	private Set<WorkAssessmentAssociation> assessmentsAssociations = Sets.newLinkedHashSet();

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "deliverable_requirement_group_id", referencedColumnName = "id", nullable = true)
	private DeliverableRequirementGroup deliverableRequirementGroup;

	@ManyToOne
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	private Company company;

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "industry_id")
	private Industry industry;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinTable(name = "project_work_association", joinColumns = @JoinColumn(name = "work_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
	private Project project;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "work_id")
	private Set<WorkCustomFieldGroupAssociation> workCustomFieldGroupAssociations = new HashSet<>();

	@Column(name = "resource_confirmation_flag", nullable = true)
	private Boolean resourceConfirmation = Boolean.FALSE;

	@Column(name = "resource_confirmation_hours")
	private Double resourceConfirmationHours = 0.0;

	@ManyToOne
	@JoinColumn(name = "client_company_id", referencedColumnName = "id", nullable = true)
	private ClientCompany clientCompany;

	@ManyToOne
	@JoinColumn(name = "client_location_id", referencedColumnName = "id", nullable = true)
	private Location location;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_template_id")
	private WorkTemplate template;

	@Column(name = "desired_skills")
	private String desiredSkills;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "buyer_support_user_id", referencedColumnName = "id", nullable = true)
	private User buyerSupportUser;

	@Column(name = "client_contact_id", nullable = true)
	private Long serviceClientContactId;

	@Column(name = "secondary_client_contact_id", nullable = true)
	private Long secondaryServiceClientContactId;

	@Column(name = "require_timetracking")
	private boolean requireTimetracking;

	@Column(name = "ivr_active_flag")
	private Boolean ivrActive = Boolean.FALSE;

	@Column(name = "short_url", length = 50)
	private String shortUrl;

	@Embedded
	private ManageMyWorkMarket manageMyWorkMarket = new ManageMyWorkMarket();

	@Column(name = "resolution", length = Constants.TEXT_MAX_LENGTH)
	@Size(min = 0, max = Constants.TEXT_MAX_LENGTH)
	private String resolution;

	@Column(name = "confirmed_flag", nullable = false)
	private boolean confirmed = false;

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "time_zone_id", nullable = false)
	private TimeZone timeZone;

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "invoice_id", referencedColumnName = "id")
	private Invoice invoice;

	private FulfillmentStrategy fulfillmentStrategy;

	@ManyToMany
	@JoinTable(name = "work_requirement_set_association",
			joinColumns = @JoinColumn(name = "work_id"),
			inverseJoinColumns = @JoinColumn(name = "requirement_set_id"))
	private List<RequirementSet> requirementSets;

	@Embedded
	public FulfillmentStrategy getFulfillmentStrategy() {
		return fulfillmentStrategy;
	}

	public void setFulfillmentStrategy(FulfillmentStrategy fulfillmentStrategy) {
		this.fulfillmentStrategy = fulfillmentStrategy;
	}

	@Column(name = "closed_on")
	private Calendar closedOn;

	@Column(name = "checkin_call_required", nullable = false)
	private boolean checkinCallRequired;

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "cancellation_reason_type_code", nullable = false)
	private CancellationReasonType cancellationReasonType = new CancellationReasonType(CancellationReasonType.NOT_CANCELLED);

	@Version
	@Column(name = "optimistic_lock_version")
	private Integer optimisticLockVersion;

	//this to avoid an ALTER TABLE on the work table
	@ManyToOne
	@JoinTable(name = "legacy_work_fee_configuration",
			joinColumns = @JoinColumn(name = "work_id"),
			inverseJoinColumns = @JoinColumn(name = "work_fee_configuration_id"))
	private WorkFeeConfiguration workFeeConfiguration;

	@Embedded
	private AccountPricingServiceTypeEntity accountPricingServiceTypeEntity;

	private String uuid;

	protected AbstractWork() {
	}

	protected AbstractWork(User buyer) {
		this.buyer = buyer;
	}

	public String getTitle() {
		return title;
	}

	@Column(name = "uuid", updatable = false)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(final String uuid) {
		this.uuid = uuid;
	}

	public String getDescription() {
		return description;
	}

	public String getInstructions() {
		return instructions;
	}

	public Boolean isPrivateInstructions() {
		return privateInstructions;
	}

	// TODO AP: refactor to conform with JavaBean convention
	public Boolean getIsOnsiteAddress() {
		return addressOnsiteFlag;
	}

	// to conform with JavaBean convention
	@Transient
	public Boolean getAddressOnsiteFlag() {
		return addressOnsiteFlag;
	}

	@Transient
	public boolean isWorkBundle() {
		return false;
	}

	public Address getAddress() {
		return address;
	}

	public List<WorkPrice> getPriceHistory() {
		return priceHistory;
	}

	public DateRange getSchedule() {
		return schedule;
	}

	@TrackChanges(type = "SCHEDULE")
	public void setSchedule(DateRange schedule) {
		this.schedule = schedule;
	}

	public Boolean getIsScheduleRange() {
		return schedule != null && schedule.isRange();
	}

	@Transient
	public Boolean getScheduleRangeFlag() {
		Boolean isScheduleRange = getIsScheduleRange();
		if (isScheduleRange != null) {
			return isScheduleRange;
		}
		return false;
	}

	public Calendar getScheduleFrom() {
		return schedule != null ? schedule.getFrom() : null;
	}

	public Calendar getScheduleThrough() {
		return schedule != null ? schedule.getThrough() : null;
	}

	public WorkStatusType getWorkStatusType() {
		return workStatusType;
	}

	public User getBuyer() {
		return buyer;
	}

	public Calendar getCancelledOn() {
		return cancelledOn;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public Set<WorkResource> getWorkResources() {
		return workResources;
	}

	public Set<WorkAssetAssociation> getAssetAssociations() {
		return assetAssociations;
	}

	public void setAssetAssociations(Set<WorkAssetAssociation> assetAssociations) {
		this.assetAssociations = assetAssociations;
	}

	@Transient
	public Set<AbstractAssessment> getAssessments() {
		Set<AbstractAssessment> assessments = Sets.newHashSet();
		for (WorkAssessmentAssociation a : assessmentsAssociations) {
			assessments.add(a.getAssessment());
		}
		return assessments;
	}

	@Transient
	public Set<AbstractAssessment> getRequiredAssessments() {
		Set<AbstractAssessment> assessments = Sets.newHashSet();
		for (WorkAssessmentAssociation a : assessmentsAssociations)
			if (a.isRequired()) {
				assessments.add(a.getAssessment());
			}
		return assessments;
	}

	public Set<WorkAssessmentAssociation> getAssessmentsAssociations() {
		return assessmentsAssociations;
	}

	public void setAssessmentsAssociations(Set<WorkAssessmentAssociation> assessmentsAssociations) {
		this.assessmentsAssociations = assessmentsAssociations;
	}

	public DeliverableRequirementGroup getDeliverableRequirementGroup() {
		return deliverableRequirementGroup;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Transient
	public String getScheduleFromString() {
		return DateUtilities.getISO8601(getScheduleFrom());
	}

	@Transient
	public String getScheduleThroughString() {
		return DateUtilities.getISO8601(getScheduleThrough());
	}

	@TrackChanges(type = "INFO")
	public void setTitle(String title) {
		this.title = title;
	}

	@TrackChanges(type = "INFO")
	public void setDescription(String description) {
		this.description = description;
	}

	@TrackChanges(type = "INFO")

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}


	public void setPrivateInstructions(Boolean privateInstructions) {
		this.privateInstructions = privateInstructions;
	}

	// to conform with JavaBean

	public void setAddressOnsiteFlag(Boolean addressOnsiteFlag) {
		this.addressOnsiteFlag = addressOnsiteFlag;
	}

	public void setIsOnsiteAddress(Boolean addressOnsiteFlag) {
		this.addressOnsiteFlag = addressOnsiteFlag;
	}

	@TrackChanges(type = "LOCATION")
	public void setAddress(Address address) {
		this.address = address;
	}

	public void setPriceHistory(List<WorkPrice> priceHistory) {
		this.priceHistory = priceHistory;
	}

	// TODO AP JavaBean
	public void setIsScheduleRange(Boolean scheduleRangeFlag) {

		// ignore
	}

	public void setScheduleFrom(Calendar scheduleFrom) {
		if (schedule == null) {
			schedule = new DateRange();
		}

		this.schedule.setFrom(scheduleFrom);
	}

	public void setScheduleThrough(Calendar scheduleThrough) {

		if (schedule == null) {
			schedule = new DateRange();
		}

		this.schedule.setThrough(scheduleThrough);

	}

	public void setWorkStatusType(WorkStatusType workStatusType) {
		this.workStatusType = workStatusType;
	}

	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

	public void setCancelledOn(Calendar cancelledOn) {
		this.cancelledOn = cancelledOn;
	}

	public void setWorkResources(Set<WorkResource> workResources) {
		this.workResources = workResources;
	}

	public void setDeliverableRequirementGroup(DeliverableRequirementGroup deliverableRequirementGroup) {
		this.deliverableRequirementGroup = deliverableRequirementGroup;
	}

	public Industry getIndustry() {
		return industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Set<WorkCustomFieldGroupAssociation> getWorkCustomFieldGroupAssociations() {
		return workCustomFieldGroupAssociations;
	}

	public void setWorkCustomFieldGroupAssociations(
			Set<WorkCustomFieldGroupAssociation> workCustomFieldGroupAssociations) {
		this.workCustomFieldGroupAssociations = workCustomFieldGroupAssociations;
	}

	@Transient
	public boolean hasWorkCustomFieldGroupAssociations() {
		return CollectionUtils.isNotEmpty(workCustomFieldGroupAssociations);
	}

	@Transient
	public List<SavedWorkCustomField> getWorkCustomFieldsForEmailDisplay() {

		List<SavedWorkCustomField> response = Lists.newArrayList();
		if (!hasWorkCustomFieldGroupAssociations()) {
			return response;
		}

		List<WorkCustomFieldGroupAssociation> associations = new ArrayList<>(getWorkCustomFieldGroupAssociations());
		Collections.sort(associations);

		for (WorkCustomFieldGroupAssociation assoc : associations) {
			if (CollectionUtils.isEmpty(assoc.getSavedWorkCustomFields())) {
				continue;
			}

			List<SavedWorkCustomField> groupFields = Lists.newArrayList();
			for (SavedWorkCustomField field : assoc.getSavedWorkCustomFields()) {
				if (field.getWorkCustomField().getShowInAssignmentEmail()) {
					groupFields.add(field);
				}
			}
			Collections.sort(groupFields);
			response.addAll(groupFields);
		}
		return response;
	}

	@TrackChanges(type = "PRICING")
	public void setPricingStrategy(PricingStrategy pricingStrategy) {
		this.pricingStrategy.setPricingStrategy(pricingStrategy);
	}

	@Transient
	public PricingStrategy getPricingStrategy() {
		return pricingStrategy.getPricingStrategy();
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public Boolean isResourceConfirmationRequired() {
		return resourceConfirmation;
	}

	@TrackChanges
	public void setResourceConfirmationRequired(Boolean resourceConfirmation) {
		this.resourceConfirmation = resourceConfirmation;
	}

	public Double getResourceConfirmationHours() {
		return resourceConfirmationHours;
	}

	@TrackChanges
	public void setResourceConfirmationHours(Double resourceConfirmationHours) {
		this.resourceConfirmationHours = resourceConfirmationHours;
	}

	public ClientCompany getClientCompany() {
		return clientCompany;
	}

	public Location getLocation() {
		return location;
	}

	@TrackChanges
	public void setClientCompany(ClientCompany clientCompany) {
		this.clientCompany = clientCompany;
	}

	@TrackChanges(type = "LOCATION")
	public void setLocation(Location location) {
		this.location = location;
	}

	// ----- Transient

	@Transient
	public String getIdHash() {
		return EncryptionUtilities.getMD5Digest(getId());
	}

	@Transient
	public boolean isDeliverableRequired() {
		return (deliverableRequirementGroup != null &&
			CollectionUtils.isNotEmpty(deliverableRequirementGroup.getDeliverableRequirements()) &&
			deliverableRequirementGroup.getHoursToComplete() > 0);
	}

	@Transient
	public boolean isCheckinRequired() {
		return this.manageMyWorkMarket.getCheckinRequiredFlag();
	}

	@Transient
	public boolean isAssignToFirstResourceEnabled() {
		return this.manageMyWorkMarket.getAssignToFirstResource();
	}

	@Transient
	public boolean isShowCheckoutNote() {
		return this.manageMyWorkMarket.getShowCheckoutNotesFlag();
	}

	@Transient
	public boolean isCheckoutNoteRequired() {
		return this.manageMyWorkMarket.getCheckoutNoteRequiredFlag();
	}

	@Transient
	public String getCheckoutNoteInstructions() {
		return this.manageMyWorkMarket.getCheckoutNoteInstructions();
	}

	@Transient
	public boolean isEnableAssignmentPrintout() {
		return this.manageMyWorkMarket.isEnableAssignmentPrintout();
	}

	@Transient
	public boolean getCheckoutNoteRequiredFlag() {
		return this.manageMyWorkMarket.getCheckoutNoteRequiredFlag();
	}

	@Transient
	public String getCheckinContactName() {
		return this.manageMyWorkMarket.getCheckinContactName();
	}

	@Transient
	public String getCheckinContactPhone() {
		return this.manageMyWorkMarket.getCheckinContactPhone();
	}

	@Transient
	public boolean isClientLocationAddress() {
		return (addressOnsiteFlag && (location != null) && (clientCompany != null));
	}

	public Set<WorkSubStatusTypeAssociation> getWorkSubStatusTypeAssociations() {
		return workSubStatusTypeAssociations;
	}

	public void setWorkSubStatusTypeAssociations(Set<WorkSubStatusTypeAssociation> workSubStatusTypeAssociations) {
		this.workSubStatusTypeAssociations = workSubStatusTypeAssociations;
	}

	public ManageMyWorkMarket getManageMyWorkMarket() {
		return manageMyWorkMarket;
	}

	public void setManageMyWorkMarket(ManageMyWorkMarket manageMyWorkMarket) {
		this.manageMyWorkMarket = manageMyWorkMarket;
	}

	public String getDesiredSkills() {
		return desiredSkills;
	}

	@TrackChanges(type = "INFO")
	public void setDesiredSkills(String desiredSkills) {
		this.desiredSkills = desiredSkills;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append("id", getId()).toString();
	}

	@Transient
	public boolean isVoidable() {
		return isDraft() || isSent() || isDeclined();
	}

	@Transient
	public boolean isCancellable() {
		return isActive();
	}

	@Transient
	public boolean isNegotiable() {
		return isSent();
	}

	@Transient
	public boolean isPricingEditable() {
		boolean isInternal = getPricingStrategyType() == PricingStrategyType.INTERNAL;

		return isDraft() ||
				(!isInternal && (isSent() || isDeclined()));
	}

	@Transient
	public boolean isReschedulable() {
		return isActive();
	}

	public void setTemplate(WorkTemplate template) {
		this.template = template;
	}

	public boolean isTemplateSet() {
		return template != null && template.getId() != null && template.getId() > 0L;
	}

	public WorkTemplate getTemplate() {
		return template;
	}

	public void setBuyerSupportUser(User buyerSupportUser) {
		this.buyerSupportUser = buyerSupportUser;
	}

	public User getBuyerSupportUser() {
		return buyerSupportUser;
	}

	@TrackChanges(type = "CONTACT")
	public void setServiceClientContactId(Long serviceClientContactId) {
		this.serviceClientContactId = serviceClientContactId;
	}

	public Long getServiceClientContactId() {
		return serviceClientContactId;
	}

	@TrackChanges(type = "CONTACT")
	public void setSecondaryServiceClientContactId(Long secondaryServiceClientContactId) {
		this.secondaryServiceClientContactId = secondaryServiceClientContactId;
	}

	public Long getSecondaryServiceClientContactId() {
		return secondaryServiceClientContactId;
	}

	@TrackChanges
	public void setRequireTimetracking(boolean requireTimetracking) {
		this.requireTimetracking = requireTimetracking;
	}

	public boolean isRequireTimetracking() {
		return requireTimetracking;
	}

	public Boolean isIvrActive() {
		return this.ivrActive;
	}

	public void setIvrActive(Boolean ivrActive) {
		this.ivrActive = ivrActive;
	}

	public String getResolution() {
		return resolution;
	}

	@TrackChanges
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public Boolean getResourceConfirmation() {
		return resourceConfirmation;
	}

	public Set<WorkSubStatusTypeAssociation> getUnResolvedWorkSubStatusTypeAssociations() {
		return unResolvedWorkSubStatusTypeAssociations;
	}

	public void setUnResolvedWorkSubStatusTypeAssociations(Set<WorkSubStatusTypeAssociation> unResolvedWorkSubStatusTypeAssociations) {
		this.unResolvedWorkSubStatusTypeAssociations = unResolvedWorkSubStatusTypeAssociations;
	}

	@Transient
	public Boolean hasServiceContact() {
		return getServiceClientContactId() != null;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	@Transient
	public String getRelativeURI() {
		return ConfigurationService.WORK_DETAILS_URL + getWorkNumber();
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	@Transient
	public Integer getPaymentTermsDays() {
		return manageMyWorkMarket.getPaymentTermsDays();
	}

	public void setPaymentTermsDays(Integer paymentTermsDays) {
		this.manageMyWorkMarket.setPaymentTermsDays(paymentTermsDays);
	}

	public Calendar getClosedOn() {
		return closedOn;
	}

	public void setClosedOn(Calendar closedOn) {
		this.closedOn = closedOn;
	}

	// Statuses
	@Transient
	public boolean isActive() {
		return getWorkStatusType().getCode().equals(WorkStatusType.ACTIVE);
	}

	@Transient
	public boolean isDraft() {
		return getWorkStatusType().getCode().equals(WorkStatusType.DRAFT);
	}

	@Transient
	public boolean isCancelled() {
		return getWorkStatusType().getCode().equals(WorkStatusType.CANCELLED) ||
				getWorkStatusType().getCode().equals(WorkStatusType.CANCELLED_PAYMENT_PENDING);
	}

	@Transient
	public boolean isClosed() {
		return getWorkStatusType().getCode().equals(WorkStatusType.CLOSED);
	}

	@Transient
	public boolean isPaid() {
		return getWorkStatusType().getCode().equals(WorkStatusType.PAID) ||
				getWorkStatusType().getCode().equals(WorkStatusType.CANCELLED_WITH_PAY);
	}

	@Transient
	public boolean isVoid() {
		return getWorkStatusType().getCode().equals(WorkStatusType.VOID);
	}

	@Transient
	public boolean isSent() {
		return getWorkStatusType().getCode().equals(WorkStatusType.SENT);
	}

	@Transient
	public boolean isComplete() {
		return getWorkStatusType().getCode().equals(WorkStatusType.COMPLETE);
	}

	@Transient
	public boolean isPaymentPending() {
		return getWorkStatusType().getCode().equals(WorkStatusType.PAYMENT_PENDING) || getWorkStatusType().getCode().equals(WorkStatusType.CANCELLED_PAYMENT_PENDING);
	}

	@Transient
	public boolean isDeclined() {
		return getWorkStatusType().getCode().equals(WorkStatusType.DECLINED);
	}

	@Transient
	public boolean isAbandoned() {
		return getWorkStatusType().getCode().equals(WorkStatusType.ABANDONED);
	}

	@Transient
	public boolean hasPaymentTerms() {
		return getManageMyWorkMarket().getPaymentTermsEnabled() && (getPaymentTermsDays() > 0);
	}

	@Transient
	public Calendar getDueDate() {
		Calendar dueOn = DateUtilities.getCalendarNow();
		if (hasPaymentTerms()) {
			dueOn.add(Calendar.DATE, getPaymentTermsDays());
		}
		return DateUtilities.getCalendarWithLastMinuteOfDay(dueOn, Constants.EST_TIME_ZONE);
	}

	@Transient
	public boolean isSetOnsiteAddress() {
		return addressOnsiteFlag != null;
	}

	@Transient
	public boolean isOffsite() {
		return isSetOnsiteAddress() && !addressOnsiteFlag;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public boolean isCheckinCallRequired() {
		return checkinCallRequired;
	}

	public void setCheckinCallRequired(boolean checkinCallRequired) {
		this.checkinCallRequired = checkinCallRequired;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	@Transient
	public boolean isInvoiced() {
		return getInvoice() != null;
	}

	public CancellationReasonType getCancellationReasonType() {
		return cancellationReasonType;
	}

	public void setCancellationReasonType(CancellationReasonType cancellationReasonType) {
		this.cancellationReasonType = cancellationReasonType;
	}

	@Transient
	public PricingStrategyType getPricingStrategyType() {
		if (getPricingStrategy() != null && getPricingStrategy().getFullPricingStrategy() != null) {
			return getPricingStrategy().getFullPricingStrategy().getPricingStrategyType();
		}
		return null;
	}

	public Integer getOptimisticLockVersion() {
		return optimisticLockVersion;
	}

	public void setOptimisticLockVersion(Integer optimisticLockVersion) {
		this.optimisticLockVersion = optimisticLockVersion;
	}

	public WorkFeeConfiguration getWorkFeeConfiguration() {
		return workFeeConfiguration;
	}

	public void setWorkFeeConfiguration(WorkFeeConfiguration workFeeConfiguration) {
		this.workFeeConfiguration = workFeeConfiguration;
	}

	@Transient
	public boolean hasLegacyWorkFeeConfiguration() {
		return workFeeConfiguration != null;
	}

	/**
	 * THIS IS A HACK -
	 * <p/>
	 * In WorkNegotiationServiceImpl:562
	 * <p/>
	 * if (negotiation.isScheduleNegotiation()) {
	 * workService.updateWorkProperties(negotiation.getWork().getId(), CollectionUtilities.newStringMap(
	 * scheduleRangeFlag", negotiation.getScheduleRangeFlag() ? "true" : "false",
	 * scheduleFrom", DateUtilities.getISO8601(negotiation.getScheduleFrom()),
	 * scheduleThrough", negotiation.getScheduleRangeFlag() ? DateUtilities.getISO8601(negotiation.getScheduleThrough()) : null
	 * ));
	 * }
	 * <p/>
	 * Because there wasn't a property, this was failing
	 * <p/>
	 * The scheduleRangeFlag is not being persisted to the database from *this* class but does
	 * seem to be persisted by classes that use the flatwork object
	 */

	public void setScheduleRangeFlag(Boolean scheduleRangeFlag) {

	}

	public AccountPricingServiceTypeEntity getAccountPricingServiceTypeEntity() {
		return accountPricingServiceTypeEntity;
	}

	public void setAccountPricingServiceTypeEntity(AccountPricingServiceTypeEntity accountPricingServiceTypeEntity) {
		this.accountPricingServiceTypeEntity = accountPricingServiceTypeEntity;
	}

	@Transient
	public AccountPricingType getAccountPricingType() {
		return accountPricingServiceTypeEntity.getAccountPricingType();
	}

	@Transient
	public void setAccountPricingType(AccountPricingType accountPricingType) {
		accountPricingServiceTypeEntity.setAccountPricingType(accountPricingType);
	}

	@Transient
	public AccountServiceType getAccountServiceType() {
		return accountPricingServiceTypeEntity.getAccountServiceType();
	}

	@Transient
	public void setAccountServiceType(AccountServiceType accountServiceType) {
		accountPricingServiceTypeEntity.setAccountServiceType(accountServiceType);
	}

	@Transient
	public boolean hasTransactionalPricing() {
		return accountPricingServiceTypeEntity.getAccountPricingType().isTransactionalPricing();
	}

	@Transient
	public boolean hasSubscriptionPricing() {
		return accountPricingServiceTypeEntity.getAccountPricingType().isSubscriptionPricing();
	}

	@Transient
	public boolean isShownInFeed() {
		Boolean showInFeedForWork = this.getManageMyWorkMarket().getShowInFeed();
		return showInFeedForWork == null ? false : showInFeedForWork;
	}

	public List<RequirementSet> getRequirementSets() {
		return requirementSets;
	}

	public void setRequirementSets(List<RequirementSet> requirementSets) {
		this.requirementSets = requirementSets;
	}

	@Transient
	public boolean hasProject() {
		return project != null;
	}

	public boolean isFinished() {
		return (this.isComplete() || this.isPaymentPending() || this.isPaid() || this.isClosed());
	}

	@Transient
	public boolean shouldOpenForWorkResource(WorkResource workResource) {
		return workResource == null && this.shouldOpenForWorkResource();
	}

	@Transient
	public boolean shouldOpenForWorkResource() {
		return this.isShownInFeed() && !this.isAssignToFirstResourceEnabled();
	}
}

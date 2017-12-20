package com.workmarket.thrift.work;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.account.pricing.AccountPricingType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.service.business.dto.DeliverableRequirementGroupDTO;
import com.workmarket.service.business.dto.PartGroupDTO;
import com.workmarket.thrift.core.DeliverableAsset;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;

public class Work implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String workNumber;
	private String title;
	private String description;
	private String instructions;
	private Boolean privateInstructions;
	private Boolean pricingEditable;
	private String desiredSkills;
	private String shortUrl;
	private com.workmarket.thrift.core.Status status;
	private List<SubStatus> subStatuses;
	private boolean inProgress;
	private Project project;
	private com.workmarket.thrift.core.Company company;
	private com.workmarket.thrift.core.Company clientCompany;
	private com.workmarket.thrift.core.User buyer;
	private com.workmarket.thrift.core.User locationContact;
	private com.workmarket.thrift.core.User secondaryLocationContact;
	private com.workmarket.thrift.core.User supportContact;
	private Resource activeResource;
	private List<Resource> resources;
	private Boolean offsiteLocation;
	private boolean newLocation;
	private com.workmarket.thrift.core.Location location;
	private Schedule schedule;
	private PricingStrategy pricing;
	private List<PricingLogEntry> pricingHistory;
	private PaymentSummary payment;
	private Invoice invoice;
	private TreeSet<com.workmarket.thrift.core.Asset> assets;
	private TreeSet<DeliverableAsset> deliverableAssets;
	private DeliverableRequirementGroupDTO deliverableRequirementGroupDTO;
	private boolean resourceConfirmationRequired;
	private double resourceConfirmationHours;
	private boolean isConfirmable;
	private Calendar confirmableDate;
	private Calendar confirmByDate;
	private boolean timetrackingRequired;
	private String resolution;
	private PartGroupDTO partGroup;
	private List<com.workmarket.thrift.core.Note> notes;
	private List<QuestionAnswerPair> questionAnswerPairs;
	private List<CustomFieldGroup> customFieldGroups;
	private List<com.workmarket.thrift.assessment.Assessment> assessments;
	private boolean delegationAllowed;
	private List<LogEntry> changelog;
	private ManageMyWorkMarket configuration;
	private Template template;
	private com.workmarket.thrift.core.Industry industry;
	private List<RoutingStrategy> routingStrategies;
	private String timeZone;
	private List<Negotiation> pendingNegotiations;
	private boolean checkinCallRequired;
	private List<com.workmarket.thrift.core.Upload> uploads;
	private boolean pendingPaymentFulfillment;
	private long timeZoneId;
	private boolean showCheckoutNotesFlag;
	private boolean checkoutNoteRequiredFlag;
	private String checkoutNoteInstructions;
	private String checkinContactName;
	private String checkinContactPhone;
	private boolean approvedAdditionalExpenses;
	private boolean approvedBonus;
	private AccountPricingType accountPricingType;
	private List<Long> requirementSetIds;
	private List<Long> followers = Lists.newArrayList();
	private List<Long> needToApplyGroups = Lists.newArrayList();
	private List<Long> firstToAcceptGroups = Lists.newArrayList();
	private boolean showInFeed = false;
	private boolean workNotifyAllowed;
	private boolean workNotifyAvailable;
	private String uniqueExternalIdValue;
	private String uniqueExternalIdDisplayName;
	private boolean documentsEnabled;

	public Work() {
		this.pendingPaymentFulfillment = false;
	}

	public long getId() {
		return this.id;
	}

	public Work setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public Work setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public String getTitle() {
		return this.title;
	}

	public Work setTitle(String title) {
		this.title = title;
		return this;
	}

	public boolean isSetTitle() {
		return this.title != null;
	}

	public String getDescription() {
		return this.description;
	}

	public Work setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSetDescription() {
		return this.description != null;
	}

	public String getInstructions() {
		return this.instructions;
	}

	public Work setInstructions(String instructions) {
		this.instructions = instructions;
		return this;
	}

	public boolean isSetInstructions() {
		return this.instructions != null;
	}

	public Boolean getPrivateInstructions() {
		return this.privateInstructions;
	}

	public Boolean isPricingEditable() {
		return this.pricingEditable;
	}

	public Work setPrivateInstructions(Boolean privateInstructions) {
		this.privateInstructions = privateInstructions;
		return this;
	}

	public Work setPricingEditable(boolean pricingEditable) {
		this.pricingEditable = pricingEditable;
		return this;
	}

	public boolean isSetPrivateInstructions() {
		return this.privateInstructions != null;
	}

	public String getDesiredSkills() {
		return this.desiredSkills;
	}

	public Work setDesiredSkills(String desiredSkills) {
		this.desiredSkills = desiredSkills;
		return this;
	}

	public boolean isSetDesiredSkills() {
		return this.desiredSkills != null;
	}

	public String getShortUrl() {
		return this.shortUrl;
	}

	public Work setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
		return this;
	}

	public boolean isSetShortUrl() {
		return this.shortUrl != null;
	}

	public com.workmarket.thrift.core.Status getStatus() {
		return this.status;
	}

	public Work setStatus(com.workmarket.thrift.core.Status status) {
		this.status = status;
		return this;
	}

	public boolean isSetStatus() {
		return this.status != null;
	}

	public int getSubStatusesSize() {
		return (this.subStatuses == null) ? 0 : this.subStatuses.size();
	}

	public java.util.Iterator<SubStatus> getSubStatusesIterator() {
		return (this.subStatuses == null) ? null : this.subStatuses.iterator();
	}

	public void addToSubStatuses(SubStatus elem) {
		if (this.subStatuses == null) {
			this.subStatuses = new ArrayList<SubStatus>();
		}
		this.subStatuses.add(elem);
	}

	public List<SubStatus> getSubStatuses() {
		return this.subStatuses;
	}

	public Work setSubStatuses(List<SubStatus> subStatuses) {
		this.subStatuses = subStatuses;
		return this;
	}

	public boolean isSetSubStatuses() {
		return this.subStatuses != null;
	}

	public boolean isInProgress() {
		return this.inProgress;
	}

	public Work setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
		return this;
	}

	public Project getProject() {
		return this.project;
	}

	public Work setProject(Project project) {
		this.project = project;
		return this;
	}

	public boolean isSetProject() {
		return this.project != null;
	}

	public com.workmarket.thrift.core.Company getCompany() {
		return this.company;
	}

	public Work setCompany(com.workmarket.thrift.core.Company company) {
		this.company = company;
		return this;
	}

	public boolean isSetCompany() {
		return this.company != null;
	}

	public com.workmarket.thrift.core.Company getClientCompany() {
		return this.clientCompany;
	}

	public Work setClientCompany(com.workmarket.thrift.core.Company clientCompany) {
		this.clientCompany = clientCompany;
		return this;
	}

	public boolean isSetClientCompany() {
		return this.clientCompany != null;
	}

	public com.workmarket.thrift.core.User getBuyer() {
		return this.buyer;
	}

	public Work setBuyer(com.workmarket.thrift.core.User buyer) {
		this.buyer = buyer;
		return this;
	}

	public boolean isSetBuyer() {
		return this.buyer != null;
	}

	public com.workmarket.thrift.core.User getLocationContact() {
		return this.locationContact;
	}

	public Work setLocationContact(com.workmarket.thrift.core.User locationContact) {
		this.locationContact = locationContact;
		return this;
	}

	public boolean isSetLocationContact() {
		return this.locationContact != null;
	}

	public com.workmarket.thrift.core.User getSecondaryLocationContact() {
		return this.secondaryLocationContact;
	}

	public Work setSecondaryLocationContact(com.workmarket.thrift.core.User secondaryLocationContact) {
		this.secondaryLocationContact = secondaryLocationContact;
		return this;
	}

	public boolean isSetSecondaryLocationContact() {
		return this.secondaryLocationContact != null;
	}

	public com.workmarket.thrift.core.User getSupportContact() {
		return this.supportContact;
	}

	public Work setSupportContact(com.workmarket.thrift.core.User supportContact) {
		this.supportContact = supportContact;
		return this;
	}

	public boolean isSetSupportContact() {
		return this.supportContact != null;
	}

	public Resource getActiveResource() {
		return this.activeResource;
	}

	public Work setActiveResource(Resource activeResource) {
		this.activeResource = activeResource;
		return this;
	}

	public boolean isSetActiveResource() {
		return this.activeResource != null;
	}

	public int getResourcesSize() {
		return (this.resources == null) ? 0 : this.resources.size();
	}

	public java.util.Iterator<Resource> getResourcesIterator() {
		return (this.resources == null) ? null : this.resources.iterator();
	}

	public void addToResources(Resource elem) {
		if (this.resources == null) {
			this.resources = new ArrayList<>();
		}
		this.resources.add(elem);
	}

	public List<Resource> getResources() {
		return this.resources;
	}

	public Work setResources(List<Resource> resources) {
		this.resources = resources;
		return this;
	}

	public boolean isSetResources() {
		return this.resources != null;
	}

	public Boolean getOffsiteLocation() {
		return this.offsiteLocation;
	}

	public boolean isOffsiteLocation() {
		return (this.offsiteLocation == null) ? false : this.offsiteLocation;
	}

	public Work setOffsiteLocation(Boolean offsiteLocation) {
		this.offsiteLocation = offsiteLocation;
		return this;
	}

	public boolean isNewLocation() {
		return this.newLocation;
	}

	public Work setNewLocation(boolean newLocation) {
		this.newLocation = newLocation;
		return this;
	}

	public com.workmarket.thrift.core.Location getLocation() {
		return this.location;
	}

	public Work setLocation(com.workmarket.thrift.core.Location location) {
		this.location = location;
		return this;
	}

	public boolean isSetLocation() {
		return this.location != null;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public Work setSchedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}

	public boolean isSetSchedule() {
		return this.schedule != null;
	}

	public PricingStrategy getPricing() {
		return this.pricing;
	}

	public Work setPricing(PricingStrategy pricing) {
		this.pricing = pricing;
		return this;
	}

	public boolean isSetPricing() {
		return this.pricing != null;
	}

	public int getPricingHistorySize() {
		return (this.pricingHistory == null) ? 0 : this.pricingHistory.size();
	}

	public java.util.Iterator<PricingLogEntry> getPricingHistoryIterator() {
		return (this.pricingHistory == null) ? null : this.pricingHistory.iterator();
	}

	public void addToPricingHistory(PricingLogEntry elem) {
		if (this.pricingHistory == null) {
			this.pricingHistory = new ArrayList<>();
		}
		this.pricingHistory.add(elem);
	}

	public List<PricingLogEntry> getPricingHistory() {
		return this.pricingHistory;
	}

	public Work setPricingHistory(List<PricingLogEntry> pricingHistory) {
		this.pricingHistory = pricingHistory;
		return this;
	}

	public boolean isSetPricingHistory() {
		return this.pricingHistory != null;
	}

	public PaymentSummary getPayment() {
		return this.payment;
	}

	public Work setPayment(PaymentSummary payment) {
		this.payment = payment;
		return this;
	}

	public boolean isSetPayment() {
		return this.payment != null;
	}

	public Invoice getInvoice() {
		return this.invoice;
	}

	public Work setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}

	public boolean isSetInvoice() {
		return this.invoice != null;
	}

	public int getAssetsSize() {
		return (this.assets == null) ? 0 : this.assets.size();
	}

	public java.util.Iterator<com.workmarket.thrift.core.Asset> getAssetsIterator() {
		return (this.assets == null) ? null : this.assets.iterator();
	}

	public void addToAssets(com.workmarket.thrift.core.Asset elem) {
		if (this.assets == null) {
			this.assets = Sets.newTreeSet();
		}
		this.assets.add(elem);
	}

	public TreeSet<com.workmarket.thrift.core.Asset> getAssets() {
		return this.assets;
	}

	public Work setAssets(TreeSet<com.workmarket.thrift.core.Asset> assets) {
		this.assets = assets;
		return this;
	}

	public boolean isSetAssets() {
		return this.assets != null;
	}

	public int getDeliverableAssetsSize() {
		return (this.deliverableAssets == null) ? 0 : this.deliverableAssets.size();
	}

	public TreeSet<DeliverableAsset> getDeliverableAssets() {
		return deliverableAssets;
	}

	public void setDeliverableAssets(TreeSet<DeliverableAsset> deliverableAssets) {
		this.deliverableAssets = deliverableAssets;
	}

	public boolean isSetDeliverableAssets() {
		return this.deliverableAssets != null;
	}

	public void addToDeliverableAssets(DeliverableAsset elem) {
		if (this.deliverableAssets == null) {
			this.deliverableAssets = Sets.newTreeSet();
		}
		this.deliverableAssets.add(elem);
	}

	public DeliverableRequirementGroupDTO getDeliverableRequirementGroupDTO() {
		return deliverableRequirementGroupDTO;
	}

	public void setDeliverableRequirementGroupDTO(DeliverableRequirementGroupDTO deliverableRequirementGroupDTO) {
		this.deliverableRequirementGroupDTO = deliverableRequirementGroupDTO;
	}

	public boolean isSetDeliverableGroupDTO() {
		return this.deliverableRequirementGroupDTO != null;
	}

	public boolean isResourceConfirmationRequired() {
		return this.resourceConfirmationRequired;
	}

	public Work setResourceConfirmationRequired(boolean resourceConfirmationRequired) {
		this.resourceConfirmationRequired = resourceConfirmationRequired;
		return this;
	}

	public double getResourceConfirmationHours() {
		return this.resourceConfirmationHours;
	}

	public Work setResourceConfirmationHours(double resourceConfirmationHours) {
		this.resourceConfirmationHours = resourceConfirmationHours;
		return this;
	}

	public boolean isSetResourceConfirmationHours() {
		return (resourceConfirmationHours > 0D);
	}

	public boolean isConfirmable() {
		return this.isConfirmable;
	}

	public Work setIsConfirmable(boolean isConfirmable) {
		this.isConfirmable = isConfirmable;
		return this;
	}

	public Calendar getConfirmableDate() {
		return confirmableDate;
	}

	public void setConfirmableDate(Calendar confirmableDate) {
		this.confirmableDate = confirmableDate;
	}

	public Calendar getConfirmByDate() {
		return confirmByDate;
	}

	public void setConfirmByDate(Calendar confirmByDate) {
		this.confirmByDate = confirmByDate;
	}

	public boolean isTimetrackingRequired() {
		return this.timetrackingRequired;
	}

	public Work setTimetrackingRequired(boolean timetrackingRequired) {
		this.timetrackingRequired = timetrackingRequired;
		return this;
	}

	public String getResolution() {
		return this.resolution;
	}

	public Work setResolution(String resolution) {
		this.resolution = resolution;
		return this;
	}

	public boolean isSetResolution() {
		return this.resolution != null;
	}

	public PartGroupDTO getPartGroup() {
		return partGroup;
	}

	public void setPartGroup(PartGroupDTO partGroup) {
		this.partGroup = partGroup;
	}

	public boolean isSetPartGroup() {
		return this.partGroup != null;
	}

	public int getNotesSize() {
		return (this.notes == null) ? 0 : this.notes.size();
	}

	public java.util.Iterator<com.workmarket.thrift.core.Note> getNotesIterator() {
		return (this.notes == null) ? null : this.notes.iterator();
	}

	public void addToNotes(com.workmarket.thrift.core.Note elem) {
		if (this.notes == null) {
			this.notes = new ArrayList<>();
		}
		this.notes.add(elem);
	}

	public List<com.workmarket.thrift.core.Note> getNotes() {
		return this.notes;
	}

	public Work setNotes(List<com.workmarket.thrift.core.Note> notes) {
		this.notes = notes;
		return this;
	}

	public boolean isSetNotes() {
		return this.notes != null;
	}

	public int getQuestionAnswerPairsSize() {
		return (this.questionAnswerPairs == null) ? 0 : this.questionAnswerPairs.size();
	}

	public java.util.Iterator<QuestionAnswerPair> getQuestionAnswerPairsIterator() {
		return (this.questionAnswerPairs == null) ? null : this.questionAnswerPairs.iterator();
	}

	public void addToQuestionAnswerPairs(QuestionAnswerPair elem) {
		if (this.questionAnswerPairs == null) {
			this.questionAnswerPairs = new ArrayList<>();
		}
		this.questionAnswerPairs.add(elem);
	}

	public List<QuestionAnswerPair> getQuestionAnswerPairs() {
		return this.questionAnswerPairs;
	}

	public Work setQuestionAnswerPairs(List<QuestionAnswerPair> questionAnswerPairs) {
		this.questionAnswerPairs = questionAnswerPairs;
		return this;
	}

	public boolean isSetQuestionAnswerPairs() {
		return this.questionAnswerPairs != null;
	}

	public int getCustomFieldGroupsSize() {
		return (this.customFieldGroups == null) ? 0 : this.customFieldGroups.size();
	}

	public java.util.Iterator<CustomFieldGroup> getCustomFieldGroupsIterator() {
		return (this.customFieldGroups == null) ? null : this.customFieldGroups.iterator();
	}

	public void addToCustomFieldGroups(CustomFieldGroup elem) {
		if (this.customFieldGroups == null) {
			this.customFieldGroups = new ArrayList<>();
		}
		this.customFieldGroups.add(elem);
	}

	public List<CustomFieldGroup> getCustomFieldGroups() {
		return this.customFieldGroups;
	}

	public Work setCustomFieldGroups(List<CustomFieldGroup> customFieldGroups) {
		this.customFieldGroups = customFieldGroups;
		return this;
	}

	public boolean isSetCustomFieldGroups() {
		return this.customFieldGroups != null;
	}

	public int getAssessmentsSize() {
		return (this.assessments == null) ? 0 : this.assessments.size();
	}

	public java.util.Iterator<com.workmarket.thrift.assessment.Assessment> getAssessmentsIterator() {
		return (this.assessments == null) ? null : this.assessments.iterator();
	}

	public void addToAssessments(com.workmarket.thrift.assessment.Assessment elem) {
		if (this.assessments == null) {
			this.assessments = new ArrayList<>();
		}
		this.assessments.add(elem);
	}

	public List<com.workmarket.thrift.assessment.Assessment> getAssessments() {
		return this.assessments;
	}

	public Work setAssessments(List<com.workmarket.thrift.assessment.Assessment> assessments) {
		this.assessments = assessments;
		return this;
	}

	public boolean isSetAssessments() {
		return this.assessments != null;
	}

	public boolean isDelegationAllowed() {
		return this.delegationAllowed;
	}

	public Work setDelegationAllowed(boolean delegationAllowed) {
		this.delegationAllowed = delegationAllowed;
		return this;
	}

	public int getChangelogSize() {
		return (this.changelog == null) ? 0 : this.changelog.size();
	}

	public java.util.Iterator<LogEntry> getChangelogIterator() {
		return (this.changelog == null) ? null : this.changelog.iterator();
	}

	public void addToChangelog(LogEntry elem) {
		if (this.changelog == null) {
			this.changelog = new ArrayList<>();
		}
		this.changelog.add(elem);
	}

	public List<LogEntry> getChangelog() {
		return this.changelog;
	}

	public Work setChangelog(List<LogEntry> changelog) {
		this.changelog = changelog;
		return this;
	}

	public boolean isSetChangelog() {
		return this.changelog != null;
	}

	public ManageMyWorkMarket getConfiguration() {
		return this.configuration;
	}

	public Work setConfiguration(ManageMyWorkMarket configuration) {
		this.configuration = configuration;
		return this;
	}

	public boolean isSetConfiguration() {
		return this.configuration != null;
	}

	public Template getTemplate() {
		return this.template;
	}

	public Work setTemplate(Template template) {
		this.template = template;
		return this;
	}

	public boolean isSetTemplate() {
		return this.template != null;
	}

	public com.workmarket.thrift.core.Industry getIndustry() {
		return this.industry;
	}

	public Work setIndustry(com.workmarket.thrift.core.Industry industry) {
		this.industry = industry;
		return this;
	}

	public boolean isSetIndustry() {
		return this.industry != null;
	}

	public int getRoutingStrategiesSize() {
		return (this.routingStrategies == null) ? 0 : this.routingStrategies.size();
	}

	public java.util.Iterator<RoutingStrategy> getRoutingStrategiesIterator() {
		return (this.routingStrategies == null) ? null : this.routingStrategies.iterator();
	}

	public void addToRoutingStrategies(RoutingStrategy elem) {
		if (this.routingStrategies == null) {
			this.routingStrategies = new ArrayList<>();
		}
		this.routingStrategies.add(elem);
	}

	public List<RoutingStrategy> getRoutingStrategies() {
		return this.routingStrategies;
	}

	public Work setRoutingStrategies(List<RoutingStrategy> routingStrategies) {
		this.routingStrategies = routingStrategies;
		return this;
	}

	public boolean isSetRoutingStrategies() {
		return this.routingStrategies != null;
	}

	public String getTimeZone() {
		return this.timeZone;
	}

	public Work setTimeZone(String timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	public boolean isSetTimeZone() {
		return this.timeZone != null;
	}

	public int getPendingNegotiationsSize() {
		return (this.pendingNegotiations == null) ? 0 : this.pendingNegotiations.size();
	}

	public java.util.Iterator<Negotiation> getPendingNegotiationsIterator() {
		return (this.pendingNegotiations == null) ? null : this.pendingNegotiations.iterator();
	}

	public void addToPendingNegotiations(Negotiation elem) {
		if (this.pendingNegotiations == null) {
			this.pendingNegotiations = new ArrayList<>();
		}
		this.pendingNegotiations.add(elem);
	}

	public List<Negotiation> getPendingNegotiations() {
		return this.pendingNegotiations;
	}

	public Work setPendingNegotiations(List<Negotiation> pendingNegotiations) {
		this.pendingNegotiations = pendingNegotiations;
		return this;
	}

	public boolean isSetPendingNegotiations() {
		return this.pendingNegotiations != null;
	}

	public boolean isCheckinCallRequired() {
		return this.checkinCallRequired;
	}

	public Work setCheckinCallRequired(boolean checkinCallRequired) {
		this.checkinCallRequired = checkinCallRequired;
		return this;
	}

	public int getUploadsSize() {
		return (this.uploads == null) ? 0 : this.uploads.size();
	}

	public java.util.Iterator<com.workmarket.thrift.core.Upload> getUploadsIterator() {
		return (this.uploads == null) ? null : this.uploads.iterator();
	}

	public void addToUploads(com.workmarket.thrift.core.Upload elem) {
		if (this.uploads == null) {
			this.uploads = new ArrayList<com.workmarket.thrift.core.Upload>();
		}
		this.uploads.add(elem);
	}

	public List<com.workmarket.thrift.core.Upload> getUploads() {
		return this.uploads;
	}

	public Work setUploads(List<com.workmarket.thrift.core.Upload> uploads) {
		this.uploads = uploads;
		return this;
	}

	public boolean isSetUploads() {
		return this.uploads != null;
	}

	public boolean isPendingPaymentFulfillment() {
		return this.pendingPaymentFulfillment;
	}

	public Work setPendingPaymentFulfillment(boolean pendingPaymentFulfillment) {
		this.pendingPaymentFulfillment = pendingPaymentFulfillment;
		return this;
	}

	public long getTimeZoneId() {
		return this.timeZoneId;
	}

	public Work setTimeZoneId(long timeZoneId) {
		this.timeZoneId = timeZoneId;
		return this;
	}

	public boolean isSetTimeZoneId() {
		return (timeZoneId > 0L);
	}

	public boolean isShowCheckoutNotesFlag() {
		return this.showCheckoutNotesFlag;
	}

	public Work setShowCheckoutNotesFlag(boolean showCheckoutNotesFlag) {
		this.showCheckoutNotesFlag = showCheckoutNotesFlag;
		return this;
	}

	public boolean isCheckoutNoteRequiredFlag() {
		return this.checkoutNoteRequiredFlag;
	}

	public Work setCheckoutNoteRequiredFlag(boolean checkoutNoteRequiredFlag) {
		this.checkoutNoteRequiredFlag = checkoutNoteRequiredFlag;
		return this;
	}

	public String getCheckoutNoteInstructions() {
		return this.checkoutNoteInstructions;
	}

	public Work setCheckoutNoteInstructions(String checkoutNoteInstructions) {
		this.checkoutNoteInstructions = checkoutNoteInstructions;
		return this;
	}

	public boolean isSetCheckoutNoteInstructions() {
		return this.checkoutNoteInstructions != null;
	}

	public String getCheckinContactName() {
		return checkinContactName;
	}

	public Work setCheckinContactName(String checkinContactName) {
		this.checkinContactName = checkinContactName;
		return this;
	}

	public String getCheckinContactPhone() {
		return checkinContactPhone;
	}

	public Work setCheckinContactPhone(String checkinContactPhone) {
		this.checkinContactPhone = checkinContactPhone;
		return this;
	}

	public boolean isApprovedAdditionalExpenses() {
		return approvedAdditionalExpenses;
	}

	public Work setApprovedAdditionalExpenses(boolean approvedAdditionalExpenses) {
		this.approvedAdditionalExpenses = approvedAdditionalExpenses;
		return this;
	}

	public boolean isApprovedBonus() {
		return approvedBonus;
	}

	public Work setApprovedBonus(boolean approvedBonus) {
		this.approvedBonus = approvedBonus;
		return this;
	}

	public AccountPricingType getAccountPricingType() {
		return accountPricingType;
	}

	public Work setAccountPricingType(AccountPricingType accountPricingType) {
		this.accountPricingType = accountPricingType;
		return this;
	}

	public boolean hasTransactionalPricing() {
		return accountPricingType.getCode().equals(AccountPricingType.TRANSACTIONAL_PRICING_TYPE);
	}

	public boolean hasSubscriptionPricing() {
		return accountPricingType.getCode().equals(AccountPricingType.SUBSCRIPTION_PRICING_TYPE);
	}

	public List<Long> getFollowers() {
		return followers;
	}

	public Work setFollowers(List<Long> followers) {
		this.followers = followers;
		return this;
	}

	public void addFollower(Long followerId) {
		followers.add(followerId);
	}

	public List<Long> getNeedToApplyGroups() {
		return needToApplyGroups;
	}

	public Work setNeedToApplyGroups(List<Long> groups) {
		this.needToApplyGroups = groups;
		return this;
	}

	public void addNeedToApplyGroup(Long groupId) {
		needToApplyGroups.add(groupId);
	}

	public List<Long> getFirstToAcceptGroups() {
		return firstToAcceptGroups;
	}

	public Work setFirstToAcceptGroups(List<Long> groups) {
		this.firstToAcceptGroups = groups;
		return this;
	}

	public void addFirstToAcceptGroup(Long groupId) {
		firstToAcceptGroups.add(groupId);
	}

	public Work setShowInFeed (boolean showInFeed){
		this.showInFeed = showInFeed;
		return this;
	}

	public boolean isShowInFeed(){
		return this.showInFeed;
	}

	public Work changeTimeZone(TimeZone timeZone) {
		if (timeZone != null) {
			setTimeZone(timeZone.getTimeZoneId());
			setTimeZoneId(timeZone.getId());
			if (schedule != null) {
				schedule = new Schedule(schedule, timeZone);
			}
		}
		return this;
	}

	public String getActiveWorkerFullName() {
		if (getActiveResource() != null && getActiveResource().getUser() != null && getActiveResource().getUser().getName() != null) {
			return getActiveResource().getUser().getName().getFullName();
		}
		return "";
	}

	public Work setRequirementSetIds(List<Long> requirementSetIds) {
		this.requirementSetIds = requirementSetIds;
		return this;
	}

	public List<Long> getRequirementSetIds() {
		return requirementSetIds;
	}

	public Work setWorkNotifyAllowed(boolean notifyAllowed) {
		this.workNotifyAllowed = notifyAllowed;
		return this;
	}

	public boolean isWorkNotifyAllowed() { return this.workNotifyAllowed; }

	public Work setWorkNotifyAvailable(boolean notifyAvailable) {
		this.workNotifyAvailable = notifyAvailable;
		return this;
	}

	public boolean isWorkNotifyAvailable() { return this.workNotifyAvailable; }

	@Override
	public boolean equals(Object that) {
		if (that == null) {
			return false;
		}
		if (that instanceof Work) {
			return this.equals((Work) that);
		}
		return false;
	}

	private boolean equals(Work that) {
		if (that == null) {
			return false;
		}

		boolean this_present_id = this.isSetId();
		boolean that_present_id = that.isSetId();
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id)) {
				return false;
			}
			if (this.id != that.id) {
				return false;
			}
		}

		boolean this_present_workNumber = this.isSetWorkNumber();
		boolean that_present_workNumber = that.isSetWorkNumber();
		if (this_present_workNumber || that_present_workNumber) {
			if (!(this_present_workNumber && that_present_workNumber)) {
				return false;
			}
			if (!this.workNumber.equals(that.workNumber)) {
				return false;
			}
		}

		boolean this_present_title = this.isSetTitle();
		boolean that_present_title = that.isSetTitle();
		if (this_present_title || that_present_title) {
			if (!(this_present_title && that_present_title)) {
				return false;
			}
			if (!this.title.equals(that.title)) {
				return false;
			}
		}

		boolean this_present_description = this.isSetDescription();
		boolean that_present_description = that.isSetDescription();
		if (this_present_description || that_present_description) {
			if (!(this_present_description && that_present_description)) {
				return false;
			}
			if (!this.description.equals(that.description)) {
				return false;
			}
		}

		boolean this_present_instructions = this.isSetInstructions();
		boolean that_present_instructions = that.isSetInstructions();
		if (this_present_instructions || that_present_instructions) {
			if (!(this_present_instructions && that_present_instructions)) {
				return false;
			}
			if (!this.instructions.equals(that.instructions)) {
				return false;
			}
		}

		boolean this_present_privateInstructions = this.isSetPrivateInstructions();
		boolean that_present_privateInstructions = that.isSetPrivateInstructions();
		if (this_present_privateInstructions || that_present_privateInstructions) {
			if (!(this_present_privateInstructions && that_present_privateInstructions)) {
				return false;
			}
			if (!this.privateInstructions.equals(that.privateInstructions)) {
				return false;
			}
		}

		boolean this_present_desiredSkills = this.isSetDesiredSkills();
		boolean that_present_desiredSkills = that.isSetDesiredSkills();
		if (this_present_desiredSkills || that_present_desiredSkills) {
			if (!(this_present_desiredSkills && that_present_desiredSkills)) {
				return false;
			}
			if (!this.desiredSkills.equals(that.desiredSkills)) {
				return false;
			}
		}

		boolean this_present_shortUrl = this.isSetShortUrl();
		boolean that_present_shortUrl = that.isSetShortUrl();
		if (this_present_shortUrl || that_present_shortUrl) {
			if (!(this_present_shortUrl && that_present_shortUrl)) {
				return false;
			}
			if (!this.shortUrl.equals(that.shortUrl)) {
				return false;
			}
		}

		boolean this_present_status = this.isSetStatus();
		boolean that_present_status = that.isSetStatus();
		if (this_present_status || that_present_status) {
			if (!(this_present_status && that_present_status)) {
				return false;
			}
			if (!this.status.equals(that.status)) {
				return false;
			}
		}

		boolean this_present_subStatuses = this.isSetSubStatuses();
		boolean that_present_subStatuses = that.isSetSubStatuses();
		if (this_present_subStatuses || that_present_subStatuses) {
			if (!(this_present_subStatuses && that_present_subStatuses)) {
				return false;
			}
			if (!this.subStatuses.equals(that.subStatuses)) {
				return false;
			}
		}

		if (this.inProgress != that.inProgress) {
				return false;
		}

		boolean this_present_project = this.isSetProject();
		boolean that_present_project = that.isSetProject();
		if (this_present_project || that_present_project) {
			if (!(this_present_project && that_present_project)) {
				return false;
			}
			if (!this.project.equals(that.project)) {
				return false;
			}
		}

		boolean this_present_company = this.isSetCompany();
		boolean that_present_company = that.isSetCompany();
		if (this_present_company || that_present_company) {
			if (!(this_present_company && that_present_company)) {
				return false;
			}
			if (!this.company.equals(that.company)) {
				return false;
			}
		}

		boolean this_present_clientCompany = this.isSetClientCompany();
		boolean that_present_clientCompany = that.isSetClientCompany();
		if (this_present_clientCompany || that_present_clientCompany) {
			if (!(this_present_clientCompany && that_present_clientCompany)) {
				return false;
			}
			if (!this.clientCompany.equals(that.clientCompany)) {
				return false;
			}
		}

		boolean this_present_buyer = this.isSetBuyer();
		boolean that_present_buyer = that.isSetBuyer();
		if (this_present_buyer || that_present_buyer) {
			if (!(this_present_buyer && that_present_buyer)) {
				return false;
			}
			if (!this.buyer.equals(that.buyer)) {
				return false;
			}
		}

		boolean this_present_locationContact = this.isSetLocationContact();
		boolean that_present_locationContact = that.isSetLocationContact();
		if (this_present_locationContact || that_present_locationContact) {
			if (!(this_present_locationContact && that_present_locationContact)) {
				return false;
			}
			if (!this.locationContact.equals(that.locationContact)) {
				return false;
			}
		}

		boolean this_present_secondaryLocationContact = this.isSetSecondaryLocationContact();
		boolean that_present_secondaryLocationContact = that.isSetSecondaryLocationContact();
		if (this_present_secondaryLocationContact || that_present_secondaryLocationContact) {
			if (!(this_present_secondaryLocationContact && that_present_secondaryLocationContact)) {
				return false;
			}
			if (!this.secondaryLocationContact.equals(that.secondaryLocationContact)) {
				return false;
			}
		}

		boolean this_present_supportContact = this.isSetSupportContact();
		boolean that_present_supportContact = that.isSetSupportContact();
		if (this_present_supportContact || that_present_supportContact) {
			if (!(this_present_supportContact && that_present_supportContact)) {
				return false;
			}
			if (!this.supportContact.equals(that.supportContact)) {
				return false;
			}
		}

		boolean this_present_activeResource = this.isSetActiveResource();
		boolean that_present_activeResource = that.isSetActiveResource();
		if (this_present_activeResource || that_present_activeResource) {
			if (!(this_present_activeResource && that_present_activeResource)) {
				return false;
			}
			if (!this.activeResource.equals(that.activeResource)) {
				return false;
			}
		}

		boolean this_present_resources = this.isSetResources();
		boolean that_present_resources = that.isSetResources();
		if (this_present_resources || that_present_resources) {
			if (!(this_present_resources && that_present_resources)) {
				return false;
			}
			if (!this.resources.equals(that.resources)) {
				return false;
			}
		}

		if (this.offsiteLocation != that.offsiteLocation) {
			return false;
		}

		if (this.newLocation != that.newLocation) {
			return false;
		}

		boolean this_present_location = this.isSetLocation();
		boolean that_present_location = that.isSetLocation();
		if (this_present_location || that_present_location) {
			if (!(this_present_location && that_present_location)) {
				return false;
			}
			if (!this.location.equals(that.location)) {
				return false;
			}
		}

		boolean this_present_schedule = this.isSetSchedule();
		boolean that_present_schedule = that.isSetSchedule();
		if (this_present_schedule || that_present_schedule) {
			if (!(this_present_schedule && that_present_schedule)) {
				return false;
			}
			if (!this.schedule.equals(that.schedule)) {
				return false;
			}
		}

		boolean this_present_pricing = this.isSetPricing();
		boolean that_present_pricing = that.isSetPricing();
		if (this_present_pricing || that_present_pricing) {
			if (!(this_present_pricing && that_present_pricing)) {
				return false;
			}
			if (!this.pricing.equals(that.pricing)) {
				return false;
			}
		}

		boolean this_present_pricingHistory = this.isSetPricingHistory();
		boolean that_present_pricingHistory = that.isSetPricingHistory();
		if (this_present_pricingHistory || that_present_pricingHistory) {
			if (!(this_present_pricingHistory && that_present_pricingHistory)) {
				return false;
			}
			if (!this.pricingHistory.equals(that.pricingHistory)) {
				return false;
			}
		}

		boolean this_present_payment = this.isSetPayment();
		boolean that_present_payment = that.isSetPayment();
		if (this_present_payment || that_present_payment) {
			if (!(this_present_payment && that_present_payment)) {
				return false;
			}
			if (!this.payment.equals(that.payment)) {
				return false;
			}
		}

		boolean this_present_invoice = this.isSetInvoice();
		boolean that_present_invoice = that.isSetInvoice();
		if (this_present_invoice || that_present_invoice) {
			if (!(this_present_invoice && that_present_invoice)) {
				return false;
			}
			if (!this.invoice.equals(that.invoice)) {
				return false;
			}
		}

		boolean this_present_assets = this.isSetAssets();
		boolean that_present_assets = that.isSetAssets();
		if (this_present_assets || that_present_assets) {
			if (!(this_present_assets && that_present_assets)) {
				return false;
			}
			if (!this.assets.equals(that.assets)) {
				return false;
			}
		}

		boolean this_present_deliverableGroupDTO = this.isSetDeliverableGroupDTO();
		boolean that_present_deliverableGroupDTO = this.isSetDeliverableGroupDTO();
		if (this_present_deliverableGroupDTO || that_present_deliverableGroupDTO) {
			if (!(this_present_deliverableGroupDTO && that_present_deliverableGroupDTO)) {
				return false;
			}
			if (!this.deliverableRequirementGroupDTO.equals(that.deliverableRequirementGroupDTO)) {
				return false;
			}
		}

		if (this.resourceConfirmationRequired != that.resourceConfirmationRequired) {
			return false;
		}

		if (this.resourceConfirmationHours != that.resourceConfirmationHours) {
			return false;
		}

		if (this.isConfirmable != that.isConfirmable) {
			return false;
		}

		if (this.timetrackingRequired != that.timetrackingRequired) {
			return false;
		}

		boolean this_present_resolution = this.isSetResolution();
		boolean that_present_resolution = that.isSetResolution();
		if (this_present_resolution || that_present_resolution) {
			if (!(this_present_resolution && that_present_resolution)) {
				return false;
			}
			if (!this.resolution.equals(that.resolution)) {
				return false;
			}
		}

		boolean this_present_notes = this.isSetNotes();
		boolean that_present_notes = that.isSetNotes();
		if (this_present_notes || that_present_notes) {
			if (!(this_present_notes && that_present_notes)) {
				return false;
			}
			if (!this.notes.equals(that.notes)) {
				return false;
			}
		}

		boolean this_present_questionAnswerPairs = this.isSetQuestionAnswerPairs();
		boolean that_present_questionAnswerPairs = that.isSetQuestionAnswerPairs();
		if (this_present_questionAnswerPairs || that_present_questionAnswerPairs) {
			if (!(this_present_questionAnswerPairs && that_present_questionAnswerPairs)) {
				return false;
			}
			if (!this.questionAnswerPairs.equals(that.questionAnswerPairs)) {
				return false;
			}
		}

		boolean this_present_customFieldGroups = this.isSetCustomFieldGroups();
		boolean that_present_customFieldGroups = that.isSetCustomFieldGroups();
		if (this_present_customFieldGroups || that_present_customFieldGroups) {
			if (!(this_present_customFieldGroups && that_present_customFieldGroups)) {
				return false;
			}
			if (!this.customFieldGroups.equals(that.customFieldGroups)) {
				return false;
			}
		}

		boolean this_present_assessments = this.isSetAssessments();
		boolean that_present_assessments = that.isSetAssessments();
		if (this_present_assessments || that_present_assessments) {
			if (!(this_present_assessments && that_present_assessments)) {
				return false;
			}
			if (!this.assessments.equals(that.assessments)) {
				return false;
			}
		}

		if (this.delegationAllowed != that.delegationAllowed) {
			return false;
		}

		boolean this_present_changelog = this.isSetChangelog();
		boolean that_present_changelog = that.isSetChangelog();
		if (this_present_changelog || that_present_changelog) {
			if (!(this_present_changelog && that_present_changelog)) {
				return false;
			}
			if (!this.changelog.equals(that.changelog)) {
				return false;
			}
		}

		boolean this_present_configuration = this.isSetConfiguration();
		boolean that_present_configuration = that.isSetConfiguration();
		if (this_present_configuration || that_present_configuration) {
			if (!(this_present_configuration && that_present_configuration)) {
				return false;
			}
			if (!this.configuration.equals(that.configuration)) {
				return false;
			}
		}

		boolean this_present_template = this.isSetTemplate();
		boolean that_present_template = that.isSetTemplate();
		if (this_present_template || that_present_template) {
			if (!(this_present_template && that_present_template)) {
				return false;
			}
			if (!this.template.equals(that.template)) {
				return false;
			}
		}

		boolean this_present_industry = this.isSetIndustry();
		boolean that_present_industry = that.isSetIndustry();
		if (this_present_industry || that_present_industry) {
			if (!(this_present_industry && that_present_industry)) {
				return false;
			}
			if (!this.industry.equals(that.industry)) {
				return false;
			}
		}

		boolean this_present_routingStrategies = this.isSetRoutingStrategies();
		boolean that_present_routingStrategies = that.isSetRoutingStrategies();
		if (this_present_routingStrategies || that_present_routingStrategies) {
			if (!(this_present_routingStrategies && that_present_routingStrategies)) {
				return false;
			}
			if (!this.routingStrategies.equals(that.routingStrategies)) {
				return false;
			}
		}

		boolean this_present_timeZone = this.isSetTimeZone();
		boolean that_present_timeZone = that.isSetTimeZone();
		if (this_present_timeZone || that_present_timeZone) {
			if (!(this_present_timeZone && that_present_timeZone)) {
				return false;
			}
			if (!this.timeZone.equals(that.timeZone)) {
				return false;
			}
		}

		boolean this_present_pendingNegotiations = this.isSetPendingNegotiations();
		boolean that_present_pendingNegotiations = that.isSetPendingNegotiations();
		if (this_present_pendingNegotiations || that_present_pendingNegotiations) {
			if (!(this_present_pendingNegotiations && that_present_pendingNegotiations)) {
				return false;
			}
			if (!this.pendingNegotiations.equals(that.pendingNegotiations)) {
				return false;
			}
		}

		if (this.checkinCallRequired != that.checkinCallRequired) {
			return false;
		}

		boolean this_present_uploads = this.isSetUploads();
		boolean that_present_uploads = that.isSetUploads();
		if (this_present_uploads || that_present_uploads) {
			if (!(this_present_uploads && that_present_uploads)) {
				return false;
			}
			if (!this.uploads.equals(that.uploads)) {
				return false;
			}
		}

		if (this.pendingPaymentFulfillment != that.pendingPaymentFulfillment) {
				return false;
		}

		boolean this_present_timeZoneId = this.isSetTimeZoneId();
		boolean that_present_timeZoneId = that.isSetTimeZoneId();
		if (this_present_timeZoneId || that_present_timeZoneId) {
			if (!(this_present_timeZoneId && that_present_timeZoneId)) {
				return false;
			}
			if (this.timeZoneId != that.timeZoneId) {
				return false;
			}
		}

		if (this.showCheckoutNotesFlag != that.showCheckoutNotesFlag) {
			return false;
		}

		if (this.checkoutNoteRequiredFlag != that.checkoutNoteRequiredFlag) {
			return false;
		}

		boolean this_present_checkoutNoteInstructions = this.isSetCheckoutNoteInstructions();
		boolean that_present_checkoutNoteInstructions = that.isSetCheckoutNoteInstructions();
		if (this_present_checkoutNoteInstructions || that_present_checkoutNoteInstructions) {
			if (!(this_present_checkoutNoteInstructions && that_present_checkoutNoteInstructions)) {
				return false;
			}
			if (!this.checkoutNoteInstructions.equals(that.checkoutNoteInstructions)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true && (isSetId());
		builder.append(present_id);
		if (present_id) {
			builder.append(id);
		}

		boolean present_workNumber = true && (isSetWorkNumber());
		builder.append(present_workNumber);
		if (present_workNumber) {
			builder.append(workNumber);
		}

		boolean present_title = true && (isSetTitle());
		builder.append(present_title);
		if (present_title) {
			builder.append(title);
		}

		boolean present_description = true && (isSetDescription());
		builder.append(present_description);
		if (present_description) {
			builder.append(description);
		}

		boolean present_instructions = true && (isSetInstructions());
		builder.append(present_instructions);
		if (present_instructions) {
			builder.append(instructions);
		}

		boolean present_privateInstructions = true && (isSetPrivateInstructions());
		builder.append(present_privateInstructions);
		if (present_privateInstructions) {
			builder.append(privateInstructions);
		}

		boolean present_desiredSkills = true && (isSetDesiredSkills());
		builder.append(present_desiredSkills);
		if (present_desiredSkills) {
			builder.append(desiredSkills);
		}

		boolean present_shortUrl = true && (isSetShortUrl());
		builder.append(present_shortUrl);
		if (present_shortUrl) {
			builder.append(shortUrl);
		}

		boolean present_status = true && (isSetStatus());
		builder.append(present_status);
		if (present_status) {
			builder.append(status);
		}

		boolean present_subStatuses = true && (isSetSubStatuses());
		builder.append(present_subStatuses);
		if (present_subStatuses) {
			builder.append(subStatuses);
		}

		boolean present_inProgress = true;
		builder.append(present_inProgress);
		if (present_inProgress) {
			builder.append(inProgress);
		}

		boolean present_project = true && (isSetProject());
		builder.append(present_project);
		if (present_project) {
			builder.append(project);
		}

		boolean present_company = true && (isSetCompany());
		builder.append(present_company);
		if (present_company) {
			builder.append(company);
		}

		boolean present_clientCompany = true && (isSetClientCompany());
		builder.append(present_clientCompany);
		if (present_clientCompany) {
			builder.append(clientCompany);
		}

		boolean present_buyer = true && (isSetBuyer());
		builder.append(present_buyer);
		if (present_buyer) {
			builder.append(buyer);
		}

		boolean present_locationContact = true && (isSetLocationContact());
		builder.append(present_locationContact);
		if (present_locationContact) {
			builder.append(locationContact);
		}

		boolean present_secondaryLocationContact = true && (isSetSecondaryLocationContact());
		builder.append(present_secondaryLocationContact);
		if (present_secondaryLocationContact) {
			builder.append(secondaryLocationContact);
		}

		boolean present_supportContact = true && (isSetSupportContact());
		builder.append(present_supportContact);
		if (present_supportContact) {
			builder.append(supportContact);
		}

		boolean present_activeResource = true && (isSetActiveResource());
		builder.append(present_activeResource);
		if (present_activeResource) {
			builder.append(activeResource);
		}

		boolean present_resources = true && (isSetResources());
		builder.append(present_resources);
		if (present_resources) {
			builder.append(resources);
		}

		boolean present_offsiteLocation = true;
		builder.append(present_offsiteLocation);
		if (present_offsiteLocation) {
			builder.append(offsiteLocation);
		}

		boolean present_newLocation = true;
		builder.append(present_newLocation);
		if (present_newLocation) {
			builder.append(newLocation);
		}

		boolean present_location = true && (isSetLocation());
		builder.append(present_location);
		if (present_location) {
			builder.append(location);
		}

		boolean present_schedule = true && (isSetSchedule());
		builder.append(present_schedule);
		if (present_schedule) {
			builder.append(schedule);
		}

		boolean present_pricing = true && (isSetPricing());
		builder.append(present_pricing);
		if (present_pricing) {
			builder.append(pricing);
		}

		boolean present_pricingHistory = true && (isSetPricingHistory());
		builder.append(present_pricingHistory);
		if (present_pricingHistory) {
			builder.append(pricingHistory);
		}

		boolean present_payment = true && (isSetPayment());
		builder.append(present_payment);
		if (present_payment) {
			builder.append(payment);
		}

		boolean present_invoice = true && (isSetInvoice());
		builder.append(present_invoice);
		if (present_invoice) {
			builder.append(invoice);
		}

		boolean present_assets = true && (isSetAssets());
		builder.append(present_assets);
		if (present_assets) {
			builder.append(assets);
		}

		boolean present_deliverableGroup = true && (isSetDeliverableGroupDTO());
		builder.append(present_deliverableGroup);
		if (present_deliverableGroup) {
			builder.append(deliverableRequirementGroupDTO);
		}

		boolean present_resourceConfirmationRequired = true;
		builder.append(present_resourceConfirmationRequired);
		if (present_resourceConfirmationRequired) {
			builder.append(resourceConfirmationRequired);
		}

		boolean present_resourceConfirmationHours = true;
		builder.append(present_resourceConfirmationHours);
		if (present_resourceConfirmationHours) {
			builder.append(resourceConfirmationHours);
		}

		boolean present_isConfirmable = true;
		builder.append(present_isConfirmable);
		if (present_isConfirmable) {
			builder.append(isConfirmable);
		}

		boolean present_timetrackingRequired = true;
		builder.append(present_timetrackingRequired);
		if (present_timetrackingRequired) {
			builder.append(timetrackingRequired);
		}

		boolean present_resolution = true && (isSetResolution());
		builder.append(present_resolution);
		if (present_resolution) {
			builder.append(resolution);
		}

		boolean present_partGroup = true && (isSetPartGroup());
		builder.append(present_partGroup);
		if (present_partGroup) {
			builder.append(partGroup);
		}

		boolean present_notes = true && (isSetNotes());
		builder.append(present_notes);
		if (present_notes) {
			builder.append(notes);
		}

		boolean present_questionAnswerPairs = true && (isSetQuestionAnswerPairs());
		builder.append(present_questionAnswerPairs);
		if (present_questionAnswerPairs) {
			builder.append(questionAnswerPairs);
		}

		boolean present_customFieldGroups = true && (isSetCustomFieldGroups());
		builder.append(present_customFieldGroups);
		if (present_customFieldGroups) {
			builder.append(customFieldGroups);
		}

		boolean present_assessments = true && (isSetAssessments());
		builder.append(present_assessments);
		if (present_assessments) {
			builder.append(assessments);
		}

		boolean present_delegationAllowed = true;
		builder.append(present_delegationAllowed);
		if (present_delegationAllowed) {
			builder.append(delegationAllowed);
		}

		boolean present_changelog = true && (isSetChangelog());
		builder.append(present_changelog);
		if (present_changelog) {
			builder.append(changelog);
		}

		boolean present_configuration = true && (isSetConfiguration());
		builder.append(present_configuration);
		if (present_configuration) {
			builder.append(configuration);
		}

		boolean present_template = true && (isSetTemplate());
		builder.append(present_template);
		if (present_template) {
			builder.append(template);
		}

		boolean present_industry = true && (isSetIndustry());
		builder.append(present_industry);
		if (present_industry) {
			builder.append(industry);
		}

		boolean present_routingStrategies = true && (isSetRoutingStrategies());
		builder.append(present_routingStrategies);
		if (present_routingStrategies) {
			builder.append(routingStrategies);
		}

		boolean present_robocallAvailable = true;
		builder.append(present_robocallAvailable);

		boolean present_robocallAllowed = true;
		builder.append(present_robocallAllowed);

		boolean present_timeZone = true && (isSetTimeZone());
		builder.append(present_timeZone);
		if (present_timeZone) {
			builder.append(timeZone);
		}

		boolean present_pendingNegotiations = true && (isSetPendingNegotiations());
		builder.append(present_pendingNegotiations);
		if (present_pendingNegotiations) {
			builder.append(pendingNegotiations);
		}

		boolean present_checkinCallRequired = true;
		builder.append(present_checkinCallRequired);
		if (present_checkinCallRequired) {
			builder.append(checkinCallRequired);
		}

		boolean present_uploads = true && (isSetUploads());
		builder.append(present_uploads);
		if (present_uploads) {
			builder.append(uploads);
		}

		boolean present_pendingPaymentFulfillment = true;
		builder.append(present_pendingPaymentFulfillment);
		if (present_pendingPaymentFulfillment) {
			builder.append(pendingPaymentFulfillment);
		}

		boolean present_timeZoneId = true && (isSetTimeZoneId());
		builder.append(present_timeZoneId);
		if (present_timeZoneId) {
			builder.append(timeZoneId);
		}

		boolean present_showCheckoutNotesFlag = true;
		builder.append(present_showCheckoutNotesFlag);
		if (present_showCheckoutNotesFlag) {
			builder.append(showCheckoutNotesFlag);
		}

		boolean present_checkoutNoteRequiredFlag = true;
		builder.append(present_checkoutNoteRequiredFlag);
		if (present_checkoutNoteRequiredFlag) {
			builder.append(checkoutNoteRequiredFlag);
		}

		boolean present_checkoutNoteInstructions = true && (isSetCheckoutNoteInstructions());
		builder.append(present_checkoutNoteInstructions);
		if (present_checkoutNoteInstructions) {
			builder.append(checkoutNoteInstructions);
		}

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Work(");
		boolean first = true;

		if (isSetId()) {
			sb.append("id:");
			sb.append(this.id);
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("workNumber:");
		if (this.workNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.workNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("title:");
		if (this.title == null) {
			sb.append("null");
		} else {
			sb.append(this.title);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("description:");
		if (this.description == null) {
			sb.append("null");
		} else {
			sb.append(this.description);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("instructions:");
		if (this.instructions == null) {
			sb.append("null");
		} else {
			sb.append(this.instructions);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("privateInstructions:");
		if (this.privateInstructions == null) {
			sb.append("null");
		} else {
			sb.append(this.privateInstructions);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("desiredSkills:");
		if (this.desiredSkills == null) {
			sb.append("null");
		} else {
			sb.append(this.desiredSkills);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("shortUrl:");
		if (this.shortUrl == null) {
			sb.append("null");
		} else {
			sb.append(this.shortUrl);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("status:");
		if (this.status == null) {
			sb.append("null");
		} else {
			sb.append(this.status);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("subStatuses:");
		if (this.subStatuses == null) {
			sb.append("null");
		} else {
			sb.append(this.subStatuses);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("inProgress:");
		sb.append(this.inProgress);
		first = false;
		if (!first) sb.append(", ");
		sb.append("project:");
		if (this.project == null) {
			sb.append("null");
		} else {
			sb.append(this.project);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("company:");
		if (this.company == null) {
			sb.append("null");
		} else {
			sb.append(this.company);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("clientCompany:");
		if (this.clientCompany == null) {
			sb.append("null");
		} else {
			sb.append(this.clientCompany);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("buyer:");
		if (this.buyer == null) {
			sb.append("null");
		} else {
			sb.append(this.buyer);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("locationContact:");
		if (this.locationContact == null) {
			sb.append("null");
		} else {
			sb.append(this.locationContact);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("secondaryLocationContact:");
		if (this.secondaryLocationContact == null) {
			sb.append("null");
		} else {
			sb.append(this.secondaryLocationContact);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("supportContact:");
		if (this.supportContact == null) {
			sb.append("null");
		} else {
			sb.append(this.supportContact);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("activeResource:");
		if (this.activeResource == null) {
			sb.append("null");
		} else {
			sb.append(this.activeResource);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("resources:");
		if (this.resources == null) {
			sb.append("null");
		} else {
			sb.append(this.resources);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("offsiteLocation:");
		sb.append(this.offsiteLocation);
		sb.append("newLocation:");
		sb.append(this.newLocation);
		first = false;
		if (!first) sb.append(", ");
		sb.append("location:");
		if (this.location == null) {
			sb.append("null");
		} else {
			sb.append(this.location);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("schedule:");
		if (this.schedule == null) {
			sb.append("null");
		} else {
			sb.append(this.schedule);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("pricing:");
		if (this.pricing == null) {
			sb.append("null");
		} else {
			sb.append(this.pricing);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("pricingHistory:");
		if (this.pricingHistory == null) {
			sb.append("null");
		} else {
			sb.append(this.pricingHistory);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("payment:");
		if (this.payment == null) {
			sb.append("null");
		} else {
			sb.append(this.payment);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("invoice:");
		if (this.invoice == null) {
			sb.append("null");
		} else {
			sb.append(this.invoice);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("assets:");
		if (this.assets == null) {
			sb.append("null");
		} else {
			sb.append(this.assets);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("deliverableRequirementGroup:");
		if (this.deliverableRequirementGroupDTO == null) {
			sb.append("null");
		} else {
			sb.append(this.deliverableRequirementGroupDTO);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("resourceConfirmationRequired:");
		sb.append(this.resourceConfirmationRequired);
		first = false;
		if (!first) sb.append(", ");
		sb.append("resourceConfirmationHours:");
		sb.append(this.resourceConfirmationHours);
		first = false;
		if (!first) sb.append(", ");
		sb.append("isConfirmable:");
		sb.append(this.isConfirmable);
		first = false;
		if (!first) sb.append(", ");
		sb.append("timetrackingRequired:");
		sb.append(this.timetrackingRequired);
		first = false;
		if (!first) sb.append(", ");
		sb.append("resolution:");
		if (this.resolution == null) {
			sb.append("null");
		} else {
			sb.append(this.resolution);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("notes:");
		if (this.notes == null) {
			sb.append("null");
		} else {
			sb.append(this.notes);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("questionAnswerPairs:");
		if (this.questionAnswerPairs == null) {
			sb.append("null");
		} else {
			sb.append(this.questionAnswerPairs);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("customFieldGroups:");
		if (this.customFieldGroups == null) {
			sb.append("null");
		} else {
			sb.append(this.customFieldGroups);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("assessments:");
		if (this.assessments == null) {
			sb.append("null");
		} else {
			sb.append(this.assessments);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("delegationAllowed:");
		sb.append(this.delegationAllowed);
		first = false;
		if (!first) sb.append(", ");
		sb.append("changelog:");
		if (this.changelog == null) {
			sb.append("null");
		} else {
			sb.append(this.changelog);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("configuration:");
		if (this.configuration == null) {
			sb.append("null");
		} else {
			sb.append(this.configuration);
		}

		first = false;
		if (!first) sb.append(", ");
		sb.append("template:");
		if (this.template == null) {
			sb.append("null");
		} else {
			sb.append(this.template);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("industry:");
		if (this.industry == null) {
			sb.append("null");
		} else {
			sb.append(this.industry);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("routingStrategies:");
		if (this.routingStrategies == null) {
			sb.append("null");
		} else {
			sb.append(this.routingStrategies);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("timeZone:");
		if (this.timeZone == null) {
			sb.append("null");
		} else {
			sb.append(this.timeZone);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("pendingNegotiations:");
		if (this.pendingNegotiations == null) {
			sb.append("null");
		} else {
			sb.append(this.pendingNegotiations);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("checkinCallRequired:");
		sb.append(this.checkinCallRequired);
		first = false;
		if (!first) sb.append(", ");
		sb.append("uploads:");
		if (this.uploads == null) {
			sb.append("null");
		} else {
			sb.append(this.uploads);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("pendingPaymentFulfillment:");
		sb.append(this.pendingPaymentFulfillment);
		first = false;
		if (isSetTimeZoneId()) {
			if (!first) sb.append(", ");
			sb.append("timeZoneId:");
			sb.append(this.timeZoneId);
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("showCheckoutNotesFlag:");
		sb.append(this.showCheckoutNotesFlag);
		first = false;
		if (!first) sb.append(", ");
		sb.append("checkoutNoteRequiredFlag:");
		sb.append(this.checkoutNoteRequiredFlag);
		first = false;
		if (!first) sb.append(", ");
		sb.append("checkoutNoteInstructions:");
		if (this.checkoutNoteInstructions == null) {
			sb.append("null");
		} else {
			sb.append(this.checkoutNoteInstructions);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}

	public String getUniqueExternalIdValue() {
		return uniqueExternalIdValue;
	}

	public Work setUniqueExternalIdValue(String uniqueExternalIdValue) {
		this.uniqueExternalIdValue = uniqueExternalIdValue;
		return this;
	}

	public String getUniqueExternalIdDisplayName() {
		return uniqueExternalIdDisplayName;
	}

	public Work setUniqueExternalIdDisplayName(String uniqueExternalIdDisplayName) {
		this.uniqueExternalIdDisplayName = uniqueExternalIdDisplayName;
		return this;
	}

	public boolean getDocumentsEnabled() {
		return documentsEnabled;
	}

	public Work setDocumentsEnabled(boolean documentsEnabled) {
		this.documentsEnabled = documentsEnabled;
		return this;
	}
}

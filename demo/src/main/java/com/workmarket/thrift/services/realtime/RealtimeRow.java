package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RealtimeRow implements Serializable {
	private static final long serialVersionUID = 1L;

	private String workNumber;
	private long orderCreationDate;
	private com.workmarket.thrift.core.TimeRange assignmentTimeRange;
	private String detailsText;
	private double spendLimit;
	private int questionCount;
	private int offers;
	private int declines;
	private List<RealtimeResource> invitedResources;
	private RealtimeCompany company;
	private String timeZoneId;
	private long modifiedOn;
	private String modifierLastName;
	private String modifierFirstName;
	private long orderSentOn;
	private RealtimeUser owner;
	private List<RealtimeRowFact> facts;
	private RealtimeUser userWorkingOnIt;
	private double healthScore;
	private boolean groupSent;
	private double percentResourcesDeclined;
	private double percentTimeToWorkElapsed;
	private long dueOn;
	private int numberOfUnansweredQuestions;
	private int percentResourcesWhoViewedAssignment;
	private int percentWithOffers;
	private int percentWithRejections;
	private RealtimeProjectData projectData;

	public RealtimeRow() {
		this.groupSent = false;
	}

	public RealtimeRow(
			String workNumber,
			long orderCreationDate,
			com.workmarket.thrift.core.TimeRange assignmentTimeRange,
			String detailsText,
			int questionCount,
			int offers,
			int declines,
			RealtimeCompany company,
			String timeZoneId,
			long modifiedOn,
			String modifierLastName,
			String modifierFirstName,
			long orderSentOn,
			RealtimeUser owner) {
		this();
		this.workNumber = workNumber;
		this.orderCreationDate = orderCreationDate;
		this.assignmentTimeRange = assignmentTimeRange;
		this.detailsText = detailsText;
		this.questionCount = questionCount;
		this.offers = offers;
		this.declines = declines;
		this.company = company;
		this.timeZoneId = timeZoneId;
		this.modifiedOn = modifiedOn;
		this.modifierLastName = modifierLastName;
		this.modifierFirstName = modifierFirstName;
		this.orderSentOn = orderSentOn;
		this.owner = owner;
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public RealtimeRow setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public long getOrderCreationDate() {
		return this.orderCreationDate;
	}

	public RealtimeRow setOrderCreationDate(long orderCreationDate) {
		this.orderCreationDate = orderCreationDate;
		return this;
	}

	public com.workmarket.thrift.core.TimeRange getAssignmentTimeRange() {
		return this.assignmentTimeRange;
	}

	public RealtimeRow setAssignmentTimeRange(com.workmarket.thrift.core.TimeRange assignmentTimeRange) {
		this.assignmentTimeRange = assignmentTimeRange;
		return this;
	}

	public boolean isSetAssignmentTimeRange() {
		return this.assignmentTimeRange != null;
	}

	public String getDetailsText() {
		return this.detailsText;
	}

	public RealtimeRow setDetailsText(String detailsText) {
		this.detailsText = detailsText;
		return this;
	}

	public boolean isSetDetailsText() {
		return this.detailsText != null;
	}

	public double getSpendLimit() {
		return this.spendLimit;
	}

	public RealtimeRow setSpendLimit(double spendLimit) {
		this.spendLimit = spendLimit;
		return this;
	}

	public boolean isSetSpendLimit() {
		return (spendLimit > 0D);
	}

	public int getQuestionCount() {
		return this.questionCount;
	}

	public RealtimeRow setQuestionCount(int questionCount) {
		this.questionCount = questionCount;
		return this;
	}

	public int getOffers() {
		return this.offers;
	}

	public RealtimeRow setOffers(int offers) {
		this.offers = offers;
		return this;
	}

	public int getDeclines() {
		return this.declines;
	}

	public RealtimeRow setDeclines(int declines) {
		this.declines = declines;
		return this;
	}

	public int getInvitedResourcesSize() {
		return (this.invitedResources == null) ? 0 : this.invitedResources.size();
	}

	public java.util.Iterator<RealtimeResource> getInvitedResourcesIterator() {
		return (this.invitedResources == null) ? null : this.invitedResources.iterator();
	}

	public void addToInvitedResources(RealtimeResource elem) {
		if (this.invitedResources == null) {
			this.invitedResources = new ArrayList<RealtimeResource>();
		}
		this.invitedResources.add(elem);
	}

	public List<RealtimeResource> getInvitedResources() {
		return this.invitedResources;
	}

	public RealtimeRow setInvitedResources(List<RealtimeResource> invitedResources) {
		this.invitedResources = invitedResources;
		return this;
	}

	public boolean isSetInvitedResources() {
		return this.invitedResources != null;
	}

	public RealtimeCompany getCompany() {
		return this.company;
	}

	public RealtimeRow setCompany(RealtimeCompany company) {
		this.company = company;
		return this;
	}

	public boolean isSetCompany() {
		return this.company != null;
	}

	public String getTimeZoneId() {
		return this.timeZoneId;
	}

	public RealtimeRow setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
		return this;
	}

	public boolean isSetTimeZoneId() {
		return this.timeZoneId != null;
	}

	public long getModifiedOn() {
		return this.modifiedOn;
	}

	public RealtimeRow setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public String getModifierLastName() {
		return this.modifierLastName;
	}

	public RealtimeRow setModifierLastName(String modifierLastName) {
		this.modifierLastName = modifierLastName;
		return this;
	}

	public boolean isSetModifierLastName() {
		return this.modifierLastName != null;
	}

	public String getModifierFirstName() {
		return this.modifierFirstName;
	}

	public RealtimeRow setModifierFirstName(String modifierFirstName) {
		this.modifierFirstName = modifierFirstName;
		return this;
	}

	public boolean isSetModifierFirstName() {
		return this.modifierFirstName != null;
	}

	public long getOrderSentOn() {
		return this.orderSentOn;
	}

	public RealtimeRow setOrderSentOn(long orderSentOn) {
		this.orderSentOn = orderSentOn;
		return this;
	}

	public RealtimeUser getOwner() {
		return this.owner;
	}

	public RealtimeRow setOwner(RealtimeUser owner) {
		this.owner = owner;
		return this;
	}

	public boolean isSetOwner() {
		return this.owner != null;
	}

	public int getFactsSize() {
		return (this.facts == null) ? 0 : this.facts.size();
	}

	public java.util.Iterator<RealtimeRowFact> getFactsIterator() {
		return (this.facts == null) ? null : this.facts.iterator();
	}

	public void addToFacts(RealtimeRowFact elem) {
		if (this.facts == null) {
			this.facts = new ArrayList<RealtimeRowFact>();
		}
		this.facts.add(elem);
	}

	public List<RealtimeRowFact> getFacts() {
		return this.facts;
	}

	public RealtimeRow setFacts(List<RealtimeRowFact> facts) {
		this.facts = facts;
		return this;
	}

	public boolean isSetFacts() {
		return this.facts != null;
	}

	public RealtimeUser getUserWorkingOnIt() {
		return this.userWorkingOnIt;
	}

	public RealtimeRow setUserWorkingOnIt(RealtimeUser userWorkingOnIt) {
		this.userWorkingOnIt = userWorkingOnIt;
		return this;
	}

	public boolean isSetUserWorkingOnIt() {
		return this.userWorkingOnIt != null;
	}

	public double getHealthScore() {
		return this.healthScore;
	}

	public RealtimeRow setHealthScore(double healthScore) {
		this.healthScore = healthScore;
		return this;
	}

	public boolean isSetHealthScore() {
		return (healthScore > 0D);
	}

	public boolean isGroupSent() {
		return this.groupSent;
	}

	public RealtimeRow setGroupSent(boolean groupSent) {
		this.groupSent = groupSent;
		return this;
	}

	public double getPercentResourcesDeclined() {
		return this.percentResourcesDeclined;
	}

	public RealtimeRow setPercentResourcesDeclined(double percentResourcesDeclined) {
		this.percentResourcesDeclined = percentResourcesDeclined;
		return this;
	}

	public boolean isSetPercentResourcesDeclined() {
		return (percentResourcesDeclined > 0D);
	}

	public double getPercentTimeToWorkElapsed() {
		return this.percentTimeToWorkElapsed;
	}

	public RealtimeRow setPercentTimeToWorkElapsed(double percentTimeToWorkElapsed) {
		this.percentTimeToWorkElapsed = percentTimeToWorkElapsed;
		return this;
	}

	public boolean isSetPercentTimeToWorkElapsed() {
		return (percentTimeToWorkElapsed > 0D);
	}

	public long getDueOn() {
		return this.dueOn;
	}

	public RealtimeRow setDueOn(long dueOn) {
		this.dueOn = dueOn;
		return this;
	}

	public boolean isSetDueOn() {
		return (dueOn > 0L);
	}

	public int getNumberOfUnansweredQuestions() {
		return this.numberOfUnansweredQuestions;
	}

	public RealtimeRow setNumberOfUnansweredQuestions(int numberOfUnansweredQuestions) {
		this.numberOfUnansweredQuestions = numberOfUnansweredQuestions;
		return this;
	}

	public boolean isSetNumberOfUnansweredQuestions() {
		return (numberOfUnansweredQuestions > 0);
	}

	public int getPercentResourcesWhoViewedAssignment() {
		return this.percentResourcesWhoViewedAssignment;
	}

	public RealtimeRow setPercentResourcesWhoViewedAssignment(int percentResourcesWhoViewedAssignment) {
		this.percentResourcesWhoViewedAssignment = percentResourcesWhoViewedAssignment;
		return this;
	}

	public boolean isSetPercentResourcesWhoViewedAssignment() {
		return (percentResourcesWhoViewedAssignment > 0);
	}

	public int getPercentWithOffers() {
		return this.percentWithOffers;
	}

	public RealtimeRow setPercentWithOffers(int percentWithOffers) {
		this.percentWithOffers = percentWithOffers;
		return this;
	}

	public boolean isSetPercentWithOffers() {
		return (percentWithOffers > 0);
	}

	public int getPercentWithRejections() {
		return this.percentWithRejections;
	}

	public RealtimeRow setPercentWithRejections(int percentWithRejections) {
		this.percentWithRejections = percentWithRejections;
		return this;
	}

	public boolean isSetPercentWithRejections() {
		return (percentWithRejections > 0);
	}

	public RealtimeProjectData getProjectData() {
		return this.projectData;
	}

	public RealtimeRow setProjectData(RealtimeProjectData projectData) {
		this.projectData = projectData;
		return this;
	}

	public boolean isSetProjectData() {
		return this.projectData != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RealtimeRow)
			return this.equals((RealtimeRow) that);
		return false;
	}

	private boolean equals(RealtimeRow that) {
		if (that == null)
			return false;

		boolean this_present_workNumber = true && this.isSetWorkNumber();
		boolean that_present_workNumber = true && that.isSetWorkNumber();
		if (this_present_workNumber || that_present_workNumber) {
			if (!(this_present_workNumber && that_present_workNumber))
				return false;
			if (!this.workNumber.equals(that.workNumber))
				return false;
		}

		boolean this_present_orderCreationDate = true;
		boolean that_present_orderCreationDate = true;
		if (this_present_orderCreationDate || that_present_orderCreationDate) {
			if (!(this_present_orderCreationDate && that_present_orderCreationDate))
				return false;
			if (this.orderCreationDate != that.orderCreationDate)
				return false;
		}

		boolean this_present_assignmentTimeRange = true && this.isSetAssignmentTimeRange();
		boolean that_present_assignmentTimeRange = true && that.isSetAssignmentTimeRange();
		if (this_present_assignmentTimeRange || that_present_assignmentTimeRange) {
			if (!(this_present_assignmentTimeRange && that_present_assignmentTimeRange))
				return false;
			if (!this.assignmentTimeRange.equals(that.assignmentTimeRange))
				return false;
		}

		boolean this_present_detailsText = true && this.isSetDetailsText();
		boolean that_present_detailsText = true && that.isSetDetailsText();
		if (this_present_detailsText || that_present_detailsText) {
			if (!(this_present_detailsText && that_present_detailsText))
				return false;
			if (!this.detailsText.equals(that.detailsText))
				return false;
		}

		boolean this_present_spendLimit = true && this.isSetSpendLimit();
		boolean that_present_spendLimit = true && that.isSetSpendLimit();
		if (this_present_spendLimit || that_present_spendLimit) {
			if (!(this_present_spendLimit && that_present_spendLimit))
				return false;
			if (this.spendLimit != that.spendLimit)
				return false;
		}

		boolean this_present_questionCount = true;
		boolean that_present_questionCount = true;
		if (this_present_questionCount || that_present_questionCount) {
			if (!(this_present_questionCount && that_present_questionCount))
				return false;
			if (this.questionCount != that.questionCount)
				return false;
		}

		boolean this_present_offers = true;
		boolean that_present_offers = true;
		if (this_present_offers || that_present_offers) {
			if (!(this_present_offers && that_present_offers))
				return false;
			if (this.offers != that.offers)
				return false;
		}

		boolean this_present_declines = true;
		boolean that_present_declines = true;
		if (this_present_declines || that_present_declines) {
			if (!(this_present_declines && that_present_declines))
				return false;
			if (this.declines != that.declines)
				return false;
		}

		boolean this_present_invitedResources = true && this.isSetInvitedResources();
		boolean that_present_invitedResources = true && that.isSetInvitedResources();
		if (this_present_invitedResources || that_present_invitedResources) {
			if (!(this_present_invitedResources && that_present_invitedResources))
				return false;
			if (!this.invitedResources.equals(that.invitedResources))
				return false;
		}

		boolean this_present_company = true && this.isSetCompany();
		boolean that_present_company = true && that.isSetCompany();
		if (this_present_company || that_present_company) {
			if (!(this_present_company && that_present_company))
				return false;
			if (!this.company.equals(that.company))
				return false;
		}

		boolean this_present_timeZoneId = true && this.isSetTimeZoneId();
		boolean that_present_timeZoneId = true && that.isSetTimeZoneId();
		if (this_present_timeZoneId || that_present_timeZoneId) {
			if (!(this_present_timeZoneId && that_present_timeZoneId))
				return false;
			if (!this.timeZoneId.equals(that.timeZoneId))
				return false;
		}

		boolean this_present_modifiedOn = true;
		boolean that_present_modifiedOn = true;
		if (this_present_modifiedOn || that_present_modifiedOn) {
			if (!(this_present_modifiedOn && that_present_modifiedOn))
				return false;
			if (this.modifiedOn != that.modifiedOn)
				return false;
		}

		boolean this_present_modifierLastName = true && this.isSetModifierLastName();
		boolean that_present_modifierLastName = true && that.isSetModifierLastName();
		if (this_present_modifierLastName || that_present_modifierLastName) {
			if (!(this_present_modifierLastName && that_present_modifierLastName))
				return false;
			if (!this.modifierLastName.equals(that.modifierLastName))
				return false;
		}

		boolean this_present_modifierFirstName = true && this.isSetModifierFirstName();
		boolean that_present_modifierFirstName = true && that.isSetModifierFirstName();
		if (this_present_modifierFirstName || that_present_modifierFirstName) {
			if (!(this_present_modifierFirstName && that_present_modifierFirstName))
				return false;
			if (!this.modifierFirstName.equals(that.modifierFirstName))
				return false;
		}

		boolean this_present_orderSentOn = true;
		boolean that_present_orderSentOn = true;
		if (this_present_orderSentOn || that_present_orderSentOn) {
			if (!(this_present_orderSentOn && that_present_orderSentOn))
				return false;
			if (this.orderSentOn != that.orderSentOn)
				return false;
		}

		boolean this_present_owner = true && this.isSetOwner();
		boolean that_present_owner = true && that.isSetOwner();
		if (this_present_owner || that_present_owner) {
			if (!(this_present_owner && that_present_owner))
				return false;
			if (!this.owner.equals(that.owner))
				return false;
		}

		boolean this_present_facts = true && this.isSetFacts();
		boolean that_present_facts = true && that.isSetFacts();
		if (this_present_facts || that_present_facts) {
			if (!(this_present_facts && that_present_facts))
				return false;
			if (!this.facts.equals(that.facts))
				return false;
		}

		boolean this_present_userWorkingOnIt = true && this.isSetUserWorkingOnIt();
		boolean that_present_userWorkingOnIt = true && that.isSetUserWorkingOnIt();
		if (this_present_userWorkingOnIt || that_present_userWorkingOnIt) {
			if (!(this_present_userWorkingOnIt && that_present_userWorkingOnIt))
				return false;
			if (!this.userWorkingOnIt.equals(that.userWorkingOnIt))
				return false;
		}

		boolean this_present_healthScore = true && this.isSetHealthScore();
		boolean that_present_healthScore = true && that.isSetHealthScore();
		if (this_present_healthScore || that_present_healthScore) {
			if (!(this_present_healthScore && that_present_healthScore))
				return false;
			if (this.healthScore != that.healthScore)
				return false;
		}

		if (this.groupSent != that.groupSent)
			return false;

		boolean this_present_percentResourcesDeclined = true && this.isSetPercentResourcesDeclined();
		boolean that_present_percentResourcesDeclined = true && that.isSetPercentResourcesDeclined();
		if (this_present_percentResourcesDeclined || that_present_percentResourcesDeclined) {
			if (!(this_present_percentResourcesDeclined && that_present_percentResourcesDeclined))
				return false;
			if (this.percentResourcesDeclined != that.percentResourcesDeclined)
				return false;
		}

		boolean this_present_percentTimeToWorkElapsed = true && this.isSetPercentTimeToWorkElapsed();
		boolean that_present_percentTimeToWorkElapsed = true && that.isSetPercentTimeToWorkElapsed();
		if (this_present_percentTimeToWorkElapsed || that_present_percentTimeToWorkElapsed) {
			if (!(this_present_percentTimeToWorkElapsed && that_present_percentTimeToWorkElapsed))
				return false;
			if (this.percentTimeToWorkElapsed != that.percentTimeToWorkElapsed)
				return false;
		}

		boolean this_present_dueOn = true && this.isSetDueOn();
		boolean that_present_dueOn = true && that.isSetDueOn();
		if (this_present_dueOn || that_present_dueOn) {
			if (!(this_present_dueOn && that_present_dueOn))
				return false;
			if (this.dueOn != that.dueOn)
				return false;
		}

		boolean this_present_numberOfUnansweredQuestions = true && this.isSetNumberOfUnansweredQuestions();
		boolean that_present_numberOfUnansweredQuestions = true && that.isSetNumberOfUnansweredQuestions();
		if (this_present_numberOfUnansweredQuestions || that_present_numberOfUnansweredQuestions) {
			if (!(this_present_numberOfUnansweredQuestions && that_present_numberOfUnansweredQuestions))
				return false;
			if (this.numberOfUnansweredQuestions != that.numberOfUnansweredQuestions)
				return false;
		}

		boolean this_present_percentResourcesWhoViewedAssignment = true && this.isSetPercentResourcesWhoViewedAssignment();
		boolean that_present_percentResourcesWhoViewedAssignment = true && that.isSetPercentResourcesWhoViewedAssignment();
		if (this_present_percentResourcesWhoViewedAssignment || that_present_percentResourcesWhoViewedAssignment) {
			if (!(this_present_percentResourcesWhoViewedAssignment && that_present_percentResourcesWhoViewedAssignment))
				return false;
			if (this.percentResourcesWhoViewedAssignment != that.percentResourcesWhoViewedAssignment)
				return false;
		}

		boolean this_present_percentWithOffers = true && this.isSetPercentWithOffers();
		boolean that_present_percentWithOffers = true && that.isSetPercentWithOffers();
		if (this_present_percentWithOffers || that_present_percentWithOffers) {
			if (!(this_present_percentWithOffers && that_present_percentWithOffers))
				return false;
			if (this.percentWithOffers != that.percentWithOffers)
				return false;
		}

		boolean this_present_percentWithRejections = true && this.isSetPercentWithRejections();
		boolean that_present_percentWithRejections = true && that.isSetPercentWithRejections();
		if (this_present_percentWithRejections || that_present_percentWithRejections) {
			if (!(this_present_percentWithRejections && that_present_percentWithRejections))
				return false;
			if (this.percentWithRejections != that.percentWithRejections)
				return false;
		}

		boolean this_present_projectData = true && this.isSetProjectData();
		boolean that_present_projectData = true && that.isSetProjectData();
		if (this_present_projectData || that_present_projectData) {
			if (!(this_present_projectData && that_present_projectData))
				return false;
			if (!this.projectData.equals(that.projectData))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_workNumber = true && (isSetWorkNumber());
		builder.append(present_workNumber);
		if (present_workNumber)
			builder.append(workNumber);

		boolean present_orderCreationDate = true;
		builder.append(present_orderCreationDate);
		if (present_orderCreationDate)
			builder.append(orderCreationDate);

		boolean present_assignmentTimeRange = true && (isSetAssignmentTimeRange());
		builder.append(present_assignmentTimeRange);
		if (present_assignmentTimeRange)
			builder.append(assignmentTimeRange);

		boolean present_detailsText = true && (isSetDetailsText());
		builder.append(present_detailsText);
		if (present_detailsText)
			builder.append(detailsText);

		boolean present_spendLimit = true && (isSetSpendLimit());
		builder.append(present_spendLimit);
		if (present_spendLimit)
			builder.append(spendLimit);

		boolean present_questionCount = true;
		builder.append(present_questionCount);
		if (present_questionCount)
			builder.append(questionCount);

		boolean present_offers = true;
		builder.append(present_offers);
		if (present_offers)
			builder.append(offers);

		boolean present_declines = true;
		builder.append(present_declines);
		if (present_declines)
			builder.append(declines);

		boolean present_invitedResources = true && (isSetInvitedResources());
		builder.append(present_invitedResources);
		if (present_invitedResources)
			builder.append(invitedResources);

		boolean present_company = true && (isSetCompany());
		builder.append(present_company);
		if (present_company)
			builder.append(company);

		boolean present_timeZoneId = true && (isSetTimeZoneId());
		builder.append(present_timeZoneId);
		if (present_timeZoneId)
			builder.append(timeZoneId);

		boolean present_modifiedOn = true;
		builder.append(present_modifiedOn);
		if (present_modifiedOn)
			builder.append(modifiedOn);

		boolean present_modifierLastName = true && (isSetModifierLastName());
		builder.append(present_modifierLastName);
		if (present_modifierLastName)
			builder.append(modifierLastName);

		boolean present_modifierFirstName = true && (isSetModifierFirstName());
		builder.append(present_modifierFirstName);
		if (present_modifierFirstName)
			builder.append(modifierFirstName);

		boolean present_orderSentOn = true;
		builder.append(present_orderSentOn);
		if (present_orderSentOn)
			builder.append(orderSentOn);

		boolean present_owner = true && (isSetOwner());
		builder.append(present_owner);
		if (present_owner)
			builder.append(owner);

		boolean present_facts = true && (isSetFacts());
		builder.append(present_facts);
		if (present_facts)
			builder.append(facts);

		boolean present_userWorkingOnIt = true && (isSetUserWorkingOnIt());
		builder.append(present_userWorkingOnIt);
		if (present_userWorkingOnIt)
			builder.append(userWorkingOnIt);

		boolean present_healthScore = true && (isSetHealthScore());
		builder.append(present_healthScore);
		if (present_healthScore)
			builder.append(healthScore);

		builder.append(true);
		builder.append(groupSent);

		boolean present_percentResourcesDeclined = true && (isSetPercentResourcesDeclined());
		builder.append(present_percentResourcesDeclined);
		if (present_percentResourcesDeclined)
			builder.append(percentResourcesDeclined);

		boolean present_percentTimeToWorkElapsed = true && (isSetPercentTimeToWorkElapsed());
		builder.append(present_percentTimeToWorkElapsed);
		if (present_percentTimeToWorkElapsed)
			builder.append(percentTimeToWorkElapsed);

		boolean present_dueOn = true && (isSetDueOn());
		builder.append(present_dueOn);
		if (present_dueOn)
			builder.append(dueOn);

		boolean present_numberOfUnansweredQuestions = true && (isSetNumberOfUnansweredQuestions());
		builder.append(present_numberOfUnansweredQuestions);
		if (present_numberOfUnansweredQuestions)
			builder.append(numberOfUnansweredQuestions);

		boolean present_percentResourcesWhoViewedAssignment = true && (isSetPercentResourcesWhoViewedAssignment());
		builder.append(present_percentResourcesWhoViewedAssignment);
		if (present_percentResourcesWhoViewedAssignment)
			builder.append(percentResourcesWhoViewedAssignment);

		boolean present_percentWithOffers = true && (isSetPercentWithOffers());
		builder.append(present_percentWithOffers);
		if (present_percentWithOffers)
			builder.append(percentWithOffers);

		boolean present_percentWithRejections = true && (isSetPercentWithRejections());
		builder.append(present_percentWithRejections);
		if (present_percentWithRejections)
			builder.append(percentWithRejections);

		boolean present_projectData = true && (isSetProjectData());
		builder.append(present_projectData);
		if (present_projectData)
			builder.append(projectData);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RealtimeRow(");
		boolean first = true;

		sb.append("workNumber:");
		if (this.workNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.workNumber);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("orderCreationDate:");
		sb.append(this.orderCreationDate);
		first = false;
		if (!first) sb.append(", ");
		sb.append("assignmentTimeRange:");
		if (this.assignmentTimeRange == null) {
			sb.append("null");
		} else {
			sb.append(this.assignmentTimeRange);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("detailsText:");
		if (this.detailsText == null) {
			sb.append("null");
		} else {
			sb.append(this.detailsText);
		}
		first = false;
		if (isSetSpendLimit()) {
			if (!first) sb.append(", ");
			sb.append("spendLimit:");
			sb.append(this.spendLimit);
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("questionCount:");
		sb.append(this.questionCount);
		first = false;
		if (!first) sb.append(", ");
		sb.append("offers:");
		sb.append(this.offers);
		first = false;
		if (!first) sb.append(", ");
		sb.append("declines:");
		sb.append(this.declines);
		first = false;
		if (isSetInvitedResources()) {
			if (!first) sb.append(", ");
			sb.append("invitedResources:");
			if (this.invitedResources == null) {
				sb.append("null");
			} else {
				sb.append(this.invitedResources);
			}
			first = false;
		}
		if (!first) sb.append(", ");
		sb.append("company:");
		if (this.company == null) {
			sb.append("null");
		} else {
			sb.append(this.company);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("timeZoneId:");
		if (this.timeZoneId == null) {
			sb.append("null");
		} else {
			sb.append(this.timeZoneId);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("modifiedOn:");
		sb.append(this.modifiedOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("modifierLastName:");
		if (this.modifierLastName == null) {
			sb.append("null");
		} else {
			sb.append(this.modifierLastName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("modifierFirstName:");
		if (this.modifierFirstName == null) {
			sb.append("null");
		} else {
			sb.append(this.modifierFirstName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("orderSentOn:");
		sb.append(this.orderSentOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("owner:");
		if (this.owner == null) {
			sb.append("null");
		} else {
			sb.append(this.owner);
		}
		first = false;
		if (isSetFacts()) {
			if (!first) sb.append(", ");
			sb.append("facts:");
			if (this.facts == null) {
				sb.append("null");
			} else {
				sb.append(this.facts);
			}
			first = false;
		}
		if (isSetUserWorkingOnIt()) {
			if (!first) sb.append(", ");
			sb.append("userWorkingOnIt:");
			if (this.userWorkingOnIt == null) {
				sb.append("null");
			} else {
				sb.append(this.userWorkingOnIt);
			}
			first = false;
		}
		if (isSetHealthScore()) {
			if (!first) sb.append(", ");
			sb.append("healthScore:");
			sb.append(this.healthScore);
			first = false;
		}

		if (!first) sb.append(", ");
		sb.append("groupSent:");
		sb.append(this.groupSent);
		first = false;

		if (isSetPercentResourcesDeclined()) {
			if (!first) sb.append(", ");
			sb.append("percentResourcesDeclined:");
			sb.append(this.percentResourcesDeclined);
			first = false;
		}
		if (isSetPercentTimeToWorkElapsed()) {
			if (!first) sb.append(", ");
			sb.append("percentTimeToWorkElapsed:");
			sb.append(this.percentTimeToWorkElapsed);
			first = false;
		}
		if (isSetDueOn()) {
			if (!first) sb.append(", ");
			sb.append("dueOn:");
			sb.append(this.dueOn);
			first = false;
		}
		if (isSetNumberOfUnansweredQuestions()) {
			if (!first) sb.append(", ");
			sb.append("numberOfUnansweredQuestions:");
			sb.append(this.numberOfUnansweredQuestions);
			first = false;
		}
		if (isSetPercentResourcesWhoViewedAssignment()) {
			if (!first) sb.append(", ");
			sb.append("percentResourcesWhoViewedAssignment:");
			sb.append(this.percentResourcesWhoViewedAssignment);
			first = false;
		}
		if (isSetPercentWithOffers()) {
			if (!first) sb.append(", ");
			sb.append("percentWithOffers:");
			sb.append(this.percentWithOffers);
			first = false;
		}
		if (isSetPercentWithRejections()) {
			if (!first) sb.append(", ");
			sb.append("percentWithRejections:");
			sb.append(this.percentWithRejections);
			first = false;
		}
		if (isSetProjectData()) {
			if (!first) sb.append(", ");
			sb.append("projectData:");
			if (this.projectData == null) {
				sb.append("null");
			} else {
				sb.append(this.projectData);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}
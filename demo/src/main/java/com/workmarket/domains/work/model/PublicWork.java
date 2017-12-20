package com.workmarket.domains.work.model;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.domains.model.pricing.PricingStrategy;
import com.workmarket.utility.StringUtilities;

public class PublicWork {
	public static final int MAX_TITLE_LENGTH = 70;
	public static final int MAX_DESCRIPTION_LENGTH = 280;

	private String workNumber;
	private String title;
	private String description;
	private String sanitizedDescription;
	private Address address;
	private PricingStrategy pricingStrategy;
	private boolean scheduleRangeFlag;
	private DateRange schedule;
	private String desiredSkills;
	private boolean available;
	private TimeZone timeZone;

	public PublicWork copy(AbstractWork work) {
		this.setWorkNumber(work.getWorkNumber())
			.setTitle(StringUtilities.truncate(work.getTitle(), MAX_TITLE_LENGTH))
			.setAddress(work.getAddress())
			.setDescription(stripDescription(work.getDescription()))
			.setSanitizedDescription(sanitizeDescription(work.getDescription()))
			.setPricingStrategy(work.getPricingStrategy())
			.setScheduleRangeFlag(work.getScheduleRangeFlag())
			.setSchedule(work.getSchedule())
			.setTimeZone(work.getTimeZone())
			.setDesiredSkills(work.getDesiredSkills())
			.setAvailable(checkAvailable(work.getWorkStatusType()))
			;
		return this;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public PublicWork setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public PublicWork setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public PublicWork setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getSanitizedDescription() {
		return sanitizedDescription;
	}

	public PublicWork setSanitizedDescription(String sanitizedDescription) {
		this.sanitizedDescription = sanitizedDescription;
		return this;
	}

	public Address getAddress() {
		return address;
	}

	public PublicWork setAddress(Address address) {
		this.address = address;
		return this;
	}

	public PricingStrategy getPricingStrategy() {
		return pricingStrategy;
	}

	public PublicWork setPricingStrategy(PricingStrategy pricingStrategy) {
		this.pricingStrategy = pricingStrategy;
		return this;
	}

	public Boolean getScheduleRangeFlag() {
		return scheduleRangeFlag;
	}

	public PublicWork setScheduleRangeFlag(Boolean scheduleRangeFlag) {
		this.scheduleRangeFlag = scheduleRangeFlag;
		return this;
	}

	public DateRange getSchedule() {
		return schedule;
	}

	public PublicWork setSchedule(DateRange schedule) {
		this.schedule = schedule;
		return this;
	}

	public String getDesiredSkills() {
		return desiredSkills;
	}

	public PublicWork setDesiredSkills(String desiredSkills) {
		this.desiredSkills = desiredSkills;
		return this;
	}

	public PublicWork setAvailable(boolean available) {
		this.available = available;
		return this;
	}

	public boolean isAvailable() {
		return available;
	}

	public PublicWork setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	private String stripDescription(String description) {
		if (description == null) { return ""; }
		return StringUtilities.stripTags(description);
	}

	private String sanitizeDescription(String description) {
		if (description == null) { return ""; }

		String sanitized = StringUtilities.truncate(
			StringUtilities.extractTextFromHTML(description),
			MAX_DESCRIPTION_LENGTH);

		if (description.length() > MAX_DESCRIPTION_LENGTH) {
			sanitized += "...";
		}

		return sanitized;
	}

	private boolean checkAvailable(WorkStatusType workStatusType) {
		return
			workStatusType != null &&
			WorkStatusType.SENT.equals(workStatusType.getCode());
	}
}
